package com.rafario.miscosas.data.local.database

import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class UserDatabaseProviderReuseTest {

    @Test
    fun reusesDatabaseForSameUid() {
        val userId = UserId("user-a")
        var openCalls = 0
        val provider = UserDatabaseProvider(openDatabase = {
            openCalls++
            createTestDatabase()
        })

        val firstCall = provider.databaseFor(userId)
        val secondCall = provider.databaseFor(userId)

        assertSame(firstCall, secondCall)
        assertEquals(1, openCalls)

        provider.closeCurrentDatabase()
    }

    @Test
    fun opensDifferentDatabaseForDifferentUids() = runTest {
        val firstUser = UserId("user-a")
        val secondUser = UserId("user-b")
        val openedIds = mutableListOf<UserId>()

        var openCalls = 0
        val provider = UserDatabaseProvider { id ->
            openedIds.add(id)
            openCalls++
            createTestDatabase()
        }

        val firstResult = provider.databaseFor(firstUser)
        val secondResult = provider.databaseFor(secondUser)

        var oldDatabaseRejectedQuery = false
        try {
            firstResult.userDao().findById(firstUser.value)
        } catch (_: Exception) {
            oldDatabaseRejectedQuery = true
        }
        assertTrue(oldDatabaseRejectedQuery)

        assertEquals(listOf(firstUser, secondUser), openedIds)

        assertNotSame(firstResult, secondResult)
        assertEquals(2, openCalls)

        provider.closeCurrentDatabase()
    }

    @Test
    fun closeCurrentDatabaseClosesCurrentContext() = runTest {
        var openCalls = 0
        val firstUser = UserId("user-a")
        val provider = UserDatabaseProvider {
            openCalls++
            createTestDatabase()
        }

        val firstResult = provider.databaseFor(firstUser)
        provider.closeCurrentDatabase()
        val firstResultClosed = provider.databaseFor(firstUser)

        assertNotSame(firstResult, firstResultClosed)
        assertEquals(2, openCalls)

        provider.closeCurrentDatabase()

        var oldDatabaseRejectedQuery = false
        try {
            firstResult.userDao().findById("missing-user")
        } catch (_: Exception) {
            oldDatabaseRejectedQuery = true
        }
        assertTrue(oldDatabaseRejectedQuery)
    }
}