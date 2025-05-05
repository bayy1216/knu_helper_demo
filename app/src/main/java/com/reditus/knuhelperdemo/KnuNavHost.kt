package com.reditus.knuhelperdemo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.reditus.knuhelperdemo.notice.NoticeScreen
import com.reditus.knuhelperdemosettings.SettingsRoute
import com.reditus.knuhelperdemosettings.SignUpScreen
import com.reditus.knuhelperdemosettings.settingsGraph
import kotlinx.serialization.Serializable
import timber.log.Timber

enum class BottomNavDestination {
    NOTICE,
    SETTINGS,
    FAVORITE,
}
@Serializable
data object NoticeRoute


@Serializable
data object FavoriteRoute

@Composable
fun KnuNavHost(
    appState: KnuAppState,
    startDestination: TopLevelDestination = TopLevelDestination.START,
) {
    Timber.d("StartDestination: $startDestination")
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<TopLevelDestination.START> { backEntry ->
            SignUpScreen(
                onClickNextScreen = {
                    navController.navigate(
                        TopLevelDestination.MAIN,
                        navOptions {
                            popUpTo(TopLevelDestination.START) {
                                inclusive = true
                            }
                        },
                    )
                },
            )
        }
        composable<TopLevelDestination.AUTH> { backEntry ->
            SignUpScreen(
                onClickNextScreen = {
                    navController.navigate(
                        TopLevelDestination.MAIN,
                        navOptions {
                            popUpTo(TopLevelDestination.AUTH) {
                                inclusive = true
                            }
                        },
                    )
                },
            )
        }
        composable<TopLevelDestination.MAIN> { backEntry ->
            MainNavHost()
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.Red),
        content = {
            NavHost(
                navController = navController,
                startDestination = NoticeRoute,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<NoticeRoute> { backEntry ->
                    NoticeScreen(
                        noticeViewModel = hiltViewModel(),
                    )
                }
                composable<FavoriteRoute> { backEntry ->
                    NoticeScreen(
                        noticeViewModel = hiltViewModel(),
                    )
                }
                settingsGraph(
                    navController = navController,
                )
            }
        },
        bottomBar = {
            KnuBottomBar(
                navController = navController,
            )
        }
    )
}

@Composable
fun KnuBottomBar(
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar {
        Row {
            BottomNavDestination.entries.forEach { destination ->
                TextButton(
                    onClick = {
                        when(destination) {
                            BottomNavDestination.NOTICE -> {
                                navController.navigate(NoticeRoute,
                                    navOptions {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    },
                                )
                            }
                            BottomNavDestination.SETTINGS -> {
                                navController.navigate(
                                    SettingsRoute,
                                    navOptions {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    },
                                )
                            }
                            BottomNavDestination.FAVORITE -> {
                                navController.navigate(FavoriteRoute,
                                    navOptions {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    },
                                )
                            }
                        }
                    },
                ) {
                    Text(
                        text = destination.name,
                    )
                }
            }
        }
    }
}