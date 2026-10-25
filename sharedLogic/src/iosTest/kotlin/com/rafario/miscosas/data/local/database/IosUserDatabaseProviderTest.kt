package com.rafario.miscosas.data.local.database

import com.rafario.miscosas.data.local.database.entity.HouseholdEntity
import com.rafario.miscosas.data.local.database.entity.SyncOutboxEntity
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.data.sync.model.SyncOperation
import com.rafario.miscosas.data.sync.model.SyncRecordType
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IosUserDatabaseProviderTest {

    @Test
    fun persistsProfileAfterClosingAndReopeningDatabase() = runTest {
        val userId = UserId("auth-t05-ios-persistence")
        val profile = UserEntity(
            id = "profile-a",
            displayName = "Perfil de prueba",
            createdAtEpochSeconds = 0L,
            createdAtNanoseconds = 0,
            updatedAtEpochSeconds = 0L,
            updatedAtNanoseconds = 0,
        )

        val firstProvider = createIosUserDatabaseProvider()
        try {
            firstProvider.databaseFor(userId)
                .userDao()
                .upsert(profile)
        } finally {
            firstProvider.closeCurrentDatabase()
        }

        val reopenedProvider = createIosUserDatabaseProvider()
        try {
            val savedProfile = reopenedProvider
                .databaseFor(userId)
                .userDao()
                .findById(profile.id)

            assertEquals("Perfil de prueba", savedProfile?.displayName)
        } finally {
            reopenedProvider.closeCurrentDatabase()
        }
    }

    @Test
    fun isolatesProfilesWhenSwitchingUids() = runTest {
        val userA = UserId("auth-t05-ios-user-a")
        val userB = UserId("auth-t05-ios-user-b")
        val profile = UserEntity(
            id = "profile-a",
            displayName = "Perfil de A",
            createdAtEpochSeconds = 0L,
            createdAtNanoseconds = 0,
            updatedAtEpochSeconds = 0L,
            updatedAtNanoseconds = 0,
        )
        val provider = createIosUserDatabaseProvider()

        try {
            provider.databaseFor(userA)
                .userDao()
                .upsert(profile)

            val profileVisibleToB = provider.databaseFor(userB)
                .userDao()
                .findById(profile.id)

            assertNull(profileVisibleToB)

            val profileAfterReturningToA = provider.databaseFor(userA)
                .userDao()
                .findById(profile.id)

            assertEquals("Perfil de A", profileAfterReturningToA?.displayName)
        } finally {
            provider.closeCurrentDatabase()
        }
    }

    @Test
    fun keepsHouseholdAndPendingOutboxIsolatedAcrossUidSwitch() = runTest {
        val userA = UserId("auth-t06-ios-user-a")
        val userB = UserId("auth-t06-ios-user-b")
        val provider = createIosUserDatabaseProvider()

        try {
            val user = UserEntity(
                id = userA.value,
                displayName = "Perfil de prueba",
                createdAtEpochSeconds = 0L,
                createdAtNanoseconds = 0,
                updatedAtEpochSeconds = 0L,
                updatedAtNanoseconds = 0
            )
            provider.databaseFor(userA).userDao().upsert(user)

            val household = HouseholdEntity(
                id = "auth-t06-ios-household-a",
                name = "HouseholdA",
                createdBy = userA.value,
                createdAtEpochSeconds = 0L,
                createdAtNanoseconds = 0,
                updatedAtEpochSeconds = 0L,
                updatedAtNanoseconds = 0,
            )
            provider.databaseFor(userA).householdDao().upsert(household)

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
            provider.databaseFor(userA).syncOutboxDao().upsert(syncEntity)

            assertNull(provider.databaseFor(userB).userDao().findById(userA.value))
            assertNull(provider.databaseFor(userB).householdDao().findById(household.id))
            assertNull(
                provider.databaseFor(userB).syncOutboxDao().findByTarget(
                    scopeId = syncEntity.scopeId,
                    recordTypeCode = syncEntity.recordTypeCode,
                    recordId = syncEntity.recordId
                )
            )

            assertEquals(
                "Perfil de prueba",
                provider.databaseFor(userA).userDao().findById(user.id)?.displayName
            )
            assertEquals(
                "HouseholdA",
                provider.databaseFor(userA)
                    .householdDao()
                    .findById(household.id)
                    ?.name,
            )

            assertEquals(
                syncEntity,
                provider.databaseFor(userA)
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