package com.rafario.miscosas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafario.miscosas.presentation.forgot_password.ForgotPasswordScreen
import com.rafario.miscosas.presentation.login.LoginScreen
import com.rafario.miscosas.presentation.on_boarding.OnBoardingCarousel
import com.rafario.miscosas.presentation.register.RegisterScreen
import com.rafario.miscosas.presentation.welcome.SplashScreen


private object AppRoute {

    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val ONBOARDING = "onboarding"
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
                onRegisterClick = {
                    navController.navigate(AppRoute.REGISTER)
                },
                onGoogleLoginClick = { }
            )
        }

        composable(AppRoute.LOGIN) {
            LoginScreen(onBackPressed = {
                navController.popBackStack()
            }, onRegisterClick = {
                navController.navigate(AppRoute.REGISTER)
            }, onForgotPasswordClick = {
                navController.navigate(AppRoute.FORGOT_PASSWORD)
            })
        }

        composable(AppRoute.REGISTER) {
            RegisterScreen(onBackPressed = {
                navController.popBackStack()
            }, onCreateAccountClick = {

            }, onLoginClick = {
                navController.navigate(AppRoute.LOGIN) {
                    popUpTo(AppRoute.REGISTER) {
                        inclusive = true
                    }
                }
            })
        }

        composable(AppRoute.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onSendLinkClick = {
                    navController.navigate(AppRoute.ONBOARDING)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoute.ONBOARDING) {
            OnBoardingCarousel(
                onFinish =  {
                    navController.popBackStack()
                },
                onSkip = {
                    navController.popBackStack()
                }
            )
        }
    }
}