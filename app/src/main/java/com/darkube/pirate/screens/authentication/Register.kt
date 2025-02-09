package com.darkube.pirate.screens.authentication

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.darkube.pirate.ui.theme.GreenColor
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.ui.theme.RedColor
import com.darkube.pirate.ui.theme.SecondaryBlue
import com.darkube.pirate.utils.fetch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

enum class Status {
    AVAILABLE, NOT_AVAILABLE, LOADING
}

@Composable
fun Register(
    mainViewModel: MainViewModel,
    setAuthenticatePage: (AuthenticatePage) -> Unit,
) {
    val backgroundColor = AppBackground
    val navButtonColor = PrimaryColor

    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val userIdIcon = R.drawable.user_id_icon
    val mailIcon = R.drawable.mail_icon
    val firstNameIcon = R.drawable.user_icon
    val lastNameIcon = R.drawable.dna_icon
    val shieldIcon = R.drawable.shield_icon
    val shieldCheckIcon = R.drawable.shield_check_icon
    val registerIcon = R.drawable.login_icon
    val returnIcon = R.drawable.undo_left_icon
    val backIcon = R.drawable.arrow_left_icon
    val nextIcon = R.drawable.arrow_right_icon

    val iconSize = 20.dp
    val textBoxColor = LightColor

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

    var isValidUsername by remember { mutableStateOf(true) }
    var isValidEmail by remember { mutableStateOf(true) }
    var isValidPasswd by remember { mutableStateOf(true) }
    var isValidConfirmPasswd by remember { mutableStateOf(true) }

    var isUsernameAvailable by remember { mutableStateOf(Status.AVAILABLE) }
    var isEmailAvailable by remember { mutableStateOf(Status.AVAILABLE) }

    var loading by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf("") }

    var step by remember { mutableIntStateOf(1) }

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
        } else if (type == "email") {
            isEmailAvailable = Status.LOADING
            url = "api/user/search/$email?type=email"
            if (email.isEmpty() || !isValidEmail) {
                makeCall = false
            }
        }
        if (makeCall) {
            fetch(
                url = url,
                callback = { response: JsonElement ->
                    val error = response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                    if (error == "__ERROR__") {
                        isEmailAvailable = Status.NOT_AVAILABLE
                        return@fetch
                    }
                    val flag = response.jsonObject["flag"]?.jsonPrimitive?.contentOrNull ?: "true"
                    if (type == "username") {
                        isUsernameAvailable =
                            if (flag == "true") Status.NOT_AVAILABLE else Status.AVAILABLE
                    } else {
                        isEmailAvailable =
                            if (flag == "true") Status.NOT_AVAILABLE else Status.AVAILABLE
                    }
                },
                type = RequestType.GET,
            )
        }
    }

    val prevStep = {
        step = (step - 1).coerceAtLeast(1)
    }
    val nextStep = {
        step = (step + 1).coerceAtMost(3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .imePadding(),
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
            when (step) {
                1 -> Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            singleLine = true,
                            onValueChange = {
                                if (it.length < 256) {
                                    firstName = it
                                }
                            },
                            label = { Text("First Name") },
                            placeholder = { Text("Enter your First Name") },
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = firstNameIcon),
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
                            value = lastName,
                            singleLine = true,
                            onValueChange = {
                                if (it.length < 256) {
                                    lastName = it
                                }
                            },
                            label = { Text("Last Name") },
                            placeholder = { Text("Enter your Last Name") },
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = lastNameIcon),
                                    contentDescription = "lastName",
                                    modifier = Modifier
                                        .size(iconSize),
                                )
                            }
                        )
                    }
                }

                2 -> Column {
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
                            label = { Text("Username") },
                            placeholder = { Text("Enter your Username") },
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = email,
                            singleLine = true,
                            onValueChange = {
                                val regex = "^[a-zA-Z0-9.]{1,64}@[a-zA-Z0-9]{2,255}.com$".toRegex()
                                if (it.length < 294) {
                                    isValidEmail = it.isEmpty() || regex.matches(it)
                                    email = it
                                    checkAvailability("email")
                                }
                            },
                            isError = !isValidEmail,
                            label = { Text("User Email") },
                            placeholder = { Text("Enter your Email") },
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = mailIcon),
                                    contentDescription = "Email",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = if (isValidEmail) LightColor else RedColor,
                                )
                            }
                        )
                    }
                    ErrorMessage(
                        !isValidEmail,
                        "Invalid Email Format.",
                    )
                    if (isValidEmail && email.isNotEmpty()) {
                        val message = when (isEmailAvailable) {
                            Status.AVAILABLE -> "Email is Available to Register."
                            Status.NOT_AVAILABLE -> "Email is Already Registered."
                            Status.LOADING -> "checking availability..."
                        }
                        AvailableStatus(isEmailAvailable, message)
                    }
                }

                3 -> Column {
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
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            visualTransformation = if (showPasswd) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
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
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxColor,
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
                                if (!isValidEmail || email.isEmpty() || isEmailAvailable != Status.AVAILABLE) {
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
                                put("first_name", firstName.trim())
                                put("last_name", lastName.trim())
                                put("username", username.trim())
                                put("email", email.trim())
                                put("passwd", passwd)
                            }

                            fetch(
                                url = "/api/user/register",
                                callback = { response: JsonElement ->
                                    val error = response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                                    if (error == "__ERROR__") {
                                        passwd = ""
                                        loading = false
                                        registerError = "An Error Occurred During Register."
                                        return@fetch
                                    }
                                    mainViewModel.viewModelScope.launch {
                                        mainViewModel.login(userDetails = response)
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
                                painter = painterResource(id = registerIcon),
                                contentDescription = "Register",
                                modifier = Modifier
                                    .size(iconSize),
                                tint = backgroundColor
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                "Register",
                                fontSize = 15.sp,
                                color = backgroundColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableStatus(status: Status, message: String) {
    val checkIcon = R.drawable.check_circle_icon
    val closeIcon = R.drawable.close_circle_icon
    val loadingIcon = R.drawable.ghost_smile_icon

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
