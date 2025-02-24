package com.darkube.pirate.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.components.ChatBubble
import com.darkube.pirate.components.DataLoading
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.FriendType
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.LightRedColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryBlue
import com.darkube.pirate.ui.theme.RedColor
import com.darkube.pirate.utils.getProfileImage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun Conversation(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    pirateId: String,
    username: String,
    profileImage: Int,
) {
    val headers = mainViewModel.getHeaders()
    val chatScreen by mainViewModel.chatScreenState.collectAsState()
    var screenLoading by remember { mutableStateOf(true) }
    val userState by mainViewModel.userState.collectAsState()
    val userId = userState.getOrDefault("_id", "")

    var updatedUsername by remember { mutableStateOf(username) }
    var updatedProfileImage by remember { mutableIntStateOf(profileImage) }

    mainViewModel.getCurrentRoute()

    val fetchFriendType = {
        screenLoading = true
        fetch(
            url = "/api/user/friend-type/$pirateId",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    screenLoading = false
                    return@fetch
                }
                val result: JsonObject = response.jsonObject["result"]?.jsonObject
                    ?: buildJsonObject { emptyMap<String, JsonObject>() }
                val type: String = result["type"]?.jsonPrimitive?.contentOrNull ?: ""
                if (FriendType.entries.any { it.value == type }) {
                    mainViewModel.setChatScreen(FriendType.valueOf(type))
                }
                screenLoading = false
            },
            headers = headers,
            type = RequestType.GET,
        )
    }
    mainViewModel.setPirateId(pirateId = pirateId)

    val fetchFriendInfo = {
        fetch(
            url = "/api/user/friend/$pirateId",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    return@fetch
                }
                val result: JsonObject = response.jsonObject["result"]?.jsonObject
                    ?: buildJsonObject { emptyMap<String, JsonObject>() }
                val firstName: String = result["first_name"]?.jsonPrimitive?.contentOrNull ?: ""
                val lastName: String = result["last_name"]?.jsonPrimitive?.contentOrNull ?: ""
                val userName: String = result["username"]?.jsonPrimitive?.contentOrNull ?: ""
                val currentProfileImage: String =
                    result["profile_image"]?.jsonPrimitive?.contentOrNull ?: ""
                mainViewModel.viewModelScope.launch {
                    mainViewModel.updateProfileInfo(
                        pirateId = pirateId,
                        firstName = firstName,
                        lastName = lastName,
                        username = userName,
                        profileImage = currentProfileImage
                    )
                    updatedUsername = userName
                    updatedProfileImage = currentProfileImage.toInt()
                }
            },
            headers = headers,
            type = RequestType.GET,
        )
    }

    LaunchedEffect(Unit) {
        mainViewModel.resetChatState()
        if (pirateId == userId) {
            screenLoading = false
            mainViewModel.setChatScreen(FriendType.SELF)
        } else {
            fetchFriendType()
            fetchFriendInfo()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (screenLoading) {
            DataLoading(modifier = Modifier, durationMillis = 1200)
        } else {
            Friends(
                pirateId = pirateId,
                username = updatedUsername,
                profileImage = updatedProfileImage,
                chatScreen = chatScreen,
                mainViewModel = mainViewModel,
                reload = { fetchFriendType() },
            )
        }
    }
}

@Composable
fun Friends(
    pirateId: String,
    username: String,
    profileImage: Int,
    chatScreen: FriendType,
    mainViewModel: MainViewModel,
    reload: () -> Unit,
) {
    val imageSize = 80.dp

    val messages by mainViewModel.userChatState.collectAsState()
    val listState = rememberLazyListState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index == 0 && !isLoading) {
                    isLoading = true
                    mainViewModel.viewModelScope.launch {
                        mainViewModel.getMessagesForPirate(pirateId = pirateId, limit = 100)
                    }
                    isLoading = false
                }
            }
    }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Remaining(
                    pirateId = pirateId,
                    username = username,
                    chatScreen = chatScreen,
                    mainViewModel = mainViewModel,
                    reload = reload,
                )
            }
        }
        items(messages.size) { index ->
            val start =
                index == messages.lastIndex || messages[index + 1].side != messages[index].side
            val end = index == 0 || messages[index - 1].side != messages[index].side
            val dayStart = index == messages.lastIndex || messages[index].receivedAt.substring(
                0,
                10
            ) != messages[index + 1].receivedAt.substring(0, 10)
            val dayEnd = index == 0 || messages[index].receivedAt.substring(
                0,
                10
            ) != messages[index - 1].receivedAt.substring(0, 10)
            ChatBubble(
                message = messages[index].message,
                side = messages[index].side,
                timeStamp = messages[index].receivedAt,
                start = start || dayStart,
                end = end || dayEnd,
                dayStart = dayStart,
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = getProfileImage(profileImage)),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(username, color = LightColor, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun Remaining(
    pirateId: String,
    username: String,
    chatScreen: FriendType,
    mainViewModel: MainViewModel,
    reload: () -> Unit,
) {
    var loading by remember { mutableStateOf(false) }
    val headers = mainViewModel.getHeaders()
    val bodyWithSender = buildJsonObject {
        put("sender_id", pirateId)
    }
    val bodyWithReceiver = buildJsonObject {
        put("receiver_id", pirateId)
    }
    val backgroundColor = AppBackground
    val iconSize = 20.dp
    val mainTextSize = 15.sp
    val subTextSize = 13.sp

    val requestIcon = R.drawable.users_group_icon
    val cancelIcon = R.drawable.forbidden_circle_icon
    val acceptIcon = R.drawable.check_circle_icon

    when (chatScreen) {
        FriendType.NOT_FRIENDS -> Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(NavBarBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "You are not yet friends with `$username`.",
                fontSize = mainTextSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Text(
                "Click on the below button to send a Message Request.",
                fontSize = subTextSize,
                textAlign = TextAlign.Center,
                color = LightColor,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            Button(
                onClick = {
                    loading = true
                    fetch(
                        url = "/api/friends/request",
                        callback = { response: JsonElement ->
                            Log.d("api-res", response.toString())
                            reload()
                            loading = false
                        },
                        body = bodyWithReceiver,
                        headers = headers,
                        type = RequestType.POST,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = requestIcon),
                        contentDescription = "Request",
                        modifier = Modifier
                            .size(iconSize),
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Request",
                        fontSize = 15.sp,
                        color = Color.White,
                    )
                }
            }
        }

        FriendType.REQUEST_SENT -> Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(NavBarBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "A message request has been sent to `$username`. You can chat once your request is accepted.",
                fontSize = mainTextSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Text(
                "Click on the below button to cancel your message request.",
                fontSize = subTextSize,
                textAlign = TextAlign.Center,
                color = LightColor,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            Button(
                onClick = {
                    loading = true
                    fetch(
                        url = "/api/friends/cancel",
                        callback = { response: JsonElement ->
                            Log.d("api-res", response.toString())
                            reload()
                            loading = false
                        },
                        body = bodyWithReceiver,
                        headers = headers,
                        type = RequestType.POST,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightRedColor,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = cancelIcon),
                        contentDescription = "Cancel",
                        modifier = Modifier
                            .size(iconSize),
                        tint = backgroundColor,
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Cancel",
                        fontSize = 15.sp,
                        color = backgroundColor,
                    )
                }
            }
        }

        FriendType.REQUEST_RECEIVED -> Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(NavBarBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "A message request has been sent to you by `$username`. You can chat once the request is accepted.",
                fontSize = mainTextSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Text(
                "Click on the below button to accept the message request.",
                fontSize = subTextSize,
                textAlign = TextAlign.Center,
                color = LightColor,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            Button(
                onClick = {
                    loading = true
                    fetch(
                        url = "/api/friends/accept",
                        callback = { response: JsonElement ->
                            Log.d("api-res", response.toString())
                            reload()
                            loading = false
                        },
                        body = bodyWithSender,
                        headers = headers,
                        type = RequestType.POST,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = acceptIcon),
                        contentDescription = "Accept",
                        modifier = Modifier
                            .size(iconSize),
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Accept",
                        fontSize = 15.sp,
                        color = Color.White,
                    )
                }
            }
        }

        FriendType.SENDER_BLOCKED -> Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(NavBarBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "You have blocked this user: `$username`. You can resume chatting once you unblock them.",
                fontSize = mainTextSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Text(
                "Click on the below button to unblock the user.",
                fontSize = subTextSize,
                textAlign = TextAlign.Center,
                color = LightColor,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            Button(
                onClick = {},
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedColor,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = cancelIcon),
                        contentDescription = "UnBlock",
                        modifier = Modifier
                            .size(iconSize),
                        tint = backgroundColor,
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Unblock",
                        fontSize = 15.sp,
                        color = backgroundColor,
                    )
                }
            }
        }

        FriendType.RECEIVER_BLOCKED -> Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(NavBarBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "You have been blocked by the user: `$username`. You can resume chatting once they unblock you.",
                fontSize = mainTextSize,
                textAlign = TextAlign.Center,
            )
        }

        else -> {}
    }
    if (chatScreen != FriendType.FRIENDS && chatScreen != FriendType.SELF) {
        Spacer(modifier = Modifier.height(400.dp))
    }
}