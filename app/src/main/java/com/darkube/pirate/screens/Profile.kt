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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.components.DividerLine
import com.darkube.pirate.components.ErrorMessage
import com.darkube.pirate.components.PixelAvatar
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.screens.authentication.AvailableStatus
import com.darkube.pirate.screens.authentication.Status
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.RedColor
import com.darkube.pirate.ui.theme.SecondaryBlue
import com.darkube.pirate.screens.authentication.Register
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.utils.fetch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val iconSize = 20.dp
    val textBoxColor = LightColor

    val userState by mainViewModel.userState.collectAsState()

    val context = LocalContext.current
    val updateIcon = R.drawable.rotate_right_round_icon
    val editIcon = R.drawable.pen_icon
    val firstNameIcon = R.drawable.user_icon
    val lastNameIcon = R.drawable.dna_icon
    val userNameIcon = R.drawable.user_id_icon
    val emailIcon = R.drawable.mail_icon
    val eyeOpenIcon = R.drawable.eye_open_icon
    val eyeCloseIcon = R.drawable.eye_closed_icon
    val shieldIcon = R.drawable.shield_icon
    val shieldCheckIcon = R.drawable.shield_check_icon

    var firstName by remember { mutableStateOf(userState.getOrDefault("first_name", "")) }
    var lastName by remember { mutableStateOf(userState.getOrDefault("last_name", "")) }
    val userName = remember { userState.getOrDefault("username", "") }
    var email by remember { mutableStateOf(userState.getOrDefault("email", "")) }
    var newPasswd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var oldPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }
    var isValidEmail by remember { mutableStateOf(true) }
    var isEmailAvailable by remember { mutableStateOf(Status.AVAILABLE) }

    val checkAvailability = {
        var makeCall = true;
        isEmailAvailable = Status.LOADING
        var url = "api/user/search/$email?type=email"
        if (email.isEmpty() || !isValidEmail) {
            makeCall = false
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
                    isEmailAvailable =
                        if (flag == "true") Status.NOT_AVAILABLE else Status.AVAILABLE
                },
                type = RequestType.GET,
            )
        }
    }
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxHeight()
            .imePadding()
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
                .padding(bottom = 8.dp)
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
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                placeholder = { Text("Enter First Name") },
                label = { Text("First Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = textBoxColor,
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
            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                placeholder = { Text("Enter Last Name") },
                label = { Text("Last Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = textBoxColor,
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
        DividerLine(verticalPadding = 20.dp, horizontalPadding = 16.dp)

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
            OutlinedTextField(
                value = userName,
                onValueChange = {},
                readOnly = true,
                label = { Text("User Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = textBoxColor,
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
            OutlinedTextField(
                value = email,
                singleLine = true,
                onValueChange = {
                    val regex = "^[a-zA-Z0-9.]{1,64}@[a-zA-Z0-9]{2,255}.com$".toRegex()
                    if (it.length < 294) {
                        isValidEmail = it.isEmpty() || regex.matches(it)
                        email = it
                        checkAvailability()
                    }
                },
                isError = !isValidEmail,
                label = { Text("User Email") },
                placeholder = { Text("Enter your Email") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedBorderColor = textBoxColor,
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = emailIcon),
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
        DividerLine(verticalPadding = 20.dp, horizontalPadding = 16.dp)
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
                .fillMaxWidth(),
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
                .padding(top = 8.dp)
                .fillMaxWidth(),
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
                        containerColor = SecondaryBlue,
                    ),
                )
                {
                    Text(
                        text = "Update",
                        color = AppBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = updateIcon),
                        contentDescription = "Accessibility Icon",
                        tint = AppBackground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
