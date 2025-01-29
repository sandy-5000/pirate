package com.darkube.pirate.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.CallsRoute
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.GroupsRoute
import com.darkube.pirate.utils.StoriesRoute
import com.darkube.pirate.utils.getRouteId

@Composable
fun BottomNavBar(
    mainViewModel: MainViewModel,
) {
    if (
        mainViewModel.currentScreen == ChatRoute.javaClass.name ||
        mainViewModel.currentScreen == GroupsRoute.javaClass.name ||
        mainViewModel.currentScreen == CallsRoute.javaClass.name ||
        mainViewModel.currentScreen == StoriesRoute.javaClass.name
    ) {
        MainBottomNavBar(mainViewModel = mainViewModel)
    }
}

@Composable
fun MainBottomNavBar(
    mainViewModel: MainViewModel,
) {
    val barHeight = 68.dp
    val backgroundColor = NavBarBackground

    val iconsMap = mapOf(
        "chat" to R.drawable.chat_round_line_icon,
        "group" to R.drawable.users_group_icon,
        "call" to R.drawable.call_calling_icon,
        "stories" to R.drawable.stories_icons,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .height(barHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TabIcon(
                iconDescription = "Chat",
                icon = iconsMap.getOrDefault("chat", R.drawable.tabs_icon),
                onClick = {
                    if (ChatRoute.javaClass.name == mainViewModel.currentScreen) {
                        return@TabIcon
                    }
                    while (mainViewModel.navController.currentDestination != null) {
                        mainViewModel.navController.popBackStack()
                    }
                    mainViewModel.navController.navigate(ChatRoute)
                    mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                },
                active = ChatRoute.javaClass.name == mainViewModel.currentScreen,
            )
            TabIcon(
                iconDescription = "Group",
                icon = iconsMap.getOrDefault("group", R.drawable.tabs_icon),
                onClick = {
                    if (GroupsRoute.javaClass.name == mainViewModel.currentScreen) {
                        return@TabIcon
                    }
                    mainViewModel.navController.popBackStack(ChatRoute, false)
                    mainViewModel.navController.navigate(GroupsRoute)
                    mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                },
                active = GroupsRoute.javaClass.name == mainViewModel.currentScreen,
            )
            TabIcon(
                iconDescription = "Call",
                icon = iconsMap.getOrDefault("call", R.drawable.tabs_icon),
                onClick = {
                    if (CallsRoute.javaClass.name == mainViewModel.currentScreen) {
                        return@TabIcon
                    }
                    mainViewModel.navController.popBackStack(ChatRoute, false)
                    mainViewModel.navController.navigate(CallsRoute)
                    mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                },
                active = CallsRoute.javaClass.name == mainViewModel.currentScreen,
            )
            TabIcon(
                iconDescription = "Stories",
                icon = iconsMap.getOrDefault("stories", R.drawable.tabs_icon),
                onClick = {
                    if (StoriesRoute.javaClass.name == mainViewModel.currentScreen) {
                        return@TabIcon
                    }
                    mainViewModel.navController.popBackStack(ChatRoute, false)
                    mainViewModel.navController.navigate(StoriesRoute)
                    mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                },
                active = StoriesRoute.javaClass.name == mainViewModel.currentScreen,
            )
        }
    }
}

@Composable
fun TabIcon(
    iconDescription: String,
    icon: Int,
    onClick: () -> Unit,
    active: Boolean = false
) {
    val iconSize = 20.dp
    val borderRadius = 16.dp
    val borderColor = PrimaryColor
    val backgroundColor = if (active) {
        borderColor
    } else {
        NavBarBackground
    }
    val contentColor = Color.White
    val borderWidth = 0.dp
    val buttonHeight = 32.dp
    val buttonWidth = 48.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
            modifier = Modifier
                .height(buttonHeight)
                .width(buttonWidth),
            shape = RoundedCornerShape(borderRadius),
            border = BorderStroke(borderWidth, borderColor)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(iconSize),
            )
        }
        Text(iconDescription, fontSize = 13.sp)
    }
}
