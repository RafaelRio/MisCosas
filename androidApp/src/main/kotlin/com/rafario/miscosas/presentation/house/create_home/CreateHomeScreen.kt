package com.rafario.miscosas.presentation.house.create_home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rafario.miscosas.R
import com.rafario.miscosas.ui.components.CustomTextButton
import com.rafario.miscosas.ui.components.CustomTextField
import com.rafario.miscosas.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHomeScreen(
    modifier: Modifier = Modifier,
    onJoinClick: () -> Unit,
    onCreateClick: () -> Unit
) {

    var homeName by remember { mutableStateOf("") }

    BackHandler(enabled = false) { }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(stringResource(R.string.create_home_topbar))
        })
    }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                stringResource(R.string.create_home_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                stringResource(R.string.create_home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            CustomTextField(
                value = homeName,
                onValueChange = { homeName = it },
                label = stringResource(R.string.home_name_label),
                placeholder = stringResource(R.string.home_name_placeholder),
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = "\uD83D\uDCA1 Elige un nombre que identifique a tu familia o vivienda, como \"Casa Madrid\" o \"Hogar García\".",
                modifier = Modifier.padding(top = 20.dp).background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                    .padding(14.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            PrimaryButton(
                text = stringResource(R.string.create_home),
                modifier = Modifier.padding(top = 30.dp)
            ) {
                onCreateClick()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.already_invited),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(8.dp))
                CustomTextButton(text = stringResource(R.string.join_home)) {
                    onJoinClick()
                }
            }
        }
    }
}