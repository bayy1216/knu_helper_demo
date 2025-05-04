package com.reditus.knuhelperdemosettings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reditus.core.system.UiState
import com.reditus.knuhelperdemo.data.notice.SiteInfo
import com.reditus.knuhelperdemo.data.notice.SubscribeModel

@Composable
fun SiteSettingsScreen(
    onBack: () -> Unit,
    siteSettingsViewModel: SiteSettingsViewModel = hiltViewModel(),
) {
    val siteInfos = siteSettingsViewModel.sites.collectAsStateWithLifecycle()
    val userSubscribeSites = siteSettingsViewModel.userSubscribeSites.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        siteSettingsViewModel.errorToast.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    SiteSettingsScreen(
        onBack = onBack,
        handleIntent = siteSettingsViewModel::handleIntent,
        siteInfos = siteInfos.value,
        userSubscribeSites = userSubscribeSites.value,
    )
}

@Composable
private fun SiteSettingsScreen(
    onBack: () -> Unit,
    handleIntent: (SiteSettingsIntent) -> Unit = {},
    siteInfos: UiState<List<SiteInfo>>,
    userSubscribeSites: UiState<List<SubscribeModel>>,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        when (userSubscribeSites) {
            is UiState.Loading -> {
                // Show loading
            }
            is UiState.Success -> {
                userSubscribeSites.data.forEach { subscribe ->
                    Text(text = subscribe.site)
                    Text(text = subscribe.color)
                    Text(text = subscribe.isAlarm.toString())
                }
            }
            is UiState.Error -> {
                // Show error
            }
        }

        when (siteInfos) {
            is UiState.Loading -> {
                // Show loading
            }
            is UiState.Success -> {
                siteInfos.data.forEach { siteInfo ->
                    Text(text = siteInfo.siteCategoryKorean)
                    Text(text = siteInfo.site)
                }
            }
            is UiState.Error -> {
                // Show error
            }
        }
    }
}