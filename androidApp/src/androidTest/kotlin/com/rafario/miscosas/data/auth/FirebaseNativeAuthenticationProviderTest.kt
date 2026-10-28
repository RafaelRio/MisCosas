package com.rafario.miscosas.data.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.rafario.miscosas.api.auth.NativeAuthenticationException
import com.rafario.miscosas.api.auth.NativeAuthenticationFailure
import com.rafario.miscosas.api.auth.NativeSessionLookupResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
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
    fun rejectsDuplicateRegistrationAsCredentialsRejected() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)
        val email = "auth-t11-duplicate-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            val originalUid = provider.registerWithEmail(email, password)
            firebaseAuth.signOut()

            val failure = try {
                provider.registerWithEmail(email, password)
                null
            } catch (exception: NativeAuthenticationException) {
                exception
            }

            assertEquals(
                NativeAuthenticationFailure.CREDENTIALS_REJECTED,
                failure?.failure,
            )

            firebaseAuth.signOut()
            assertEquals(originalUid, provider.signInWithEmail(email, password))
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

    @Test
    fun rejectsVerificationRequestWhenSignedOut() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)

        try {
            val failure = try {
                provider.sendEmailVerification()
                null
            } catch (exception: NativeAuthenticationException) {
                exception
            }

            assertEquals(
                NativeAuthenticationFailure.UNEXPECTED,
                failure?.failure,
            )
            assertNull(firebaseAuth.currentUser)
        } finally {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun mapsFirebaseNetworkExceptionToTemporary() {
        assertEquals(
            NativeAuthenticationFailure.TEMPORARY,
            FirebaseNetworkException("Test network failure")
                .toNativeAuthenticationFailure(),
        )
    }

    @Test
    fun mapsUnreachableAuthDuringSignInToTemporary() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        // TEST-NET-1 is reserved for documentation and has no Auth Emulator.
        firebaseAuth.useEmulator("192.0.2.1", 9099)
        firebaseAuth.signOut()

        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)

        try {
            val failure = try {
                provider.signInWithEmail(
                    email = "auth-t11-network-${UUID.randomUUID()}@example.com",
                    password = "Valid-test-password-123!",
                )
                null
            } catch (exception: NativeAuthenticationException) {
                exception
            }

            assertEquals(
                "Firebase cause: ${failure?.cause?.javaClass?.name}: ${failure?.cause?.message}",
                NativeAuthenticationFailure.TEMPORARY,
                failure?.failure,
            )
            assertNull(firebaseAuth.currentUser)
        } finally {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun mapsFirebaseTooManyRequestsExceptionToRateLimited() {
        assertEquals(
            NativeAuthenticationFailure.RATE_LIMITED,
            FirebaseTooManyRequestsException("Test rate limit")
                .toNativeAuthenticationFailure(),
        )
    }

    @Test
    fun propagatesCancellationWhileSignInRequestIsPending() = runBlocking {
        val server = ServerSocket(0)
        val requestAccepted = CountDownLatch(1)
        val releaseServer = CountDownLatch(1)
        val acceptedSocket = AtomicReference<Socket?>()
        val serverThread = Thread {
            try {
                acceptedSocket.set(server.accept())
                requestAccepted.countDown()
                releaseServer.await()
            } catch (_: Exception) {
                // Closing the socket/server is the expected cleanup path.
            }
        }
        serverThread.start()

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", server.localPort)
        firebaseAuth.signOut()
        val provider = FirebaseNativeAuthenticationProvider(firebaseAuth)

        try {
            val pendingSignIn = async(start = CoroutineStart.UNDISPATCHED) {
                provider.signInWithEmail(
                    email = "auth-t11-cancel@example.com",
                    password = "Valid-test-password-123!",
                )
            }

            assertTrue(
                "Firebase Auth request did not reach the local test server",
                requestAccepted.await(5, TimeUnit.SECONDS),
            )

            pendingSignIn.cancel(CancellationException("Test cancellation"))

            val cancellation = try {
                pendingSignIn.await()
                null
            } catch (exception: CancellationException) {
                exception
            }

            assertTrue("Auth cancellation should propagate", cancellation != null)
            assertNull(firebaseAuth.currentUser)
        } finally {
            firebaseAuth.signOut()
            releaseServer.countDown()
            acceptedSocket.get()?.close()
            server.close()
            serverThread.join(1_000)
        }
    }
}
