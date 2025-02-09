package com.darkube.pirate.screens.authenticate

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
import com.darkube.pirate.screens.AuthenticatePage
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.SecondaryBlue
import kotlinx.coroutines.launch


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
