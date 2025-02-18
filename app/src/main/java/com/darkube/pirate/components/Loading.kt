package com.darkube.pirate.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor

@Composable
fun Loading(
    modifier: Modifier,
    durationMillis: Int = 900
) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val loadingText = if (scale < 1.03) {
        "loading ..  "
    } else if (scale < 1.06) {
        "loading ... "
    } else {
        "loading ...."
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.pirate_flag),
            contentDescription = "Loading Image",
            modifier = Modifier
                .size(100.dp)
                .scale(scale),
        )
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.loading_icon),
                contentDescription = "Loading",
                modifier = Modifier
                    .size(16.dp)
                    .rotate(rotation),
                tint = LightColor,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = loadingText,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LoadingSpinner(
    modifier: Modifier,
) {
    var startAnimation by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.spinner_icon),
            contentDescription = "Loading Image",
            modifier = Modifier
                .size(200.dp)
                .rotate(rotation),
        )
    }
}
