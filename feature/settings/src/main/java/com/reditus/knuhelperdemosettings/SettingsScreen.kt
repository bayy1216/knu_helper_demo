package com.reditus.knuhelperdemosettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.reditus.core.design.KnuTheme
import com.reditus.core.design.KnuThemes
import com.reditus.core.design.common.DefaultLayout

@Composable
fun SettingsScreen(
    onClickSiteSettings: () -> Unit = {},
) {
    DefaultLayout(
        title = "Settings",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TextButton(
                onClick = { onClickSiteSettings() },
            ) {
                Text(
                    text = "Settings",
                )
            }

        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    KnuTheme{
        SettingsScreen()
    }
}