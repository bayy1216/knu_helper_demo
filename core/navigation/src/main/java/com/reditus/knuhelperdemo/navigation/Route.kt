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