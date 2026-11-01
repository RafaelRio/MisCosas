//
//  SessionRestorationComposition.swift
//  iosApp
//
//  Created by Rafael Río on 01/11/2026.
//

import SharedLogic

final class SessionRestorationComposition {
    let facade: SessionRestorationFacade

    init() {
        let nativeAuthenticationProvider = FirebaseNativeAuthenticationProvider()
        let remoteUserProfileRepository = FirestoreRemoteUserProfileRepository()

        self.facade = SessionRestorationFacadeFactory_iosKt
            .createIosSessionRestorationFacade(
                nativeAuthenticationProvider: nativeAuthenticationProvider,
                remoteUserProfileRepository: remoteUserProfileRepository
            )
    }
}
