package com.rafario.miscosas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafario.miscosas.presentation.login.LoginScreen
import com.rafario.miscosas.presentation.welcome.SplashScreen


private object AppRoute {

    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = AppRoute.SPLASH
    ) {
        composable(AppRoute.SPLASH) {
            SplashScreen(
                onSplashFinished = {

                },
                onLoginClick = {
                    navController.navigate(AppRoute.LOGIN)
                },
                onRegisterClick = { },
                onGoogleLoginClick = { }
            )
        }

        composable(AppRoute.LOGIN) {
            LoginScreen(onBackPressed = {
                navController.popBackStack()
            }, onRegisterClick = {
                navController.navigate(AppRoute.REGISTER)
            })
        }

        composable(AppRoute.REGISTER) {

        }
    }
}