package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.User
import com.rafario.miscosas.domain.model.UserId

internal interface UserRepository {
    suspend fun findById(userId: UserId): User?

    suspend fun save(user: User)
}
