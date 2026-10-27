package com.rafario.miscosas.data.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.rafario.miscosas.api.auth.NativeAuthenticationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
}
