//
// Created by Río Pérez, Rafael on 18/08/2026.
//

import Foundation

enum AuthRoute: Hashable {
    case login
    case register
    case forgotPassword
    case recoveryEmailSent(email: String)
    case verifyEmail(email: String)
    case onboarding
    case createHome
    case joinHome
}