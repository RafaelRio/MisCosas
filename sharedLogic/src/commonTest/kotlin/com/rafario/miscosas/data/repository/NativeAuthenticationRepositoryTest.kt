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
        }
        val repository = NativeAuthenticationRepository(provider)

        val caughtException = assertFailsWith<IllegalStateException> {
            repository.getCurrentSession()
        }

        assertSame(exception, caughtException)
    }

    private class FakeNativeAuthenticationProvider(
        private val result: NativeSessionLookupResult =
            NativeSessionLookupResult.NoSession,
    ) :
        NativeAuthenticationProvider {

        override suspend fun getCurrentSession(): NativeSessionLookupResult {
            return result
        }
    }
}
