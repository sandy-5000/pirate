package com.darkube.pirate.screens.home

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.HomeScreen
import com.darkube.pirate.types.RequestScreen


@Composable
fun Home(
    modifier: Modifier,
    mainViewModel: MainViewModel,
) {
    val pages = listOf(HomeScreen.CHATS, HomeScreen.REQUESTS)
    val homeScreen by mainViewModel.homeScreenState.collectAsState()
    val requestScreenFilter by mainViewModel.requestScreenFilter.collectAsState()
    var currentPageIndex by remember { mutableIntStateOf(0) }

    mainViewModel.getCurrentRoute()

    LaunchedEffect(currentPageIndex) {
        mainViewModel.setHomeScreen(pages[currentPageIndex])
    }

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { _, dragAmount ->
//                    when {
//                        dragAmount > 90 -> {
//                            if (currentPageIndex > 0) {
//                                currentPageIndex--
//                            }
//                        }
//                        dragAmount < -90 -> {
//                            if (currentPageIndex < pages.size - 1) {
//                                currentPageIndex++
//                            }
//                        }
//                    }
//                }
//            }
//    ) {
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
//    }
}
