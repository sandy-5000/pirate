package com.darkube.pirate.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.darkube.pirate.types.UserDetails
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.DataBase
import com.darkube.pirate.utils.getRouteId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MainViewModel(
    val navController: NavHostController,
    private val dataBase: DataBase,
) : ViewModel() {

    var currentScreen by mutableStateOf(getRouteId(null))
        private set

    private val _userState = MutableStateFlow(mapOf("logged_in" to "loading"))
    val userState: StateFlow<Map<String, String>> = _userState.asStateFlow()

    fun navigate(route: NavDestination, flag: Boolean = false) {
        if (flag) {
            navController.navigate(route)
        }
        currentScreen = getRouteId(route)
    }

    fun setScreen(routeName: String) {
        currentScreen = routeName
    }

    fun setAllUserDetails() {
        viewModelScope.launch {
            val userDetailsList = dataBase.userDetailsDao.getAll().first()
            val userDetailsMap = userDetailsList.associate { it.key to it.value }.toMutableMap()
            val loginStatus = userDetailsMap["logged_in"] ?: "false"
            userDetailsMap["logged_in"] = loginStatus
            _userState.value = userDetailsMap
        }
    }

    suspend fun login(userDetails: JsonElement) {
        val userId = userDetails.jsonObject["_id"]?.jsonPrimitive?.contentOrNull ?: ""
        val firstName = userDetails.jsonObject["first_name"]?.jsonPrimitive?.contentOrNull ?: ""
        val lastName = userDetails.jsonObject["last_name"]?.jsonPrimitive?.contentOrNull ?: ""
        val username = userDetails.jsonObject["username"]?.jsonPrimitive?.contentOrNull ?: ""
        val email = userDetails.jsonObject["email"]?.jsonPrimitive?.contentOrNull ?: ""
        val bio = userDetails.jsonObject["bio"]?.jsonPrimitive?.contentOrNull ?: ""
        val token = userDetails.jsonObject["token"]?.jsonPrimitive?.contentOrNull ?: ""
        dataBase.userDetailsDao.update(UserDetails(key = "_id", value = userId))
        dataBase.userDetailsDao.update(UserDetails(key = "first_name", value = firstName))
        dataBase.userDetailsDao.update(UserDetails(key = "last_name", value = lastName))
        dataBase.userDetailsDao.update(UserDetails(key = "username", value = username))
        dataBase.userDetailsDao.update(UserDetails(key = "email", value = email))
        dataBase.userDetailsDao.update(UserDetails(key = "bio", value = bio))
        dataBase.userDetailsDao.update(UserDetails(key = "token", value = token))
        dataBase.userDetailsDao.update(UserDetails(key = "logged_in", value = "true"))
        setAllUserDetails()
    }

    suspend fun logout() {
        dataBase.userDetailsDao.deleteAll()
        while (navController.currentDestination != null) {
            navController.popBackStack()
        }
        navController.navigate(ChatRoute)
        setScreen(getRouteId(navController.currentDestination))
        setAllUserDetails()
    }
}
