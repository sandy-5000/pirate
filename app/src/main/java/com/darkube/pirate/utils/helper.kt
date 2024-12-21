package com.darkube.pirate.utils

import androidx.navigation.NavDestination

fun getRouteId(currentRoute: NavDestination?): String {
    if (currentRoute == null) {
        return HomeRoute.javaClass.name
    }
    return currentRoute.route.toString()
}