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
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
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

    val showBackButton = !setOf(
        ChatRoute.javaClass.name,
        GroupsRoute.javaClass.name,
        CallsRoute.javaClass.name,
        StoriesRoute.javaClass.name
    ).contains(mainViewModel.currentScreen)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(barHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column() {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showBackButton) {
                    IconButton(
                        modifier = Modifier.padding(start = titlePadding),
                        onClick = {
                            mainViewModel.navController.popBackStack()
                            mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_left_icon),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(iconSize),
                        )
                    }
                }
                Text(
                    text = titles.getOrDefault(mainViewModel.currentScreen, ""),
                    modifier = Modifier.padding(
                        start = if (showBackButton) {
                            0.dp
                        } else {
                            titlePadding + titlePadding
                        }
                    ),
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = sidesPadding),
        ) {
            TopBarOptions(mainViewModel = mainViewModel)
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
            .background(NavBarBackground)
            .padding(start = 8.dp, end = 32.dp)
    ) {
        DropdownMenuItem(
            text = { Text("New group") },
            onClick = {
                Toast.makeText(context, "New group", Toast.LENGTH_SHORT).show()
                expanded = false
            })
        DropdownMenuItem(
            text = { Text("Invite friends") },
            onClick = {
                Toast.makeText(context, "Invite friends", Toast.LENGTH_SHORT).show()
                expanded = false
            })
        DropdownMenuItem(
            text = { Text("Mark all read") },
            onClick = {
                Toast.makeText(context, "Mark all read", Toast.LENGTH_SHORT).show()
                expanded = false
            })
        DropdownMenuItem(
            text = { Text("Profile") },
            onClick = {
                mainViewModel.navController.navigate(ProfileRoute)
                mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                expanded = false
            })
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = {
                mainViewModel.navController.navigate(SettingsRoute)
                mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                expanded = false
            })
    }
}
