package com.darkube.pirate.components

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.MessageParser
import com.darkube.pirate.services.SocketManager
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.FriendType
import com.darkube.pirate.types.MessageType
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun AppChatInput(
    pirateId: String,
    username: String,
    mainViewModel: MainViewModel,
) {
    val chatScreen by mainViewModel.chatScreenState.collectAsState()
    if (chatScreen == FriendType.FRIENDS || chatScreen == FriendType.SELF) {
        ChatInput(pirateId = pirateId, username = username, mainViewModel = mainViewModel)
    }
}

@Composable
fun ChatInput(
    pirateId: String,
    username: String,
    mainViewModel: MainViewModel,
) {
    val sendIcon = R.drawable.plain_icon
    val plusIcon = R.drawable.add_icon
    val micIcon = R.drawable.microphone_icon
    val cameraIcon = R.drawable.camera_icon
    val gifIcon = R.drawable.gif_icon

    val context = LocalContext.current
    val iconSize = 24.dp
    var message by remember { mutableStateOf("") }
    val textBoxBackground = NavBarBackground

    val userState by mainViewModel.userState.collectAsState()
    val userId = userState.getOrDefault("_id", "")

    var isLongPress by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message.isEmpty()) {
            SocketManager.stoppedTyping()
        } else {
            SocketManager.startedTyping()
        }
    }

    val sendEncryptedMessage = { encryptedMessage: String ->
        val body = buildJsonObject {
            put("message", encryptedMessage)
        }
        fetch(
            url = "/api/pushtoken/message/$pirateId",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "Failed to Sent Message",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    loading = false
                    return@fetch
                }
                mainViewModel.viewModelScope.launch {
                    mainViewModel.updateNewMessageForPirate(
                        pirateId = pirateId,
                        message = message,
                        type = MessageType.TEXT.value,
                        side = 0,
                        username = username,
                    )
                    loading = false
                    message = ""
                }
            },
            body = body,
            headers = mainViewModel.getHeaders(),
            type = RequestType.POST,
        )
    }

    val sendMessage = {
        loading = true
        if (userId == pirateId) {
            mainViewModel.viewModelScope.launch {
                mainViewModel.updateNewMessageForPirate(
                    pirateId = pirateId,
                    message = message.trim(),
                    type = MessageType.TEXT.value,
                    side = 0,
                    username = username,
                )
                loading = false
                message = ""
            }
        } else {
            fetch(
                url = "/api/user/public_key/$pirateId",
                callback = { response: JsonElement ->
                    val error =
                        response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                    if (error.isNotEmpty()) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "Failed to get key",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        loading = false
                        return@fetch
                    }
                    val publicKey: String =
                        response.jsonObject["result"]?.jsonObject?.get("public_key")?.jsonPrimitive?.contentOrNull
                            ?: ""
                    if (publicKey.isEmpty()) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "Failed to Sent Message",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        loading = false
                        return@fetch
                    }
                    val encryptedMessage = MessageParser.encrypt(message.trim(), publicKey)
                    sendEncryptedMessage(encryptedMessage)
                },
                headers = mainViewModel.getHeaders(),
                type = RequestType.GET,
            )
        }
    }

    Row(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .padding(start = 8.dp, top = 0.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
                .padding(end = 4.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(textBoxBackground)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                modifier = Modifier
                    .size(iconSize)
                    .padding(0.dp),
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(id = gifIcon),
                    contentDescription = "GIF",
                    modifier = Modifier
                        .size(iconSize),
                )
            }
            BasicTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp),
                maxLines = 4,
                textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
                cursorBrush = SolidColor(LightColor),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(4.dp)) {
                        if (message.isEmpty()) {
                            Text(
                                text = "Pirate message",
                                style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        innerTextField()
                    }
                }
            )
            AnimatedVisibility(message.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(iconSize)
                            .padding(0.dp),
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = plusIcon),
                            contentDescription = "Plus",
                            modifier = Modifier
                                .size(iconSize),
                            tint = Color.White,
                        )
                    }
                }
            }
            AnimatedVisibility(message.isEmpty()) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(iconSize)
                            .padding(0.dp),
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = cameraIcon),
                            contentDescription = "Camera",
                            modifier = Modifier
                                .size(iconSize),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        modifier = Modifier
                            .size(iconSize)
                            .padding(0.dp),
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = micIcon),
                            contentDescription = "Mic",
                            modifier = Modifier
                                .size(iconSize),
                        )
                    }
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(
                enabled = !loading,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(25.dp))
                    .background(color = PrimaryBlue)
                    .size(iconSize + 12.dp),
                onClick = {
                    if (isLongPress) {
                        if (message.isEmpty()) {
                            return@IconButton
                        } else {
                            Toast
                                .makeText(context, "Long pressed!", Toast.LENGTH_SHORT)
                                .show()
                            isLongPress = true
                        }
                    }
                    if (message.trim().isEmpty()) {
                        return@IconButton
                    } else {
                        sendMessage()
                    }
                }
            ) {
                AnimatedVisibility(message.isEmpty()) {
                    Icon(
                        painter = painterResource(id = plusIcon),
                        contentDescription = "Plus",
                        modifier = Modifier
                            .size(iconSize),
                        tint = Color.White,
                    )
                }
                AnimatedVisibility(message.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = sendIcon),
                        contentDescription = "Send Message",
                        modifier = Modifier
                            .padding(top = 2.dp, end = 2.dp)
                            .size(iconSize),
                        tint = Color.White,
                    )
                }
            }
        }
    }
}
