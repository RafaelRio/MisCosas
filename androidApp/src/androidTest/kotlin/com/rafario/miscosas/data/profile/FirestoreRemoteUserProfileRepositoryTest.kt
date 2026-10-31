package com.rafario.miscosas.data.profile

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class FirestoreRemoteUserProfileRepositoryTest {

    @Test
    fun readsMissingProfileThenUpsertsAndReadsOwnProfile() = runBlocking {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.useEmulator("127.0.0.1", 9099)
        firebaseAuth.signOut()

        val firestore = FirebaseFirestore.getInstance()
        firestore.useEmulator("127.0.0.1", 8080)

        val repository = FirestoreRemoteUserProfileRepository(firestore)
        val email = "firestore-t15-${UUID.randomUUID()}@example.com"
        val password = "Valid-test-password-123!"

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = UserId(
                firebaseAuth.currentUser?.uid
                    ?: throw AssertionError("Auth Emulator did not return a user UID"),
            )

            assertNull(repository.findById(userId))

            firestore.disableNetwork().await()
            val offlineReadFailure = try {
                repository.findById(userId)
                null
            } catch (exception: FirebaseFirestoreException) {
                exception
            } finally {
                firestore.enableNetwork().await()
            }
            assertNotNull(
                "An offline read must not be treated as confirmed remote absence",
                offlineReadFailure,
            )
            assertEquals(userId.value, firebaseAuth.currentUser?.uid)

            val foreignReadFailure = try {
                repository.findById(UserId("another-test-user"))
                null
            } catch (exception: FirebaseFirestoreException) {
                exception
            }
            assertEquals(
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                foreignReadFailure?.code,
            )

            val user = User(
                id = userId,
                displayName = "Perfil de prueba",
                createdAt = Instant.parse("2026-08-27T09:00:00Z"),
                updatedAt = Instant.parse("2026-08-27T09:10:00Z"),
            )

            repository.upsert(user)

            assertEquals(user, repository.findById(userId))

            val updatedUser = user.copy(
                displayName = "Perfil actualizado",
                updatedAt = Instant.parse("2026-08-27T09:20:00Z"),
            )
            repository.upsert(updatedUser)

            assertEquals(updatedUser, repository.findById(userId))
        } finally {
            firebaseAuth.signOut()
        }
    }
}
