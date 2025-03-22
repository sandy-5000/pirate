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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pirate.R
import com.pirate.types.HomeScreen
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryColor
import com.pirate.utils.InviteFriendsRoute
import com.pirate.utils.ProfileRoute
import com.pirate.utils.SettingsRoute
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel

@Composable
fun Home(
    mainViewModel: MainViewModel,
) {
    val homeScreen by mainViewModel.homeScreenState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (homeScreen) {
            HomeScreen.CHATS -> Chats(mainViewModel = mainViewModel)

            HomeScreen.REQUESTS -> Requests(mainViewModel = mainViewModel)

            HomeScreen.FRIENDS -> Friends(mainViewModel = mainViewModel)

            else -> {}
        }
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            displayTitle = when (homeScreen) {
                HomeScreen.CHATS -> "Chats"
                HomeScreen.REQUESTS -> "Requests"
                HomeScreen.FRIENDS -> "Friends"
                else -> ""
            },
            mainViewModel = mainViewModel,
        )
        BottomBar(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter), mainViewModel = mainViewModel
        )
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    displayTitle: String,
    mainViewModel: MainViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    val userState by mainViewModel.userState.collectAsState()
    val profileImage = userState.getOrDefault("profile_image", "8").toInt()
    val imageSize = 40.dp
    val optionHeight = 48.dp
    val cornerSize = 16.dp
    val optionsIcon = Icons.Default.MoreVert

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(AppBackground)
            .padding(start = 24.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = getProfileImage(profileImage)),
                contentDescription = "chats",
                modifier = Modifier
                    .size(imageSize)
                    .clip(shape = CircleShape),
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = displayTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
            )
        }
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
                                    .clickable(onClick = {})
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Mark all read")
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(optionHeight)
                                    .clickable(onClick = {
                                        expanded = false
                                        mainViewModel.navController.navigate(InviteFriendsRoute)
                                    })
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Invite friends")
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(optionHeight)
                                    .clickable(onClick = {})
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Filter unread chats")
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(optionHeight)
                                    .clickable(onClick = {
                                        expanded = false
                                        mainViewModel.navController.navigate(SettingsRoute)
                                    })
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Settings")
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(optionHeight)
                                    .clickable(onClick = {
                                        expanded = false
                                        mainViewModel.navController.navigate(ProfileRoute)
                                    })
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("View Profile")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val iconSize = 24.dp
    val iconButtonHeight = iconSize + 44.dp
    val iconButtonWidth = iconSize + 48.dp
    val cornerSize = 12.dp
    val chatIcon = R.drawable.icon_chat_round_line
    val requestIcon = R.drawable.icon_passport
    val friendsIcon = R.drawable.icon_users_group

    Card(
        shape = RoundedCornerShape(cornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = NavBarBackground),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(cornerSize))
                .background(NavBarBackground),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Column(
                modifier = Modifier
                    .height(iconButtonHeight)
                    .width(iconButtonWidth)
                    .clickable(onClick = {
                        mainViewModel.setHomeScreen(HomeScreen.CHATS)
                    })
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = chatIcon),
                    contentDescription = "chats",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize),
                )
                Text(text = "Chats", fontSize = 13.sp)
            }
            Column(
                modifier = Modifier
                    .height(iconButtonHeight)
                    .width(iconButtonWidth)
                    .clickable(onClick = {
                        mainViewModel.setHomeScreen(HomeScreen.REQUESTS)
                    })
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = requestIcon),
                    contentDescription = "request",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize),
                )
                Text(text = "Requests", fontSize = 13.sp)
            }
            Column(
                modifier = Modifier
                    .height(iconButtonHeight)
                    .width(iconButtonWidth)
                    .clickable(onClick = {
                        mainViewModel.setHomeScreen(HomeScreen.FRIENDS)
                    })
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = friendsIcon),
                    contentDescription = "friends",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize),
                )
                Text(text = "friends", fontSize = 13.sp)
            }
        }
    }
}
