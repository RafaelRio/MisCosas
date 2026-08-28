package com.rafario.miscosas.domain.usecase

import com.rafario.miscosas.domain.model.UserId

internal class RegistrationPartiallyCompletedException(
    val authenticatedUserId: UserId,
    val displayName: String,
    cause: Throwable,
) : Exception(
    "Authentication succeeded, but the local user profile could not be saved",
    cause,
)
