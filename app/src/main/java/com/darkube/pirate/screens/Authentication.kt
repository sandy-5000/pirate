package com.darkube.pirate.screens

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
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.ui.theme.SecondaryBlue
import kotlinx.coroutines.launch

enum class AuthenticatePage {
    LOGIN, REGISTER, OTP, NEW_PASSWORD
}

@Composable
fun Authentication(
    mainViewModel: MainViewModel
) {
    var authenticatePage by remember { mutableStateOf(AuthenticatePage.LOGIN) }
    val setAuthenticatePage = { page: AuthenticatePage ->
        authenticatePage = page
    }
    when (authenticatePage) {
        AuthenticatePage.LOGIN -> Login(
            mainViewModel = mainViewModel,
            setAuthenticatePage = setAuthenticatePage,
        )

        AuthenticatePage.REGISTER -> Register(
            mainViewModel = mainViewModel,
            setAuthenticatePage = setAuthenticatePage,
        )

        AuthenticatePage.OTP -> OTPScreen(
            setAuthenticatePage = setAuthenticatePage,
        )

        AuthenticatePage.NEW_PASSWORD -> NewPassword(
            mainViewModel = mainViewModel,
            setAuthenticatePage = setAuthenticatePage,
        )
    }
}

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

    var username by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                OutlinedTextField(
                    value = username,
                    singleLine = true,
                    onValueChange = { username = it },
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
                    onValueChange = { passwd = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your Password") },
                    modifier = Modifier
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
                horizontalArrangement = Arrangement.SpaceBetween
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
                Text(
                    "Forgot Password",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = {
                        setAuthenticatePage(AuthenticatePage.OTP)
                    })
                )
            }
            Button(
                onClick = {
                    if (username.trim() == "") {
                        return@Button
                    }
                    mainViewModel.viewModelScope.launch {
                        mainViewModel.login(username = username)
                    }
                },
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

    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

    var step by remember { mutableIntStateOf(1) }

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
                            value = name,
                            singleLine = true,
                            onValueChange = { name = it },
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
                            value = name,
                            singleLine = true,
                            onValueChange = { name = it },
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
                            onValueChange = { username = it },
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
                                )
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = email,
                            singleLine = true,
                            onValueChange = { email = it },
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
                                )
                            }
                        )
                    }
                }

                3 -> Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = passwd,
                            singleLine = true,
                            onValueChange = { passwd = it },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                    ) {
                        OutlinedTextField(
                            value = confirmPasswd,
                            singleLine = true,
                            onValueChange = { confirmPasswd = it },
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
                                )
                            },
                        )
                    }
                }

                4 -> Empty()
            }
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
                        onClick = nextStep,
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
                            mainViewModel.viewModelScope.launch {
                                mainViewModel.login(username = "sandyblaze")
                            }
                        },
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

                else -> Empty()
            }
        }
    }
}

@Composable
fun OTPScreen(
    setAuthenticatePage: (AuthenticatePage) -> Unit,
) {
    val backgroundColor = AppBackground
    val navButtonColor = PrimaryColor

    val mailIcon = R.drawable.mail_icon
    val keyIcon = R.drawable.key_square_icon
    val sendIcon = R.drawable.map_arrow_square_icon
    val submitIcon = R.drawable.recive_square_icon
    val returnIcon = R.drawable.undo_left_icon

    val iconSize = 20.dp
    val textBoxColor = LightColor

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                OutlinedTextField(
                    value = email,
                    singleLine = true,
                    onValueChange = { email = it },
                    label = { Text("Recovery Email") },
                    placeholder = { Text("Enter your Recovery Email") },
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
                            contentDescription = "User Email",
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
                    value = otp,
                    singleLine = true,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP") },
                    placeholder = { Text("Enter your OTP") },
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
                            painter = painterResource(id = keyIcon),
                            contentDescription = "Shield",
                            modifier = Modifier
                                .size(iconSize),
                        )
                    },
                )
            }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = {

                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = navButtonColor,
                    ),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                ) {
                    Row {
                        Text(
                            "Send OTP",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            painter = painterResource(id = sendIcon),
                            contentDescription = "Send OTP",
                            modifier = Modifier
                                .size(iconSize),
                            tint = Color.White,
                        )
                    }
                }
                Button(
                    onClick = {
                        setAuthenticatePage(AuthenticatePage.NEW_PASSWORD)
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryBlue,
                    ),
                ) {
                    Row {
                        Text(
                            "Submit",
                            fontSize = 15.sp,
                            color = backgroundColor
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            painter = painterResource(id = submitIcon),
                            contentDescription = "Submit",
                            modifier = Modifier
                                .size(iconSize),
                            tint = backgroundColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewPassword(
    mainViewModel: MainViewModel,
    setAuthenticatePage: (AuthenticatePage) -> Unit,
) {
    val backgroundColor = AppBackground
    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val shieldIcon = R.drawable.shield_icon
    val shieldCheckIcon = R.drawable.shield_check_icon
    val changePasswdIcon = R.drawable.key_square_2_icon
    val returnIcon = R.drawable.undo_left_icon

    val iconSize = 20.dp
    val textBoxColor = LightColor

    var newPasswd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                OutlinedTextField(
                    value = newPasswd,
                    singleLine = true,
                    onValueChange = { newPasswd = it },
                    label = { Text("New Password") },
                    placeholder = { Text("Enter New Password") },
                    modifier = Modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                OutlinedTextField(
                    value = confirmPasswd,
                    singleLine = true,
                    onValueChange = { confirmPasswd = it },
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
                        )
                    },
                )
            }
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
            Button(
                onClick = {
                    mainViewModel.viewModelScope.launch {
                        mainViewModel.login(username = "sandyblaze")
                    }
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryBlue,
                )
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = changePasswdIcon),
                        contentDescription = "Change Password",
                        modifier = Modifier
                            .size(iconSize),
                        tint = backgroundColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Change Password",
                        fontSize = 15.sp,
                        color = backgroundColor
                    )
                }
            }
        }
    }
}

@Composable
fun Empty() {
}