package com.rafario.miscosas.api.session

import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.usecase.GetSessionStateUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

public class SessionRestorationFacade internal constructor(
    private val getSessionStateUseCase: GetSessionStateUseCase,
) {
    private val requestMutex = Mutex()
    private val useCaseMutex = Mutex()
    private var activeRestore: Deferred<SessionState>? = null

    public suspend fun restoreSession(): SessionState = coroutineScope {
        val requestedRestore = async(start = CoroutineStart.LAZY) {
            useCaseMutex.withLock {
                getSessionStateUseCase()
            }
        }

        requestMutex.withLock {
            activeRestore?.cancel()
            activeRestore = requestedRestore
        }

        try {
            requestedRestore.start()
            requestedRestore.await()
        } finally {
            withContext(NonCancellable) {
                requestMutex.withLock {
                    if (activeRestore === requestedRestore) {
                        activeRestore = null
                    }
                }
            }
        }
    }
}
