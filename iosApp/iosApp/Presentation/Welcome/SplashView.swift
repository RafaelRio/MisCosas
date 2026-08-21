//
// Created by Río Pérez, Rafael on 18/08/2026.
//

import Foundation
import SwiftUI

struct SplashView: View {

    let onFinished: () -> Void

    var body: some View {
        ZStack {
            Color.blue
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Text("📦")
                    .font(.system(size: 64))

                Text("app_name")
                    .font(.system(size: 36, weight: .bold))

                Text("app_subtitle")
                    .font(.system(size: 17))
                    .multilineTextAlignment(.center)
                    .opacity(0.8)
            }
            .foregroundStyle(.white)
            .padding(.horizontal, 24)
        }
        .task {
            try? await Task.sleep(for: .seconds(1.5))
            onFinished()
        }
    }
}