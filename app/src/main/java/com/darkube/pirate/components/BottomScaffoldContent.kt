package com.darkube.pirate.components

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.Details
import com.darkube.pirate.types.DetailsKey
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.types.SettingsBottomComponent
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryBlue
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.ui.theme.RedColor
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.services.DatabaseProvider
import com.darkube.pirate.utils.getProfileImage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Composable
fun MainScreenBottomScaffold(
    mainViewModel: MainViewModel,
) {
    val homeScreen by mainViewModel.homeScreenState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .background(AppBackground),
    ) {
        if (HomeScreen.CHATS == homeScreen || HomeScreen.REQUESTS == homeScreen) {
            Search(mainViewModel = mainViewModel)
        }
    }
}

@Composable
fun Search(
    mainViewModel: MainViewModel,
) {
    val textBoxBackground = NavBarBackground
    var searchBar by remember { mutableStateOf("") }
    val iconSize = 20.dp
    val searchIcon = R.drawable.search_icon
    val plainIcon = R.drawable.plain_icon
    val horizontalPadding = 24.dp
    val scrollState = rememberScrollState()
    val headers = mainViewModel.getHeaders()
    val focusRequesterSearch = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var loading by remember { mutableStateOf(false) }
    var users by remember { mutableStateOf(arrayOf<Details>()) }

    val fetchUsers = {
        loading = true
        fetch(
            url = "/api/friends/search/${searchBar.trim()}",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    loading = false
                    return@fetch
                }
                val result: JsonArray = response.jsonObject["result"]?.jsonArray
                    ?: buildJsonArray { emptyArray<JsonObject>() }
                users = result.map { details: JsonElement ->
                    val detailObject = details.jsonObject
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull
                            ?: "",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull
                            ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull
                            ?: "",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull
                            ?: "3").toInt()
                    )
                }.toTypedArray()
                loading = false
            },
            headers = headers,
            type = RequestType.GET,
        )
    }

    val searchUsers = {
        if (searchBar.trim().isNotEmpty()) {
            fetchUsers()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
    ) {
        TextField(
            modifier = Modifier
                .focusRequester(focusRequesterSearch)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(top = 4.dp, bottom = 16.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .background(textBoxBackground),
            value = searchBar,
            onValueChange = { searchBar = it },
            placeholder = { Text("enter username") },
            label = { Text("Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.White,
                focusedBorderColor = textBoxBackground,
                unfocusedBorderColor = textBoxBackground,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    searchUsers()
                }
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = searchIcon),
                    contentDescription = "firstNameIcon",
                    modifier = Modifier.size(iconSize),
                )
            },
            trailingIcon = {
                IconButton(
                    enabled = !loading,
                    onClick = searchUsers
                ) {
                    Icon(
                        painter = painterResource(id = plainIcon),
                        contentDescription = "Search",
                        modifier = Modifier.size(iconSize),
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(scrollState)
                .weight(1f),
        ) {
            if (loading) {
                SearchLoading(durationMillis = 1200, modifier = Modifier.weight(1f))
            } else {
                DisplayAllUsers(users = users, mainViewModel = mainViewModel)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DisplayAllUsers(users: Array<Details>, mainViewModel: MainViewModel) {
    if (users.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("nothing here", fontSize = 14.sp)
        }
    } else {
        users.forEach { detail ->
            DisplayUser(
                firstName = detail.firstName,
                lastName = detail.lastName,
                username = detail.username,
                userId = detail.id,
                profileImage = detail.profileImage,
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
fun DisplayUser(
    firstName: String,
    lastName: String,
    username: String,
    userId: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val requestIcon = R.drawable.arrow_right_icon
    val iconSize = 24.dp
    val imageSize = 48.dp
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Image(
                painter = painterResource(id = getProfileImage(profileImage)),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(imageSize)
                    .clip(shape = CircleShape),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$firstName $lastName",
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = username,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(
            onClick = {
                keyboardController?.hide()
                mainViewModel.navController.navigate(
                    ChatRoute(
                        pirateId = userId,
                        username = username,
                        profileImage = profileImage,
                    )
                )
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(shape = CircleShape)
                .background(NavBarBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryColor)
                    .padding(start = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = requestIcon),
                    contentDescription = "View",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreenBottomScaffold(
    mainViewModel: MainViewModel,
) {
    val imageSize = 60.dp
    val iconSize = 16.dp
    val checkIcon = R.drawable.check_circle_icon
    val userState by mainViewModel.userState.collectAsState()
    val context = LocalContext.current
    val headers = mainViewModel.getHeaders()
    val profileImage = userState.getOrDefault("profile_image", "5").toInt()

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 64.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Select your profile picture", color = LightColor, fontSize = 14.sp)
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
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
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun SettingScreenBottomScaffold(
    mainViewModel: MainViewModel,
) {
    val bottomComponent by mainViewModel.settingsScreenBottomComponent.collectAsState()
    val horizontalPadding = 20.dp
    val logoutIcon = R.drawable.exit_icon
    val refreshIcon = R.drawable.refresh_icon
    val iconSize = 20.dp
    val userState by mainViewModel.userState.collectAsState()
    val context = LocalContext.current

    val logoutUser = {
        mainViewModel.viewModelScope.launch {
            mainViewModel.logout()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(vertical = 20.dp),
    ) {
        when (bottomComponent) {
            SettingsBottomComponent.APPEARANCE -> {
                var isOn by remember { mutableStateOf(true) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Dark Theme",
                        fontSize = 16.sp,
                        color = LightColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Switch(
                        checked = isOn,
                        enabled = false,
                        onCheckedChange = { isOn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NavBarBackground,
                            checkedTrackColor = PrimaryBlue,
                            checkedBorderColor = NavBarBackground,
                            uncheckedThumbColor = NavBarBackground,
                            uncheckedTrackColor = PrimaryColor,
                            uncheckedBorderColor = NavBarBackground,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Currently, the application is only available in dark theme. Appearance options will be added soon!",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Stay tuned for future updates and customization options!",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(120.dp))
            }

            SettingsBottomComponent.CHATS -> {
                val scrollState = rememberScrollState()

                LaunchedEffect(Unit) {
                    mainViewModel.fetchFriends()
                }

                val loadingFriends by mainViewModel.requestScreenLoadingFriends.collectAsState()
                val friends by mainViewModel.requestScreenDateFriends.collectAsState()
                val mutedFriends by mainViewModel.chatNotifications.collectAsState()

                val mutedFriendsIds = remember(mutedFriends) {
                    mutedFriends.filter { it.value == "true" }.keys.toSet()
                }

                val filteredFriends by remember(friends, mutedFriends) {
                    derivedStateOf {
                        friends.filter { (DetailsKey.CHAT_NOTIFICATION.value + ":" + it.id) in mutedFriendsIds }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = horizontalPadding)
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Muted Chats",
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 16.sp,
                        color = LightColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(
                        enabled = !loadingFriends,
                        onClick = {
                            mainViewModel.fetchFriends()
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(shape = CircleShape)
                            .background(NavBarBackground)
                    ) {
                        Icon(
                            painter = painterResource(id = refreshIcon),
                            contentDescription = "reload",
                            modifier = Modifier
                                .size(iconSize),
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth(),
                    ) {
                        filteredFriends.map { details ->
                            DisplayFriend(
                                firstName = details.firstName,
                                lastName = details.lastName,
                                pirateId = details.id,
                                username = details.username,
                                profileImage = details.profileImage,
                                mainViewModel = mainViewModel,
                            )
                        }
                        if (filteredFriends.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .height(320.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    "No user's chat is muted",
                                    color = LightColor,
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }

            SettingsBottomComponent.NOTIFICATIONS -> {
                var loading by remember { mutableStateOf(false) }
                val notificationsOn by remember {
                    derivedStateOf {
                        userState.getOrDefault(DetailsKey.APP_NOTIFICATION.value, "false") != "true"
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Show Notifications",
                        fontSize = 16.sp,
                        color = LightColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Switch(
                        checked = notificationsOn,
                        enabled = !loading,
                        onCheckedChange = {
                            loading = true
                            mainViewModel.viewModelScope.launch {
                                if (it) {
                                    mainViewModel.removeMuteNotifications()
                                } else {
                                    mainViewModel.setMuteNotifications()
                                }
                                loading = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NavBarBackground,
                            checkedBorderColor = NavBarBackground,
                            uncheckedThumbColor = NavBarBackground,
                            uncheckedTrackColor = PrimaryColor,
                            uncheckedBorderColor = NavBarBackground,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "You can set your the notifications preferences.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Customize your notification settings to enable or disable notifications based on your preference.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(120.dp))
            }

            SettingsBottomComponent.DATA_AND_STORAGE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                ) {
                    Text(
                        text = "Data and Storage",
                        fontSize = 16.sp,
                        color = LightColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Used Storage: " + DatabaseProvider.getDatabaseSize(context = context),
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your data is securely stored on your device for fast access and better privacy.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "You can clear your app's storage by selecting 'Clear Data' in the app settings.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(80.dp))
            }

            SettingsBottomComponent.ABOUT -> {
                Text(
                    text = "About Pirate",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 16.sp,
                    color = LightColor,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Hoist the sails and set course for adventure! Pirate lets ye chat with yer crew in a simple, fast, and fun way. No distractions, just smooth sailing!",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Pirates never spill their secrets! With Pirate, ye can chat freely, knowing yer messages stay safe in the treasure chest of privacy.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(60.dp))
            }

            SettingsBottomComponent.LOGOUT -> {
                Text(
                    text = "Logout From this Device",
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .padding(top = horizontalPadding),
                    fontSize = 16.sp,
                    color = LightColor,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Logging out of the application will delete all chat data from your device, and it cannot be recovered.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "If you still want to Log out, click on the below button.",
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    fontSize = 14.sp, color = LightColor,
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            logoutUser()
                        },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedColor,
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = logoutIcon),
                            contentDescription = "Logout",
                            modifier = Modifier
                                .size(iconSize),
                            tint = NavBarBackground
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavBarBackground,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }

            else -> {}
        }
    }
}

@Composable
fun DisplayFriend(
    firstName: String,
    lastName: String,
    username: String,
    pirateId: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val unMuteIcon = R.drawable.volume_loud_icon
    val iconSize = 24.dp
    val imageSize = 48.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Image(
                painter = painterResource(id = getProfileImage(profileImage)),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(imageSize)
                    .clip(shape = CircleShape),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$firstName $lastName",
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = username,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(
            onClick = {
                mainViewModel.viewModelScope.launch {
                    mainViewModel.removeChatNotifications(pirateId = pirateId)
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(shape = CircleShape)
                .background(NavBarBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryColor)
                    .padding(start = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = unMuteIcon),
                    contentDescription = "View",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
            }
        }
    }
}