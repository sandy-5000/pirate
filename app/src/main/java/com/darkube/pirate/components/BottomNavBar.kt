package com.darkube.pirate.components

import android.util.Log
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
import androidx.navigation.NavController
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.CallsRoute
import com.darkube.pirate.utils.GroupsRoute
import com.darkube.pirate.utils.HomeRoute
import com.darkube.pirate.utils.StoriesRoute

data class NavIcon(
    val description: String,
    val icon: Int,
    val route: Any,
    val routeName: String,
)

@Composable
fun BottomNavBar(
    tab: String,
    onTabChange: (String) -> Unit,
    navController: NavController,
) {
    val barHeight = 68.dp
    val backgroundColor = NavBarBackground

    val icons = listOf(
        NavIcon(description = "Chat", icon = R.drawable.chat_round_line_icon, route = HomeRoute, routeName = HomeRoute.javaClass.name),
        NavIcon(description = "Group", icon = R.drawable.users_group_icon, route = GroupsRoute, routeName = GroupsRoute.javaClass.name),
        NavIcon(description = "Call", icon = R.drawable.call_calling_icon, route = CallsRoute, routeName = CallsRoute.javaClass.name),
        NavIcon(description = "Stories", icon = R.drawable.stories_icons, route = StoriesRoute, routeName = StoriesRoute.javaClass.name),
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
            icons.forEach { navIcon ->
                TabIcon(
                    iconDescription = navIcon.description,
                    icon = navIcon.icon,
                    onClick = {
                        onTabChange(navIcon.routeName)
                        navController.navigate(navIcon.route)
                    },
                    active = tab == navIcon.routeName,
                )
            }
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
