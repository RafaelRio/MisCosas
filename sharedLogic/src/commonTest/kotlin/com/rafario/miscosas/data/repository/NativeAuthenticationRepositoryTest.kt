package com.rafario.miscosas.data.repository

import com.rafario.miscosas.api.auth.NativeAuthenticationProvider
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

class NativeAuthenticationRepositoryTest {

    @Test
    fun returnsNullWhenNativeProviderHasNoSession() = runTest {
        val provider = FakeNativeAuthenticationProvider()
        val repository = NativeAuthenticationRepository(provider)

        val result = repository.getCurrentUserId()

        assertNull(result)
    }

    @Test
    fun returnsUserIdWhenNativeProviderHasSession() = runTest {
        val provider = FakeNativeAuthenticationProvider(id = "user-123")
        val repository = NativeAuthenticationRepository(provider)

        val result = repository.getCurrentUserId()

        assertEquals(UserId("user-123"), result)
    }

    @Test
    fun propagatesFailureWhenNativeSessionLookupFails() = runTest {
        val exception = IllegalStateException("No se pudo consultar la sesión")

        val provider = object : NativeAuthenticationProvider {
            override suspend fun getCurrentUserId(): String? {
                throw exception
            }
        }
        val repository = NativeAuthenticationRepository(provider)

        val caughtException = assertFailsWith<IllegalStateException> {
            repository.getCurrentUserId()
        }

        assertSame(exception, caughtException)
    }

    private class FakeNativeAuthenticationProvider(val id: String? = null) :
        NativeAuthenticationProvider {

        override suspend fun getCurrentUserId(): String? = id
    }
}