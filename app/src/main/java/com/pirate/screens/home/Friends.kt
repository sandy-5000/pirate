package com.pirate.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.pirate.components.DataLoading
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryColor
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel

@Composable
fun Friends(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val friendsScrollState = rememberScrollState()

    val loadingFriends by mainViewModel.requestScreenLoadingFriends.collectAsState()
    val friends by mainViewModel.requestScreenDateFriends.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.friendsScreenLoaded()
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
                modifier = Modifier
                    .verticalScroll(friendsScrollState)
                    .weight(1f),
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                if (loadingFriends) {
                    DataLoading(durationMillis = 1200, modifier = modifier.weight(1f))
                } else if (friends.isEmpty()) {
                    EmptyList("You Have No Friends", modifier = Modifier.weight(1f))
                } else {
                    friends.forEach { friend ->
                        Friend(
                            displayName = friend.firstName + " " + friend.lastName,
                            username = friend.username,
                            userId = friend.id,
                            profileImage = friend.profileImage,
                            mainViewModel = mainViewModel,
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun Friend(
    displayName: String,
    username: String,
    userId: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 28.dp
    val verticalPadding = 12.dp
    val messageIcon = R.drawable.icon_chat_round_line
    val iconSize = 20.dp
    val imageSize = 48.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Image(
                painter = painterResource(id = getProfileImage(profileImage)),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(imageSize)
                    .clip(shape = CircleShape),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = displayName,
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = username,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(
            onClick = {
//                mainViewModel.setChatScreen(FriendType.INVALID)
//                mainViewModel.navController.navigate(
//                    ChatRoute(
//                        pirateId = userId,
//                        username = username,
//                        profileImage = profileImage,
//                    )
//                )
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(shape = CircleShape)
                .background(NavBarBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = messageIcon),
                    contentDescription = "Message",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun EmptyList(message: String = "Empty List", modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = LightColor, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}
