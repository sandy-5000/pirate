package com.darkube.pirate.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.HomeScreen


@Composable
fun Home(
    modifier: Modifier,
    mainViewModel: MainViewModel,
) {
    val homeScreen by mainViewModel.homeScreenState.collectAsState()

    when (homeScreen) {
        HomeScreen.CHATS -> Chat(
            mainViewModel = mainViewModel,
            modifier = modifier,
        )

        HomeScreen.REQUESTS -> Requests(
            mainViewModel = mainViewModel,
            modifier = modifier,
        )

        HomeScreen.CALLS -> Call(
            modifier = modifier,
        )

        HomeScreen.STORIES -> Stories(
            modifier = modifier
        )
    }
}
