package com.pirate.screens.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.pirate.R
import com.pirate.components.ErrorMessage
import com.pirate.components.UnderLoading
import com.pirate.services.fetch
import com.pirate.types.AuthenticatePage
import com.pirate.types.RequestType
import com.pirate.types.Status
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.GreenColor
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.PrimaryBlue
import com.pirate.ui.theme.PrimaryColor
import com.pirate.ui.theme.RedColor
import com.pirate.ui.theme.SecondaryBlue
import com.pirate.viewModels.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun Register(
    mainViewModel: MainViewModel,
    setAuthenticatePage: (AuthenticatePage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = AppBackground
    val navButtonColor = PrimaryColor

    val eyeOpenIcon = R.drawable.icon_eye_open
    val eyeCloseIcon = R.drawable.icon_eye_closed
    val userIdIcon = R.drawable.icon_emoji_funny_square
    val firstNameIcon = R.drawable.icon_user
    val dnaIcon = R.drawable.icon_dna
    val shieldIcon = R.drawable.icon_shield
    val shieldCheckIcon = R.drawable.icon_shield_check
    val registerIcon = R.drawable.icon_login
    val returnIcon = R.drawable.icon_undo_left
    val backIcon = R.drawable.icon_arrow_left
    val nextIcon = R.drawable.icon_arrow_right

    val iconSize = 20.dp
    val textBoxColor = LightColor

    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterUsername = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterConfirmPassword = remember { FocusRequester() }

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

    var isValidUsername by remember { mutableStateOf(true) }
    var isValidPasswd by remember { mutableStateOf(true) }
    var isValidConfirmPasswd by remember { mutableStateOf(true) }

    var isUsernameAvailable by remember { mutableStateOf(Status.AVAILABLE) }

    val scrollState = rememberScrollState()

    var loading by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf("") }

    var step by remember { mutableIntStateOf(1) }

    val prevStep = {
        step = (step - 1).coerceAtLeast(1)
    }
    val nextStep = {
        step = (step + 1).coerceAtMost(3)
    }

    // TODO: implement DeBounce
    val checkAvailability = { type: String ->
        var makeCall = step == 2
        var url = "/api/user/search/"
        if (type == "username") {
            isUsernameAvailable = Status.LOADING
            url = "api/user/search/$username"
            if (username.isEmpty() || !isValidUsername) {
                makeCall = false
            }
        }
        if (makeCall) {
            fetch(
                url = url,
                callback = { response: JsonElement ->
                    val error = response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                    if (error == "__ERROR__") {
                        isUsernameAvailable = Status.NOT_AVAILABLE
                        return@fetch
                    }
                    val flag = response.jsonObject["flag"]?.jsonPrimitive?.contentOrNull ?: "true"
                    if (type == "username") {
                        isUsernameAvailable =
                            if (flag == "true") Status.NOT_AVAILABLE else Status.AVAILABLE
                    }
                },
                type = RequestType.GET,
            )
        }
    }

    Column(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .background(color = backgroundColor)
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
                painter = painterResource(id = R.drawable.app_icon_pirate_flag),
                contentDescription = "App Logo",
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                1 -> Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 8.dp),
                    ) {
                        Text(
                            text = "Pick a name, ––– matey!\nThis be what others see!\nYe can leave it empty if ye wish!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = name,
                            singleLine = true,
                            onValueChange = {
                                if (it.length < 256) {
                                    name = it
                                }
                            },
                            label = { Text("Display Name") },
                            placeholder = { Text("Enter your Name") },
                            modifier = Modifier
                                .focusRequester(focusRequesterName)
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                                selectionColors = TextSelectionColors(
                                    handleColor = LightColor,
                                    backgroundColor = Color.DarkGray,
                                ),
                                cursorColor = LightColor,
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    nextStep()
                                }
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = firstNameIcon),
                                    contentDescription = "User ID",
                                    modifier = Modifier
                                        .size(iconSize),
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = dnaIcon),
                                    contentDescription = "Dna",
                                    modifier = Modifier
                                        .size(iconSize),
                                )
                            }
                        )
                    }
                }

                2 -> Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 8.dp),
                    ) {
                        Text(
                            text = "Pick a username, captain!\nThe name your shipmates will recognize!\nMake it memorable!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = username,
                            singleLine = true,
                            onValueChange = {
                                val regex = "^[a-z0-9-_.]{4,64}$".toRegex()
                                if (it.length < 64) {
                                    isValidUsername = it.isEmpty() || regex.matches(it)
                                    username = it
                                    checkAvailability("username")
                                }
                            },
                            isError = !isValidUsername,
                            label = { Text("username") },
                            placeholder = { Text("Enter your username") },
                            modifier = Modifier
                                .focusRequester(focusRequesterUsername)
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                                selectionColors = TextSelectionColors(
                                    handleColor = LightColor,
                                    backgroundColor = Color.DarkGray,
                                ),
                                cursorColor = LightColor,
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = userIdIcon),
                                    contentDescription = "User ID",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = if (isValidUsername) LightColor else RedColor,
                                )
                            }
                        )
                    }
                    ErrorMessage(
                        !isValidUsername,
                        "username must have min 4 characters can only contain a-z0-9_-. symbols.",
                    )
                    if (isValidUsername && username.isNotEmpty()) {
                        val message = when (isUsernameAvailable) {
                            Status.AVAILABLE -> "username is Available."
                            Status.NOT_AVAILABLE -> "username is Not Available."
                            Status.LOADING -> "checking availability..."
                        }
                        AvailableStatus(isUsernameAvailable, message)
                    }
                }

                3 -> Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 8.dp),
                    ) {
                        Text(
                            text = "Set yer secret passphrase, matey!\nKeep it safe, or risk bein' marooned!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = passwd,
                            singleLine = true,
                            onValueChange = {
                                passwd = it
                                isValidPasswd = it.isEmpty() || it.length >= 8
                            },
                            isError = !isValidPasswd,
                            label = { Text("Password") },
                            placeholder = { Text("Enter your Password") },
                            modifier = Modifier
                                .focusRequester(focusRequesterPassword)
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            visualTransformation = if (showPasswd) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                                selectionColors = TextSelectionColors(
                                    handleColor = LightColor,
                                    backgroundColor = Color.DarkGray,
                                ),
                                cursorColor = LightColor,
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusRequesterConfirmPassword.requestFocus()
                                }
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = shieldIcon),
                                    contentDescription = "Shield",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = if (isValidPasswd) LightColor else RedColor,
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
                                        tint = if (isValidPasswd) LightColor else RedColor,
                                    )
                                }
                            }
                        )
                    }
                    ErrorMessage(
                        !isValidPasswd,
                        "Password Must contain minimum 8 characters.",
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = confirmPasswd,
                            singleLine = true,
                            onValueChange = {
                                confirmPasswd = it
                                isValidConfirmPasswd = it.isEmpty() || confirmPasswd == passwd
                            },
                            isError = !isValidConfirmPasswd,
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Confirm your Password") },
                            modifier = Modifier
                                .focusRequester(focusRequesterConfirmPassword)
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                                selectionColors = TextSelectionColors(
                                    handleColor = LightColor,
                                    backgroundColor = Color.DarkGray,
                                ),
                                cursorColor = LightColor,
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = shieldCheckIcon),
                                    contentDescription = "Shield",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = if (isValidConfirmPasswd) LightColor else RedColor,
                                )
                            },
                        )
                    }
                    ErrorMessage(
                        !isValidConfirmPasswd,
                        "Password Didn't Match.",
                    )
                }
            }
            UnderLoading(loading, "Registering...")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 40.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = returnIcon),
                        contentDescription = "Return",
                        tint = Color.White,
                        modifier = Modifier
                            .size(iconSize)
                            .padding(end = 4.dp),
                    )
                    Text(
                        "Return to Login",
                        color = Color.White,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.None,
                        modifier = Modifier.clickable(onClick = {
                            setAuthenticatePage(AuthenticatePage.LOGIN)
                        })
                    )
                }
                when (step) {
                    1, 2 -> Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        horizontalArrangement = if (step == 1) {
                            Arrangement.End
                        } else {
                            Arrangement.SpaceBetween
                        }
                    ) {
                        if (step != 1) {
                            Button(
                                onClick = prevStep,
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = navButtonColor,
                                ),
                                contentPadding = PaddingValues(start = 8.dp, end = 16.dp),
                            ) {
                                Row {
                                    Icon(
                                        painter = painterResource(id = backIcon),
                                        contentDescription = "Back",
                                        modifier = Modifier
                                            .size(iconSize),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        "Back",
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = {
                                if (step == 2) {
                                    if (!isValidUsername || username.isEmpty() || isUsernameAvailable != Status.AVAILABLE) {
                                        return@Button
                                    }
                                }
                                nextStep()
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navButtonColor,
                            ),
                            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
                        ) {
                            Row {
                                Text(
                                    "Next",
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Icon(
                                    painter = painterResource(id = nextIcon),
                                    contentDescription = "Next",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    3 -> Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = prevStep,
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = navButtonColor,
                            ),
                            contentPadding = PaddingValues(start = 8.dp, end = 16.dp),
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = backIcon),
                                    contentDescription = "Back",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    "Back",
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                        Button(
                            onClick = {
                                if (!isValidPasswd || passwd.isEmpty()) {
                                    return@Button
                                }
                                if (!isValidConfirmPasswd || confirmPasswd.isEmpty()) {
                                    return@Button
                                }
                                loading = true

                                val body = buildJsonObject {
                                    put("name", name.trim())
                                    put("username", username.trim())
                                    put("passwd", passwd)
                                }

                                fetch(
                                    url = "/api/user/register",
                                    callback = { response: JsonElement ->
                                        val error =
                                            response.jsonObject["error"]?.jsonPrimitive?.contentOrNull
                                                ?: ""
                                        if (error.isNotEmpty()) {
                                            passwd = ""
                                            loading = false
                                            registerError = "An Error Occurred During Register."
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
                                containerColor = PrimaryBlue,
                            ),
                        ) {
                            Row {
                                Icon(
                                    painter = painterResource(id = registerIcon),
                                    contentDescription = "Register",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    "Register",
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AvailableStatus(status: Status, message: String) {
    val checkIcon = R.drawable.icon_check_circle
    val closeIcon = R.drawable.icon_close_circle
    val loadingIcon = R.drawable.icon_ghost_smile

    val iconSize = 12.dp

    AnimatedVisibility(status == Status.AVAILABLE) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = checkIcon),
                contentDescription = "Check",
                modifier = Modifier
                    .size(iconSize),
                tint = GreenColor
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                message,
                color = GreenColor,
                fontSize = 13.sp,
            )
        }
    }
    AnimatedVisibility(status == Status.NOT_AVAILABLE) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = closeIcon),
                contentDescription = "Close",
                modifier = Modifier
                    .size(iconSize),
                tint = RedColor,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                message,
                color = RedColor,
                fontSize = 13.sp,
            )
        }
    }
    AnimatedVisibility(status == Status.LOADING) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = loadingIcon),
                contentDescription = "Loading",
                modifier = Modifier
                    .size(iconSize),
                tint = LightColor,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                message,
                color = LightColor,
                fontSize = 13.sp,
            )
        }
    }
}