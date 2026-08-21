//
// Created by Río Pérez, Rafael on 18/08/2026.
//

import Foundation
import SwiftUI

struct WelcomeView: View {

    let onLoginClick: () -> Void
    let onRegisterClick: () -> Void
    let onHowItWorksClick: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {

                Spacer()
                    .frame(height: 60)

                Text("📦")
                    .font(.system(size: 56))

                Text("Bienvenido a Cosas")
                    .font(.system(size: 30, weight: .bold))
                    .padding(.top, 24)

                Text(
                    "El inventario inteligente para tu hogar. Garantías, facturas, manuales y todo lo importante siempre a mano."
                )
                    .font(.system(size: 17))
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                    .padding(.top, 12)

                Spacer()
                    .frame(height: 48)

                PrimaryButton(onClick: {
                    onLoginClick()
                }, text: "login")
                .padding(.horizontal, 24)

                SecondaryButton(onClick: {
                    onRegisterClick()
                }, text: "create_free_account")
                .padding(.horizontal, 24)
                .padding(.top, 12)

                Button {
                    onHowItWorksClick()
                } label: {
                    Text("¿Cómo funciona? Ver demo")
                }
                .padding(.top, 16)
            }
        }.navigationBarBackButtonHidden(true)
    }
}
