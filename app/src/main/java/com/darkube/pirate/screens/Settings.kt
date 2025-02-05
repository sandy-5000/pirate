package com.darkube.pirate.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.ui.theme.RedColor
import kotlinx.coroutines.launch

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val userState by mainViewModel.userState.collectAsState()
    val scrollState = rememberScrollState()
    val userName = userState.getOrDefault("user_name", "pirate")

    val topPadding = 68.dp
    val padding = 24.dp
    val internalPadding = 16.dp
    val imageSize = 92.dp
    val logoutButtonColor = RedColor

    val accountIcon = R.drawable.user_id_icon
    val ratingIcon = R.drawable.heart_icon
    val appearanceIcon = R.drawable.sun_icon
    val chatIcon = R.drawable.chat_round_line_icon
    val storiesIcon = R.drawable.stories_icons
    val notificationsIcon = R.drawable.bell_icon
    val privacyIcon = R.drawable.lock_icon
    val dataAndStorageIcon = R.drawable.pie_icon
    val helpIcon = R.drawable.question_icon
    val inviteFriendsIcon = R.drawable.mail_icon
    val logoutIcon = R.drawable.logout_icon

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(top = padding + topPadding + internalPadding),
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
                painter = painterResource(id = R.drawable.default_profile_img),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .padding(start = internalPadding)
                    .size(imageSize)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.padding(start = internalPadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Sandy Blaze",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "+91 00000 00000",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = userName,
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.padding(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {

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
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {

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

                })
                .padding(internalPadding),
        ) {
            Icon(
                painter = painterResource(id = storiesIcon),
                contentDescription = "Stories",
                modifier = Modifier.padding(start = internalPadding, end = internalPadding)
            )
            Text(
                text = "Stories",
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
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {

                })
                .padding(internalPadding),
        ) {
            Icon(
                painter = painterResource(id = helpIcon),
                contentDescription = "Help",
                modifier = Modifier.padding(start = internalPadding, end = internalPadding)
            )
            Text(
                text = "Help",
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
                painter = painterResource(id = inviteFriendsIcon),
                contentDescription = "Invite your friends",
                modifier = Modifier.padding(start = internalPadding, end = internalPadding)
            )
            Text(
                text = "Invite your friends",
                color = Color.White,
            )
        }


        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    mainViewModel.viewModelScope.launch {
                        mainViewModel.logout()
                    }
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

@Composable
fun Divider() {
    val verticalPadding = 8.dp
    val horizontalPadding = 16.dp

    Row(
        modifier = Modifier.padding(
            start = horizontalPadding,
            end = horizontalPadding,
            top = verticalPadding,
            bottom = verticalPadding
        )
    ) {
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}