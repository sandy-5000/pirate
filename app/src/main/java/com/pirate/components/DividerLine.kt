package com.pirate.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DividerLine(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 8.dp,
    horizontalPadding: Dp = 16.dp,
    color: Color = Color.Gray,
) {
    Row(
        modifier = modifier.padding(
            horizontal = horizontalPadding,
            vertical = verticalPadding,
        )
    ) {
        HorizontalDivider(
            color = color,
            thickness = 1.dp
        )
    }
}
