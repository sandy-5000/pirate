package com.darkube.pirate.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.SecondaryBlue

@Composable
fun Authentication() {
    val backgroundColor = AppBackground
    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val userIdIcons = R.drawable.user_id_icon
    val shieldIcon = R.drawable.shield_icon
    val loginIcon = R.drawable.login_icon
    val iconSize = 20.dp
    val textBoxColor = LightColor

    var username by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
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
                        Image(
                            painter = painterResource(id = userIdIcons),
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
                    visualTransformation = if (showPasswd) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        focusedBorderColor = textBoxColor,
                    ),
                    leadingIcon = {
                        Image(
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
                            Image(
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
            Button(
                onClick = {},
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryBlue,
                )
            ) {
                Row {
                    Image(
                        painter = painterResource(id = loginIcon),
                        contentDescription = "User ID",
                        modifier = Modifier
                            .size(iconSize),
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