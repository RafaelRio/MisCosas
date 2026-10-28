package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.AuthenticationSessionResult
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignInWithEmailUseCaseTest {

    @Test
    fun forwardsTrimmedEmailAndShortPasswordToAuthentication() = runTest {
        val userId = UserId("user-id-prueba")
        val email = "  Ana@Example.com  "
        val password = "x"
        val repository = FakeAuthenticationRepository(
            userId
        )

        val useCase = SignInWithEmailUseCase(repository)

        val result = useCase(
            email = email, password = password
        )

        assertEquals(userId, result)
        assertEquals("Ana@Example.com", repository.receivedEmail)
        assertEquals(password, repository.receivedPassword)
        assertEquals(1, repository.signInCalls)
    }

    @Test
    fun rejectsBasicInvalidEmailsBeforeAuthentication() = runTest {
        val userId = UserId("user-id-prueba")
        val repository = FakeAuthenticationRepository(
            userIdToReturn = userId
        )
        val useCase = SignInWithEmailUseCase(repository)

        listOf("ana.example.com", "@example.com", "ana@", "", "    ").forEach {
            assertFailsWith<IllegalArgumentException> { useCase(it, "x") }
        }

        assertEquals(0, repository.signInCalls)
    }

    @Test
    fun rejectsBasicEmptyPasswordBeforeAuthentication() = runTest {
        val userId = UserId("user-id-prueba")
        val repository = FakeAuthenticationRepository(
            userIdToReturn = userId
        )
        val useCase = SignInWithEmailUseCase(repository)


        assertFailsWith<IllegalArgumentException> {
            useCase("ana@example.com", "")
        }


        assertEquals(0, repository.signInCalls)
    }

    private class FakeAuthenticationRepository(
        private val userIdToReturn: UserId,
    ) : AuthenticationRepository {

        var receivedEmail: String? = null
            private set

        var receivedPassword: String? = null
            private set

        var signInCalls: Int = 0
            private set

        override suspend fun registerWithEmail(
            email: String,
            password: String,
        ): UserId {
            error("No se debe registrar durante el inicio de sesión")
        }

        override suspend fun signInWithEmail(
            email: String, password: String
        ): UserId {
            receivedEmail = email
            receivedPassword = password
            signInCalls++
            return userIdToReturn
        }

        override suspend fun getCurrentSession(): AuthenticationSessionResult {
            error("No se debe consultar la sesión durante el inicio de sesión")
        }

        override suspend fun sendEmailVerification() {

        }
    }
}