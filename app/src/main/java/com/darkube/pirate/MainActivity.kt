package com.darkube.pirate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkube.pirate.components.AppFloatingActionButton
import com.darkube.pirate.components.AppTopBar
import com.darkube.pirate.components.BottomNavBar
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.screens.Authentication
import com.darkube.pirate.screens.Call
import com.darkube.pirate.screens.Chat
import com.darkube.pirate.screens.Group
import com.darkube.pirate.screens.Profile
import com.darkube.pirate.screens.Settings
import com.darkube.pirate.screens.Stories
import com.darkube.pirate.ui.theme.PirateTheme
import com.darkube.pirate.utils.CallsRoute
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.GroupsRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.SettingsRoute
import com.darkube.pirate.utils.StoriesRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PirateTheme {
                Screen()
            }
        }
    }
}

@Composable
fun Screen() {
    val flag = true
    if (flag) {
        MainScreen()
    } else {
        Authentication()
    }
}


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainViewModel = MainViewModel(navController = navController)
    mainViewModel.setScreen(ChatRoute.javaClass.name)

    fun handleBack() {
        mainViewModel.navController.popBackStack()
        if (ChatRoute.javaClass.name != mainViewModel.currentScreen) {
            mainViewModel.navController.currentDestination?.let {
                mainViewModel.navigate(it)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(mainViewModel = mainViewModel) },
        bottomBar = { BottomNavBar(mainViewModel = mainViewModel) },
        floatingActionButton = { AppFloatingActionButton(mainViewModel = mainViewModel) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ChatRoute,
        ) {
            composable<ChatRoute> {
                BackHandler {
                    handleBack()
                }
                Chat(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<GroupsRoute> {
                BackHandler {
                    handleBack()
                }
                Group(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<CallsRoute> {
                BackHandler {
                    handleBack()
                }
                Call(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<StoriesRoute> {
                BackHandler {
                    handleBack()
                }
                Stories(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<SettingsRoute> {
                BackHandler {
                    handleBack()
                }
                Settings(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
            composable<ProfileRoute> {
                BackHandler {
                    handleBack()
                }
                Profile(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PirateTheme {
        Screen()
    }
}