//
//  LoginScreen.swift
//  iosApp
//
//  Created by Río Pérez, Rafael on 19/08/2026.
//

import SwiftUI

struct LoginView: View {
    
    @Environment(\.dismiss) private var dismiss
    
    @State private var email = ""
    @State private var password = ""
    
    let onForgotPasswordClick: () -> Void
    let onLoginClick: () -> Void
    
    var body: some View {
        ZStack {
            Color(red: 242 / 255, green: 242 / 255, blue: 247 / 255)
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                customNavigationBar
                
                ScrollView {
                    VStack(spacing: 0) {
                        
                        header
                        
                        formCard
                            .padding(.horizontal, 16)
                            .padding(.top, 8)
                        
                        forgotPasswordButton
                        
                        loginButton
                        
                        divider
                        
                        appleButton
                    }
                }
            }
        }
        .toolbar(.hidden, for: .navigationBar)
    }
}

private extension LoginView {
    
    var customNavigationBar: some View {
        ZStack {
            Text("login")
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(.black)
            
            HStack {
                Button {
                    dismiss()
                } label: {
                    HStack(spacing: 3) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 17, weight: .semibold))
                        
                        Text("Atrás")
                            .font(.system(size: 17))
                    }
                    .foregroundStyle(Color(hex: "#166FF5"))
                }
                
                Spacer()
            }
            .padding(.horizontal, 16)
        }
        .frame(height: 44)
        .background(Color.white)
        .overlay(alignment: .bottom) {
            Rectangle()
                .fill(Color.black.opacity(0.22))
                .frame(height: 0.5)
        }
    }
    
    var header: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Bienvenido de vuelta")
                .font(.system(size: 28, weight: .bold))
                .foregroundStyle(.black)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            Text("Accede a tu inventario familiar")
                .font(.system(size: 15))
                .foregroundStyle(
                    Color.black.opacity(0.6)
                )
                .padding(.top, 6)
        }
        .padding(.horizontal, 16)
        .padding(.top, 24)
        .padding(.bottom, 16)
    }
    
    var formCard: some View {
        VStack(spacing: 0) {
            
            HStack(spacing: 12) {
                
                Text("Email")
                    .font(.system(size: 17))
                    .foregroundStyle(Color.black.opacity(0.6))
                    .frame(width: 96, alignment: .leading)
                
                TextField(
                    "email_placeholder",
                    text: $email
                )
                .font(.system(size: 17))
                .textInputAutocapitalization(.never)
                .keyboardType(.emailAddress)
                .autocorrectionDisabled()
            }
            .frame(height: 52.5)
            .overlay(alignment: .bottom) {
                Rectangle()
                    .fill(Color.black.opacity(0.29))
                    .frame(height: 0.5)
            }
            
            HStack(spacing: 12) {
                
                Text("password_label")
                    .font(.system(size: 17))
                    .foregroundStyle(Color.black.opacity(0.6))
                    .frame(width: 96, alignment: .leading)
                
                SecureField(
                    "password_placeholder",
                    text: $password
                )
                .font(.system(size: 17))
            }
            .frame(height: 52)
        }
        .padding(.horizontal, 16)
        .background(Color.white)
        .clipShape(
            RoundedRectangle(cornerRadius: 13)
        )
    }
    
    var forgotPasswordButton: some View {
        HStack {
            Spacer()
            
            Button {
                onForgotPasswordClick()
            } label: {
                Text("forgot_password")
                    .font(.system(size: 15))
                    .foregroundStyle(Color(hex: "#166FF5"))
            }
        }
        .padding(.horizontal, 20)
        .padding(.top, 16)
        .padding(.bottom, 20)
    }
    
    var loginButton: some View {
        Button {
            onLoginClick()
        } label: {
            Text("Continuar")
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 54)
                .background(
                    Color(hex: "#166FF5")
                )
                .clipShape(
                    RoundedRectangle(cornerRadius: 14)
                )
        }
        .padding(.horizontal, 16)
    }
    
    var divider: some View {
        HStack(spacing: 12) {
            
            Rectangle()
                .fill(Color.black.opacity(0.29))
                .frame(height: 0.5)
            
            Text("o continúa con")
                .font(.system(size: 13))
                .foregroundStyle(Color.black.opacity(0.3))
                .fixedSize()
            
            Rectangle()
                .fill(Color.black.opacity(0.29))
                .frame(height: 0.5)
        }
        .padding(.horizontal, 16)
        .padding(.top, 20)
    }
    
    var appleButton: some View {
        Button {
            // Apple login
        } label: {
            HStack(spacing: 10) {
                
                Image(systemName: "apple.logo")
                    .font(.system(size: 18))
                    .foregroundStyle(.white)
                
                Text("Iniciar sesión con Apple")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(.white)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 55)
            .background(Color.black)
            .clipShape(
                RoundedRectangle(cornerRadius: 14)
            )
        }
        .padding(.horizontal, 16)
        .padding(.top, 20)
    }
}

extension Color {
    
    init(hex: String) {
        let hex = hex.trimmingCharacters(
            in: CharacterSet.alphanumerics.inverted
        )
        
        var value: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&value)
        
        let r = Double((value >> 16) & 0xFF) / 255
        let g = Double((value >> 8) & 0xFF) / 255
        let b = Double(value & 0xFF) / 255
        
        self.init(
            red: r,
            green: g,
            blue: b
        )
    }
}
