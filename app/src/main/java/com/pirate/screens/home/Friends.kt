package com.pirate.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirate.R
import com.pirate.components.DataLoading
import com.pirate.types.FriendType
import com.pirate.ui.theme.AppBackground
import com.pirate.ui.theme.LightColor
import com.pirate.ui.theme.NavBarBackground
import com.pirate.ui.theme.PrimaryColor
import com.pirate.utils.ChatRoute
import com.pirate.utils.getProfileImage
import com.pirate.viewModels.MainViewModel

enum class ViewMode {
    LIST, GRID
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Friends(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val friendsScrollState = rememberScrollState()
    val iconSize = 20.dp

    val horizontalPadding = 20.dp
    val searchIcon = R.drawable.icon_search
    val listViewIcon = R.drawable.icon_list
    val gridViewIcon = R.drawable.icon_boxes
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }

    val textBoxBackground = NavBarBackground
    var searchString by remember { mutableStateOf("") }

    val loadingFriends by mainViewModel.requestScreenLoadingFriends.collectAsState()
    val friends by mainViewModel.requestScreenDateFriends.collectAsState()

    val filteredFriends by remember(friends, searchString) {
        mutableStateOf(
            friends.filter { friend ->
                val stringList = friend.name.split(" ") + friend.username
                searchString.isEmpty() || stringList.any { it.startsWith(searchString, ignoreCase = true) }
            }
        )
    }

    LaunchedEffect(Unit) {
        mainViewModel.friendsScreenLoaded()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!loadingFriends && friends.isNotEmpty()) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .padding(top = 60.dp, bottom = 8.dp)
                        .clip(shape = RoundedCornerShape(32.dp))
                        .background(textBoxBackground),
                    value = searchString,
                    onValueChange = { searchString = it },
                    placeholder = { Text("enter text to search") },
                    label = { Text("Search Friends") },
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
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = searchIcon),
                            contentDescription = "searchIcon",
                            modifier = Modifier.size(iconSize),
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 4.dp),
                            onClick = {
                                viewMode = when (viewMode) {
                                    ViewMode.LIST -> ViewMode.GRID
                                    ViewMode.GRID -> ViewMode.LIST
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (viewMode) {
                                        ViewMode.LIST -> gridViewIcon
                                        ViewMode.GRID -> listViewIcon
                                    }
                                ),
                                contentDescription = "viewIcon",
                                modifier = Modifier
                                    .size(iconSize),
                            )
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .verticalScroll(friendsScrollState)
                    .weight(1f),
            ) {
                if (loadingFriends) {
                    DataLoading(
                        durationMillis = 1200,
                        modifier = modifier.weight(1f),
                        message = "Fetching your friends..."
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                } else if (friends.isEmpty()) {
                    EmptyList(message = "You Have No Friends", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(60.dp))
                } else {
                    if (ViewMode.LIST == viewMode) {
                        filteredFriends.forEach { friend ->
                            FriendList(
                                name = friend.name,
                                username = friend.username,
                                userId = friend.id,
                                profileImage = friend.profileImage,
                                mainViewModel = mainViewModel,
                            )
                        }
                    } else if (ViewMode.GRID == viewMode) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            filteredFriends.forEach { friend ->
                                FriendGrid(
                                    name = friend.name,
                                    username = friend.username,
                                    userId = friend.id,
                                    profileImage = friend.profileImage,
                                    mainViewModel = mainViewModel,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun FriendList(
    name: String,
    username: String,
    userId: String,
    profileImage: String,
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 28.dp
    val verticalPadding = 12.dp
    val messageIcon = R.drawable.icon_chat_round_line
    val iconSize = 20.dp
    val imageSize = 48.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
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
fun FriendGrid(
    name: String,
    username: String,
    userId: String,
    profileImage: String,
    mainViewModel: MainViewModel,
) {
    val iconSize = 16.dp
    val imageSize = 72.dp
    val messageIcon = R.drawable.icon_chat_round_line

    Column(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = AppBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(8.dp),
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.2f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = getProfileImage(profileImage.toInt())),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(shape = CircleShape),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = name,
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = username,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Button(
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavBarBackground,
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = messageIcon),
                            contentDescription = "Message",
                            modifier = Modifier
                                .size(iconSize),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Message",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
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
        Text(message, color = LightColor, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}
