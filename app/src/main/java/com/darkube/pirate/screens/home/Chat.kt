package com.darkube.pirate.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkube.pirate.components.PixelAvatar
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.ui.theme.PrimaryColor
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.getRouteId

enum class ChatList {
    PIRATES, CREWS
}

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val horizontalPadding = 24.dp

    var selectedFilter by remember { mutableStateOf(ChatList.PIRATES) }

    val mateys = listOf(
        listOf("sandy-blaze.0", "Hey, what are you doing?"),
        listOf("jhema.7", "Nani?"),
        listOf("kakarot", "Hey, how strong are we talking?"),
        listOf("luffy", "I will become the Pirate King."),
        listOf("naruto", "I will become the Hokage."),
        listOf("captain", "The time has come my CREW, charge..."),
        listOf("cassi-storm", "doing something?"),
        listOf("twin-braids", "Hello, what are you doing?"),
        listOf("abs-zero", "Let's freeze the whole world."),
        listOf("minipixel", "Hey, what are you doing?"),
    )

    val crews = listOf(
        listOf("universe-7", "Hey, how strong are we talking?"),
        listOf("leaf-village", "I will become the Hokage."),
        listOf("ua-high", "Have no fear cause I am here."),
        listOf("hunter-association", "We got a new S-rank hunter."),
    )

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxHeight(),
    ) {
        Row(
            modifier = Modifier
                .padding(start = horizontalPadding, end = horizontalPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            InputChip(
                modifier = Modifier.padding(end = 12.dp),
                selected = selectedFilter == ChatList.PIRATES,
                onClick = {
                    selectedFilter = ChatList.PIRATES
                },
                label = { Text("Pirates") },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = PrimaryColor,
                )
            )
            InputChip(
                modifier = Modifier.padding(end = 12.dp),
                selected = selectedFilter == ChatList.CREWS,
                onClick = {
                    selectedFilter = ChatList.CREWS
                },
                label = { Text("Crews") },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = PrimaryColor,
                )
            )
        }
        when (selectedFilter) {
            ChatList.PIRATES ->
                mateys.forEach { details ->
                    ChatRow(details[0], details[1], mainViewModel)
                }

            ChatList.CREWS ->
                crews.forEach { details ->
                    ChatRow(details[0], details[1], mainViewModel)
                }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ChatRow(
    username: String,
    lastMessage: String = "",
    mainViewModel: MainViewModel,
) {
    val horizontalPadding = 24.dp

    Row(
        modifier = Modifier
            .clickable(onClick = {
                mainViewModel.navController.navigate(ChatRoute(pirateId = username))
                mainViewModel.setScreen(getRouteId(mainViewModel.navController.currentDestination))
            })
            .padding(start = horizontalPadding, end = horizontalPadding)
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PixelAvatar(username = username)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = username,
                color = Color.LightGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = lastMessage,
                color = Color.LightGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
