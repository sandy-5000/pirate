package com.darkube.pirate.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.components.DividerLine
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.SettingsBottomComponent
import com.darkube.pirate.ui.theme.RedColor
import com.darkube.pirate.utils.InviteFriendsRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.getProfileImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun Settings(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    openBottomModel: (SettingsBottomComponent) -> Job,
) {
    val userState by mainViewModel.userState.collectAsState()
    val scrollState = rememberScrollState()
    val firstName = userState.getOrDefault("first_name", "pirate")
    val lastName = userState.getOrDefault("last_name", "")
    val fullName = "$firstName $lastName".trim()
    val email = userState.getOrDefault("email", "pirate@ship.com")
    val username = userState.getOrDefault("username", "pirate")
    val userBio = userState.getOrDefault("bio", "")
    val profileImage = userState.getOrDefault("profile_image", "8").toInt()

    val internalPadding = 16.dp
    val imageSize = 92.dp
    val logoutButtonColor = RedColor

    val accountIcon = R.drawable.user_icon
    val ratingIcon = R.drawable.heart_icon
    val appearanceIcon = R.drawable.sun_icon
    val chatIcon = R.drawable.chat_round_line_icon
    val storiesIcon = R.drawable.stories_icons
    val notificationsIcon = R.drawable.bell_icon
    val privacyIcon = R.drawable.lock_icon
    val dataAndStorageIcon = R.drawable.pie_icon
    val helpIcon = R.drawable.question_icon
    val inviteFriendsIcon = R.drawable.mail_icon
    val logoutIcon = R.drawable.exit_icon

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
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
                    openBottomModel(SettingsBottomComponent.APPEARANCE)
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
                    openBottomModel(SettingsBottomComponent.CHATS)
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
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable(onClick = {
//
//                })
//                .padding(internalPadding),
//
//            ) {
//            Icon(
//                painter = painterResource(id = storiesIcon),
//                contentDescription = "Stories",
//                modifier = Modifier.padding(start = internalPadding, end = internalPadding)
//            )
//            Text(
//                text = "Stories",
//                color = Color.White,
//            )
//        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    openBottomModel(SettingsBottomComponent.NOTIFICATIONS)
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
                    openBottomModel(SettingsBottomComponent.PRIVACY)
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
                    openBottomModel(SettingsBottomComponent.DATA_AND_STORAGE)
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
                    openBottomModel(SettingsBottomComponent.ABOUT)
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
                    openBottomModel(SettingsBottomComponent.LOGOUT)
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
}
