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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.services.fetch
import com.darkube.pirate.types.Details
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.types.RequestType
import com.darkube.pirate.ui.theme.AppBackground
import com.darkube.pirate.ui.theme.NavBarBackground
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.ChatRoute
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
            .fillMaxHeight(0.8f)
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
            .background(NavBarBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Select you profile picture", color = Color.White)
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
        Spacer(modifier = Modifier.height(40.dp))
    }
}
