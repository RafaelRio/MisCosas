package com.rafario.miscosas.data.local.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.Test
import androidx.test.platform.app.InstrumentationRegistry
import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
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
}