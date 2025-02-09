package com.darkube.pirate.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.LightColor

@Composable
fun UnderLoading(status: Boolean, message: String = "loading...") {
    val loadingIcon = R.drawable.loading_icon

    val iconSize = 12.dp

    AnimatedVisibility(status) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var startAnimation by remember { mutableStateOf(false) }
            val rotation by animateFloatAsState(
                targetValue = if (startAnimation) 360f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = "rotation"
            )
            LaunchedEffect(Unit) {
                startAnimation = true
            }

            Icon(
                painter = painterResource(id = loadingIcon),
                contentDescription = "Loading",
                modifier = Modifier
                    .size(iconSize)
                    .rotate(rotation),
                tint = LightColor
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                message,
                color = LightColor,
                fontSize = 13.sp,
            )
        }
    }
}
