package com.pirate.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirate.R
import com.pirate.components.DataLoading
import com.pirate.services.fetch
import com.pirate.types.RequestType
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightRedColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryColor
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Composable
fun Requests(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val requestsScrollState = rememberScrollState()
    val horizontalPadding = 24.dp
    val minTabHeight = 220.dp

    val loadingMessageRequest by mainViewModel.requestScreenLoadingRequests.collectAsState()
    val loadingPendingRequest by mainViewModel.requestScreenLoadingPendings.collectAsState()
    val requests by mainViewModel.requestScreenDateRequests.collectAsState()
    val pendings by mainViewModel.requestScreenDatePendings.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.requestScreenLoaded()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(requestsScrollState)
                    .weight(1f),
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Message Requests",
                        modifier = Modifier
                            .padding(16.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (loadingMessageRequest) {
                    DataLoading(
                        durationMillis = 1200, modifier = Modifier
                            .fillMaxWidth()
                            .height(minTabHeight)
                    )
                } else if (requests.isEmpty()) {
                    EmptyList(
                        "No Message Requests",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(minTabHeight)
                    )
                } else {
                    requests.forEach { messageRequest ->
                        MessageRequest(
                            displayName = messageRequest.name,
                            username = messageRequest.username,
                            userId = messageRequest.id,
                            profileImage = messageRequest.profileImage,
                            mainViewModel = mainViewModel,
                            reload = {
                                mainViewModel.fetchMessageRequests()
                                mainViewModel.fetchFriends()
                            },
                        )
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Pending Requests",
                        modifier = Modifier
                            .padding(16.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (loadingPendingRequest) {
                    DataLoading(
                        durationMillis = 1200, modifier = Modifier
                            .fillMaxWidth()
                            .height(minTabHeight)
                    )
                } else if (pendings.isEmpty()) {
                    EmptyList(
                        "No Pending Requests",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(minTabHeight)
                    )
                } else {
                    pendings.forEach { pendingRequest ->
                        PendingRequest(
                            displayName = pendingRequest.name,
                            username = pendingRequest.username,
                            userId = pendingRequest.id,
                            profileImage = pendingRequest.profileImage,
                            mainViewModel = mainViewModel,
                            reload = {
                                mainViewModel.fetchPendingRequests()
                                mainViewModel.fetchFriends()
                            },
                        )
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun MessageRequest(
    displayName: String,
    username: String,
    userId: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
    reload: () -> Unit
) {
    var loading by remember { mutableStateOf(false) }
    val horizontalPadding = 28.dp
    val verticalPadding = 24.dp

    val acceptIcon = R.drawable.icon_check_circle
    val rejectIcon = R.drawable.icon_close_circle
    val iconSize = 24.dp
    val backgroundColor = AppBackground

    val headers = mainViewModel.getHeaders()
    val body = buildJsonObject {
        put("sender_id", userId)
    }

    Column(
        modifier = Modifier
            .clickable(onClick = {})
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = displayName,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = username, fontSize = 14.sp)
        }
        UserProfile(
            displayName = displayName,
            username = username,
            profileImage = profileImage,
            horizontalPadding = 0.dp,
            verticalPadding = 0.dp,
            imageSize = 60.dp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = {
                    loading = true
                    fetch(
                        url = "/api/friends/reject",
                        callback = { response: JsonElement ->
                            Log.d("api-res", response.toString())
                            reload()
                            loading = false
                        },
                        body = body,
                        headers = headers,
                        type = RequestType.POST,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightRedColor,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = rejectIcon),
                        contentDescription = "Reject",
                        modifier = Modifier
                            .size(iconSize),
                        tint = backgroundColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Reject",
                        fontSize = 15.sp,
                        color = backgroundColor,
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Button(
                onClick = {
                    loading = true
                    fetch(
                        url = "/api/friends/accept",
                        callback = { response: JsonElement ->
                            Log.d("api-res", response.toString())
                            reload()
                            loading = false
                        },
                        body = body,
                        headers = headers,
                        type = RequestType.POST,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = acceptIcon),
                        contentDescription = "Accept",
                        modifier = Modifier
                            .size(iconSize),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Accept",
                        fontSize = 15.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
fun PendingRequest(
    displayName: String,
    username: String,
    userId: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
    reload: () -> Unit,
) {
    var loading by remember { mutableStateOf(false) }
    val horizontalPadding = 28.dp
    val verticalPadding = 24.dp
    val backgroundColor = AppBackground
    val cancelIcon = R.drawable.icon_forbidden_circle
    val iconSize = 20.dp

    val headers = mainViewModel.getHeaders()
    val body = buildJsonObject {
        put("receiver_id", userId)
    }

    Column(
        modifier = Modifier
            .clickable(onClick = {})
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .fillMaxWidth(),
    ) {
        UserProfile(
            displayName = displayName,
            username = username,
            profileImage = profileImage,
            horizontalPadding = 0.dp,
            verticalPadding = 0.dp,
            imageSize = 60.dp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = {
                    loading = true
                    fetch(
                        url = "/api/friends/cancel",
                        callback = { response: JsonElement ->
                            Log.d("api-res", response.toString())
                            reload()
                            loading = false
                        },
                        body = body,
                        headers = headers,
                        type = RequestType.POST,
                    )
                },
                enabled = !loading,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightRedColor,
                ),
                contentPadding = PaddingValues(start = 8.dp, end = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = cancelIcon),
                        contentDescription = "Cancel",
                        modifier = Modifier
                            .size(iconSize),
                        tint = backgroundColor,
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Cancel",
                        fontSize = 15.sp,
                        color = backgroundColor,
                    )
                }
            }
        }
    }
}

@Composable
fun UserProfile(
    displayName: String,
    username: String,
    profileImage: Int,
    verticalPadding: Dp = 4.dp,
    horizontalPadding: Dp = 20.dp,
    imageSize: Dp = 92.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageSize + 8.dp)
            .padding(vertical = verticalPadding, horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = getProfileImage(profileImage)),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = displayName,
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
        }
    }
}