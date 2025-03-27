package com.pirate.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewModelScope
import com.pirate.R
import com.pirate.components.AppChatInput
import com.pirate.components.ChatBubble
import com.pirate.components.DataLoading
import com.pirate.services.SocketManager
import com.pirate.services.fetch
import com.pirate.types.FriendType
import com.pirate.types.PreferencesKey
import com.pirate.types.RequestType
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.LightRedColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryBlue
import com.pirate.ui.theme.PrimaryColor
import com.pirate.ui.theme.RedColor
import com.pirate.utils.InviteFriendsRoute
import com.pirate.utils.ProfileRoute
import com.pirate.utils.SettingsRoute
import com.pirate.utils.getMinutesDifference
import com.pirate.utils.getProfileImage
import com.pirate.utils.timestampToLocal
import com.pirate.viewModels.MainViewModel
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
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    pirateId: String,
    username: String,
    profileImage: String,
) {
    val headers = mainViewModel.getHeaders()
    val chatScreen by mainViewModel.chatScreenState.collectAsState()
    var screenLoading by remember { mutableStateOf(true) }
    val userState by mainViewModel.userState.collectAsState()
    val userId = userState.getOrDefault("_id", "")

    var updatedUsername by remember { mutableStateOf(username) }
    var updatedProfileImage by remember { mutableStateOf(profileImage) }

    mainViewModel.getCurrentRoute()
    mainViewModel.setPirateId(pirateId = pirateId)

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
                val name: String = result["name"]?.jsonPrimitive?.contentOrNull ?: ""
                val userName: String = result["username"]?.jsonPrimitive?.contentOrNull ?: ""
                val currentProfileImage: String =
                    result["profile_image"]?.jsonPrimitive?.contentOrNull ?: ""
                mainViewModel.viewModelScope.launch {
                    mainViewModel.updateProfileInfo(
                        pirateId = pirateId,
                        name = name,
                        username = userName,
                        profileImage = currentProfileImage
                    )
                    updatedUsername = userName
                    updatedProfileImage = currentProfileImage
                }
            },
            headers = headers,
            type = RequestType.GET,
        )
    }

    LaunchedEffect(Unit) {
        mainViewModel.resetChatState()
        mainViewModel.setLastOpened(pirateId = pirateId)
        mainViewModel.setOtherUserOnline(false)
        mainViewModel.setOtherUserTyping(false)
        if (pirateId == userId) {
            screenLoading = false
            mainViewModel.setChatScreen(FriendType.SELF)
        } else {
            fetchFriendType()
        }
        fetchFriendInfo()
    }

    LaunchedEffect(chatScreen) {
        if (chatScreen == FriendType.FRIENDS) {
            SocketManager.enterChatRoute(otherPirateId = pirateId)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 68.dp, bottom = 68.dp)
                .imePadding(),
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
        ChatScreenTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            pageTitle = username,
            profileImage = profileImage,
            pirateId = pirateId,
            mainViewModel = mainViewModel
        )
        AppChatInput(
            modifier = Modifier.align(Alignment.BottomCenter),
            pirateId = pirateId,
            mainViewModel = mainViewModel,
        )
    }
}

@Composable
fun Friends(
    pirateId: String,
    username: String,
    profileImage: String,
    chatScreen: FriendType,
    mainViewModel: MainViewModel,
    reload: () -> Unit,
) {
    val imageSize = 80.dp

    val messages by mainViewModel.userChatState.collectAsState()
    val listState = rememberLazyListState()
    var isLoading by remember { mutableStateOf(false) }
    val isPirateTyping by mainViewModel.otherUserTyping.collectAsState()

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
        modifier = Modifier.fillMaxSize(),
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
        if (chatScreen == FriendType.FRIENDS && isPirateTyping) {
            item {
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 20.dp)
                        .clip(shape = CircleShape)
                        .background(NavBarBackground)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = "typing...",
                        modifier = Modifier.padding(bottom = 2.dp),
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                }
            }
        }
        items(messages.size) { index ->
            val start =
                index == messages.lastIndex || messages[index + 1].side != messages[index].side ||
                        getMinutesDifference(
                            messages[index + 1].receivedAt,
                            messages[index].receivedAt,
                        ) > 5
            val end =
                index == 0 || messages[index - 1].side != messages[index].side ||
                        getMinutesDifference(
                            messages[index].receivedAt,
                            messages[index - 1].receivedAt,
                        ) > 5
            val dayStart = index == messages.lastIndex ||
                    timestampToLocal(messages[index].receivedAt).first != timestampToLocal(messages[index + 1].receivedAt).first
            val dayEnd = index == 0 ||
                    timestampToLocal(messages[index].receivedAt).first != timestampToLocal(messages[index - 1].receivedAt).first
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
                        painter = painterResource(id = getProfileImage(profileImage.toInt())),
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

    val requestIcon = R.drawable.icon_users_group
    val cancelIcon = R.drawable.icon_forbidden_circle
    val acceptIcon = R.drawable.icon_check_circle

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

@Composable
fun ChatScreenTopBar(
    modifier: Modifier = Modifier,
    pageTitle: String,
    profileImage: String,
    pirateId: String,
    mainViewModel: MainViewModel,
) {
    val barHeight = 68.dp
    val iconPadding = 16.dp
    val titlePadding = 12.dp
    val iconSize = 20.dp
    val sidesPadding = 18.dp
    val backGroundColor = AppBackground
    val userState by mainViewModel.userState.collectAsState()
    val hideOnlineStatus by remember(userState) {
        derivedStateOf {
            userState.getOrDefault(PreferencesKey.HIDE_ONLINE_STATUS.value, "false") == "true"
        }
    }
    val isPirateOnline by mainViewModel.otherUserOnline.collectAsState()

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val optionHeight = 48.dp
    val optionsIcon = Icons.Default.MoreVert
    val cornerSize = 16.dp
    val mutedFriends by mainViewModel.chatNotifications.collectAsState()
    val mutedFriendsIds = remember(mutedFriends) {
        mutedFriends.filter { it.value == "true" }.keys.toSet()
    }
    var showClearChatDialog by remember { mutableStateOf(false) }
    val clearChatIcon = R.drawable.icon_broom

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .background(color = backGroundColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier.padding(start = iconPadding),
                    onClick = {
                        SocketManager.exitChatRoute(pirateId)
                        mainViewModel.navController.popBackStack()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(iconSize),
                    )
                }
                Image(
                    painter = painterResource(id = getProfileImage(profileImage.toInt())),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = pageTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(
                            start = titlePadding,
                        ),
                    )
                    if (isPirateOnline && !hideOnlineStatus) {
                        Text(
                            text = "online",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(
                                start = titlePadding,
                            ),
                        )
                    }
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = sidesPadding),
        ) {
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = optionsIcon, contentDescription = "Menu"
                    )
                }
                if (expanded) {
                    Popup(
                        alignment = Alignment.TopEnd,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(focusable = true)
                    ) {
                        Card(
                            shape = RoundedCornerShape(cornerSize),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                            modifier = Modifier
                                .width(200.dp)
                                .padding(top = 48.dp, end = 8.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(cornerSize))
                                    .background(PrimaryColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(optionHeight)
                                        .clickable(onClick = {
                                            showClearChatDialog = true
                                            expanded = false
                                        })
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Clear Chat")
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(optionHeight)
                                        .clickable(onClick = {
                                            mainViewModel.viewModelScope.launch {
                                                if ((PreferencesKey.MUTED_CHATS.value + ":" + pirateId) in mutedFriendsIds) {
                                                    mainViewModel.removeChatNotifications(pirateId = pirateId)
                                                } else {
                                                    mainViewModel.setChatNotifications(pirateId = pirateId)
                                                }
                                            }
                                            Toast.makeText(context, "Notifications preference applied", Toast.LENGTH_SHORT)
                                                .show()
                                            expanded = false
                                        })
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = if ((PreferencesKey.MUTED_CHATS.value + ":" + pirateId) in mutedFriendsIds) {
                                            "Unmute notifications"
                                        } else {
                                            "Mute notifications"
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatDialog = false },
            title = { Text(text = "Clear Chat", color = Color.White) },
            text = {
                Text(
                    text = "By clicking \"Clear\", your chat history will be erased permanently. This will not affect other user in the chat.",
                    color = LightColor,
                )
            },
            containerColor = NavBarBackground,
            confirmButton = {
                Button(
                    onClick = {
                        mainViewModel.viewModelScope.launch {
                            mainViewModel.clearPirateChat(pirateId = pirateId)
                            showClearChatDialog = false
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedColor,
                    ),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                ) {
                    Icon(
                        painter = painterResource(id = clearChatIcon),
                        contentDescription = "Clear Chat",
                        modifier = Modifier
                            .size(iconSize),
                        tint = AppBackground,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clear",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppBackground,
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearChatDialog = false },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                }
            }
        )
    }
}
