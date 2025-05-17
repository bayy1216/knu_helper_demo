package com.reditus.knuhelperdemo.navigation

import kotlinx.serialization.Serializable

enum class BottomNavDestination(
    val route: BottomRoute
) {
    NOTICE(BottomRoute.NoticeRoute),
    FAVORITE(BottomRoute.FavoriteRoute),
    SETTINGS(BottomRoute.SettingsRoute),
}


sealed interface BottomRoute{

    @Serializable
    data object NoticeRoute : BottomRoute

    @Serializable
    data object FavoriteRoute : BottomRoute

    @Serializable
    data object SettingsRoute: BottomRoute
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