package com.darkube.pirate.screens.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.components.ErrorMessage
import com.darkube.pirate.components.UnderLoading
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.AuthenticatePage
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.SecondaryBlue
import com.darkube.pirate.services.fetch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Composable
fun Login(
    mainViewModel: MainViewModel,
    setAuthenticatePage: (AuthenticatePage) -> Unit,
) {
    val backgroundColor = AppBackground
    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val userIdIcon = R.drawable.user_id_icon
    val shieldIcon = R.drawable.shield_icon
    val loginIcon = R.drawable.login_icon
    val forwardIcon = R.drawable.undo_right_icon

    val iconSize = 20.dp
    val textBoxColor = LightColor

    val focusRequesterUserName = remember { FocusRequester() }
    val focusRequesterPasswd = remember { FocusRequester() }

    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .imePadding()
            .background(color = backgroundColor)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.pirate_flag),
                contentDescription = "App Logo",
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                OutlinedTextField(
                    value = username,
                    singleLine = true,
                    onValueChange = {
                        loginError = ""
                        username = it
                    },
                    label = { Text("Username") },
                    placeholder = { Text("Enter your Username") },
                    modifier = Modifier
                        .focusRequester(focusRequesterUserName)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        focusedBorderColor = textBoxColor,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusRequesterPasswd.requestFocus()
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = userIdIcon),
                            contentDescription = "User ID",
                            modifier = Modifier
                                .size(iconSize),
                        )
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                OutlinedTextField(
                    value = passwd,
                    singleLine = true,
                    onValueChange = {
                        loginError = ""
                        passwd = it
                    },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your Password") },
                    modifier = Modifier
                        .focusRequester(focusRequesterPasswd)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth(),
                    visualTransformation = if (showPasswd) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        focusedBorderColor = textBoxColor,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = shieldIcon),
                            contentDescription = "Shield",
                            modifier = Modifier
                                .size(iconSize),
                        )
                    },
                    trailingIcon = {
                        OutlinedIconButton(
                            onClick = { showPasswd = !showPasswd },
                            border = BorderStroke(0.dp, Color.Transparent)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (showPasswd) {
                                        eyeCloseIcon
                                    } else {
                                        eyeOpenIcon
                                    }
                                ),
                                contentDescription = "Password visibility",
                                modifier = Modifier
                                    .size(iconSize),
                            )
                        }
                    }
                )
            }
            UnderLoading(loading, "Logging In...")
            ErrorMessage(loginError.isNotEmpty(), loginError)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 40.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = forwardIcon),
                        contentDescription = "Return",
                        tint = Color.White,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(end = 4.dp),
                    )
                    Text(
                        "Don't have account?",
                        color = Color.White,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.None,
                        modifier = Modifier.clickable(onClick = {
                            setAuthenticatePage(AuthenticatePage.REGISTER)
                        })
                    )
                }
//                Text(
//                    "Forgot Password",
//                    color = Color.LightGray,
//                    fontSize = 14.sp,
//                    textDecoration = TextDecoration.Underline,
//                    modifier = Modifier.clickable(onClick = {
//                        setAuthenticatePage(AuthenticatePage.OTP)
//                    })
//                )
            }
            Button(
                onClick = {
                    if (username.trim() == "" || passwd.isEmpty()) {
                        return@Button
                    }
                    loading = true
                    val body = buildJsonObject {
                        put("username", username.trim())
                        put("passwd", passwd)
                    }
                    fetch(
                        url = "/api/user/login",
                        callback = { response: JsonElement ->
                            val error =
                                response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                            if (error.isNotEmpty()) {
                                passwd = ""
                                loading = false
                                loginError = "User Doesn't Exists or Incorrect Credentials"
                                return@fetch
                            }
                            val result: JsonObject = response.jsonObject["result"]?.jsonObject
                                ?: buildJsonObject { emptyMap<String, String>() }
                            val token: String = response.jsonObject["token"]?.jsonPrimitive?.contentOrNull ?: ""
                            mainViewModel.viewModelScope.launch {
                                mainViewModel.login(userDetails = result, token = token)
                            }
                        },
                        type = RequestType.POST,
                        body = body,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryBlue,
                )
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = loginIcon),
                        contentDescription = "Login",
                        modifier = Modifier
                            .size(iconSize),
                        tint = backgroundColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Login",
                        fontSize = 15.sp,
                        color = backgroundColor
                    )
                }
            }
        }
    }
}
