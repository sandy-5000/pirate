package com.darkube.pirate.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.ChatRow
import com.darkube.pirate.types.EventInfo
import com.darkube.pirate.types.EventType
import com.darkube.pirate.types.FriendType
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.types.MessageType
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

    private val _homeScreenState = MutableStateFlow(HomeScreen.CHATS)
    val homeScreenState: StateFlow<HomeScreen> = _homeScreenState.asStateFlow()

    fun setHomeScreen(screen: HomeScreen) {
        _homeScreenState.value = screen
    }

    private val _chatScreenState = MutableStateFlow(FriendType.INVALID)
    val chatScreenState: StateFlow<FriendType> = _chatScreenState.asStateFlow()

    fun setChatScreen(screen: FriendType) {
        _chatScreenState.value = screen
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

    fun updateProfileImage(pirateId: String, username: String, profileImage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.friendsInfoDao.updateMainInfo(
                pirateId = pirateId,
                username = username,
                image = profileImage,
            )
            fetchChatsList()
        }
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
}
