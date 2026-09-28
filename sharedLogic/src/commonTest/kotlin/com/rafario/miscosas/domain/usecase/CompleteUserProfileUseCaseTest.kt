package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class CompleteUserProfileUseCaseTest {

    @Test
    fun doesNotSaveWhenLocalUserAlreadyExists() = runTest {
        val existingUser = User(
            id = UserId("firebase-user-123"),
            displayName = "Rafael",
            createdAt = Instant.parse("2026-08-27T09:00:00Z"),
            updatedAt = Instant.parse("2026-08-28T10:00:00Z"),
        )

        val repository = ExistingUserRepository(existingUser)

        val createUserUseCase = CreateUserUseCase(
            userRepository = repository,
        )

        val useCase = CompleteUserProfileUseCase(
            userRepository = repository,
            createUserUseCase = createUserUseCase,
        )

        useCase(
            userId = existingUser.id,
            displayName = "Nombre del reintento",
        )

        assertEquals(existingUser.id, repository.requestedUserId)
        assertEquals(0, repository.saveCalls)
    }

    @Test
    fun createsLocalProfileWhenUserDoesNotExist() = runTest {
        val userId = UserId("firebase-user-123")
        val displayName = "Rafael"
        val now = Instant.parse("2026-08-29T09:00:00Z")

        val clock = object : Clock {
            override fun now(): Instant = now
        }

        val repository = NonExistingUserRepository()

        val createUserUseCase = CreateUserUseCase(
            userRepository = repository,
            clock = clock
        )

        val useCase = CompleteUserProfileUseCase(
            userRepository = repository,
            createUserUseCase = createUserUseCase,
        )

        useCase(
            userId = userId,
            displayName = displayName,
        )

        assertEquals(userId, repository.requestedUserId)
        assertEquals(1, repository.saveCalls)
        assertEquals(
            User(
                id = userId,
                displayName = displayName,
                createdAt = now,
                updatedAt = now,
            ),
            repository.savedUser,
        )
    }

    @Test
    fun preservesCreatedProfileWhenCompletionIsRetried() = runTest {
        val userId = UserId("firebase-user-123")
        val createdAt = Instant.parse("2026-08-29T09:00:00Z")
        var now = createdAt
        val clock = object : Clock {
            override fun now(): Instant = now
        }
        val repository = InMemoryUserRepository()
        val useCase = CompleteUserProfileUseCase(
            userRepository = repository,
            createUserUseCase = CreateUserUseCase(repository, clock),
        )
        val expectedUser = User(
            id = userId,
            displayName = "Rafael",
            createdAt = createdAt,
            updatedAt = createdAt,
        )

        useCase(userId, "Rafael")

        assertEquals(expectedUser, repository.findById(userId))
        assertEquals(1, repository.saveCalls)

        now = Instant.parse("2026-08-30T10:00:00Z")
        useCase(userId, "Nombre del reintento")

        assertEquals(expectedUser, repository.findById(userId))
        assertEquals(1, repository.saveCalls)
    }

    private class InMemoryUserRepository : UserRepository {
        private val users = mutableMapOf<UserId, User>()

        var saveCalls: Int = 0
            private set

        override suspend fun findById(userId: UserId): User? = users[userId]

        override suspend fun save(user: User) {
            users[user.id] = user
            saveCalls++
        }
    }

    private class ExistingUserRepository(
        private val existingUser: User,
    ) : UserRepository {

        var requestedUserId: UserId? = null
            private set

        var saveCalls: Int = 0
            private set

        override suspend fun findById(userId: UserId): User? {
            requestedUserId = userId
            return existingUser.takeIf { it.id == userId }
        }

        override suspend fun save(user: User) {
            saveCalls++
        }
    }

    private class NonExistingUserRepository: UserRepository {

        var requestedUserId: UserId? = null
            private set

        var savedUser: User? = null
            private set

        var saveCalls: Int = 0
            private set

        override suspend fun findById(userId: UserId): User? {
            requestedUserId = userId
            return null
        }

        override suspend fun save(user: User) {
            savedUser = user
            saveCalls++
        }
    }
}
