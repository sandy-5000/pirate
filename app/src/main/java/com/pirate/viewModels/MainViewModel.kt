package com.pirate.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.pirate.models.types.Preferences
import com.pirate.models.types.UserDetails
import com.pirate.services.DataBase
import com.pirate.services.KeyStoreManager
import com.pirate.services.fetch
import com.pirate.types.Details
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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
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

    // == REQUESTS - States
    private var requestDetailsFetched = false
    fun requestScreenLoaded() {
        if (requestDetailsFetched) {
            return
        }
        fetchMessageRequests()
        fetchPendingRequests()
        requestDetailsFetched = true
    }

    private val _requestScreenLoadingRequests = MutableStateFlow(true)
    val requestScreenLoadingRequests: StateFlow<Boolean> =
        _requestScreenLoadingRequests.asStateFlow()
    private val _requestScreenLoadingPendings = MutableStateFlow(true)
    val requestScreenLoadingPendings: StateFlow<Boolean> =
        _requestScreenLoadingPendings.asStateFlow()

    private val _requestScreenDateRequests = MutableStateFlow(emptyList<Details>())
    val requestScreenDateRequests: StateFlow<List<Details>> =
        _requestScreenDateRequests.asStateFlow()
    private val _requestScreenDatePendings = MutableStateFlow(emptyList<Details>())
    val requestScreenDatePendings: StateFlow<List<Details>> =
        _requestScreenDatePendings.asStateFlow()

    fun fetchMessageRequests() {
        _requestScreenLoadingRequests.value = true
        fetch(
            url = "/api/user/message-requests",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    _requestScreenLoadingRequests.value = false
                    return@fetch
                }
                val result: JsonArray = response.jsonObject["result"]?.jsonArray
                    ?: buildJsonArray { emptyArray<String>() }
                _requestScreenDateRequests.value = result.map { details ->
                    val detailObject = details.jsonObject["sender_id"]?.jsonObject
                        ?: buildJsonObject { emptyMap<String, String>() }
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "N/A",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull
                            ?: "2").toInt()
                    )
                }
                _requestScreenLoadingRequests.value = false
            },
            headers = getHeaders(),
            type = RequestType.GET,
        )
    }

    fun fetchPendingRequests() {
        _requestScreenLoadingPendings.value = true
        fetch(
            url = "/api/user/pending-requests",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    _requestScreenLoadingPendings.value = false
                    return@fetch
                }
                val result: JsonArray = response.jsonObject["result"]?.jsonArray
                    ?: buildJsonArray { emptyArray<JsonObject>() }
                _requestScreenDatePendings.value = result.map { details ->
                    val detailObject = details.jsonObject["receiver_id"]?.jsonObject
                        ?: buildJsonObject { emptyMap<String, String>() }
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "N/A",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull
                            ?: "10").toInt()
                    )
                }
                _requestScreenLoadingPendings.value = false
            },
            headers = getHeaders(),
            type = RequestType.GET,
        )
    }

    // == FRIENDS - States
    private var friendsFetched = false
    fun friendsScreenLoaded() {
        if (friendsFetched) {
            return
        }
        fetchFriends()
        friendsFetched = true
    }

    private val _requestScreenLoadingFriends = MutableStateFlow(true)
    val requestScreenLoadingFriends: StateFlow<Boolean> = _requestScreenLoadingFriends.asStateFlow()

    private val _requestScreenDateFriends = MutableStateFlow(emptyList<Details>())
    val requestScreenDateFriends: StateFlow<List<Details>> = _requestScreenDateFriends.asStateFlow()

    fun fetchFriends() {
        _requestScreenLoadingFriends.value = true
        fetch(
            url = "/api/user/friends",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    _requestScreenLoadingFriends.value = false
                    return@fetch
                }
                val result: JsonObject = response.jsonObject["result"]?.jsonObject
                    ?: buildJsonObject { emptyMap<String, JsonObject>() }
                val friendsList: JsonArray = result["friends"]?.jsonArray
                    ?: buildJsonArray { emptyArray<JsonObject>() }
                _requestScreenDateFriends.value = friendsList.map { details ->
                    val detailObject = details.jsonObject
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "N/A",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull
                            ?: "3").toInt()
                    )
                }
                _requestScreenLoadingFriends.value = false
            },
            headers = getHeaders(),
            type = RequestType.GET,
        )
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

    // SettingsScreen - States

    suspend fun setMuteNotifications() {
        dataBase.userDetailsModel.update(
            UserDetails(
                key = PreferencesKey.APP_NOTIFICATION.value,
                value = "true"
            )
        )
        setAllUserDetails()
    }

    suspend fun removeMuteNotifications() {
        dataBase.userDetailsModel.delete(key = PreferencesKey.APP_NOTIFICATION.value)
        setAllUserDetails()
    }

    suspend fun setHideOnlineStatus() {
        dataBase.userDetailsModel.update(
            UserDetails(
                key = PreferencesKey.HIDE_ONLINE_STATUS.value,
                value = "true"
            )
        )
        setAllUserDetails()
    }

    suspend fun removeHideOnlineStatus() {
        dataBase.userDetailsModel.delete(key = PreferencesKey.HIDE_ONLINE_STATUS.value)
        setAllUserDetails()
    }

    private val _chatNotifications = MutableStateFlow(mapOf<String, String>())
    val chatNotifications: StateFlow<Map<String, String>> = _chatNotifications.asStateFlow()

    private fun setAllChatNotifications() {
        viewModelScope.launch {
            val chatNotificationsList = dataBase.preferencesModel.getMutedChats().first()
            val chatNotificationsMap =
                chatNotificationsList.associate { it.key to it.value }.toMutableMap()
            _chatNotifications.value = chatNotificationsMap
        }
    }

    suspend fun setChatNotifications(pirateId: String) {
        dataBase.preferencesModel.update(
            Preferences(
                key = PreferencesKey.MUTED_CHATS.value + ":" + pirateId,
                value = "true"
            )
        )
        setAllChatNotifications()
    }

    suspend fun removeChatNotifications(pirateId: String) {
        dataBase.preferencesModel.delete(key = PreferencesKey.MUTED_CHATS.value + ":" + pirateId)
        setAllChatNotifications()
    }

    // ________ SettingsScreen
}
