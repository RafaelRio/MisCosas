package com.rafario.miscosas.data.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.rafario.miscosas.api.auth.NativeAuthenticationException
import com.rafario.miscosas.api.auth.NativeAuthenticationFailure
import com.rafario.miscosas.api.auth.NativeSessionLookupResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FirebaseNativeAuthenticationProviderTest {

    @Test
    fun registersWithAuthEmulatorAndReturnsAuthenticatedUid() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)
        val email = "auth-t11-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            val returnedUid = try {
                provider.registerWithEmail(
                    email = email,
                    password = password,
                )
            } catch (failure: NativeAuthenticationException) {
                throw AssertionError(
                    "Auth Emulator registration failed with category ${failure.failure}",
                    failure,
                )
            }

            assertTrue("Firebase should return a non-empty UID", returnedUid.isNotBlank())
            assertEquals(returnedUid, firebaseAuth.currentUser?.uid)
        } finally {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun signsInWithAuthEmulatorAndReturnsExistingUid() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)
        val email = "auth-t11-signin-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            val registeredUid = provider.registerWithEmail(
                email = email,
                password = password,
            )
            firebaseAuth.signOut()

            val signedInUid = provider.signInWithEmail(
                email = email,
                password = password,
            )

            assertEquals(registeredUid, signedInUid)
            assertEquals(signedInUid, firebaseAuth.currentUser?.uid)
        } finally {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun rejectsUnknownAccountAsCredentialsRejected() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)
        val email = "auth-t11-invalid-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            val failure = try {
                provider.signInWithEmail(email, password)
                null
            } catch (exception: NativeAuthenticationException) {
                exception
            }

            assertEquals(
                NativeAuthenticationFailure.CREDENTIALS_REJECTED,
                failure?.failure,
            )
            assertNull(firebaseAuth.currentUser)
        } finally {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun returnsNoSessionWhenThereIsNoAuthenticatedUser() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)

        val result = provider.getCurrentSession()

        assertEquals(NativeSessionLookupResult.NoSession, result)
    }

    @Test
    fun returnsAuthenticatedSessionAfterRefreshingCurrentUser() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)
        val email = "auth-t11-session-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            val registeredUid = provider.registerWithEmail(
                email = email,
                password = password,
            )

            val result = provider.getCurrentSession()

            assertEquals(
                NativeSessionLookupResult.Authenticated(
                    userId = registeredUid,
                    isEmailVerified = false,
                ),
                result,
            )
        } finally {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun sendsVerificationEmailForAuthenticatedUser() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)
        val email = "auth-t11-verify-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            val registeredUid = provider.registerWithEmail(
                email = email,
                password = password,
            )

            provider.sendEmailVerification()

            val currentUser = firebaseAuth.currentUser
            assertEquals(registeredUid, currentUser?.uid)
            assertFalse(currentUser?.isEmailVerified ?: true)
        } finally {
            firebaseAuth.signOut()
        }
    }
}
