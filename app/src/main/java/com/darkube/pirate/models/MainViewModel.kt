package com.darkube.pirate.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.darkube.pirate.types.UserDetails
import com.darkube.pirate.utils.DataBase
import com.darkube.pirate.utils.getRouteId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    val navController: NavHostController,
    private val dataBase: DataBase,
) : ViewModel() {

    var currentScreen by mutableStateOf(getRouteId(null))
        private set

    private val _userDetails = MutableStateFlow<Map<String, String>>(emptyMap())
    val userDetails: StateFlow<Map<String, String>> = _userDetails.asStateFlow()

    fun navigate(route: NavDestination, flag: Boolean = false) {
        if (flag) {
            navController.navigate(route)
        }
        currentScreen = getRouteId(route)
    }

    fun setScreen(routeName: String) {
        currentScreen = routeName
    }

    fun isUserLoggedIn(): Boolean {
        return _userDetails.value.getOrDefault("logged_in", "false") == "true"
    }

    fun setAllUserDetails() {
        viewModelScope.launch {
            val userDetailsList = dataBase.userDetailsDao.getAll().first()
            _userDetails.value = userDetailsList.associate { it.key to it.value }
        }
    }

    suspend fun login(username: String) {
        dataBase.userDetailsDao.update(UserDetails(key = "user_name", value = username))
        dataBase.userDetailsDao.update(UserDetails(key = "logged_in", value = "true"))
        setAllUserDetails()
    }

    suspend fun logout() {
        dataBase.userDetailsDao.delete(key = "user_name")
        dataBase.userDetailsDao.delete(key = "logged_in")
        setAllUserDetails()
    }
}
