package com.darkube.pirate.screens.authenticate

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.screens.AuthenticatePage
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.ui.theme.SecondaryBlue


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
