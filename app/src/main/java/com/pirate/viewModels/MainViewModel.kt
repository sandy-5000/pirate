package com.pirate.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.pirate.models.types.UserDetails
import com.pirate.services.DataBase
import com.pirate.services.KeyStoreManager
import com.pirate.services.fetch
import com.pirate.types.RequestType
import com.pirate.types.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put


class MainViewModel(
    val navController: NavController,
    private val dataBase: DataBase,
) : ViewModel() {

    companion object {

        private var instance: MainViewModel? = null

        fun init(viewModel: MainViewModel) {
            instance = viewModel
        }

        fun isApplicationOn(): Boolean {
            return instance != null
        }

    }

    fun getCurrentRoute(): String {
        return (navController.currentBackStackEntry?.destination?.route ?: Routes.HOME.value).split(
            "/"
        ).first()
    }

    private val _userState = MutableStateFlow(mapOf("logged_in" to "loading"))
    val userState: StateFlow<Map<String, String>> = _userState.asStateFlow()

    fun setAllUserDetails() {
        viewModelScope.launch {
            val userDetailsList = dataBase.userDetailsModel.getAll().first()
            val userDetailsMap = userDetailsList.associate { it.key to it.value }.toMutableMap()
            val loginStatus = userDetailsMap["logged_in"] ?: "false"
            userDetailsMap["logged_in"] = loginStatus
            _userState.value = userDetailsMap
        }
    }

    val generateAndUploadKeys = { token: String ->
        KeyStoreManager.regenerateKeyPair()
        val publicKey = KeyStoreManager.getPublicKey().toString()
        val headers = mapOf("token" to token)
        val body = buildJsonObject {
            put("public_key", publicKey)
        }
        fetch(
            url = "/api/user/public_key",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    return@fetch
                }
            },
            body = body,
            headers = headers,
            type = RequestType.PATCH,
        )
    }

    suspend fun login(userDetails: JsonObject, token: String) {
        val keys = listOf("_id", "first_name", "last_name", "username", "email", "bio")
        keys.forEach { key ->
            val value = userDetails.jsonObject[key]?.jsonPrimitive?.contentOrNull ?: ""
            dataBase.userDetailsModel.update(UserDetails(key = key, value = value))
        }
        val profileImage =
            userDetails.jsonObject["profile_image"]?.jsonPrimitive?.contentOrNull ?: "5"
        dataBase.userDetailsModel.update(UserDetails(key = "profile_image", value = profileImage))
        dataBase.userDetailsModel.update(UserDetails(key = "token", value = token))
        dataBase.userDetailsModel.update(UserDetails(key = "logged_in", value = "true"))
        generateAndUploadKeys(token)
        setAllUserDetails()
    }

}
