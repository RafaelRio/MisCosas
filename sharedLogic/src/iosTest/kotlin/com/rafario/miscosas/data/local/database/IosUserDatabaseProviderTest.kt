package com.rafario.miscosas.data.local.database

import com.rafario.miscosas.data.local.database.entity.UserEntity
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
}