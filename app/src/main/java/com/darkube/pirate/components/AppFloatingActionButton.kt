package com.darkube.pirate.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor

@Composable
fun AppFloatingActionButton(
    mainViewModel: MainViewModel
) {
    val borderRadius = 12.dp
    val backgroundColor = PrimaryColor
    val borderColor = NavBarBackground
    val contentColor = Color.White
    val buttonSize = 52.dp
    val iconSize = 20.dp
    val iconDescription = "New Chat"

    val homeScreen by mainViewModel.homeScreenState.collectAsState()
    val icon = when (homeScreen) {
        HomeScreen.CHATS -> R.drawable.pen_icon
        HomeScreen.REQUESTS -> R.drawable.add_circle_icon
        HomeScreen.CALLS -> R.drawable.trash_bin_icon
        HomeScreen.STORIES -> R.drawable.album_icon
    }

    OutlinedIconButton(
        onClick = { /* Handle click */ },
        modifier = Modifier
            .size(buttonSize),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
        ),
        shape = RoundedCornerShape(borderRadius),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = iconDescription,
            modifier = Modifier
                .size(iconSize),
        )
    }
}
