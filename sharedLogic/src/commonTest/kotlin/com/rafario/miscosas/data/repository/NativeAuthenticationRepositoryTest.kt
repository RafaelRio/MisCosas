package com.rafario.miscosas.data.repository

import com.rafario.miscosas.api.auth.NativeAuthenticationProvider
import com.rafario.miscosas.api.auth.NativeSessionLookupResult
import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class NativeAuthenticationRepositoryTest {

    @Test
    fun mapsNoNativeSessionToDomainNoSession() = runTest {
        val provider = FakeNativeAuthenticationProvider()
        val repository = NativeAuthenticationRepository(provider)

        val result = repository.getCurrentSession()

        assertEquals(AuthenticationSessionResult.NoSession, result)
    }

    @Test
    fun mapsAuthenticatedNativeSessionToDomainSession() = runTest {
        val provider = FakeNativeAuthenticationProvider(
            result = NativeSessionLookupResult.Authenticated(
                userId = "user-123",
                isEmailVerified = false,
            ),
        )
        val repository = NativeAuthenticationRepository(provider)

        val result = repository.getCurrentSession()

        assertEquals(
            AuthenticationSessionResult.Authenticated(
                userId = UserId("user-123"),
                isEmailVerified = false,
            ),
            result,
        )
    }

    @Test
    fun mapsNativeRecoverableErrorToDomainRecoverableError() = runTest {
        val provider = FakeNativeAuthenticationProvider(
            result = NativeSessionLookupResult.RecoverableError,
        )
        val repository = NativeAuthenticationRepository(provider)

        val result = repository.getCurrentSession()

        assertEquals(AuthenticationSessionResult.RecoverableError, result)
    }

    @Test
    fun propagatesFailureWhenNativeSessionLookupFails() = runTest {
        val exception = IllegalStateException("No se pudo consultar la sesión")

        val provider = object : NativeAuthenticationProvider {
            override suspend fun getCurrentSession(): NativeSessionLookupResult {
                throw exception
            }

            override suspend fun signInWithEmail(
                email: String, password: String
            ): String {
                return "unused-user-id"
            }

            override suspend fun registerWithEmail(
                email: String,
                password: String
            ): String {
                return "unused-user-id"
            }

            override suspend fun sendEmailVerification() {
            }
        }
        val repository = NativeAuthenticationRepository(provider)

        val caughtException = assertFailsWith<IllegalStateException> {
            repository.getCurrentSession()
        }

        assertSame(exception, caughtException)
    }

    @Test
    fun mapsNativeSignInUidAndForwardsCredentials() = runTest {
        val provider = FakeNativeAuthenticationProvider()
        val nativeRepository = NativeAuthenticationRepository(
            provider = provider
        )

        val result = nativeRepository.signInWithEmail(
            email = "Ana@Example.com", password = "x"
        )

        assertEquals(UserId("native-user-id"), result)
        assertEquals("Ana@Example.com", provider.receivedEmail)
        assertEquals("x", provider.receivedPassword)
        assertEquals(1, provider.signInCalls)
    }

    @Test
    fun mapsNativeRegistrationUidAndForwardsCredentials() = runTest {
        val provider = FakeNativeAuthenticationProvider()
        val repository = NativeAuthenticationRepository(provider)

        val result = repository.registerWithEmail(
            email = "Ana@Example.com",
            password = "Password123",
        )

        assertEquals(UserId("native-user-id"), result)
        assertEquals("Ana@Example.com", provider.registeredEmail)
        assertEquals("Password123", provider.registeredPassword)
        assertEquals(1, provider.registerCalls)
        assertEquals(0, provider.signInCalls)
    }

    @Test
    fun forwardsEmailVerificationRequestToNativeProvider() = runTest {
        val provider = FakeNativeAuthenticationProvider()
        val repository = NativeAuthenticationRepository(provider)

        repository.sendEmailVerification()

        assertEquals(1, provider.sendVerificationCalls)
    }

    private class FakeNativeAuthenticationProvider(
        private val result: NativeSessionLookupResult = NativeSessionLookupResult.NoSession,
    ) : NativeAuthenticationProvider {

        var receivedEmail: String? = null
        var receivedPassword: String? = null
        var signInCalls = 0
        var registeredEmail: String? = null
        var registeredPassword: String? = null
        var registerCalls = 0
        var sendVerificationCalls = 0

        override suspend fun getCurrentSession(): NativeSessionLookupResult {
            return result
        }

        override suspend fun signInWithEmail(
            email: String, password: String
        ): String {
            receivedEmail = email
            receivedPassword = password
            signInCalls++
            return "native-user-id"
        }

        override suspend fun registerWithEmail(
            email: String,
            password: String
        ): String {
            registeredEmail = email
            registeredPassword = password
            registerCalls++
            return "native-user-id"
        }

        override suspend fun sendEmailVerification() {
            sendVerificationCalls++
        }
    }
}
