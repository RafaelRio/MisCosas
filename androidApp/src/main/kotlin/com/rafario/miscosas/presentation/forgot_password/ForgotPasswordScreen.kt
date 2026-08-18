package com.rafario.miscosas.presentation.forgot_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafario.miscosas.R
import com.rafario.miscosas.ui.components.CustomTextButton
import com.rafario.miscosas.ui.components.CustomTextField
import com.rafario.miscosas.ui.components.CustomTopAppBar
import com.rafario.miscosas.ui.components.EmojiIcon
import com.rafario.miscosas.ui.components.PrimaryButton
import com.rafario.miscosas.ui.theme.TextPrimary
import com.rafario.miscosas.ui.theme.TextSecondary

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onSendLinkClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    var email by remember { mutableStateOf("") }

    Scaffold(topBar = {
        CustomTopAppBar(
            title = stringResource(R.string.recover_password),
            icon = Icons.Default.ArrowBackIosNew
        ) { onBackPressed() }
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            EmojiIcon(
                modifier = Modifier.padding(top = 32.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                icon = "🔑"
            )

            Text(
                stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.padding(top = 20.dp)
            )

            Text(
                stringResource(R.string.recover_password_instructions),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.email_label),
                placeholder = stringResource(R.string.email_placeholder),
                modifier = Modifier.padding(top = 24.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.send_link),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                onSendLinkClick()
            }

            CustomTextButton(
                text = stringResource(R.string.back_to_login),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            ) {
                onLoginClick()
            }
        }
    }
}