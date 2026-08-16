package com.rafario.miscosas.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.visible
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafario.miscosas.R
import com.rafario.miscosas.ui.customViews.PrimaryButton
import com.rafario.miscosas.ui.customViews.SecondaryButton
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,   //Se usa para cuando ya has iniciado sesion
    onLoginClick: () -> Unit,        //Se usa para cuando quieres iniciar sesion
    onRegisterClick: () -> Unit,     //Se usa para cuando quieres registrarte
    onGoogleLoginClick: () -> Unit, //Se usa para cuando quieres iniciar sesion con google
) {

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(3000.milliseconds)
        showBottomSheet = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.app_name), color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Tu pasaporte digital para todo lo que posees",
            fontSize = 17.sp,
            lineHeight = 25.5.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        LinearProgressIndicator(Modifier.visible(!showBottomSheet))
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = bottomSheetState,
            sheetGesturesEnabled = false,
            dragHandle = null,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false,
                shouldDismissOnClickOutside = false
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                PrimaryButton(text = "Crear cuenta") {
                    onRegisterClick()
                }

                Spacer(Modifier.height(16.dp))

                SecondaryButton(text = "Iniciar sesión") {
                    showBottomSheet = false
                    onLoginClick()
                }
            }

        }
    }

}