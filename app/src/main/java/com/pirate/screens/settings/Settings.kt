package com.pirate.screens.settings

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.pirate.R
import com.pirate.components.BasicTopBar
import com.pirate.components.DividerLine
import com.pirate.services.DatabaseProvider
import com.pirate.services.fetch
import com.pirate.types.PreferencesKey
import com.pirate.types.RequestType
import com.pirate.types.SettingsBottomComponent
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryBlue
import com.pirate.ui.theme.PrimaryColor
import com.pirate.ui.theme.RedColor
import com.pirate.utils.InviteFriendsRoute
import com.pirate.utils.PrivacyRoute
import com.pirate.utils.ProfileRoute
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
fun Settings(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    bottomModel: Boolean,
    openModel: () -> Unit,
    closeModel: () -> Unit,
) {
    val userState by mainViewModel.userState.collectAsState()
    val scrollState = rememberScrollState()
    val fullName = userState.getOrDefault("name", "pirate")
    val email = userState.getOrDefault("email", "pirate@ship.com")
    val username = userState.getOrDefault("username", "pirate")
    val userBio = userState.getOrDefault("bio", "")
    val profileImage = userState.getOrDefault("profile_image", "8").toInt()

    val internalPadding = 16.dp
    val imageSize = 92.dp
    val logoutButtonColor = RedColor

    val accountIcon = R.drawable.icon_user
    val ratingIcon = R.drawable.icon_heart
    val appearanceIcon = R.drawable.icon_sun
    val chatIcon = R.drawable.icon_chat_round_line
    val storiesIcon = R.drawable.icon_stories
    val notificationsIcon = R.drawable.icon_bell
    val privacyIcon = R.drawable.icon_lock
    val dataAndStorageIcon = R.drawable.icon_pie
    val helpIcon = R.drawable.icon_question
    val inviteFriendsIcon = R.drawable.icon_mail
    val logoutIcon = R.drawable.icon_exit

    var bottomComponent by remember { mutableStateOf(SettingsBottomComponent.NONE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 0.dp, bottom = 0.dp, start = internalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = getProfileImage(profileImage)),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .padding(start = internalPadding)
                        .size(imageSize)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .padding(start = internalPadding),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (fullName.isNotEmpty()) {
                        Text(
                            text = fullName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = email,
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = username,
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (userBio.isNotEmpty()) {
                        Text(
                            text = userBio,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        mainViewModel.navController.navigate(ProfileRoute)
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = accountIcon),
                    contentDescription = "Account",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Account",
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {

                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = ratingIcon),
                    contentDescription = "Rate Pirate",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Rate Pirate",
                    color = Color.White,
                )
            }
            DividerLine()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        openModel()
                        bottomComponent = SettingsBottomComponent.APPEARANCE
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = appearanceIcon),
                    contentDescription = "Appearance",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Appearance",
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        openModel()
                        bottomComponent = SettingsBottomComponent.CHATS
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = chatIcon),
                    contentDescription = "Chats",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Chats",
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        openModel()
                        bottomComponent = SettingsBottomComponent.NOTIFICATIONS
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = notificationsIcon),
                    contentDescription = "Notifications",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Notifications",
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        mainViewModel.navController.navigate(PrivacyRoute)
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = privacyIcon),
                    contentDescription = "Privacy",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Privacy",
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        openModel()
                        bottomComponent = SettingsBottomComponent.DATA_AND_STORAGE
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = dataAndStorageIcon),
                    contentDescription = "Data and Storage",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Data and Storage",
                    color = Color.White,
                )
            }
            DividerLine()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        openModel()
                        bottomComponent = SettingsBottomComponent.ABOUT
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = helpIcon),
                    contentDescription = "About",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "About",
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            mainViewModel.navController.navigate(InviteFriendsRoute)
                        }
                    )
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = inviteFriendsIcon),
                    contentDescription = "Invite your friends",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding)
                )
                Text(
                    text = "Invite your friends",
                    color = Color.White,
                )
            }
            DividerLine()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        openModel()
                        bottomComponent = SettingsBottomComponent.LOGOUT
                    })
                    .padding(internalPadding),
            ) {
                Icon(
                    painter = painterResource(id = logoutIcon),
                    contentDescription = "Logout",
                    modifier = Modifier.padding(start = internalPadding, end = internalPadding),
                    tint = logoutButtonColor
                )
                Text(
                    text = "Logout",
                    color = logoutButtonColor,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
        }
        BasicTopBar(
            title = "Settings",
            modifier = Modifier.align(Alignment.TopCenter),
            mainViewModel = mainViewModel,
        )
        SettingsBottomModal(
            modifier = Modifier.align(Alignment.BottomCenter),
            mainViewModel = mainViewModel,
            visible = bottomModel,
            closeModel = closeModel,
            settingsComponent = bottomComponent,
        )
    }
}

@Composable
fun SettingsBottomModal(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    visible: Boolean,
    closeModel: () -> Unit,
    settingsComponent: SettingsBottomComponent,
) {
    val cornerShape = 16.dp
    val edgeHeight = 18.dp

    val horizontalPadding = 20.dp
    val logoutIcon = R.drawable.icon_exit
    val refreshIcon = R.drawable.icon_restart
    val iconSize = 20.dp
    val userState by mainViewModel.userState.collectAsState()
    val context = LocalContext.current

    val logoutUser = {
        mainViewModel.viewModelScope.launch {
            mainViewModel.logout()
        }
    }

    var offsetY by remember { mutableFloatStateOf(0f) }
    var animationDuration by remember { mutableIntStateOf(0) }
    val maxDrag = 2000f
    val animatedOffset by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(animationDuration)
    )

    AnimatedVisibility(
        visible = visible && SettingsBottomComponent.NONE != settingsComponent,
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
                    when (settingsComponent) {
                        SettingsBottomComponent.APPEARANCE -> {
                            var isOn by remember { mutableStateOf(true) }
                            Spacer(modifier = Modifier.height(24.dp))
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
                                    friends.filter { (PreferencesKey.MUTED_CHATS.value + ":" + it.id) in mutedFriendsIds }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontalPadding),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Muted Chats",
                                    fontSize = 20.sp,
                                    color = LightColor,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                IconButton(
                                    enabled = !loadingFriends,
                                    onClick = {
                                        mainViewModel.setAllChatNotifications()
                                    },
                                    modifier = Modifier
                                        .clip(shape = CircleShape)
                                        .background(PrimaryColor)
                                        .size(iconSize + 16.dp)
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
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = horizontalPadding),
                                text = "You can Unmute individual users notification preferences.",
                                fontSize = 14.sp, color = LightColor,
                            )

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
                                            name = details.name,
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
                                    userState.getOrDefault(
                                        PreferencesKey.APP_NOTIFICATION.value,
                                        "false"
                                    ) != "true"
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Card(
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = horizontalPadding,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
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
                                                checkedThumbColor = LightColor,
                                                checkedBorderColor = LightColor,
                                                uncheckedThumbColor = LightColor,
                                                uncheckedTrackColor = PrimaryColor,
                                                uncheckedBorderColor = LightColor,
                                            )
                                        )
                                    }
                                }
                            }
                            Card(
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 16.dp
                                    )
                                ) {
                                    Text(
                                        text = "You can set your notifications preferences.",
                                        modifier = Modifier.padding(horizontal = horizontalPadding),
                                        fontSize = 14.sp, color = LightColor,
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "Customize your notification settings to enable or disable notifications based on your preference.",
                                        modifier = Modifier.padding(horizontal = horizontalPadding),
                                        fontSize = 14.sp, color = LightColor,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(80.dp))
                        }

                        SettingsBottomComponent.DATA_AND_STORAGE -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            ) {
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(shape = RoundedCornerShape(8.dp))
                                            .background(NavBarBackground)
                                            .padding(20.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "Storage used: ",
                                            fontSize = 16.sp,
                                            color = LightColor,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = DatabaseProvider.getDatabaseSize(context = context),
                                            fontSize = 16.sp,
                                            color = LightColor,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                            Card(
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 16.dp)
                                ) {
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
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }

                        SettingsBottomComponent.ABOUT -> {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "About Pirate",
                                modifier = Modifier.padding(horizontal = horizontalPadding),
                                fontSize = 20.sp,
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
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Logout From this Device",
                                modifier = Modifier
                                    .padding(horizontal = horizontalPadding)
                                    .padding(top = horizontalPadding),
                                fontSize = 18.sp,
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
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun DisplayFriend(
    name: String,
    username: String,
    pirateId: String,
    profileImage: String,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val unMuteIcon = R.drawable.icon_volume_loud
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
                painter = painterResource(id = getProfileImage(profileImage.toInt())),
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
                    text = name,
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