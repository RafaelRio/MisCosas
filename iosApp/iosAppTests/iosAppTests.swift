//
//  iosAppTests.swift
//  iosAppTests
//
//  Created by Rafael Río on 28/10/2026.
//

import FirebaseCore
import FirebaseAuth
import SharedLogic
import Testing
@testable import MisCosas

struct iosAppTests {
    @Test
    func registersAndRestoresSessionFromAuthEmulator() async throws {
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        let auth = Auth.auth()
        auth.useEmulator(withHost: "127.0.0.1", port: 9099)
        try? auth.signOut()

        defer {
            try? auth.signOut()
        }

        let provider = FirebaseNativeAuthenticationProvider(auth: auth)
        let email = "ios-\(UUID().uuidString.lowercased())@example.com"
        let password = "TestPassword123!"

        let uid = try await withCheckedThrowingContinuation {
            (continuation: CheckedContinuation<String, Error>) in

            provider.registerWithEmail(email: email, password: password) { uid, error in
                if let error {
                    continuation.resume(throwing: error)
                } else if let uid {
                    continuation.resume(returning: uid)
                } else {
                    continuation.resume(
                        throwing: NSError(
                            domain: "iosAppTests",
                            code: 1,
                            userInfo: [NSLocalizedDescriptionKey: "El registro no devolvió UID"]
                        )
                    )
                }
            }
        }

        let session = try await withCheckedThrowingContinuation {
            (continuation: CheckedContinuation<NativeSessionLookupResult, Error>) in

            provider.getCurrentSession { result, error in
                if let error {
                    continuation.resume(throwing: error)
                } else if let result {
                    continuation.resume(returning: result)
                } else {
                    continuation.resume(
                        throwing: NSError(
                            domain: "iosAppTests",
                            code: 2,
                            userInfo: [NSLocalizedDescriptionKey: "La consulta no devolvió sesión"]
                        )
                    )
                }
            }
        }

        let authenticated = session as? NativeSessionLookupResultAuthenticated
        #expect(authenticated?.userId == uid)
        #expect(authenticated?.isEmailVerified == false)
    }
}
