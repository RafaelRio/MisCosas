package com.rafario.miscosas.presentation.house.join_home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafario.miscosas.R
import com.rafario.miscosas.ui.components.CustomTextField
import com.rafario.miscosas.ui.components.CustomTopAppBar
import com.rafario.miscosas.ui.components.PrimaryButton

@Composable
fun JoinHomeScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onJoinPressed: () -> Unit
) {
    var homeCode by remember { mutableStateOf("") }

    Scaffold(topBar = {
        CustomTopAppBar(
            title = stringResource(R.string.join_home),
            icon = Icons.Default.ArrowBackIosNew
        ) { onBackPressed() }
    }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                stringResource(R.string.join_home_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                stringResource(R.string.join_home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            CustomTextField(
                value = homeCode,
                onValueChange = { homeCode = it },
                label = stringResource(R.string.home_code_label),
                placeholder = stringResource(R.string.home_code_placeholder),
                modifier = Modifier.padding(top = 24.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.join_home_action),
                modifier = Modifier.padding(top = 30.dp)
            ) {

            }
        }
    }
}