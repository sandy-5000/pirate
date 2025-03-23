package com.pirate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.pirate.R
import com.pirate.ui.theme.PrimaryBlue
import com.pirate.ui.theme.PrimaryColor
import com.pirate.utils.timestampToLocal

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubble(
    message: String,
    side: Int,
    timeStamp: Long,
    start: Boolean = false,
    end: Boolean = false,
    dayStart: Boolean = false,
) {
    val horizontalPadding = 20.dp
    val backGroundColor = if (side == 1) PrimaryColor else PrimaryBlue
    val contentColor = Color.White
    val checkSolidIcon = R.drawable.icon_check_filled
    val iconSize = 13.dp
    val dateTime = timestampToLocal(timeStamp)

    val topPadding = if (start) 4.dp else 0.dp
    val bottomPadding = if (end) 4.dp else 0.dp

    val topStartRound = if (side == 0 || start) 16.dp else 4.dp
    val bottomStartRound = if (side == 0 || end) 16.dp else 4.dp
    val topEndRound = if (side == 1 || start) 16.dp else 4.dp
    val bottomEndRound = if (side == 1 || end) 16.dp else 4.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = if (side == 1) Arrangement.Start else Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = topPadding, bottom = bottomPadding),
            horizontalArrangement = if (side == 1) Arrangement.Start else Arrangement.End,
        ) {
            FlowRow(
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = topStartRound,
                            bottomStart = bottomStartRound,
                            topEnd = topEndRound,
                            bottomEnd = bottomEndRound
                        )
                    )
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
                        text = dateTime.second.substring(0, 5),
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
    if (dayStart) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(dateTime.first, fontSize = 12.sp)
        }
    }
}
