//
//  FirebaseNativeAuthenticationProvider.swift
//  iosApp
//
//  Created by Rafael Río on 28/10/2026.
//

import FirebaseAuth
import SharedLogic

final class FirebaseNativeAuthenticationProvider:
    NSObject,
    NativeAuthenticationProvider {

    private let auth: Auth

    init(auth: Auth = Auth.auth()) {
        self.auth = auth
    }

    func getCurrentSession(
        completionHandler: @escaping (
            (any NativeSessionLookupResult)?,
            Error?
        ) -> Void
    ) {
        guard let user = auth.currentUser else {
            completionHandler(
                NativeSessionLookupResultNoSession.shared,
                nil
            )
            return
        }

        user.reload { [weak self] error in
            guard let self else { return }

            if let error {
                if self.isInvalidSession(error) {
                    completionHandler(
                        NativeSessionLookupResultNoSession.shared,
                        nil
                    )
                } else {
                    completionHandler(
                        NativeSessionLookupResultRecoverableError.shared,
                        nil
                    )
                }
                return
            }

            completionHandler(
                NativeSessionLookupResultAuthenticated(
                    userId: user.uid,
                    isEmailVerified: user.isEmailVerified
                ),
                nil
            )
        }
    }

    func signInWithEmail(
        email: String,
        password: String,
        completionHandler: @escaping (String?, Error?) -> Void
    ) {
        auth.signIn(withEmail: email, password: password) { [weak self] result, error in
            guard let self else { return }

            if let error {
                completionHandler(nil, self.nativeError(for: error))
                return
            }

            guard let uid = result?.user.uid else {
                completionHandler(nil, self.nativeError(.unexpected))
                return
            }

            completionHandler(uid, nil)
        }
    }

    func registerWithEmail(
        email: String,
        password: String,
        completionHandler: @escaping (String?, Error?) -> Void
    ) {
        auth.createUser(withEmail: email, password: password) { [weak self] result, error in
            guard let self else { return }

            if let error {
                completionHandler(nil, self.nativeError(for: error))
                return
            }

            guard let uid = result?.user.uid else {
                completionHandler(nil, self.nativeError(.unexpected))
                return
            }

            completionHandler(uid, nil)
        }
    }

    func sendEmailVerification(
        completionHandler: @escaping (Error?) -> Void
    ) {
        guard let user = auth.currentUser else {
            completionHandler(nativeError(.unexpected))
            return
        }

        user.sendEmailVerification { [weak self] error in
            guard let self else { return }
            completionHandler(error.map { self.nativeError(for: $0) })
        }
    }

    private func isInvalidSession(_ error: Error) -> Bool {
        let nsError = error as NSError
        guard nsError.domain == AuthErrorDomain,
              let code = AuthErrorCode(rawValue: nsError.code) else {
            return false
        }

        switch code {
        case .userNotFound, .userDisabled, .invalidUserToken, .userTokenExpired:
            return true
        default:
            return false
        }
    }

    private func nativeError(for error: Error) -> any Error {
        let nsError = error as NSError
        var failure = NativeAuthenticationFailure.unexpected

        if nsError.domain == AuthErrorDomain,
           let code = AuthErrorCode(rawValue: nsError.code) {
            switch code {
            case .networkError:
                failure = .temporary
            case .tooManyRequests:
                failure = .rateLimited
            case .invalidCredential, .invalidEmail, .wrongPassword,
                 .userNotFound, .userDisabled, .invalidUserToken,
                 .userTokenExpired, .emailAlreadyInUse:
                failure = .credentialsRejected
            default:
                break
            }
        }

        return nativeError(failure)
    }

    private func nativeError(
        _ failure: NativeAuthenticationFailure
    ) -> any Error {
        NativeAuthenticationException(
            failure: failure,
            cause: nil
        ).asError()
    }
}
