package com.reditus.knuhelperdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.reditus.knuhelperdemo.navigation.BottomNavDestination
import com.reditus.knuhelperdemo.navigation.BottomRoute
import com.reditus.knuhelperdemo.notice.NoticeScreen
import com.reditus.knuhelperdemosettings.SignUpScreen
import com.reditus.knuhelperdemosettings.settingsGraph
import kotlinx.serialization.Serializable
import timber.log.Timber



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


@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let{
        BottomNavDestination.entries.map { it.route }.any{ route->
            currentDestination.hasRoute(route::class)
        }
    } ?: false
    val currentBottomRoute = currentDestination?.let {
        BottomNavDestination.entries.firstOrNull { route->
            currentDestination.hasRoute(route.route::class)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        content = { paddingValues->
            NavHost(
                navController = navController,
                startDestination = BottomNavDestination.NOTICE.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                composable<BottomRoute.NoticeRoute> { backEntry ->
                    NoticeScreen(
                        noticeViewModel = hiltViewModel(),
                    )
                }
                composable<BottomRoute.FavoriteRoute> { backEntry ->
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
            if(showBottomBar && currentBottomRoute != null){
                KnuBottomBar(
                    // 내비게이션 바만큼 패딩을 추가하여 겹치지 않게 처리
                    modifier = Modifier.padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
                    currentDestination = currentBottomRoute,
                    onDestinationClick = { destination->
                        navController.navigate(destination.route,
                            navOptions {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
                            },
                        )
                    }
                )
            }
        }
    )
}

@Composable
fun KnuBottomBar(
    modifier: Modifier = Modifier,
    currentDestination: BottomNavDestination,
    onDestinationClick: (BottomNavDestination)->Unit,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavDestination.entries.forEach{destination ->
            Column {
                TextButton(
                    onClick = {
                        onDestinationClick(destination)
                    },
                ) {
                    Text(
                        text = destination.name,
                        color = if(currentDestination == destination){
                            Color.Blue
                        }else{
                            Color.Red
                        }
                    )
                }
            }
        }
    }
}