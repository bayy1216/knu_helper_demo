package com.reditus.core.design.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reditus.core.design.KnuTheme
import com.reditus.core.design.KnuThemes

@Composable
fun ErrorContent(
    errorMessage: String?,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = errorMessage ?: "잠시 후, 다시 시도해 주세요.",
            modifier = Modifier.padding(16.dp),
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = KnuThemes.colors.primary,
            ),
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "Retry")
        }
    }
}

@Preview
@Composable
fun ErrorContentPreview() {
    KnuTheme {
        DefaultLayout(
            title = "Hello"
        ) {
            ErrorContent(
                errorMessage = "An error occurred",
                onRetry = {},
            )
        }
    }
}