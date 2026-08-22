package com.rafario.miscosas.data.local.database.mapper

import com.rafario.miscosas.data.local.database.entity.UserEntity
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class UserEntityMapperTest {

    @Test
    fun mapsUserToEntityPreservingAllFieldsAndNanoseconds() {
        val createdAt = Instant.parse("2026-08-22T10:00:00.000000400Z")
        val updatedAt = Instant.parse("2026-08-22T10:00:00.000000500Z")
        val user = User(
            id = UserId("firebase-user_A1b2C3"),
            displayName = "Rafael",
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        val entity = user.toEntity()

        assertEquals("firebase-user_A1b2C3", entity.id)
        assertEquals("Rafael", entity.displayName)
        assertEquals(createdAt.epochSeconds, entity.createdAtEpochSeconds)
        assertEquals(400, entity.createdAtNanoseconds)
        assertEquals(updatedAt.epochSeconds, entity.updatedAtEpochSeconds)
        assertEquals(500, entity.updatedAtNanoseconds)
    }

    @Test
    fun mapsUserEntityToDomainPreservingAllFieldsAndNanoseconds() {
        val createdAt = Instant.parse("2026-08-22T10:00:00.000000400Z")
        val updatedAt = Instant.parse("2026-08-22T10:00:00.000000500Z")
        val entity = UserEntity(
            id = "firebase-user_A1b2C3",
            displayName = "Rafael",
            createdAtEpochSeconds = createdAt.epochSeconds,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = updatedAt.epochSeconds,
            updatedAtNanoseconds = 500,
        )

        val user = entity.toDomain()

        assertEquals(UserId("firebase-user_A1b2C3"), user.id)
        assertEquals("Rafael", user.displayName)
        assertEquals(createdAt, user.createdAt)
        assertEquals(updatedAt, user.updatedAt)
    }

    @Test
    fun rejectsStoredNanosecondsOutsideValidRange() {
        val instant = Instant.parse("2026-08-22T10:00:00Z")
        val entity = UserEntity(
            id = "firebase-user_A1b2C3",
            displayName = "Rafael",
            createdAtEpochSeconds = instant.epochSeconds,
            createdAtNanoseconds = 400,
            updatedAtEpochSeconds = instant.epochSeconds,
            updatedAtNanoseconds = 500,
        )

        assertFailsWith<IllegalStateException> {
            entity.copy(createdAtNanoseconds = -1).toDomain()
        }

        assertFailsWith<IllegalStateException> {
            entity.copy(updatedAtNanoseconds = 1_000_000_000).toDomain()
        }
    }
}