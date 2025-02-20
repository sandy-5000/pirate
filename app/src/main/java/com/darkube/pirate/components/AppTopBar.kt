package com.darkube.pirate.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkube.pirate.ui.theme.NavBarBackground
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.utils.InviteFriendsRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.SettingsRoute
import com.darkube.pirate.utils.getProfileImage
import com.darkube.pirate.utils.getRouteId

@Composable
fun MainScreenTopBar(
    mainViewModel: MainViewModel,
) {
    val topPadding = 36.dp
    val barHeight = 68.dp
    val sidesPadding = 18.dp
    val titlePadding = 32.dp
    val backGroundColor = AppBackground

    val homeScreen by mainViewModel.homeScreenState.collectAsState()
    val pageTitle: String = when (homeScreen) {
        HomeScreen.CHATS -> "Chats"
        HomeScreen.REQUESTS -> "Requests"
        HomeScreen.CALLS -> "Call History"
        HomeScreen.STORIES -> "Stories"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backGroundColor)
            .padding(top = topPadding)
            .height(barHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = pageTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        start = titlePadding,
                    ),
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = sidesPadding),
        ) {
            MainScreenTopBarOptions(mainViewModel = mainViewModel, homeScreen = homeScreen)
        }
    }
}

@Composable
fun ChatScreenTopBar(
    pageTitle: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
) {
    val topPadding = 36.dp
    val barHeight = 68.dp
    val iconPadding = 16.dp
    val titlePadding = 12.dp
    val iconSize = 20.dp
    val sidesPadding = 18.dp
    val backGroundColor = AppBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backGroundColor)
            .padding(top = topPadding)
            .height(barHeight),
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
                        mainViewModel.navController.popBackStack()
                        mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_icon),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(iconSize),
                    )
                }
                Image(
                    painter = painterResource(id = getProfileImage(profileImage)),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
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
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = sidesPadding),
        ) {
            ChatScreenTopBarOptions(mainViewModel = mainViewModel)
        }
    }
}

@Composable
fun BasicTopBar(
    mainViewModel: MainViewModel,
    pageTitle: String,
) {
    val topPadding = 36.dp
    val barHeight = 68.dp
    val titlePadding = 16.dp
    val iconSize = 20.dp
    val backGroundColor = AppBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backGroundColor)
            .padding(top = topPadding)
            .height(barHeight),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier.padding(start = titlePadding),
                    onClick = {
                        mainViewModel.navController.popBackStack()
                        mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_icon),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(iconSize),
                    )
                }
                Text(
                    text = pageTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(
                        start = titlePadding,
                    ),
                )
            }
        }
    }
}

@Composable
fun MainScreenTopBarOptions(
    mainViewModel: MainViewModel,
    homeScreen: HomeScreen,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert, contentDescription = "Menu"
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(screenWidth * 0.5f)
            .background(NavBarBackground),
    ) {
//        if (HomeScreen.CHATS == homeScreen) {
//            DropdownMenuItem(
//                text = {
//                    Text(
//                        "New group",
//                        modifier = Modifier.padding(start = 8.dp),
//                    )
//                },
//                onClick = {
//                    Toast.makeText(context, "New group", Toast.LENGTH_SHORT).show()
//                    expanded = false
//                })
//        }
        DropdownMenuItem(
            text = {
                Text(
                    "Invite friends",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                mainViewModel.navController.navigate(InviteFriendsRoute)
                mainViewModel.setScreen((getRouteId(mainViewModel.navController.currentDestination)))
                expanded = false
            })
        if (HomeScreen.CHATS == homeScreen) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Mark all read",
                        modifier = Modifier.padding(start = 8.dp),
                    )
                },
                onClick = {
                    Toast.makeText(context, "Mark all read", Toast.LENGTH_SHORT).show()
                    expanded = false
                })
        }
        DropdownMenuItem(
            text = {
                Text(
                    "Profile",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                mainViewModel.navController.navigate(ProfileRoute)
                mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                expanded = false
            })
        DropdownMenuItem(
            text = {
                Text(
                    "Settings",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                mainViewModel.navController.navigate(SettingsRoute)
                mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                expanded = false
            })
    }
}

@Composable
fun ChatScreenTopBarOptions(
    mainViewModel: MainViewModel,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert, contentDescription = "Menu"
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(screenWidth * 0.5f)
            .background(NavBarBackground),
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    "Chat Settings",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                Toast.makeText(context, "Chat Settings", Toast.LENGTH_SHORT).show()
                expanded = false
            })
        DropdownMenuItem(
            text = {
                Text(
                    "Search in Chat",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                Toast.makeText(context, "Search in Chat", Toast.LENGTH_SHORT).show()
                expanded = false
            })
        DropdownMenuItem(
            text = {
                Text(
                    "Mute Notifications",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                Toast.makeText(context, "Mute Notifications", Toast.LENGTH_SHORT).show()
                expanded = false
            })
    }
}
