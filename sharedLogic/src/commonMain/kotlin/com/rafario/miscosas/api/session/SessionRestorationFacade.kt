package com.rafario.miscosas.api.session

import com.rafario.miscosas.domain.model.SessionState
import com.rafario.miscosas.domain.usecase.GetSessionStateUseCase

public class SessionRestorationFacade internal constructor(
    private val getSessionStateUseCase: GetSessionStateUseCase,
) {
    public suspend fun restoreSession(): SessionState =
        getSessionStateUseCase()
}