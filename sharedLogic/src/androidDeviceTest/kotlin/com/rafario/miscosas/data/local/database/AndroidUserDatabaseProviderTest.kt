package com.rafario.miscosas.data.local.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class AndroidUserDatabaseProviderTest {

    @Test
    fun opensUidDatabaseWithAndroidContext() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val provider = createAndroidUserDatabaseProvider(context)
        val database = provider.databaseFor(UserId("auth-t04-instrumentation-smoke"))

        try {
            val user = database.userDao().findById("missing-user")
            assertNull(user)
        } finally {
            provider.closeCurrentDatabase()
        }
    }

    @Test
    fun persistsUserWhenSameUidDatabaseIsReopened() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val userId = UserId("auth-t04-persistence-smoke")
        val profileId = "profile-a"
        val firstProvider = createAndroidUserDatabaseProvider(context)

        try {
            firstProvider.databaseFor(userId).userDao().upsert(
                UserEntity(
                    id = profileId,
                    displayName = "Perfil de prueba",
                    createdAtEpochSeconds = 0L,
                    createdAtNanoseconds = 0,
                    updatedAtEpochSeconds = 0L,
                    updatedAtNanoseconds = 0,
                )
            )
        } finally {
            firstProvider.closeCurrentDatabase()
        }

        val reopenedProvider = createAndroidUserDatabaseProvider(context)
        try {
            val savedUser = reopenedProvider
                .databaseFor(userId)
                .userDao()
                .findById(profileId)

            assertEquals("Perfil de prueba", savedUser?.displayName)
        } finally {
            reopenedProvider.closeCurrentDatabase()
        }
    }

    @Test
    fun doesNotShareProfileBetweenDifferentUids() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val userId = UserId("auth-t04-persistence-smoke")
        val profileId = "profile-a"
        val firstProvider = createAndroidUserDatabaseProvider(context)

        try {
            firstProvider.databaseFor(userId).userDao().upsert(
                UserEntity(
                    id = profileId,
                    displayName = "Perfil de prueba",
                    createdAtEpochSeconds = 0L,
                    createdAtNanoseconds = 0,
                    updatedAtEpochSeconds = 0L,
                    updatedAtNanoseconds = 0,
                )
            )
        } finally {
            firstProvider.closeCurrentDatabase()
        }

        val reopenedProvider = createAndroidUserDatabaseProvider(context)
        try {
            val savedUser = reopenedProvider
                .databaseFor(UserId("user-b"))
                .userDao()
                .findById(profileId)

            assertNull(savedUser)
        } finally {
            reopenedProvider.closeCurrentDatabase()
        }
    }

    @Test
    fun switchingUidKeepsProfilesIsolatedAndPreservesData() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val userId = UserId("auth-t04-persistence-smoke")
        val profileId = "profile-a"
        val provider = createAndroidUserDatabaseProvider(context)
        val userA = UserEntity(
            id = profileId,
            displayName = "Perfil de prueba",
            createdAtEpochSeconds = 0L,
            createdAtNanoseconds = 0,
            updatedAtEpochSeconds = 0L,
            updatedAtNanoseconds = 0,
        )

        try {
            provider.databaseFor(userId).userDao().upsert(userA)
            val checkB = provider.databaseFor(UserId("user-b")).userDao().findById(profileId)

            assertNull(checkB)

            val checkA = provider.databaseFor(userId).userDao().findById(profileId)

            assertEquals("Perfil de prueba", checkA?.displayName)

        } finally {
            provider.closeCurrentDatabase()
        }
    }

    @Test
    fun keepsHouseholdAndPendingOutboxIsolatedAcrossUidSwitch() = runTest {
        val userAId = UserId("auth-t06-android-user-a")
        val userBId = UserId("auth-t06-android-user-b")
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val provider = createAndroidUserDatabaseProvider(context)

        try {
            val userA = UserEntity(
                id = userAId.value,
                displayName = "Perfil de prueba",
                createdAtEpochSeconds = 0L,
                createdAtNanoseconds = 0,
                updatedAtEpochSeconds = 0L,
                updatedAtNanoseconds = 0,
            )

            provider.databaseFor(userAId).userDao().upsert(userA)

            val household = HouseholdEntity(
                id = "auth-t06-android-household-a",
                name = "HouseholdA",
                createdBy = userA.id,
                createdAtEpochSeconds = 0L,
                createdAtNanoseconds = 0,
                updatedAtEpochSeconds = 0L,
                updatedAtNanoseconds = 0,
            )

            provider.databaseFor(userAId).householdDao().upsert(household)

            val syncEntity = SyncOutboxEntity(
                scopeId = household.id,
                recordTypeCode = SyncRecordType.HOUSEHOLD.code,
                recordId = household.id,
                mutationId = "550e8400-e29b-41d4-a716-446655440001",
                operationCode = SyncOperation.UPSERT.code,
                baseRemoteVersion = null,
                enqueuedAtEpochSeconds = 0L,
                enqueuedAtNanoseconds = 0,
            )

            provider.databaseFor(userAId).syncOutboxDao().upsert(syncEntity)

            //Aquí irán las comprobaciones de B y el regreso a A

            assertNull(provider.databaseFor(userBId).userDao().findById(userA.id))
            assertNull(provider.databaseFor(userBId).householdDao().findById(household.id))
            assertNull(
                provider.databaseFor(userBId).syncOutboxDao().findByTarget(
                    scopeId = syncEntity.scopeId,
                    recordTypeCode = SyncRecordType.HOUSEHOLD.code,
                    recordId = syncEntity.recordId,
                )
            )

            assertEquals(
                "Perfil de prueba",
                provider.databaseFor(userAId).userDao().findById(userA.id)?.displayName
            )
            assertEquals(
                "HouseholdA",
                provider.databaseFor(userAId)
                    .householdDao()
                    .findById(household.id)
                    ?.name,
            )

            assertEquals(
                syncEntity,
                provider.databaseFor(userAId)
                    .syncOutboxDao()
                    .findByTarget(
                        scopeId = syncEntity.scopeId,
                        recordTypeCode = syncEntity.recordTypeCode,
                        recordId = syncEntity.recordId,
                    ),
            )
        } finally {
            provider.closeCurrentDatabase()
        }
    }
}
