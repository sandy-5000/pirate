package com.darkube.pirate.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.utils.CallsRoute
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.GroupsRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.SettingsRoute
import com.darkube.pirate.utils.StoriesRoute
import com.darkube.pirate.utils.getRouteId

val titles = mapOf(
    ChatRoute.javaClass.name to "Chats",
    GroupsRoute.javaClass.name to "User Groups",
    CallsRoute.javaClass.name to "Call History",
    StoriesRoute.javaClass.name to "Stories",
    SettingsRoute.javaClass.name to "Settings",
    ProfileRoute.javaClass.name to "Profile",
)

@Composable
fun AppTopBar(
    mainViewModel: MainViewModel,
) {
    val topPadding = 36.dp
    val barHeight = 68.dp
    val sidesPadding = 18.dp
    val titlePadding = 16.dp
    val iconSize = 20.dp
    val backGroundColor = AppBackground

    val isMainScreen = setOf(
        ChatRoute.javaClass.name,
        GroupsRoute.javaClass.name,
        CallsRoute.javaClass.name,
        StoriesRoute.javaClass.name
    ).contains(mainViewModel.currentScreen)

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
                if (!isMainScreen) {
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
                }
                Text(
                    text = titles.getOrDefault(mainViewModel.currentScreen, ""),
                    fontSize = if (isMainScreen) 20.sp else 16.sp,
                    fontWeight = if (isMainScreen) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.padding(
                        start = if (isMainScreen) titlePadding + titlePadding else titlePadding,
                    ),
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = sidesPadding),
        ) {
            if (isMainScreen) {
                TopBarOptions(mainViewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun TopBarOptions(
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
                    "New group",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                Toast.makeText(context, "New group", Toast.LENGTH_SHORT).show()
                expanded = false
            })
        DropdownMenuItem(
            text = {
                Text(
                    "Invite friends",
                    modifier = Modifier.padding(start = 8.dp),
                )
            },
            onClick = {
                Toast.makeText(context, "Invite friends", Toast.LENGTH_SHORT).show()
                expanded = false
            })
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
