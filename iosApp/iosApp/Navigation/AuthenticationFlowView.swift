//
// Created by Río Pérez, Rafael on 18/08/2026.
//

import SwiftUI

struct AuthenticationFlowView: View {

    @State private var path: [AuthRoute] = []

    let onAuthenticationFinished: () -> Void

    var body: some View {
        NavigationStack(path: $path) {

            WelcomeView(
                onLoginClick: {
                    path.append(.login)
                },
                onRegisterClick: {
                    path.append(.register)
                },
                onHowItWorksClick: {
                    // ...
                }
            )
            .navigationDestination(for: AuthRoute.self) { route in
                destination(for: route)
            }
        }
    }

    @ViewBuilder
    private func destination(for route: AuthRoute) -> some View {

        switch route {

        case .login:
            EmptyView()


        case .register:
            EmptyView()

        case .forgotPassword:
            EmptyView()

        case .recoveryEmailSent(let email):
            EmptyView()

        case .verifyEmail(let email):
            EmptyView()

        case .onboarding:
            EmptyView()

        case .createHome:
            EmptyView()

        case .joinHome:
            EmptyView()
        }
    }


    private func replaceCurrentRoute(with route: AuthRoute) {
        if !path.isEmpty {
            path.removeLast()
        }

        path.append(route)
    }

    private func goToLogin() {
        path = [.login]
    }
}