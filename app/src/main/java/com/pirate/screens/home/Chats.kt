package com.pirate.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirate.R
import com.pirate.models.types.FriendsInfo
import com.pirate.types.FriendType
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryLightColor
import com.pirate.utils.ChatRoute
import com.pirate.utils.getProfileImage
import com.pirate.utils.timestampToLocal
import com.pirate.viewModels.MainViewModel

enum class ChatFilter {
    ALL, UNREAD
}

@Composable
fun Chats(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val horizontalPadding = 24.dp
    val ghostIcon = R.drawable.icon_ghost_smile

    var chatFilter by remember { mutableStateOf(ChatFilter.ALL) }

    val chatsList by mainViewModel.chatsListState.collectAsState()
    val filteredChatList by remember(chatsList, chatFilter) {
        mutableStateOf(chatsList.filter { chat ->
            when (chatFilter) {
                ChatFilter.ALL -> true
                ChatFilter.UNREAD -> chat.receivedAt > chat.lastOpenedAt
            }
        })
    }

    LaunchedEffect(Unit) {
        mainViewModel.fetchChatsList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .fillMaxHeight()
                    .weight(1f),
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            chatFilter = ChatFilter.ALL
                        },
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ChatFilter.ALL == chatFilter) {
                                PrimaryLightColor
                            } else {
                                AppBackground
                            },
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("All Chats", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            chatFilter = ChatFilter.UNREAD
                        },
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ChatFilter.UNREAD == chatFilter) {
                                PrimaryLightColor
                            } else {
                                AppBackground
                            },
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("Unread", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (filteredChatList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .clip(shape = RoundedCornerShape(12.dp))
                                .background(NavBarBackground)
                                .padding(horizontal = 12.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                painter = painterResource(id = ghostIcon),
                                contentDescription = "Ghost",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (chatFilter) {
                                    ChatFilter.ALL -> "Your chats are empty. Try messaging your friends or searching for new ones."
                                    ChatFilter.UNREAD -> "Hi, Pirate\nYou've reviewed all your chats.\nSwitch to 'All Chats', to find your chats."
                                },
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
                filteredChatList
                    .forEach { chat ->
                        ChatRow(
                            pirateId = chat.pirateId,
                            username = chat.username,
                            lastMessage = if (chat.lastMessageId.isEmpty()) {
                                "Tap to Chat ..."
                            } else {
                                chat.lastMessage
                            },
                            receivedAt = chat.receivedAt,
                            profileImage = chat.image,
                            unreadMessages = chat.receivedAt > chat.lastOpenedAt,
                            mainViewModel = mainViewModel,
                        )
                    }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ChatRow(
    pirateId: String,
    username: String,
    lastMessage: String = "",
    receivedAt: Long,
    profileImage: String,
    unreadMessages: Boolean,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val imageSize = 60.dp
    val iconSize = 16.dp
    val dateTime = timestampToLocal(receivedAt)
    val newMessagesIcon = R.drawable.icon_chat_unread

    Row(
        modifier = Modifier
            .clickable(onClick = {
                mainViewModel.setChatScreen(FriendType.INVALID)
                mainViewModel.navController.navigate(
                    ChatRoute(
                        pirateId = pirateId,
                        username = username,
                        profileImage = profileImage,
                    )
                )
            })
            .padding(start = horizontalPadding, end = horizontalPadding)
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = getProfileImage(profileImage.toInt())),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f),
        ) {
            Text(
                text = username,
                color = Color.LightGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = lastMessage,
                color = Color.LightGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize + 4.dp)
                    .padding(horizontal = 2.dp),
            ) {
                if (unreadMessages) {
                    Icon(
                        painter = painterResource(id = newMessagesIcon),
                        contentDescription = "New Message",
                        modifier = Modifier
                            .size(iconSize),
                    )
                }
            }
            Text(
                text = dateTime.second.substring(0, 5),
                color = Color.LightGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}
