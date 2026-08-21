package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserIdTest {

    @Test
    fun acceptsOpaqueNonUuidUserId() {
        val id = UserId(value = "firebase-user_A1b2C3")

        assertEquals("firebase-user_A1b2C3", id.value)
    }

    @Test
    fun rejectsBlankUserId() {
        assertFailsWith<IllegalArgumentException> {
            UserId(value = "   ")
        }
    }

    @Test
    fun returnsUserIdValueAsString() {
        val id = UserId(value = "firebase-user_A1b2C3")

        assertEquals(id.value, id.toString())
    }
}