//
//  Button.swift
//  iosApp
//
//  Created by Río Pérez, Rafael on 19/08/2026.
//

import SwiftUI

struct PrimaryButton: View {
    let onClick: () -> Void
    let text: LocalizedStringKey
    
    var body: some View {
        Button {
            onClick()
        } label: {
            Text(text)
                .frame(maxWidth: .infinity)
        }
        .background(AppColors.primary)
        .controlSize(.large)
        .buttonBorderShape(.roundedRectangle(radius: 14))
        
    }
}

struct SecondaryButton: View {
    let onClick: () -> Void
    let text: LocalizedStringKey
    
    var body: some View {
        Button {
            onClick()
        } label: {
            Text(text)
                .frame(maxWidth: .infinity)
        }
        .controlSize(.large)
        .buttonBorderShape(.roundedRectangle(radius: 14))
        .background(AppColors.primary)
    }
}
