package com.rafario.miscosas.data.profile

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.RemoteUserProfileRepository
import kotlinx.coroutines.tasks.await
import kotlin.time.Instant

internal class FirestoreRemoteUserProfileRepository(
    private val firestore: FirebaseFirestore
) : RemoteUserProfileRepository {
    override suspend fun findById(userId: UserId): User? {
        val snapshot = firestore.collection("users").document(userId.value).get().await()

        if (!snapshot.exists()) return null

        val displayName =
            snapshot.getString("displayName") ?: error("El perfil remoto no contiene displayName")

        val createdAt = snapshot.getTimestamp("createdAt")?.toKotlinInstant()
            ?: error("El perfil remoto no contiene createdAt")

        val updatedAt = snapshot.getTimestamp("updatedAt")?.toKotlinInstant()
            ?: error("El perfil remoto no contiene updatedAt")

        return User(
            id = userId,
            displayName = displayName,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    override suspend fun upsert(user: User) {
        val profile = mapOf(
            "displayName" to user.displayName,
            "createdAt" to user.createdAt.toFirestoreTimestamp(),
            "updatedAt" to user.updatedAt.toFirestoreTimestamp(),
        )

        firestore.collection("users").document(user.id.value).set(profile).await()
    }

    private fun Timestamp.toKotlinInstant(): Instant =
        Instant.fromEpochSeconds(seconds, nanoseconds)

    private fun Instant.toFirestoreTimestamp(): Timestamp =
        Timestamp(epochSeconds, nanosecondsOfSecond)
}
