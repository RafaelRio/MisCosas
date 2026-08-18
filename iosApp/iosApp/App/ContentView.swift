import SharedLogic
import SwiftUI

struct ContentView: View {
    @State private var appFlow: AppFlow = .loading

    var body: some View {
        switch appFlow {

        case .loading:
            SplashView {
                appFlow = .authentication
            }

        case .authentication:
            AuthenticationFlowView(
                onAuthenticationFinished: {
                    appFlow = .main
                }
            )

        case .main:
            // De momento, hasta que creemos MainTabView
            Text("Main App")
        }
    }
}
