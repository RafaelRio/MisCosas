package com.rafario.miscosas.api.session

import com.rafario.miscosas.api.auth.NativeAuthenticationProvider
import com.rafario.miscosas.data.local.database.createIosUserDatabaseProvider
import com.rafario.miscosas.data.repository.NativeAuthenticationRepository
import com.rafario.miscosas.data.repository.RoomUserRepositoryProvider
import com.rafario.miscosas.domain.repository.RemoteUserProfileRepository
import com.rafario.miscosas.domain.usecase.GetSessionStateUseCase

public fun createIosSessionRestorationFacade(
    nativeAuthenticationProvider: NativeAuthenticationProvider,
    remoteUserProfileRepository: RemoteUserProfileRepository,
): SessionRestorationFacade {
    val nativeAuthenticationRepository = NativeAuthenticationRepository(
        provider = nativeAuthenticationProvider
    )
    val roomFactory = createIosUserDatabaseProvider()
    val roomProvider = RoomUserRepositoryProvider(
        database = roomFactory, repository = nativeAuthenticationRepository
    )

    return SessionRestorationFacade(
        GetSessionStateUseCase(
            repository = nativeAuthenticationRepository,
            userRepositoryProvider = roomProvider,
            remoteUserProfileRepository = remoteUserProfileRepository
        )
    )
}