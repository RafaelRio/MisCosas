package com.rafario.miscosas.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafario.miscosas.R
import com.rafario.miscosas.ui.TextTertiary
import com.rafario.miscosas.ui.customViews.CustomPasswordTextField
import com.rafario.miscosas.ui.customViews.CustomTextButton
import com.rafario.miscosas.ui.customViews.CustomTextField
import com.rafario.miscosas.ui.customViews.PrimaryButton
import com.rafario.miscosas.ui.customViews.SocialLoginButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onRegisterClick: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable {
                            onBackPressed()
                        }
                        .padding(horizontal = 20.dp)
                )
            }
        )
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                placeholder = "Ingresa tu correo electrónico"
            )

            CustomPasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Ingresa tu contraseña",
                modifier = Modifier.padding(top = 16.dp)
            )

            CustomTextButton(
                text = "¿Olvidaste la contraseña?",
                modifier = Modifier
                    .align(Alignment.End)
            ) {

            }

            PrimaryButton(text = "Iniciar sesión", modifier = Modifier.padding(top = 16.dp)) {

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "o",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            SocialLoginButton(
                text = stringResource(R.string.continue_google),
                icon = painterResource(R.drawable.ic_google)
            ) {
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿Sin cuenta?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(8.dp))
                CustomTextButton(text = "Crear cuenta") {
                    onRegisterClick()
                }
            }

        }
    }
}

