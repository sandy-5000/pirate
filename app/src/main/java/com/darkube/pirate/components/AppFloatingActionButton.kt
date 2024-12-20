package com.darkube.pirate.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor

@Composable
fun AppFloatingActionButton() {
    val borderRadius = 12.dp
    val backgroundColor = PrimaryColor
    val borderColor = NavBarBackground
    val contentColor = Color.White
    val buttonSize = 52.dp
    val iconSize = 20.dp
    val iconDescription = "New Chat"

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
        Image(
            painter = painterResource(id = R.drawable.pen_icon),
            contentDescription = iconDescription,
            modifier = Modifier
                .size(iconSize),
        )
    }
}
