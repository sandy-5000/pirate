package com.darkube.pirate.screens.home

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.components.DataLoading
import com.darkube.pirate.components.DividerLine
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.FriendType
import com.darkube.pirate.types.RequestScreen
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.LightRedColor
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.getProfileImage
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Composable
fun Requests(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val requestsScrollState = rememberScrollState()
    val friendsScrollState = rememberScrollState()

    val horizontalPadding = 24.dp
    val minTabHeight = 220.dp

    val selectedFilter by mainViewModel.requestScreenFilter.collectAsState()

    val loadingMessageRequest by mainViewModel.requestScreenLoadingRequests.collectAsState()
    val loadingPendingRequest by mainViewModel.requestScreenLoadingPendings.collectAsState()
    val loadingFriends by mainViewModel.requestScreenLoadingFriends.collectAsState()
    val requests by mainViewModel.requestScreenDateRequests.collectAsState()
    val pendings by mainViewModel.requestScreenDatePendings.collectAsState()
    val friends by mainViewModel.requestScreenDateFriends.collectAsState()

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var shouldRefresh by remember { mutableStateOf(false) }
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) dragOffset.coerceAtMost(300f) else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            if (selectedFilter == RequestScreen.REQUESTS) {
                mainViewModel.fetchMessageRequests()
                mainViewModel.fetchPendingRequests()
            } else if (selectedFilter == RequestScreen.FRIENDS) {
                mainViewModel.fetchFriends()
            }
            delay(2000)
            shouldRefresh = false
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.requestScreenLoaded()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        dragOffset += dragAmount * 0.4f
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        if (dragOffset > 250) {
                            shouldRefresh = true
                        }
                        dragOffset = 0f
                    }
                )
            },
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier
                    .padding(start = horizontalPadding, end = horizontalPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                InputChip(
                    selected = RequestScreen.REQUESTS == selectedFilter,
                    onClick = {
                        mainViewModel.setRequestScreenFilter(RequestScreen.REQUESTS)
                    },
                    label = { Text("Requests") },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = PrimaryColor,
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                InputChip(
                    selected = RequestScreen.FRIENDS == selectedFilter,
                    onClick = {
                        mainViewModel.setRequestScreenFilter(RequestScreen.FRIENDS)
                    },
                    label = { Text("Friends") },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = PrimaryColor,
                    )
                )
            }
            DividerLine(horizontalPadding = 100.dp)
            when (selectedFilter) {
                RequestScreen.REQUESTS -> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(requestsScrollState)
                            .weight(1f),
                    ) {
                        Text(
                            text = "Message Requests",
                            modifier = Modifier
                                .padding(horizontal = horizontalPadding),
                            fontWeight = FontWeight.SemiBold,
                        )
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
                                    displayName = messageRequest.firstName + " " + messageRequest.lastName,
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
                        Text(
                            text = "Pending Requests",
                            modifier = Modifier
                                .padding(horizontal = horizontalPadding),
                            fontWeight = FontWeight.SemiBold,
                        )
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
                                    displayName = pendingRequest.firstName + " " + pendingRequest.lastName,
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
                    }
                }

                RequestScreen.FRIENDS -> Column(
                    modifier = Modifier
                        .verticalScroll(friendsScrollState)
                        .weight(1f),
                ) {
                    if (loadingFriends) {
                        DataLoading(durationMillis = 1200, modifier = modifier.weight(1f))
                    } else if (friends.isEmpty()) {
                        EmptyList("You Have No Friends", modifier = Modifier.weight(1f))
                    } else {
                        friends.forEach { friend ->
                            Friend(
                                displayName = friend.firstName + " " + friend.lastName,
                                username = friend.username,
                                userId = friend.id,
                                profileImage = friend.profileImage,
                                mainViewModel = mainViewModel,
                            )
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
            DividerLine(horizontalPadding = 100.dp)
        }
        if (animatedOffset > 0) {
            Box(
                modifier = Modifier
                    .padding(top = 40.dp + animatedOffset.dp)
                    .align(Alignment.TopCenter)
                    .clip(shape = CircleShape)
                    .background(PrimaryColor)
                    .padding(4.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.spinner_icon),
                    modifier = Modifier
                        .rotate(degrees = animatedOffset * 4f)
                        .size(28.dp),
                    contentDescription = "loading",
                )
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

    val acceptIcon = R.drawable.check_circle_icon
    val rejectIcon = R.drawable.close_circle_icon
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
    val cancelIcon = R.drawable.forbidden_circle_icon
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
fun Friend(
    displayName: String,
    username: String,
    userId: String,
    profileImage: Int,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 28.dp
    val verticalPadding = 12.dp
    val messageIcon = R.drawable.chat_round_line_icon
    val iconSize = 20.dp
    val imageSize = 48.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
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
                    text = displayName,
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
                mainViewModel.setChatScreen(FriendType.INVALID)
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
                    .background(PrimaryColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = messageIcon),
                    contentDescription = "Message",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
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

@Composable
fun EmptyList(message: String = "Empty List", modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = LightColor, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}
