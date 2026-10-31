package com.rafario.miscosas.data.repository

import com.rafario.miscosas.data.local.database.UserDatabaseProvider
import com.rafario.miscosas.domain.model.UserId
import com.rafario.miscosas.domain.repository.AuthenticationRepository
import com.rafario.miscosas.domain.repository.UserRepository
import com.rafario.miscosas.domain.repository.UserRepositoryProvider
import com.rafario.miscosas.domain.session.OwnProfileAccessGuard

internal class RoomUserRepositoryProvider(
    val database: UserDatabaseProvider,
    val repository: AuthenticationRepository
) : UserRepositoryProvider {
    override fun forUser(userId: UserId): UserRepository {
        val db = database.databaseFor(userId)
        return RoomUserRepository(
            database = db,
            ownProfileAccessGuard = OwnProfileAccessGuard(repository),
        )
    }

    override fun closeCurrent() {
        database.closeCurrentDatabase()
    }
}