package com.rafario.miscosas.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AuthenticationSessionResultTest {

    @Test
    fun noSessionIsDistinctFromRecoverableError() {
        val noSession: AuthenticationSessionResult =
            AuthenticationSessionResult.NoSession
        val recoverableError: AuthenticationSessionResult =
            AuthenticationSessionResult.RecoverableError

        assertNotEquals(noSession, recoverableError)
    }

    @Test
    fun authenticatedResultRetainsUserIdAndUnverifiedStatus() {
        val userId = UserId("auth-t07-domain-user")
        val result = AuthenticationSessionResult.Authenticated(
            userId = userId,
            isEmailVerified = false,
        )

        assertEquals(userId, result.userId)
        assertEquals(false, result.isEmailVerified)
    }

    @Test
    fun authenticatedResultRetainsVerifiedStatus() {
        val userId = UserId("auth-t07-domain-verified-user")
        val result = AuthenticationSessionResult.Authenticated(
            userId = userId,
            isEmailVerified = true,
        )

        assertEquals(userId, result.userId)
        assertEquals(true, result.isEmailVerified)
    }
}
