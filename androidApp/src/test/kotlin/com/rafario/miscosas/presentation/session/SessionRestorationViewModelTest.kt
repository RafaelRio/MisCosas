package com.rafario.miscosas.presentation.session

import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Instant

class SessionRestorationViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun currentRestoreFailureEmitsRecoverableError() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = SessionRestorationViewModel {
                throw IllegalStateException("Temporary session restoration failure")
            }

            viewModel.restore()
            runCurrent()

            assertEquals(SessionState.RecoverableError, viewModel.sessionState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun lateErrorFromCancelledRestoreDoesNotReplaceNewestSession() = runTest {
        val userB = createUser("firebase-user-b", "Usuario B")
        val firstRestoreStarted = CompletableDeferred<Unit>()
        val releaseFirstRestore = CompletableDeferred<Unit>()
        val firstErrorProduced = CompletableDeferred<Unit>()
        var invocationCount = 0

        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = SessionRestorationViewModel {
                when (++invocationCount) {
                    1 -> withContext(NonCancellable) {
                        firstRestoreStarted.complete(Unit)
                        releaseFirstRestore.await()
                        firstErrorProduced.complete(Unit)
                        throw IllegalStateException("Late failure from restore A")
                    }

                    2 -> SessionState.Ready(userB)
                    else -> error("Unexpected restore invocation: $invocationCount")
                }
            }

            viewModel.restore()
            runCurrent()
            firstRestoreStarted.await()

            viewModel.restore()
            runCurrent()
            assertEquals(SessionState.Ready(userB), viewModel.sessionState.value)

            releaseFirstRestore.complete(Unit)
            runCurrent()
            firstErrorProduced.await()
            runCurrent()

            assertEquals(SessionState.Ready(userB), viewModel.sessionState.value)
            assertEquals(2, invocationCount)
        } finally {
            releaseFirstRestore.complete(Unit)
            runCurrent()
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun lateReadyFromCancelledRestoreDoesNotReplaceNewestSession() = runTest {
        val userA = createUser("firebase-user-a", "Usuario A")
        val userB = createUser("firebase-user-b", "Usuario B")
        val firstRestoreStarted = CompletableDeferred<Unit>()
        val releaseFirstRestore = CompletableDeferred<Unit>()
        val firstResultProduced = CompletableDeferred<Unit>()
        var invocationCount = 0

        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = SessionRestorationViewModel {
                when (++invocationCount) {
                    1 -> withContext(NonCancellable) {
                        firstRestoreStarted.complete(Unit)
                        releaseFirstRestore.await()
                        firstResultProduced.complete(Unit)
                        SessionState.Ready(userA)
                    }

                    2 -> SessionState.Ready(userB)
                    else -> error("Unexpected restore invocation: $invocationCount")
                }
            }

            assertEquals(SessionState.Restoring, viewModel.sessionState.value)

            viewModel.restore()
            runCurrent()
            firstRestoreStarted.await()

            viewModel.restore()
            runCurrent()
            assertEquals(SessionState.Ready(userB), viewModel.sessionState.value)

            releaseFirstRestore.complete(Unit)
            runCurrent()
            firstResultProduced.await()
            runCurrent()

            assertEquals(SessionState.Ready(userB), viewModel.sessionState.value)
            assertEquals(2, invocationCount)
        } finally {
            releaseFirstRestore.complete(Unit)
            runCurrent()
            Dispatchers.resetMain()
        }
    }

    private fun createUser(id: String, displayName: String) = User(
        id = UserId(id),
        displayName = displayName,
        createdAt = Instant.parse("2026-11-09T10:00:00Z"),
        updatedAt = Instant.parse("2026-11-09T10:00:00Z"),
    )
}
