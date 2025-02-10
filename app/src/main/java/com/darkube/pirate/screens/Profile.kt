package com.darkube.pirate.screens


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.darkube.pirate.R
import com.darkube.pirate.ui.theme.LightColor


@Composable
fun Profile(
    modifier: Modifier = Modifier,
) {
    val topPadding = 90.dp
    val padding = 10.dp
    val internalPadding = 16.dp
    val iconSize = 20.dp
    val textBoxColor = LightColor

    val context = LocalContext.current
    val updateIcon = R.drawable.rotate_right_round_svgrepo_com__3_
    val editIcon = R.drawable.pen_icon
    val firstNameIcon = R.drawable.user_icon
    val lastNameIcon = R.drawable.dna_icon
    val userNameIcon = R.drawable.user_id_icon
    val emailIcon = R.drawable.mail_icon
    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val shieldIcon = R.drawable.shield_icon
    val shieldCheckIcon = R.drawable.shield_check_icon

    var firstName by remember { mutableStateOf("") };
    var lastName by remember { mutableStateOf("") };
    val userName = remember { "Sandy Blaze" };
    var email by remember { mutableStateOf("") };
    var newPasswd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var oldPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = padding + topPadding + internalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(value = firstName, onValueChange = { firstName = it },
                placeholder = { Text("Enter First Name") },
                label = { Text("First Name") },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = editIcon),
                        contentDescription = "edit",
                        modifier = Modifier.size(iconSize),
                    )
                },

                        leadingIcon = {
                    Icon(
                        painter = painterResource(id = firstNameIcon),
                        contentDescription = "firstNameIcon",
                        modifier = Modifier.size(iconSize),
                    )
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(internalPadding),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(value = lastName, onValueChange = { lastName = it },
                placeholder = { Text("Enter Last Name") },
                label = { Text("Last Name") }, trailingIcon = {
                    Icon(
                        painter = painterResource(id = editIcon),
                        contentDescription = "edit",
                        modifier = Modifier.size(iconSize),
                    )
                },
                        leadingIcon = {
                    Icon(
                        painter = painterResource(id = lastNameIcon),
                        contentDescription = "lastNameIcon",
                        modifier = Modifier.size(iconSize),
                    )
                }
            )

        }
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(internalPadding),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = userName,
                onValueChange = {},
                readOnly = true,
                label = { Text("User Name") }
            ,          leadingIcon = {
                    Icon(
                        painter = painterResource(id = userNameIcon),
                        contentDescription = "userNameIcon",
                        modifier = Modifier.size(iconSize),
                    )
                })


        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(internalPadding),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(value = email, onValueChange = { email = it },
                placeholder = { Text("Enter Email") },
                label = { Text("Email") }, trailingIcon = {
                    Icon(
                        painter = painterResource(id = editIcon),
                        contentDescription = "edit",
                        modifier = Modifier.size(iconSize),
                    )
                },     leadingIcon = {
                    Icon(
                        painter = painterResource(id = emailIcon),
                        contentDescription = "emailIcon",
                        modifier = Modifier.size(iconSize),
                    )
                })

        }
        Divider()
        Row(
            modifier = Modifier
               .fillMaxWidth()
                .padding(internalPadding),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = newPasswd,
                singleLine = true,
                onValueChange = { newPasswd = it },
                label = { Text("New Password") },
                placeholder = { Text("Enter New Password") },

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
            modifier = Modifier
               .fillMaxWidth()
                .padding(internalPadding),
           horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = confirmPasswd,
                singleLine = true,
                onValueChange = { confirmPasswd = it },
                label = { Text("Confirm Password") },
                placeholder = { Text("Confirm your Password") },

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(internalPadding),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = oldPasswd,
                singleLine = true,
                onValueChange = { oldPasswd = it },
                label = { Text("Old Password") },
                placeholder = { Text("Enter Old Password") },

                visualTransformation = PasswordVisualTransformation(),
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
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(18.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(internalPadding),
            horizontalArrangement = Arrangement.End

        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show()

                },
            )
            {
                Text(
                    text = "UPDATE",
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = updateIcon),
                    contentDescription = "Accessibility Icon",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                )

            }
        }

    }
}
