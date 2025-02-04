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
import kotlinx.coroutines.launch

class MainViewModel(
    val navController: NavHostController,
    private val dataBase: DataBase,
): ViewModel() {

    var currentScreen by mutableStateOf(getRouteId(null))
        private set

    var userDetails by mutableStateOf(mapOf<String, String>())
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

    fun isUserLoggedIn(): Boolean {
        return userDetails.getOrDefault("logged_in", "false") == "true"
    }

    fun setAllUserDetails() {
        val userDetails = mutableMapOf<String, String>()
        viewModelScope.launch {
            dataBase.userDetailsDao.getAll().collect { userDetailsList ->
                userDetailsList.forEach { userDetail ->
                    Log.d("sandy", userDetail.key + " " + userDetail.value)
                    userDetails[userDetail.key] = userDetail.value
                }
            }
        }
        this.userDetails = userDetails
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
