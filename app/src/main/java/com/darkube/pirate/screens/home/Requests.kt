package com.darkube.pirate.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.components.Loading
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.LightRedColor
import com.darkube.pirate.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

enum class RequestScreen {
    MESSAGE_REQUESTS, PENDING_REQUESTS, FRIENDS
}

@Composable
fun Requests(
    modifier: Modifier = Modifier,
) {
    val messageRequestsScrollState = rememberScrollState()
    val pendingRequestsScrollState = rememberScrollState()
    val friendsScrollState = rememberScrollState()

    var loadingMessageRequest by remember { mutableStateOf(true) }
    var loadingPendingRequest by remember { mutableStateOf(true) }
    var loadingFriends by remember { mutableStateOf(true) }

    val horizontalScrollState = rememberScrollState()
    val horizontalPadding = 24.dp

    var selectedFilter by remember { mutableStateOf(RequestScreen.MESSAGE_REQUESTS) }

    var requests by remember {
        mutableStateOf(arrayOf<Array<String>>())
    }

    LaunchedEffect(Unit) {
        delay(1000L)
        loadingMessageRequest = false
        delay(1000L)
        loadingPendingRequest = false
        delay(1000L)
        loadingFriends = false
        delay(1000L)
        requests = arrayOf(
            arrayOf("Sandy Blaze", "sandy-blaze.0", ""),
            arrayOf("Cassi Storm", "cassi-storm", ""),
            arrayOf("Show Bitch", "show-bitch", ""),
            arrayOf("Call none", "call.none.1", ""),
            arrayOf("Captain", "captain", ""),
            arrayOf("Deep Seek", "deep-seek.100", ""),
            arrayOf("Twin Braids", "twin-braids", ""),
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(start = horizontalPadding, end = horizontalPadding)
                .horizontalScroll(horizontalScrollState)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            InputChip(
                modifier = Modifier.padding(end = 12.dp),
                selected = RequestScreen.MESSAGE_REQUESTS == selectedFilter,
                onClick = {
                    selectedFilter = RequestScreen.MESSAGE_REQUESTS
                },
                label = { Text("Requests") },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = PrimaryColor,
                )
            )
            InputChip(
                modifier = Modifier.padding(end = 12.dp),
                selected = RequestScreen.PENDING_REQUESTS == selectedFilter,
                onClick = {
                    selectedFilter = RequestScreen.PENDING_REQUESTS
                },
                label = { Text("Pending Requests") },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = PrimaryColor,
                )
            )
            InputChip(
                modifier = Modifier.padding(end = 12.dp),
                selected = RequestScreen.FRIENDS == selectedFilter,
                onClick = {
                    selectedFilter = RequestScreen.FRIENDS
                },
                label = { Text("Friends") },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = PrimaryColor,
                )
            )
        }
        when (selectedFilter) {
            RequestScreen.MESSAGE_REQUESTS -> Column(
                modifier = Modifier
                    .verticalScroll(messageRequestsScrollState)
                    .weight(1f),
            ) {
                if (loadingMessageRequest) {
                    Loading(modifier = modifier.weight(1f))
                } else if (requests.isEmpty()) {
                    EmptyList("No Message Requests", modifier = Modifier.weight(1f))
                }
                requests.forEach { request ->
                    MessageRequest(request[0], request[1], request[2])
                }
                Spacer(modifier = Modifier.height(60.dp))
            }

            RequestScreen.PENDING_REQUESTS -> Column(
                modifier = Modifier
                    .verticalScroll(pendingRequestsScrollState)
                    .weight(1f),
            ) {
                if (loadingPendingRequest) {
                    Loading(modifier = modifier.weight(1f))
                } else if (requests.isEmpty()) {
                    EmptyList("No Pending Requests", modifier = Modifier.weight(1f))
                }
                requests.forEach { request ->
                    PendingRequest(request[0], request[1], request[2])
                }
                Spacer(modifier = Modifier.height(60.dp))
            }

            RequestScreen.FRIENDS -> Column(
                modifier = Modifier
                    .verticalScroll(friendsScrollState)
                    .weight(1f),
            ) {
                if (loadingFriends) {
                    Loading(modifier = modifier.weight(1f))
                } else if (requests.isEmpty()) {
                    EmptyList("You Have No Friends", modifier = Modifier.weight(1f))
                }
                requests.forEach { request ->
                    Friend(request[0], request[1], request[2])
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun MessageRequest(displayName: String, username: String, userId: String) {
    var loading by remember { mutableStateOf(false) }
    val horizontalPadding = 28.dp
    val verticalPadding = 24.dp

    val acceptIcon = R.drawable.check_circle_icon
    val rejectIcon = R.drawable.close_circle_icon
    val iconSize = 24.dp
    val backgroundColor = AppBackground

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
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
        Row(
            modifier = Modifier
                .padding(start = 4.dp),
        ) {
            IconButton(
                onClick = {},
                enabled = !loading,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = PrimaryColor)
                    .size(iconSize + 12.dp),
            ) {
                Icon(
                    painter = painterResource(id = acceptIcon),
                    contentDescription = "Accept",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            IconButton(
                onClick = {},
                enabled = !loading,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = LightRedColor)
                    .size(iconSize + 12.dp),
            ) {
                Icon(
                    painter = painterResource(id = rejectIcon),
                    contentDescription = "Reject",
                    modifier = Modifier
                        .size(iconSize),
                    tint = backgroundColor
                )
            }
        }
    }
}

@Composable
fun PendingRequest(displayName: String, username: String, userId: String) {
    var loading by remember { mutableStateOf(false) }
    val horizontalPadding = 28.dp
    val verticalPadding = 24.dp
    val backgroundColor = AppBackground
    val cancelIcon = R.drawable.forbidden_circle_icon
    val iconSize = 20.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
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
        Button(
            onClick = {},
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

@Composable
fun Friend(displayName: String, username: String, userId: String) {
    var loading by remember { mutableStateOf(false) }
    val horizontalPadding = 28.dp
    val verticalPadding = 24.dp
    val backgroundColor = AppBackground
    val removeIcon = R.drawable.trash_bin_icon
    val iconSize = 20.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
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
        Button(
            onClick = {},
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
                    painter = painterResource(id = removeIcon),
                    contentDescription = "Cancel",
                    modifier = Modifier
                        .size(iconSize),
                    tint = backgroundColor
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    "Remove",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = backgroundColor,
                )
            }
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
        Text(message, color = Color.White)
    }
}