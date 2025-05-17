package com.reditus.knuhelperdemosettings

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.reditus.knuhelperdemo.navigation.BottomRoute
import com.reditus.knuhelperdemo.navigation.TopLevelDestination
import kotlinx.serialization.Serializable


@Serializable
data object SiteSettingsRoute

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
) {
    composable<BottomRoute.SettingsRoute> { backEntry->
        val entry = navController.getBackStackEntry(TopLevelDestination.MAIN)
        val siteSettingsViewModel: SiteSettingsViewModel = hiltViewModel(entry)
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