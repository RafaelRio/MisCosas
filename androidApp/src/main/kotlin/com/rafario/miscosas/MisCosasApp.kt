package com.rafario.miscosas

import androidx.compose.runtime.Composable
import com.rafario.miscosas.navigation.AppNavGraph
import com.rafario.miscosas.ui.theme.MisCosasTheme

@Composable
fun MisCosasApp() {
    MisCosasTheme {
        AppNavGraph()
    }
}
