package com.reditus.knuhelperdemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.reditus.core.system.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import timber.log.Timber


@Composable
fun KnuApp(
    networkMonitor: NetworkMonitor,
    startDestination: TopLevelDestination = TopLevelDestination.START,
    appState: KnuAppState = rememberCitySaviorAppState(networkMonitor = networkMonitor),
) {
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(isOffline) {
        if (isOffline) {
            Timber.d("CitySaviorApp", "isOffline: $isOffline")
            snackbarHostState.showSnackbar(
                message = "Offline",
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            KnuNavHost(
                appState = appState,
                startDestination = startDestination,
            )
        }
    }
}

@Composable
fun rememberCitySaviorAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): KnuAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
    ) {
        KnuAppState(
            navController,
            coroutineScope,
            networkMonitor,
        )
    }
}

@Stable
class KnuAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            TopLevelDestination.valueLists().forEach { topDes ->
                if(currentDestination?.hierarchy?.any { hierarchy ->
                        hierarchy.hasRoute(topDes::class) } == true){
                    return topDes
                }
            }
            return null
        }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.valueLists()
}

sealed class TopLevelDestination() {
    @Serializable
    data object START : TopLevelDestination()
    @Serializable
    data object AUTH : TopLevelDestination()
    @Serializable
    data object MAIN : TopLevelDestination()

    companion object {
        fun valueLists(): List<TopLevelDestination> {
            return listOf(START, AUTH, MAIN)
        }
    }
}