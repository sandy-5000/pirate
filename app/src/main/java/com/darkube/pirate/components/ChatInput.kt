package com.darkube.pirate.components

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryBlue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun ChatInput(
    pirateId: String,
    mainViewModel: MainViewModel,
) {
    val sendIcon = R.drawable.map_arrow_right_icon
    val plusIcon = R.drawable.album_icon
    val emojiIcon = R.drawable.smile_circle_icon
    val gifIcon = R.drawable.gif_icon

    val context = LocalContext.current
    val iconSize = 22.dp
    val trailingIconSize = 24.dp
    var message by remember { mutableStateOf("") }
    val textBoxBackground = NavBarBackground

    Row(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type your message") },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .background(textBoxBackground),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.White,
                cursorColor = LightColor,
                focusedBorderColor = textBoxBackground,
                unfocusedBorderColor = textBoxBackground,
            ),
            textStyle = TextStyle(fontSize = 14.sp),
            leadingIcon = {
                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = gifIcon),
                        contentDescription = "GIF",
                        modifier = Modifier
                            .size(trailingIconSize),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = emojiIcon),
                        contentDescription = "Emoji",
                        modifier = Modifier
                            .size(trailingIconSize),
                    )
                }
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = plusIcon),
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(trailingIconSize),
                )
            },
            maxLines = 4,
        )
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(25.dp))
                    .background(color = PrimaryBlue),
                onClick = {
                    if (message.isEmpty()) {
                        return@IconButton
                    }
                    val body = buildJsonObject {
                        put("message", message)
                    }
                    fetch(
                        url = "/api/pushtoken/message/$pirateId",
                        callback = { response: JsonElement ->
                            val error =
                                response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                            if (error.isNotEmpty()) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Failed to Sent Message", Toast.LENGTH_LONG).show()
                                }
                                return@fetch
                            }
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, "Message Sent Successfully", Toast.LENGTH_LONG).show()
                            }
                            message = ""
                        },
                        body = body,
                        headers = mainViewModel.getHeaders(),
                        type = RequestType.POST,
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = sendIcon),
                    contentDescription = "Send Message",
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(iconSize),
                    tint = AppBackground,
                )
            }
        }
    }
}
