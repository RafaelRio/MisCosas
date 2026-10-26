package com.rafario.miscosas.api.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class NativeSessionLookupResultTest {

    @Test
    fun noSessionIsDistinctFromRecoverableError() {
        val noSession: NativeSessionLookupResult = NativeSessionLookupResult.NoSession
        val recoverableError: NativeSessionLookupResult = NativeSessionLookupResult.RecoverableError
        assertNotEquals(noSession, recoverableError)
    }

    @Test
    fun authenticatedResultPreservesUidAndVerificationStatus() {
        val authenticated = NativeSessionLookupResult.Authenticated(
            userId = "user-prueba",
            isEmailVerified = false
        )

        assertEquals("user-prueba", authenticated.userId)
        assertFalse(authenticated.isEmailVerified)
    }
}