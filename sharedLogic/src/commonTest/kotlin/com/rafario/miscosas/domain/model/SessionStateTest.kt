package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class SessionStateTest {

    @Test
    fun restoringIsDistinctFromSignedOut() {
        val signedOut: SessionState = SessionState.SignedOut
        val restoring: SessionState = SessionState.Restoring
        assertNotEquals(signedOut, restoring)
    }

    @Test
    fun unverifiedIsDistinctFromProfilePending() {
        val userId = UserId("usuario-prueba-a")

        val unverified: SessionState = SessionState.Unverified(userId)
        val profilePending: SessionState = SessionState.ProfilePending(userId)

        assertNotEquals(unverified, profilePending)
        val result = assertIs<SessionState.Unverified>(unverified)
        assertEquals(userId, result.userId)
    }
}