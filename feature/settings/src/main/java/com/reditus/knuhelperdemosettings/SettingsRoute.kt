package com.reditus.knuhelperdemosettings

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

@Serializable
data object SiteSettingsRoute

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
) {
    composable<SettingsRoute> {
        val siteSettingsViewModel: SiteSettingsViewModel = hiltViewModel()
        SettingsScreen(
            onClickSiteSettings = {
                navController.navigate(SiteSettingsRoute)
            },
            onTestApi = {
                siteSettingsViewModel.testApi()
            }
        )
    }
    composable<SiteSettingsRoute> {
        SiteSettingsScreen(
            onBack = {
                navController.popBackStack()
            },
        )
    }
}