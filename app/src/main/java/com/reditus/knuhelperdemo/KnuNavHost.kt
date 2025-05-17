package com.reditus.knuhelperdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.reditus.knuhelperdemo.navigation.BottomNavDestination
import com.reditus.knuhelperdemo.navigation.BottomRoute
import com.reditus.knuhelperdemo.navigation.TopLevelDestination
import com.reditus.knuhelperdemo.notice.NoticeScreen
import com.reditus.knuhelperdemosettings.SignUpScreen
import com.reditus.knuhelperdemosettings.settingsGraph


@Composable
fun KnuNavHost(
    appState: KnuAppState,
    startDestination: TopLevelDestination = TopLevelDestination.START,
) {
    val navController = appState.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentBottomRoute = currentDestination?.let {
        BottomNavDestination.entries.firstOrNull { route ->
            currentDestination.hasRoute(route.route::class)
        }
    }
    val modifier = if(currentBottomRoute != null){
        Modifier.navigationBarsPadding()
    }else{
        Modifier
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = modifier,
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                rootGraph(navController = navController)
            }
        },
        bottomBar = {
            if (currentBottomRoute != null) {
                KnuBottomBar(
                    // 내비게이션 바만큼 패딩을 추가하여 겹치지 않게 처리
                    currentDestination = currentBottomRoute,
                    onDestinationClick = { destination ->
                        navController.navigate(
                            destination.route,
                            navOptions {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            },
                        )
                    }
                )
            }
        }
    )
}

fun NavGraphBuilder.rootGraph(
    navController: NavController
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
    mainGraph(navController = navController)
}

fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    navigation<TopLevelDestination.MAIN>(
        startDestination = BottomRoute.NoticeRoute,
    ) {
        composable<BottomRoute.NoticeRoute> { backEntry ->
            val entry = navController.getBackStackEntry(TopLevelDestination.MAIN)
            NoticeScreen(
                noticeViewModel = hiltViewModel(entry),
            )
        }
        composable<BottomRoute.FavoriteRoute> { backEntry ->
            val entry = navController.getBackStackEntry(TopLevelDestination.MAIN)
            NoticeScreen(
                noticeViewModel = hiltViewModel(entry),
            )
        }
        settingsGraph(
            navController = navController,
        )
    }
}


@Composable
fun KnuBottomBar(
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    currentDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavDestination.entries.forEach { destination ->
            Column {
                TextButton(
                    onClick = {
                        onDestinationClick(destination)
                    },
                ) {
                    Text(
                        text = destination.name,
                        color = if (currentDestination == destination) {
                            Color.Blue
                        } else {
                            Color.Red
                        }
                    )
                }
            }
        }
    }
}