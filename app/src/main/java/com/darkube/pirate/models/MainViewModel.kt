package com.darkube.pirate.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.ChatRow
import com.darkube.pirate.types.Details
import com.darkube.pirate.types.EventInfo
import com.darkube.pirate.types.EventType
import com.darkube.pirate.types.FriendType
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.types.MessageType
import com.darkube.pirate.types.RequestScreen
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.types.Routes
import com.darkube.pirate.types.room.UserChat
import com.darkube.pirate.types.room.UserDetails
import com.darkube.pirate.utils.DataBase
import com.darkube.pirate.utils.HomeRoute
import kotlinx.coroutines.Dispatchers
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

        fun emit(eventInfo: EventInfo) {
            if (eventInfo.type == EventType.MESSAGE) {
                if (
                    instance?.getCurrentRoute() == Routes.HOME.value &&
                    instance?.homeScreenState?.value == HomeScreen.CHATS
                ) {
                    instance?.fetchChatsList()
                }
                if (
                    instance?.getCurrentRoute() == Routes.CHAT.value &&
                    instance?.currentPirateId == eventInfo.id
                ) {
                    instance?.updateNewMessageForPirate(
                        pirateId = eventInfo.id,
                        message = eventInfo.message,
                        type = MessageType.TEXT.value,
                        side = 1
                    )
                }
            }
        }

        fun reloadRequestsData() {
            if (instance?.getCurrentRoute() == Routes.HOME.value && instance?.homeScreenState?.value == HomeScreen.REQUESTS) {
                instance?.fetchMessageRequests()
                instance?.fetchPendingRequests()
                instance?.fetchFriends()
            } else {
                instance?.requestDetailsFetched = false
            }
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
            val userDetailsList = dataBase.userDetailsDao.getAll().first()
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

    suspend fun login(userDetails: JsonObject, token: String) {
        val keys = listOf("_id", "first_name", "last_name", "username", "email", "bio")
        keys.forEach { key ->
            val value = userDetails.jsonObject[key]?.jsonPrimitive?.contentOrNull ?: ""
            dataBase.userDetailsDao.update(UserDetails(key = key, value = value))
        }
        val profileImage =
            userDetails.jsonObject["profile_image"]?.jsonPrimitive?.contentOrNull ?: "5"
        dataBase.userDetailsDao.update(UserDetails(key = "profile_image", value = profileImage))
        dataBase.userDetailsDao.update(UserDetails(key = "token", value = token))
        dataBase.userDetailsDao.update(UserDetails(key = "logged_in", value = "true"))
        setAllUserDetails()
    }

    suspend fun logout() {
        dataBase.userDetailsDao.deleteAll()
        coroutineScope {
            val job = async { setAllUserDetails() }
            job.await()
        }
        while (navController.currentDestination != null) {
            navController.popBackStack()
        }
        navController.navigate(HomeRoute)
    }

    fun updateProfileInfo(
        pirateId: String,
        firstName: String,
        lastName: String,
        username: String,
        profileImage: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.friendsInfoDao.updateAllInfo(
                pirateId = pirateId,
                firstName = firstName,
                lastName = lastName,
                username = username,
                image = profileImage,
            )
            fetchChatsList()
        }
    }

    // HomeScreen - States
    // == CHAT - States
    private val _homeScreenState = MutableStateFlow(HomeScreen.CHATS)
    val homeScreenState: StateFlow<HomeScreen> = _homeScreenState.asStateFlow()

    fun setHomeScreen(screen: HomeScreen) {
        _homeScreenState.value = screen
    }

    private val _chatsListState = MutableStateFlow(emptyList<ChatRow>())
    val chatsListState: StateFlow<List<ChatRow>> = _chatsListState.asStateFlow()

    fun fetchChatsList() {
        viewModelScope.launch {
            val chatsList = dataBase.lastMessageDao.getAllMessages().first()
            val friendsList = dataBase.friendsInfoDao.getAll().first()
            val imageMap = friendsList.associateBy({ it.pirateId }, { it.image })
            val charRows = chatsList.map { chat ->
                ChatRow(
                    pirateId = chat.pirateId,
                    username = chat.username,
                    message = chat.message,
                    receiveTime = chat.receiveTime,
                    image = imageMap.getOrDefault(chat.pirateId, "12")
                )
            }
            _chatsListState.value = charRows
        }
    }

    // == REQUESTS - States
    private val _requestScreenFilter = MutableStateFlow(RequestScreen.REQUESTS)
    val requestScreenFilter: StateFlow<RequestScreen> = _requestScreenFilter.asStateFlow()

    fun setRequestScreenFilter(filter: RequestScreen) {
        _requestScreenFilter.value = filter
    }

    private var requestDetailsFetched = false
    fun requestScreenLoaded() {
        if (requestDetailsFetched) {
            return
        }
        fetchMessageRequests()
        fetchPendingRequests()
        fetchFriends()
        requestDetailsFetched = true
    }

    private val _requestScreenLoadingRequests = MutableStateFlow(true)
    val requestScreenLoadingRequests: StateFlow<Boolean> =
        _requestScreenLoadingRequests.asStateFlow()
    private val _requestScreenLoadingPendings = MutableStateFlow(true)
    val requestScreenLoadingPendings: StateFlow<Boolean> =
        _requestScreenLoadingPendings.asStateFlow()
    private val _requestScreenLoadingFriends = MutableStateFlow(true)
    val requestScreenLoadingFriends: StateFlow<Boolean> = _requestScreenLoadingFriends.asStateFlow()

    private val _requestScreenDateRequests = MutableStateFlow(emptyList<Details>())
    val requestScreenDateRequests: StateFlow<List<Details>> =
        _requestScreenDateRequests.asStateFlow()
    private val _requestScreenDatePendings = MutableStateFlow(emptyList<Details>())
    val requestScreenDatePendings: StateFlow<List<Details>> =
        _requestScreenDatePendings.asStateFlow()
    private val _requestScreenDateFriends = MutableStateFlow(emptyList<Details>())
    val requestScreenDateFriends: StateFlow<List<Details>> = _requestScreenDateFriends.asStateFlow()

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
    private val _chatScreenState = MutableStateFlow(FriendType.INVALID)
    val chatScreenState: StateFlow<FriendType> = _chatScreenState.asStateFlow()

    fun setChatScreen(screen: FriendType) {
        _chatScreenState.value = screen
    }

    private var currentPirateId by mutableStateOf("")
    fun setPirateId(pirateId: String) {
        currentPirateId = pirateId
    }

    private val _userChatState = MutableStateFlow(emptyList<UserChat>())
    val userChatState: StateFlow<List<UserChat>> = _userChatState.asStateFlow()
    private var messageOffset by mutableIntStateOf(0)

    fun resetChatState() {
        setChatScreen(FriendType.INVALID)
        _userChatState.value = emptyList()
        messageOffset = 0
    }

    fun updateNewMessageForPirate(
        pirateId: String,
        message: String,
        type: String,
        side: Int,
        username: String = "",
    ) {
        if (pirateId != currentPirateId) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (side == 0) {
                dataBase.lastMessageDao.upsertMessage(
                    pirateId = pirateId,
                    username = username,
                    message = message
                )
                dataBase.userChatDao.insertMessage(
                    pirateId = pirateId,
                    message = message,
                    type = type,
                    side = side
                )
            }
            var id = -1
            if (_userChatState.value.isNotEmpty()) {
                id = _userChatState.value[0].id
            }
            val newMessages = dataBase.userChatDao.getLatestInsertedMessage(pirateId, id)
            _userChatState.value = newMessages + _userChatState.value
            messageOffset += newMessages.size
        }
    }

    fun getMessagesForPirate(pirateId: String, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldMessages = dataBase.userChatDao.getMessagesForPirate(
                pirateId = pirateId,
                limit = limit,
                offset = messageOffset
            )
            _userChatState.value += oldMessages
            messageOffset += limit
        }
    }
    // ________ ChatScreen
}
