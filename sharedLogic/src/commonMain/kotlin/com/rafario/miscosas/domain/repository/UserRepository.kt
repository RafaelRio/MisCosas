package com.rafario.miscosas.domain.repository

import com.rafario.miscosas.domain.model.User

internal interface UserRepository {
    suspend fun save(user: User)
}
