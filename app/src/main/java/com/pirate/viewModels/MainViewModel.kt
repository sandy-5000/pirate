package com.pirate.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.pirate.models.types.UserDetails
import com.pirate.services.DataBase
import com.pirate.services.KeyStoreManager
import com.pirate.services.fetch
import com.pirate.types.HomeScreen
import com.pirate.types.PreferencesKey
import com.pirate.types.RequestType
import com.pirate.types.Routes
import com.pirate.utils.HomeRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    val navController: NavHostController,
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

        fun getCurrentPirateId(): String {
            if (instance == null || instance?.getCurrentRoute() != Routes.CHAT.value) {
                return ""
            }
            return instance?.currentPirateId ?: ""
        }

        fun setAppInForeground(flag: Boolean) {
            instance?.appInForeground?.value = flag
        }

        fun getAppInForeground(): Boolean {
            return instance?.appInForeground?.value ?: false
        }

        fun hideOnlineStatus(): Boolean {
            return instance?._userState?.value?.getOrDefault(
                PreferencesKey.HIDE_ONLINE_STATUS.value,
                "false"
            ) == "true"
        }

        fun setOtherUserOnline(flag: Boolean) {
            instance?.setOtherUserOnline(flag)
        }

        fun setOtherUserTyping(flag: Boolean) {
            instance?.setOtherUserTyping(flag)
        }
    }

    fun getCurrentRoute(): String {
        return (navController.currentBackStackEntry?.destination?.route ?: Routes.HOME.value).split(
            "/"
        ).first()
    }

    private var appInForeground = mutableStateOf(false)

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

    fun getHeaders(): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        headers["token"] = userState.value.getOrDefault("token", "")
        return headers
    }

    fun updatePushToken(token: String) {
        val body: JsonObject = buildJsonObject {
            put("token", token)
        }
        fetch(
            url = "/api/pushtoken/update",
            callback = {},
            type = RequestType.PUT,
            body = body,
            headers = getHeaders(),
        )
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
        val keys = listOf("_id", "name", "username", "email", "bio")
        keys.forEach { key ->
            val value = userDetails.jsonObject[key]?.jsonPrimitive?.contentOrNull ?: ""
            dataBase.userDetailsModel.update(UserDetails(key = key, value = value))
        }
        val profileImage =
            userDetails.jsonObject["profile_image"]?.jsonPrimitive?.contentOrNull ?: "5"
        with(dataBase.userDetailsModel){
            update(UserDetails(key = "profile_image", value = profileImage))
            update(UserDetails(key = "token", value = token))
            update(UserDetails(key = "logged_in", value = "true"))
        }
        generateAndUploadKeys(token)
        setAllUserDetails()
    }

    suspend fun logout() {
        dataBase.userDetailsModel.deleteAll()
        dataBase.userChatsModel.deleteAll()
        dataBase.friendsInfoModel.deleteAll()


        coroutineScope {
            val job = async { setAllUserDetails() }
            job.await()
        }
        while (navController.currentDestination != null) {
            navController.popBackStack()
        }
        navController.navigate(HomeRoute)
    }

    // HomeScreen - States

    // == CHAT - States
    private val _homeScreenState = MutableStateFlow(HomeScreen.CHATS)
    val homeScreenState: StateFlow<HomeScreen> = _homeScreenState.asStateFlow()

    fun setHomeScreen(screen: HomeScreen) {
        _homeScreenState.value = screen
    }

    // ________ HomeScreen

    // ChatScreen - States

    private var currentPirateId by mutableStateOf("")
    fun setPirateId(pirateId: String) {
        currentPirateId = pirateId
    }

    private val _otherUserOnline = MutableStateFlow(false)
    val otherUserOnline: StateFlow<Boolean> = _otherUserOnline.asStateFlow()
    fun setOtherUserOnline(flag: Boolean) {
        _otherUserOnline.value = flag
    }

    private val _otherUserTyping = MutableStateFlow(false)
    val otherUserTyping: StateFlow<Boolean> = _otherUserTyping.asStateFlow()
    fun setOtherUserTyping(flag: Boolean) {
        _otherUserTyping.value = flag
    }
    // ________ ChatScreen

}
