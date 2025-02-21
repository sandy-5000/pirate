package com.darkube.pirate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.BubbleBlue
import com.darkube.pirate.ui.theme.PrimaryBlue
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.ui.theme.SecondaryBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubble(message: String, side: Int, timeStamp: String) {
    val horizontalPadding = 20.dp
    val backGroundColor = if (side == 1) PrimaryColor else PrimaryBlue
    val contentColor = Color.White
    val trimmedTimeStamp = timeStamp.substring(11, 16)
    val checkSolidIcon = R.drawable.check_filled_icon
    val iconSize = 13.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = if (side == 1) Arrangement.Start else Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 4.dp),
            horizontalArrangement = if (side == 1) Arrangement.Start else Arrangement.End,
        ) {
            FlowRow(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(backGroundColor)
                    .padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 10.dp,
                        end = 10.dp,
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = contentColor,
                    fontWeight = FontWeight.W400,
                )
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = trimmedTimeStamp,
                        fontSize = 11.sp,
                        color = contentColor,
                        fontWeight = FontWeight.Medium,
                    )
                    if (side == 0) {
                        Box(
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = checkSolidIcon),
                                contentDescription = "edit",
                                modifier = Modifier
                                    .padding(start = 7.dp)
                                    .size(iconSize),
                            )
                            Icon(
                                painter = painterResource(id = checkSolidIcon),
                                contentDescription = "edit",
                                modifier = Modifier
                                    .size(iconSize)
                                    .zIndex(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}
