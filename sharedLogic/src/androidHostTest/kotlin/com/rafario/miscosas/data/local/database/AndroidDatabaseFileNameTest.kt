package com.rafario.miscosas.data.local.database

import com.rafario.miscosas.domain.model.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AndroidDatabaseFileNameTest {

    @Test
    fun testDatabaseName() {
        val firstUserId = UserId("user-a")
        val secondUserId = UserId("user-b")

        val firstName = returnDeterministicName(firstUserId)
        val repeatFirstName = returnDeterministicName(firstUserId)
        val secondName = returnDeterministicName(secondUserId)

        assertNotEquals(firstName, secondName)
        assertEquals(firstName, repeatFirstName)

        assertFalse(firstName.contains(firstUserId.value))
        assertTrue(firstName.startsWith("miscosas-") && firstName.endsWith(".db"))

        val hash = firstName.removePrefix("miscosas-").removeSuffix(".db")
        assertEquals(64, hash.length)
        assertTrue(hash.all { it in '0'..'9' || it in 'a'..'f' })
    }
}