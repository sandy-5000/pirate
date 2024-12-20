package com.darkube.pirate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkube.pirate.ui.theme.NavBarBackground

@Composable
fun AppTopBar() {
    val topPadding = 36.dp
    val barHeight = 68.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(barHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Top Bar",
        )
    }
}
