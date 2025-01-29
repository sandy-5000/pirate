package com.darkube.pirate.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.darkube.pirate.utils.getRouteId

class MainViewModel(val navController: NavController): ViewModel() {
    var currentScreen by mutableStateOf(getRouteId(null))
        private set

    fun navigate(route: NavDestination, flag: Boolean = false) {
        if (flag) {
            navController.navigate(route)
        }
        currentScreen = getRouteId(route)
    }

    fun setScreen(routeName: String) {
        currentScreen = routeName
    }
}
