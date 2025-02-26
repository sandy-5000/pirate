package com.darkube.pirate.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.DetailsKey
import com.darkube.pirate.types.FriendType
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.getProfileImage
import com.darkube.pirate.utils.utcToLocal

enum class ChatList {
    PIRATES, CREWS
}

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val horizontalPadding = 24.dp
    val ghostIcon = R.drawable.ghost_smile_icon

    mainViewModel.fetchChatsList()
    var selectedFilter by remember { mutableStateOf(ChatList.PIRATES) }
    val chatsList by mainViewModel.chatsListState.collectAsState()
    val lastOpened by mainViewModel.lastOpened.collectAsState()

    val crews = listOf(
        listOf("universe-7", "Hey, how strong are we talking?", "10"),
        listOf("leaf-village", "I will become the Hokage.", "1"),
        listOf("ua-high", "Have no fear cause I am here.", "12"),
        listOf("hunter-association", "We got a new S-rank hunter.", "10"),
    )

    LaunchedEffect(Unit) {
        mainViewModel.setAllLastOpened()
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxHeight(),
    ) {
        Row(
            modifier = Modifier
                .padding(start = horizontalPadding, end = horizontalPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            InputChip(
                modifier = Modifier.padding(end = 12.dp),
                selected = selectedFilter == ChatList.PIRATES,
                onClick = {
                    selectedFilter = ChatList.PIRATES
                },
                label = { Text("Pirates") },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = PrimaryColor,
                    selectedLabelColor = Color.White,
                )
            )
//            InputChip(
//                modifier = Modifier.padding(end = 12.dp),
//                selected = selectedFilter == ChatList.CREWS,
//                onClick = {
//                    selectedFilter = ChatList.CREWS
//                },
//                label = { Text("Crews") },
//                colors = InputChipDefaults.inputChipColors(
//                    selectedContainerColor = PrimaryColor,
//                )
//            )
        }
        when (selectedFilter) {
            ChatList.PIRATES -> {
                if (chatsList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(top = 160.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
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
                                modifier = Modifier.size(32.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your chats are empty. Try messaging your friends or searching for new ones.",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
                chatsList.forEach { chat ->
                    ChatRow(
                        pirateId = chat.pirateId,
                        username = chat.username,
                        lastMessage = chat.message,
                        receivedAt = chat.receiveTime,
                        profileImage = chat.image.toInt(),
                        unreadMessages = chat.receiveTime > lastOpened.getOrDefault(
                            DetailsKey.LAST_OPENED.value + ":" + chat.pirateId,
                            "0000-01-01 00:00"
                        ),
                        mainViewModel = mainViewModel,
                    )
                }
            }

            ChatList.CREWS ->
                crews.forEach { details ->
                    ChatRow(
                        details[0],
                        details[0],
                        details[1],
                        "",
                        details[2].toInt(),
                        false,
                        mainViewModel
                    )
                }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ChatRow(
    pirateId: String,
    username: String,
    lastMessage: String = "",
    receivedAt: String,
    profileImage: Int,
    unreadMessages: Boolean,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val imageSize = 60.dp
    val iconSize = 16.dp
    val dateTime = utcToLocal(receivedAt)
    val newMessagesIcon = R.drawable.chat_unread_icon

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
            painter = painterResource(id = getProfileImage(profileImage)),
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
