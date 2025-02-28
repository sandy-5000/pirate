package com.darkube.pirate.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.R.drawable.share_icon
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor

@Composable
fun InviteFriends(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val textBoxBackground = NavBarBackground
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val shareLink by remember { mutableStateOf("https://www.thepirate.com") }
    val shareIcon = share_icon
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareLink)
        type = "text/plain"
    }
    val copyIcon = R.drawable.copy_icon
    val linkIcon = R.drawable.map_arrow_square_icon
    val iconSize = 20.dp
    val imageSize = 100.dp
    val pirateFlag = R.drawable.pirate_flag_outline

    val shareIntent = Intent.createChooser(sendIntent, null)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 40.dp)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = pirateFlag),
                contentDescription = "Shield",
                modifier = Modifier
                    .size(imageSize),
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Ahoy, Matey!",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Gather yer crew and set sail for adventure! Join me on Pirate for fast and simple chats—no buried treasure required!",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 14.sp,
            color = LightColor,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(40.dp))
        TextField(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .background(textBoxBackground),
            value = shareLink,
            onValueChange = {},
            readOnly = true,
            label = { Text("Invite Friends") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.White,
                focusedBorderColor = AppBackground,
                unfocusedBorderColor = AppBackground,
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = linkIcon),
                    contentDescription = "Share Icon",
                    modifier = Modifier.size(iconSize),
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = copyIcon),
                    contentDescription = "Copy Icon",
                    modifier = Modifier
                        .size(iconSize)
                        .clickable(onClick = {
                            val clip =
                                android.content.ClipData.newPlainText("Copied Text", shareLink)
                            clipboardManager.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                                .show()
                        }),
                )
            },
            textStyle = TextStyle(fontSize = 14.sp)
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    context.startActivity(shareIntent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = shareIcon),
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Share",
                    color = Color.White,
                )
            }
        }
    }
}
