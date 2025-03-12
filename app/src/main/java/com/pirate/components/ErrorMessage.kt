package com.pirate.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.pirate.ui.theme.RedColor

@Composable
fun ErrorMessage(flag: Boolean, message: String) {
    AnimatedVisibility(flag) {
        Text(
            message,
            color = RedColor,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(0.8f),
        )
    }
}
