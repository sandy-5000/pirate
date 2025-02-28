package com.darkube.pirate.screens

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.components.DividerLine
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.KeyStoreManager
import com.darkube.pirate.services.MessageParser
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.DetailsKey
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.ui.theme.SecondaryBlue
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun Privacy(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val horizontalPadding = 20.dp
    val iconSize = 20.dp
    val generateIcon = R.drawable.key_square_icon
    val copyIcon = R.drawable.copy_icon
    val publicKeyIcon = R.drawable.lock_icon
    val privateKeyIcon = R.drawable.key_square_2_icon
    var loading by remember { mutableStateOf(false) }
    var generatingKeys by remember { mutableStateOf(false) }
    val userState by mainViewModel.userState.collectAsState()
    var publicKey by remember { mutableStateOf(KeyStoreManager.getPublicKey().toString()) }
    val hideOnlineStatus by remember {
        derivedStateOf {
            userState.getOrDefault(
                DetailsKey.HIDE_ONLINE_STATUS.value,
                "false"
            ) == "true"
        }
    }
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val generateAndUploadKeys = {
        generatingKeys = true
        KeyStoreManager.regenerateKeyPair()
        publicKey = KeyStoreManager.getPublicKey().toString()
        val headers = mainViewModel.getHeaders()
        val body = buildJsonObject {
            put("public_key", publicKey)
        }
        fetch(
            url = "/api/user/public_key",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    generatingKeys = false
                    return@fetch
                }
                generatingKeys = false
            },
            body = body,
            headers = headers,
            type = RequestType.PATCH,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = horizontalPadding)
    ) {
        Text(
            text = "Online Status",
            modifier = Modifier
                .padding(bottom = 20.dp, top = 8.dp),
            fontSize = 18.sp,
            color = LightColor,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Hide Status",
                fontSize = 16.sp,
                color = LightColor,
                fontWeight = FontWeight.W500,
            )
            Switch(
                checked = hideOnlineStatus,
                enabled = !loading,
                onCheckedChange = {
                    loading = true
                    mainViewModel.viewModelScope.launch {
                        if (it) {
                            mainViewModel.setHideOnlineStatus()
                        } else {
                            mainViewModel.removeHideOnlineStatus()
                        }
                        loading = false
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NavBarBackground,
                    checkedBorderColor = NavBarBackground,
                    uncheckedThumbColor = NavBarBackground,
                    uncheckedTrackColor = PrimaryColor,
                    uncheckedBorderColor = NavBarBackground,
                )
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "You can set your the online status preferences.",
            fontSize = 14.sp, color = LightColor,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Customize your online status settings based on your preference. You will be able to see others' online status only if yours is visible.",
            fontSize = 14.sp, color = LightColor,
        )
        DividerLine(verticalPadding = 20.dp, horizontalPadding = 4.dp)
        Text(
            text = "Key Pair",
            modifier = Modifier
                .padding(bottom = 20.dp, top = 8.dp),
            fontSize = 18.sp,
            color = LightColor,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = publicKeyIcon),
                contentDescription = "firstNameIcon",
                modifier = Modifier.size(iconSize),
                tint = LightColor,
            )
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                Text(
                    text = "Public Key",
                    fontSize = 13.sp,
                    color = LightColor,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = publicKey.replace("\\s+", ""),
                    fontSize = 10.sp,
                    color = LightColor,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                painter = painterResource(id = copyIcon),
                contentDescription = "edit",
                modifier = Modifier
                    .clickable(onClick = {
                        val clip =
                            android.content.ClipData.newPlainText("Copied Text", publicKey)
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Public Key Copied", Toast.LENGTH_SHORT)
                            .show()
                    })
                    .size(iconSize),
                tint = LightColor,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = privateKeyIcon),
                contentDescription = "firstNameIcon",
                modifier = Modifier.size(iconSize),
                tint = LightColor,
            )
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                Text(
                    text = "Private Key",
                    fontSize = 13.sp,
                    color = LightColor,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = "*".repeat(100),
                    fontSize = 20.sp,
                    color = LightColor,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                )
            }
            Icon(
                painter = painterResource(id = copyIcon),
                contentDescription = "edit",
                modifier = Modifier.size(iconSize),
                tint = LightColor,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Generate a new key pair:",
                fontSize = 14.sp, color = LightColor,
            )
            Button(
                enabled = !generatingKeys,
                onClick = {
                    generateAndUploadKeys()
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryBlue,
                    disabledContainerColor = NavBarBackground,
                ),
                contentPadding = PaddingValues(horizontal = 12.dp),
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = generateIcon),
                        contentDescription = "Generate",
                        modifier = Modifier
                            .size(iconSize),
                        tint = AppBackground
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Generate",
                        fontSize = 15.sp,
                        color = AppBackground
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Text(
//                text = "Generate a new key pair:",
//                fontSize = 14.sp, color = LightColor,
//            )
//            Button(
//                onClick = {
//                    val encryptedMsg = MessageParser.encrypt("Sandyblaze", publicKey)
//                    Log.d("key-o", encryptedMsg)
//                    val message = MessageParser.decrypt(encryptedMsg)
//                    Log.d("key-o", message)
//                },
//                shape = RoundedCornerShape(4.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = SecondaryBlue,
//                    disabledContainerColor = NavBarBackground,
//                ),
//                contentPadding = PaddingValues(horizontal = 12.dp),
//            ) {
//                Row {
//                    Icon(
//                        painter = painterResource(id = generateIcon),
//                        contentDescription = "Generate",
//                        modifier = Modifier
//                            .size(iconSize),
//                        tint = AppBackground
//                    )
//                    Spacer(modifier = Modifier.size(4.dp))
//                    Text(
//                        "Test",
//                        fontSize = 15.sp,
//                        color = AppBackground
//                    )
//                }
//            }
//        }
    }
}
