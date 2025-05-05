package com.reditus.knuhelperdemosettings

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
        SettingsScreen(
            onClickSiteSettings = {
                navController.navigate(SiteSettingsRoute)
            },
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