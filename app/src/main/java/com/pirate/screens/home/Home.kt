package com.pirate.screens.home

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewModelScope
import com.pirate.R
import com.pirate.components.SearchLoading
import com.pirate.services.fetch
import com.pirate.types.Details
import com.pirate.types.HomeScreen
import com.pirate.types.RequestType
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryColor
import com.pirate.ui.theme.PrimaryLightColor
import com.pirate.utils.ChatRoute
import com.pirate.utils.InviteFriendsRoute
import com.pirate.utils.ProfileRoute
import com.pirate.utils.SettingsRoute
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt

enum class RequestsType {
    MESSAGE_REQUESTS, PENDING_REQUESTS
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    bottomModel: Boolean,
    openModel: () -> Unit,
    closeModel: () -> Unit,
) {
    val homeScreen by mainViewModel.homeScreenState.collectAsState()

    var offsetY by remember { mutableFloatStateOf(0f) }
    var animationDuration by remember { mutableIntStateOf(0) }
    val maxDrag = 900f
    val animatedOffset by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(animationDuration)
    )

    var requestsType by remember { mutableStateOf(RequestsType.MESSAGE_REQUESTS) }

    val reloadData = {
        if (HomeScreen.FRIENDS == homeScreen) {
            mainViewModel.fetchFriends()
        } else if (HomeScreen.REQUESTS == homeScreen) {
            mainViewModel.fetchMessageRequests()
            mainViewModel.fetchPendingRequests()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = {
                        animationDuration = 0
                    },
                    onVerticalDrag = { _, dragAmount ->
                        offsetY = (offsetY + dragAmount).coerceIn(0f, maxDrag)
                    },
                    onDragEnd = {
                        animationDuration = 900
                        if (offsetY > 800f) {
                            reloadData()
                        }
                        offsetY = 0f
                    }
                )
            },
    ) {
        when (homeScreen) {
            HomeScreen.CHATS -> Chats(mainViewModel = mainViewModel)

            HomeScreen.REQUESTS -> Requests(
                mainViewModel = mainViewModel,
                requestsType = requestsType,
                setRequestsType = { type ->
                    requestsType = type
                },
            )

            HomeScreen.FRIENDS -> Friends(mainViewModel = mainViewModel)

            else -> {}
        }
        if (HomeScreen.CHATS != homeScreen) {
            Icon(
                painter = painterResource(id = R.drawable.icon_restart),
                modifier = Modifier
                    .offset { IntOffset(0, animatedOffset.roundToInt()) }
                    .rotate(degrees = animatedOffset * 0.5f)
                    .align(Alignment.TopCenter)
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .background(PrimaryLightColor)
                    .padding(8.dp)
                    .padding(bottom = 1.dp),
                contentDescription = "loading",
            )
        }
        TopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            displayTitle = when (homeScreen) {
                HomeScreen.CHATS -> "Chats"
                HomeScreen.REQUESTS -> "Requests"
                HomeScreen.FRIENDS -> "Friends"
                else -> ""
            },
            mainViewModel = mainViewModel,
        )
        BottomActionButton(
            modifier = Modifier
                .padding(bottom = 108.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomEnd),
            visible = HomeScreen.CHATS == homeScreen,
            openBottomModal = openModel,
            icon = R.drawable.icon_search,
        )
        BottomActionButton(
            modifier = Modifier
                .padding(bottom = 108.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomEnd),
            visible = HomeScreen.REQUESTS == homeScreen,
            openBottomModal = {
                if (RequestsType.MESSAGE_REQUESTS == requestsType) {
                    requestsType = RequestsType.PENDING_REQUESTS
                } else if (RequestsType.PENDING_REQUESTS == requestsType) {
                    requestsType = RequestsType.MESSAGE_REQUESTS
                }
            },
            icon = R.drawable.icon_transfer_horizontal_square,
        )
        BottomBar(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter),
            mainViewModel = mainViewModel
        )
        Search(
            modifier = Modifier.align(Alignment.BottomCenter),
            mainViewModel = mainViewModel,
            visible = bottomModel,
            closeModel = closeModel,
        )
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    displayTitle: String,
    mainViewModel: MainViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    val userState by mainViewModel.userState.collectAsState()
    val profileImage = userState.getOrDefault("profile_image", "8").toInt()
    val imageSize = 40.dp
    val optionHeight = 48.dp
    val cornerSize = 16.dp
    val optionsIcon = Icons.Default.MoreVert

    Card(
        colors = CardDefaults.cardColors(containerColor = AppBackground),
        shape = RectangleShape,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RectangleShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(AppBackground)
                .padding(start = 24.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = getProfileImage(profileImage)),
                    contentDescription = "chats",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(shape = CircleShape),
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = displayTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                )
            }
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = optionsIcon, contentDescription = "Menu"
                    )
                }
                if (expanded) {
                    Popup(
                        alignment = Alignment.TopEnd,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(focusable = true)
                    ) {
                        Card(
                            shape = RoundedCornerShape(cornerSize),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = NavBarBackground),
                            modifier = Modifier
                                .width(200.dp)
                                .padding(top = 48.dp, end = 8.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(cornerSize))
                                    .background(PrimaryColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(optionHeight)
                                        .clickable(onClick = {
                                            mainViewModel.viewModelScope.launch {
                                                expanded = false
                                                mainViewModel.markAllRead()
                                            }
                                        })
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Mark all read")
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(optionHeight)
                                        .clickable(onClick = {
                                            expanded = false
                                            mainViewModel.navController.navigate(InviteFriendsRoute)
                                        })
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Invite friends")
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(optionHeight)
                                        .clickable(onClick = {
                                            expanded = false
                                            mainViewModel.navController.navigate(SettingsRoute)
                                        })
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Settings")
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(optionHeight)
                                        .clickable(onClick = {
                                            expanded = false
                                            mainViewModel.navController.navigate(ProfileRoute)
                                        })
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("View Profile")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val iconSize = 24.dp
    val iconButtonHeight = iconSize + 44.dp
    val iconButtonWidth = iconSize + 48.dp
    val cornerSize = 12.dp
    val chatIcon = R.drawable.icon_chat_round_line
    val requestIcon = R.drawable.icon_passport
    val friendsIcon = R.drawable.icon_users_group

    Card(
        shape = RoundedCornerShape(cornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavBarBackground,
            contentColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(cornerSize)),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Column(
                modifier = Modifier
                    .height(iconButtonHeight)
                    .width(iconButtonWidth)
                    .clickable(onClick = {
                        mainViewModel.setHomeScreen(HomeScreen.CHATS)
                    })
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = chatIcon),
                    contentDescription = "chats",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize),
                )
                Text(text = "Chats", fontSize = 13.sp)
            }
            Column(
                modifier = Modifier
                    .height(iconButtonHeight)
                    .width(iconButtonWidth)
                    .clickable(onClick = {
                        mainViewModel.setHomeScreen(HomeScreen.REQUESTS)
                    })
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = requestIcon),
                    contentDescription = "request",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize),
                )
                Text(text = "Requests", fontSize = 13.sp)
            }
            Column(
                modifier = Modifier
                    .height(iconButtonHeight)
                    .width(iconButtonWidth)
                    .clickable(onClick = {
                        mainViewModel.setHomeScreen(HomeScreen.FRIENDS)
                    })
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = friendsIcon),
                    contentDescription = "friends",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize),
                )
                Text(text = "friends", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun BottomActionButton(
    modifier: Modifier,
    visible: Boolean,
    openBottomModal: () -> Unit,
    icon: Int,
) {
    val iconSize = 24.dp
    val cornerSize = 12.dp

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { 3 * it / 2 }),
        exit = slideOutVertically(targetOffsetY = { 3 * it / 2 }),
        modifier = modifier,
    ) {
        Card(
            shape = RoundedCornerShape(cornerSize),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryColor,
                contentColor = Color.White
            ),
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .clickable(onClick = { openBottomModal() })
                    .padding(16.dp),
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "BottomIcon",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun Search(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    visible: Boolean,
    closeModel: () -> Unit,
) {
    val cornerShape = 16.dp
    val edgeHeight = 18.dp

    val textBoxBackground = NavBarBackground
    var searchBar by remember { mutableStateOf("") }
    val iconSize = 20.dp
    val searchIcon = R.drawable.icon_search
    val plainIcon = R.drawable.icon_plain
    val horizontalPadding = 24.dp
    val scrollState = rememberScrollState()
    val headers = mainViewModel.getHeaders()
    val focusRequesterSearch = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var loading by remember { mutableStateOf(false) }
    var users by remember { mutableStateOf(arrayOf<Details>()) }

    var offsetY by remember { mutableFloatStateOf(0f) }
    var animationDuration by remember { mutableIntStateOf(0) }
    val maxDrag = 2000f
    val animatedOffset by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(animationDuration)
    )

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
                        username = detailObject["username"]?.jsonPrimitive?.contentOrNull ?: "",
                        name = detailObject["name"]?.jsonPrimitive?.contentOrNull ?: "",
                        id = detailObject["_id"]?.jsonPrimitive?.contentOrNull ?: "",
                        profileImage = detailObject["profile_image"]?.jsonPrimitive?.contentOrNull
                            ?: "3"
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

    AnimatedVisibility(
        visible = visible,
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
                        .fillMaxHeight(0.8f)
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
                    Spacer(modifier = Modifier.height(8.dp))
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
                                selectionColors = TextSelectionColors(
                                    handleColor = LightColor,
                                    backgroundColor = Color.DarkGray,
                                ),
                                cursorColor = LightColor,
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
                name = detail.name,
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
    name: String,
    username: String,
    userId: String,
    profileImage: String,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val requestIcon = R.drawable.icon_arrow_right
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
