package com.darkube.pirate.components

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.darkube.pirate.ui.theme.LightColor
import java.security.MessageDigest

val onCirclePixels = setOf(
    Pair(0, 2), Pair(0, 7),
    Pair(2, 0), Pair(2, 9),
    Pair(7, 0), Pair(7, 9),
    Pair(9, 2), Pair(9, 7),
)

@Composable
fun PixelAvatar(username: String) {
    val hash = md5Hash(username)

    val gridSize = 10

    val pixelSize = 19f
    val spacing = 2.2f
    val avatarSize = 60.dp

    val isFilled = BooleanArray(gridSize * gridSize)

    for (y in 0 until gridSize) {
        for (x in 0 until (gridSize + 1) / 2) {
            val index = (y * gridSize + x) % hash.size
            val value = hash[index].toInt() and 0xFF
            isFilled[y * gridSize + x] = value % 2 == 0
            isFilled[y * gridSize + (gridSize - 1 - x)] = value % 2 == 0
        }
    }

    Canvas(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(30.dp))
            .border(1.dp, LightColor, RoundedCornerShape(30.dp))
            .size(avatarSize)
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        val canvas = drawContext.canvas.nativeCanvas

        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                if (isFilled[y * gridSize + x] && !onCirclePixels.contains(Pair(y, x)) ) {
                    paint.color = Color.rgb(0xA8, 0xAA, 0xB4)
                    val left = x * (pixelSize + spacing)
                    val top = y * (pixelSize + spacing)
                    val right = left + pixelSize
                    val bottom = top + pixelSize

                    canvas.drawRect(RectF(left, top, right, bottom), paint)
                }
            }
        }
    }
}

fun md5Hash(input: String): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(input.toByteArray())
}
