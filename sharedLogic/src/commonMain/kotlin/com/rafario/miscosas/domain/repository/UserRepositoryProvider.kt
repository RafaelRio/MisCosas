package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.UserId

internal interface UserRepositoryProvider {
    fun forUser(userId: UserId): UserRepository

    fun closeCurrent()
}