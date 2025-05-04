package com.reditus.core.design.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reditus.core.design.KnuTheme
import com.reditus.core.design.KnuThemes
import kotlinx.coroutines.delay

@Composable
fun LoadingSpinner(
    delay: Long = 200L,
){
    var showSpinner by remember { mutableStateOf(false) }

    // 지정된 시간 후에 스피너 보이도록 설정
    LaunchedEffect(Unit) {
        delay(delay)
        showSpinner = true
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (showSpinner) {
            CircularProgressIndicator(
                color = KnuThemes.colors.primary,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp),
            )
        }
    }
}

@Composable
@Preview
private fun LoadingSpinnerPreview() {
    KnuTheme{
        DefaultLayout(
            title= "Loading Spinner",
        ) {
            LoadingSpinner(
                delay = 3000,
            )
        }
    }
}