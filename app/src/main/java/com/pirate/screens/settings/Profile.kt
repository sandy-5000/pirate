package com.pirate.screens.settings

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.pirate.R
import com.pirate.components.BasicTopBar
import com.pirate.components.DividerLine
import com.pirate.components.ErrorMessage
import com.pirate.components.UnderLoading
import com.pirate.screens.authentication.AvailableStatus
import com.pirate.services.fetch
import com.pirate.types.ProfileUpdateType
import com.pirate.types.RequestType
import com.pirate.types.Status
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryBlue
import com.pirate.ui.theme.RedColor
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.math.roundToInt

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    bottomModel: Boolean,
    openModel: () -> Unit,
    closeModel: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val iconSize = 20.dp
    val textBoxColor = LightColor
    val textBoxBackground = NavBarBackground
    val imageSize = 92.dp

    val userState by mainViewModel.userState.collectAsState()

    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterNewPassword = remember { FocusRequester() }
    val focusRequesterConfirmPassword = remember { FocusRequester() }
    val focusRequesterOldPassword = remember { FocusRequester() }
    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val saveIcon = R.drawable.icon_file_check
    val editIcon = R.drawable.icon_pen
    val nameIcon = R.drawable.icon_user
    val dnaIcon = R.drawable.icon_dna
    val userNameIcon = R.drawable.icon_user_id
    val emailIcon = R.drawable.icon_mail
    val eyeOpenIcon = R.drawable.icon_eye_open
    val eyeCloseIcon = R.drawable.icon_eye_closed
    val shieldIcon = R.drawable.icon_shield
    val shieldCheckIcon = R.drawable.icon_shield_check
    val updateEmailIcon = R.drawable.icon_pen_new_round
    val changePasswdIcon = R.drawable.icon_key_square_2

    var name by remember { mutableStateOf(userState.getOrDefault("name", "")) }
    val userName = remember { userState.getOrDefault("username", "") }
    var email by remember { mutableStateOf(userState.getOrDefault("email", "")) }
    var newPasswd by remember { mutableStateOf("") }
    var confirmPasswd by remember { mutableStateOf("") }
    var oldPasswd by remember { mutableStateOf("") }
    var showPasswd by remember { mutableStateOf(false) }
    val profileImage = userState.getOrDefault("profile_image", "8").toInt()

    val currentEmail = userState.getOrDefault("email", "")
    var isEmailAvailable by remember { mutableStateOf(Status.AVAILABLE) }

    var isValidEmail by remember { mutableStateOf(true) }
    var isValidPassword by remember { mutableStateOf(true) }
    var isValidConfirmPassword by remember { mutableStateOf(true) }

    var loadingNameUpdate by remember { mutableStateOf(false) }
    var loadingEmailUpdate by remember { mutableStateOf(false) }
    var loadingPasswordUpdate by remember { mutableStateOf(false) }

    val checkAvailability = {
        var makeCall = true
        isEmailAvailable = Status.LOADING
        val url = "api/user/search/$email?type=email"
        if (email.isEmpty() || !isValidEmail) {
            makeCall = false
        }
        if (makeCall) {
            fetch(
                url = url,
                callback = { response: JsonElement ->
                    val error = response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                    if (error.isNotEmpty()) {
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

    val updateDetails = { type: ProfileUpdateType ->
        val url = when (type) {
            ProfileUpdateType.DISPLAY_NAME -> "/api/user/profile?type=DISPLAY_NAME"
            ProfileUpdateType.EMAIL -> "/api/user/profile?type=EMAIL"
            ProfileUpdateType.PASSWORD -> "/api/user/profile?type=PASSWORD"
        }
        val body: JsonObject = buildJsonObject {
            if (ProfileUpdateType.DISPLAY_NAME == type) {
                put("name", name)
            } else if (ProfileUpdateType.EMAIL == type) {
                put("email", email)
            } else if (ProfileUpdateType.PASSWORD == type) {
                put("old_passwd", oldPasswd)
                put("new_passwd", newPasswd)
            }
        }
        val headers = mainViewModel.getHeaders()
        when (type) {
            ProfileUpdateType.DISPLAY_NAME -> loadingNameUpdate = true
            ProfileUpdateType.EMAIL -> loadingEmailUpdate = true
            ProfileUpdateType.PASSWORD -> {
                loadingPasswordUpdate = true
                newPasswd = ""
                confirmPasswd = ""
                oldPasswd = ""
            }
        }
        fetch(
            url = url,
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    val errorMessage = when (type) {
                        ProfileUpdateType.DISPLAY_NAME -> "Error while updating Display Name."
                        ProfileUpdateType.EMAIL -> "Error while updating Email."
                        ProfileUpdateType.PASSWORD -> "Error while updating Password."
                    }
                    when (type) {
                        ProfileUpdateType.DISPLAY_NAME -> loadingNameUpdate = false
                        ProfileUpdateType.EMAIL -> loadingEmailUpdate = false
                        ProfileUpdateType.PASSWORD -> loadingPasswordUpdate = false
                    }
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    return@fetch
                }
                val message = when (type) {
                    ProfileUpdateType.DISPLAY_NAME -> "Display Name Updated Successfully."
                    ProfileUpdateType.EMAIL -> "Email Updated Successfully."
                    ProfileUpdateType.PASSWORD -> "Password Updated Successfully."
                }
                val result: JsonObject = response.jsonObject["result"]?.jsonObject
                    ?: buildJsonObject { emptyMap<String, String>() }
                val token: String = response.jsonObject["token"]?.jsonPrimitive?.contentOrNull ?: ""
                mainViewModel.viewModelScope.launch {
                    mainViewModel.login(userDetails = result, token = token)
                    when (type) {
                        ProfileUpdateType.DISPLAY_NAME -> loadingNameUpdate = false
                        ProfileUpdateType.EMAIL -> loadingEmailUpdate = false
                        ProfileUpdateType.PASSWORD -> loadingPasswordUpdate = false
                    }
                }
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            },
            type = RequestType.PATCH,
            body = body,
            headers = headers,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
    ) {
        Column(
            modifier = modifier
                .imePadding()
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = getProfileImage(profileImage)),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(imageSize)
                                .clip(CircleShape)
                                .clickable(onClick = {
                                    openModel()
                                })
                        )
                        Icon(
                            painter = painterResource(id = updateEmailIcon),
                            contentDescription = "Selected",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(shape = CircleShape)
                                .background(AppBackground)
                                .size(24.dp)
                                .padding(2.dp),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(userName)
                        Text(email, fontSize = 14.sp)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                ) {
                    Text(
                        "Personal Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    Row(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .focusRequester(focusRequesterName)
                                .clip(shape = RoundedCornerShape(32.dp))
                                .background(textBoxBackground),
                            value = name, onValueChange = { name = it },
                            placeholder = { Text("Enter Display Name") },
                            label = { Text("Display Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = textBoxBackground,
                                unfocusedBorderColor = textBoxBackground,
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
                                    painter = painterResource(id = nameIcon),
                                    contentDescription = "name",
                                    modifier = Modifier.size(iconSize),
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = dnaIcon),
                                    contentDescription = "dna",
                                    modifier = Modifier.size(iconSize),
                                )
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    UnderLoading(loadingNameUpdate, "updating display name...")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                updateDetails(ProfileUpdateType.DISPLAY_NAME)
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                            ),
                            enabled = !loadingNameUpdate,
                        )
                        {
                            Icon(
                                painter = painterResource(id = saveIcon),
                                contentDescription = "Save",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Save",
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            DividerLine(verticalPadding = 20.dp, horizontalPadding = 40.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                ) {
                    Text(
                        "Account Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
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
                            label = { Text("username") },
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
                            },
                            trailingIcon = {
                                Spacer(modifier = Modifier.width(iconSize))
                            }
                        )
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
                            placeholder = { Text("Enter Email") },
                            label = { Text("Email") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = Color.White,
                                focusedBorderColor = AppBackground,
                                unfocusedBorderColor = AppBackground,
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
                                    painter = painterResource(id = emailIcon),
                                    contentDescription = "Email",
                                    modifier = Modifier
                                        .size(iconSize),
                                    tint = if (isValidEmail) Color.White else RedColor,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = editIcon),
                                    contentDescription = "edit",
                                    modifier = Modifier.size(iconSize),
                                )
                            }
                        )
                    }
                    ErrorMessage(
                        !isValidEmail,
                        "Invalid Email Format.",
                    )
                    if (isValidEmail && email.isNotEmpty() && email != currentEmail) {
                        val message = when (isEmailAvailable) {
                            Status.AVAILABLE -> "Email is Available to Register."
                            Status.NOT_AVAILABLE -> "Email is Already Registered."
                            Status.LOADING -> "checking availability..."
                        }
                        AvailableStatus(isEmailAvailable, message)
                    }
                    Spacer(Modifier.height(4.dp))
                    UnderLoading(loadingEmailUpdate, "updating email address...")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (email.isNotEmpty() && isValidEmail) {
                                    keyboardController?.hide()
                                    updateDetails(ProfileUpdateType.EMAIL)
                                }
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                            ),
                            enabled = !loadingEmailUpdate
                                    && isValidEmail
                                    && email.isNotEmpty()
                                    && email != currentEmail
                                    && isEmailAvailable == Status.AVAILABLE,
                        )
                        {
                            Icon(
                                painter = painterResource(id = updateEmailIcon),
                                contentDescription = "Update Email",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Update Email",
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            DividerLine(verticalPadding = 20.dp, horizontalPadding = 40.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                ) {
                    Text(
                        "Change Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        OutlinedTextField(
                            modifier = Modifier.focusRequester(focusRequesterNewPassword),
                            value = newPasswd,
                            singleLine = true,
                            onValueChange = {
                                newPasswd = it
                                isValidPassword = it.isEmpty() || it.length >= 8
                            },
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
                    ErrorMessage(
                        !isValidPassword,
                        "Password Must contain minimum 8 characters.",
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.focusRequester(focusRequesterConfirmPassword),
                            value = confirmPasswd,
                            singleLine = true,
                            onValueChange = {
                                confirmPasswd = it
                                isValidConfirmPassword = it.isEmpty() || confirmPasswd == newPasswd
                            },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Confirm your Password") },
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
                    ErrorMessage(
                        !isValidConfirmPassword,
                        "Password Didn't Match.",
                    )
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
                                    painter = painterResource(id = shieldIcon),
                                    contentDescription = "Shield",
                                    modifier = Modifier
                                        .size(iconSize),
                                )
                            },
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    UnderLoading(loadingPasswordUpdate, "updating password...")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (oldPasswd.isNotEmpty() && isValidPassword && isValidConfirmPassword) {
                                    keyboardController?.hide()
                                    updateDetails(ProfileUpdateType.PASSWORD)
                                }
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                            ),
                            enabled = !loadingPasswordUpdate && oldPasswd.isNotEmpty() && isValidPassword && isValidConfirmPassword,
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
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
        BasicTopBar(
            title = "Profile",
            modifier = Modifier.align(Alignment.TopCenter),
            mainViewModel = mainViewModel,
        )
        ProfileBottomModal(
            modifier = Modifier.align(Alignment.BottomCenter),
            mainViewModel = mainViewModel,
            visible = bottomModel,
            closeModel = closeModel,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileBottomModal(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    visible: Boolean,
    closeModel: () -> Unit,
) {
    val cornerShape = 16.dp
    val edgeHeight = 18.dp
    val crossIcon = R.drawable.icon_cross
    val crossIconSize = 28.dp

    val imageSize = 60.dp
    val iconSize = 16.dp
    val checkIcon = R.drawable.icon_check_circle
    val userState by mainViewModel.userState.collectAsState()
    val context = LocalContext.current
    val headers = mainViewModel.getHeaders()
    val profileImage = userState.getOrDefault("profile_image", "5").toInt()
    val scrollState = rememberScrollState()

    val updateProfileImage = { imageIndex: Int ->
        val body: JsonObject = buildJsonObject {
            put("profile_image", imageIndex)
        }
        fetch(
            url = "/api/user/profile?type=PROFILE_IMAGE",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "error while updating profile picture",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@fetch
                }
                val result: JsonObject = response.jsonObject["result"]?.jsonObject
                    ?: buildJsonObject { emptyMap<String, String>() }
                val token: String = response.jsonObject["token"]?.jsonPrimitive?.contentOrNull ?: ""
                closeModel()
                mainViewModel.viewModelScope.launch {
                    mainViewModel.login(userDetails = result, token = token)
                }
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Profile Picture updated...", Toast.LENGTH_LONG).show()
                }
            },
            headers = headers,
            body = body,
            type = RequestType.PATCH
        )
    }

    var offsetY by remember { mutableFloatStateOf(0f) }
    var animationDuration by remember { mutableIntStateOf(0) }
    val maxDrag = 2000f
    val animatedOffset by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(animationDuration)
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.7f)
                    .background(AppBackground)
                    .clickable(onClick = {
                        closeModel()
                    })
            )
            Surface(
                modifier = modifier
                    .offset { IntOffset(0, animatedOffset.roundToInt()) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = cornerShape,
                                topEnd = cornerShape
                            )
                        )
                        .background(AppBackground)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    animationDuration = 0
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    offsetY = (offsetY + dragAmount).coerceIn(0f, maxDrag)
                                },
                                onDragEnd = {
                                    animationDuration = 300
                                    if (offsetY < 400f) {
                                        offsetY = 0f
                                    } else {
                                        closeModel()
                                        offsetY = 0f
                                    }
                                }
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(edgeHeight + 2.dp)
                            .background(LightColor),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(edgeHeight)
                                .clip(
                                    shape = RoundedCornerShape(
                                        topStart = cornerShape,
                                        topEnd = cornerShape
                                    )
                                )
                                .background(AppBackground),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(4.dp)
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(LightColor)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Select your profile picture",
                            color = LightColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        IconButton(onClick = {
                            closeModel()
                        }) {
                            Icon(
                                painter = painterResource(id = crossIcon),
                                contentDescription = "close",
                                modifier = Modifier
                                    .size(crossIconSize)
                                    .clip(shape = CircleShape),
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            repeat(13) { index ->
                                Box(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = getProfileImage(index)),
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .clickable(onClick = {
                                                updateProfileImage(index)
                                            })
                                            .size(imageSize)
                                            .clip(shape = CircleShape),
                                    )
                                    if (index == profileImage) {
                                        Icon(
                                            painter = painterResource(id = checkIcon),
                                            contentDescription = "Selected",
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .clip(shape = CircleShape)
                                                .background(AppBackground)
                                                .size(iconSize),
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
