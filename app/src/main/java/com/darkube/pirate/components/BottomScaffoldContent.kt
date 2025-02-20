package com.darkube.pirate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.darkube.pirate.utils.getRouteId
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
            SearchUser(mainViewModel = mainViewModel)
        }
    }
}

@Composable
fun SearchUser(
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

    var loading by remember { mutableStateOf(false) }
    var users by remember { mutableStateOf(arrayOf<Details>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
    ) {
        TextField(
            modifier = Modifier
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
                imeAction = ImeAction.Next
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
                    onClick = {
                        if (searchBar.trim().isEmpty()) {
                            return@IconButton
                        }
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
                                            ?: ""
                                    )
                                }.toTypedArray()
                                loading = false
                            },
                            headers = headers,
                            type = RequestType.GET,
                        )
                    }
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
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp
    val requestIcon = R.drawable.arrow_right_icon
    val iconSize = 20.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {})
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
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
                    painter = painterResource(id = requestIcon),
                    contentDescription = "View",
                    modifier = Modifier
                        .size(iconSize),
                    tint = Color.White
                )
                Text(
                    "View",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }
    }
}