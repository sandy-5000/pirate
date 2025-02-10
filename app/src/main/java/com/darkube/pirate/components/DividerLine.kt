package com.darkube.pirate.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DividerLine() {
    val verticalPadding = 8.dp
    val horizontalPadding = 16.dp

    Row(
        modifier = Modifier.padding(
            start = horizontalPadding,
            end = horizontalPadding,
            top = verticalPadding,
            bottom = verticalPadding
        )
    ) {
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}
