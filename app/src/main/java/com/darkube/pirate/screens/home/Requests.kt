package com.darkube.pirate.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.R
import com.darkube.pirate.components.DataLoading
import com.darkube.pirate.components.DividerLine
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.Details
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.LightRedColor
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.getProfileImage
import com.darkube.pirate.utils.getRouteId
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

enum class RequestScreen {
    MESSAGE_REQUESTS, PENDING_REQUESTS, FRIENDS
}

@Composable
fun Requests(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
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

    var requests by remember { mutableStateOf(arrayOf<Details>()) }
    var pendings by remember { mutableStateOf(arrayOf<Details>()) }
    var friends by remember { mutableStateOf(arrayOf<Details>()) }

    val headers = mainViewModel.getHeaders()

    val fetchMessageRequests = {
        loadingMessageRequest = true
        fetch(
            url = "/api/user/message-requests",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    loadingMessageRequest = false
                    return@fetch
                }
                val result: JsonArray = response.jsonObject["result"]?.jsonArray
                    ?: buildJsonArray { emptyArray<String>() }
                requests = result.map { details ->
                    val detailObject = details.jsonObject["sender_id"]?.jsonObject
                        ?: buildJsonObject { emptyMap<String, String>() }
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "N/A",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull ?: "2").toInt()
                    )
                }.toTypedArray()
                loadingMessageRequest = false
            },
            headers = headers,
            type = RequestType.GET,
        )
    }

    val fetchPendingRequests = {
        loadingPendingRequest = true
        fetch(
            url = "/api/user/pending-requests",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    loadingPendingRequest = false
                    return@fetch
                }
                val result: JsonArray = response.jsonObject["result"]?.jsonArray
                    ?: buildJsonArray { emptyArray<JsonObject>() }
                pendings = result.map { details ->
                    val detailObject = details.jsonObject["receiver_id"]?.jsonObject
                        ?: buildJsonObject { emptyMap<String, String>() }
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "N/A",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull ?: "10").toInt()
                    )
                }.toTypedArray()
                loadingPendingRequest = false
            },
            headers = headers,
            type = RequestType.GET,
        )
    }

    val fetchFriends = {
        loadingFriends = true
        fetch(
            url = "/api/user/friends",
            callback = { response: JsonElement ->
                val error =
                    response.jsonObject["error"]?.jsonPrimitive?.contentOrNull ?: ""
                if (error.isNotEmpty()) {
                    loadingFriends = false
                    return@fetch
                }
                val result: JsonObject = response.jsonObject["result"]?.jsonObject
                    ?: buildJsonObject { emptyMap<String, JsonObject>() }
                val friendsList: JsonArray = result["friends"]?.jsonArray
                    ?: buildJsonArray { emptyArray<JsonObject>() }
                friends = friendsList.map { details ->
                    val detailObject = details.jsonObject
                    Details(
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        firstName = detailObject["first_name"]?.jsonPrimitive?.contentOrNull
                            ?: "N/A",
                        lastName = detailObject["last_name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "N/A",
                        profileImage = (detailObject["profile_image"]?.jsonPrimitive?.contentOrNull ?: "3").toInt()
                    )
                }.toTypedArray()
                loadingFriends = false
            },
            headers = headers,
            type = RequestType.GET,
        )
    }

    LaunchedEffect(Unit) {
        fetchMessageRequests()
        fetchPendingRequests()
        fetchFriends()
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
            horizontalArrangement = Arrangement.Start,
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
        DividerLine(horizontalPadding = 32.dp)
        when (selectedFilter) {
            RequestScreen.MESSAGE_REQUESTS -> Column(
                modifier = Modifier
                    .verticalScroll(messageRequestsScrollState)
                    .weight(1f),
            ) {
                if (loadingMessageRequest) {
                    DataLoading(durationMillis = 2000, modifier = modifier.weight(1f))
                } else if (requests.isEmpty()) {
                    EmptyList("No Message Requests", modifier = Modifier.weight(1f))
                }
                requests.forEach { messageRequest ->
                    MessageRequest(
                        displayName = messageRequest.firstName + " " + messageRequest.lastName,
                        username = messageRequest.username,
                        userId = messageRequest.id,
                        profileImage = messageRequest.profileImage,
                        mainViewModel = mainViewModel,
                        reload = {
                            fetchMessageRequests()
                            fetchFriends()
                        },
                    )
                }
                Spacer(modifier = Modifier.height(60.dp))
            }

            RequestScreen.PENDING_REQUESTS -> Column(
                modifier = Modifier
                    .verticalScroll(pendingRequestsScrollState)
                    .weight(1f),
            ) {
                if (loadingPendingRequest) {
                    DataLoading(durationMillis = 2000, modifier = modifier.weight(1f))
                } else if (pendings.isEmpty()) {
                    EmptyList("No Pending Requests", modifier = Modifier.weight(1f))
                }
                pendings.forEach { pendingRequest ->
                    PendingRequest(
                        displayName = pendingRequest.firstName + " " + pendingRequest.lastName,
                        username = pendingRequest.username,
                        userId = pendingRequest.id,
                        profileImage = pendingRequest.profileImage,
                        mainViewModel = mainViewModel,
                        reload = {
                            fetchPendingRequests()
                            fetchFriends()
                        },
                    )
                }
                Spacer(modifier = Modifier.height(60.dp))
            }

            RequestScreen.FRIENDS -> Column(
                modifier = Modifier
                    .verticalScroll(friendsScrollState)
                    .weight(1f),
            ) {
                if (loadingFriends) {
                    DataLoading(durationMillis = 2000, modifier = modifier.weight(1f))
                } else if (friends.isEmpty()) {
                    EmptyList("You Have No Friends", modifier = Modifier.weight(1f))
                }
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
    val verticalPadding = 24.dp
    val messageIcon = R.drawable.chat_round_line_icon
    val iconSize = 20.dp

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
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    mainViewModel.navController.navigate(ChatRoute(username))
                    mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
                },
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
                        painter = painterResource(id = messageIcon),
                        contentDescription = "Message",
                        modifier = Modifier
                            .size(iconSize),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Message",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
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
