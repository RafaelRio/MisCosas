import SwiftUI

import FirebaseCore
import SwiftUI
import UIKit

final class FirebaseAppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(FirebaseAppDelegate.self)
    private var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}