package com.pirate.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.pirate.R
import com.pirate.components.BasicTopBar
import com.pirate.ui.theme.RedColor
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel

@Composable
fun Settings(
    mainViewModel: MainViewModel,
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

    Box() {
        Column(
            modifier = Modifier
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
        }
        BasicTopBar(
            title = "Settings",
            modifier = Modifier.align(Alignment.TopCenter),
            mainViewModel = mainViewModel,
        )
    }
}