package com.rafario.miscosas.presentation

import androidx.compose.runtime.Composable
import com.rafario.miscosas.navigation.AppNavGraph
import com.rafario.miscosas.ui.MisCosasTheme

@Composable
fun MisCosasApp() {
    MisCosasTheme {
        AppNavGraph()
    }
}
