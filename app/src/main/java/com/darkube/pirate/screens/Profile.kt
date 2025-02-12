package com.darkube.pirate.screens


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.components.DividerLine
import com.darkube.pirate.components.PixelAvatar
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor


@Composable
fun Profile(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val iconSize = 20.dp
    val textBoxColor = LightColor
    val textBoxBackground = NavBarBackground

    val userState by mainViewModel.userState.collectAsState()

    val focusRequesterFirstName = remember { FocusRequester() }
    val focusRequesterLastName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterNewPassword = remember { FocusRequester() }
    val focusRequesterConfirmPassword = remember { FocusRequester() }
    val focusRequesterOldPassword = remember { FocusRequester() }

    val context = LocalContext.current
    val saveIcon = R.drawable.file_check_icon
    val editIcon = R.drawable.pen_icon
    val firstNameIcon = R.drawable.user_icon
    val lastNameIcon = R.drawable.dna_icon
    val userNameIcon = R.drawable.user_id_icon
    val emailIcon = R.drawable.mail_icon
    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val shieldIcon = R.drawable.shield_icon
    val shieldCheckIcon = R.drawable.shield_check_icon
    val updateEmailIcon = R.drawable.pen_new_round_icon
    val changePasswdIcon = R.drawable.key_square_2_icon

    var firstName by remember { mutableStateOf(userState.getOrDefault("first_name", "")) }
    var lastName by remember { mutableStateOf(userState.getOrDefault("last_name", "")) }
    val userName = remember { userState.getOrDefault("username", "") }
    var email by remember { mutableStateOf(userState.getOrDefault("email", "")) }
    var newPasswd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var oldPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .imePadding()
            .fillMaxHeight()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .height(72.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PixelAvatar(username = userName)
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(userName)
                    Text(email)
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    "Personal Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .focusRequester(focusRequesterFirstName)
                    .clip(shape = RoundedCornerShape(32.dp))
                    .background(textBoxBackground),
                value = firstName, onValueChange = { firstName = it },
                placeholder = { Text("Enter First Name") },
                label = { Text("First Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = textBoxBackground,
                    unfocusedBorderColor = textBoxBackground,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusRequesterLastName.requestFocus()
                    }
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = firstNameIcon),
                        contentDescription = "firstNameIcon",
                        modifier = Modifier.size(iconSize),
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = editIcon),
                        contentDescription = "edit",
                        modifier = Modifier.size(iconSize),
                    )
                },
            )
        }
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .focusRequester(focusRequesterLastName)
                    .clip(shape = RoundedCornerShape(32.dp))
                    .background(textBoxBackground),
                value = lastName, onValueChange = { lastName = it },
                placeholder = { Text("Enter Last Name") },
                label = { Text("Last Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = textBoxBackground,
                    unfocusedBorderColor = textBoxBackground,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = lastNameIcon),
                        contentDescription = "lastNameIcon",
                        modifier = Modifier.size(iconSize),
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = editIcon),
                        contentDescription = "edit",
                        modifier = Modifier.size(iconSize),
                    )
                },
            )

        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                    ),
                )
                {
                    Icon(
                        painter = painterResource(id = saveIcon),
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save",
                        color = Color.White,
                    )
                }
            }
        }
        DividerLine(verticalPadding = 20.dp, horizontalPadding = 40.dp)
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    "Account Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = userName,
                onValueChange = {},
                readOnly = true,
                label = { Text("User Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = AppBackground,
                    unfocusedBorderColor = AppBackground,
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = userNameIcon),
                        contentDescription = "userNameIcon",
                        modifier = Modifier.size(iconSize),
                    )
                })
        }
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier.focusRequester(focusRequesterEmail),
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter Email") },
                label = { Text("Email") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = AppBackground,
                    unfocusedBorderColor = AppBackground,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = emailIcon),
                        contentDescription = "emailIcon",
                        modifier = Modifier.size(iconSize),
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = editIcon),
                        contentDescription = "edit",
                        modifier = Modifier.size(iconSize),
                    )
                },
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                    ),
                )
                {
                    Icon(
                        painter = painterResource(id = updateEmailIcon),
                        contentDescription = "Update Email",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Update Email",
                        color = Color.White,
                    )
                }
            }
        }
        DividerLine(verticalPadding = 20.dp, horizontalPadding = 40.dp)
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    "Change Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequesterNewPassword),
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequesterConfirmPassword),
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
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusRequesterOldPassword.requestFocus()
                    }
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
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequesterOldPassword),
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
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                    ),
                )
                {
                    Icon(
                        painter = painterResource(id = changePasswdIcon),
                        contentDescription = "Change Password",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Change Password",
                        color = Color.White,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
