package com.reditus.knuhelperdemo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.reditus.knuhelperdemo.notice.NoticeScreen
import com.reditus.knuhelperdemosettings.SignUpScreen
import timber.log.Timber

@Composable
fun KnuNavHost(
    appState: KnuAppState,
    modifier: Modifier = Modifier,
    startDestination: TopLevelDestination = TopLevelDestination.START,
) {
    Timber.d("StartDestination: $startDestination")
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ){
        composable<TopLevelDestination.START> { backEntry ->
            SignUpScreen(
                onClickNextScreen = {
                    navController.navigate(TopLevelDestination.MAIN)
                },
            )
        }
        composable<TopLevelDestination.AUTH> { backEntry ->
            SignUpScreen(
                onClickNextScreen = {
                    navController.navigate(TopLevelDestination.MAIN)
                },
            )
        }
        composable<TopLevelDestination.MAIN> { backEntry ->
            NoticeScreen(
                noticeViewModel = hiltViewModel(),
            )
        }
    }
}
