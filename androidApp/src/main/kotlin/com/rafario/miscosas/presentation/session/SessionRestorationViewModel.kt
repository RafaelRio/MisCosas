package com.rafario.miscosas.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafario.miscosas.domain.model.SessionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionRestorationViewModel(
    private val getSessionState: suspend () -> SessionState,
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Restoring)
    val sessionState = _sessionState.asStateFlow()

    private var identifier = 0
    private var job: Job? = null

    fun restore() {
        val requestId = ++identifier
        job?.cancel()
        _sessionState.value = SessionState.Restoring
        job = viewModelScope.launch {
            try {
                val result = getSessionState()
                if (requestId == identifier) {
                    _sessionState.value = result
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (requestId == identifier) {
                    _sessionState.value = SessionState.RecoverableError
                }
            }
        }
    }
}