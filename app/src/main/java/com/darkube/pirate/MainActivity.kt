package com.darkube.pirate

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkube.pirate.components.AppFloatingActionButton
import com.darkube.pirate.components.AppTopBar
import com.darkube.pirate.components.BottomNavBar
import com.darkube.pirate.components.Loading
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.screens.authentication.Authentication
import com.darkube.pirate.screens.Call
import com.darkube.pirate.screens.Chat
import com.darkube.pirate.screens.Group
import com.darkube.pirate.screens.Profile
import com.darkube.pirate.screens.Settings
import com.darkube.pirate.screens.Stories
import com.darkube.pirate.ui.theme.PirateTheme
import com.darkube.pirate.utils.CallsRoute
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.DatabaseProvider
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
                Screen(context = this)
            }
        }
    }
}

@Composable
fun Screen(context: Context) {
    val navController = rememberNavController()
    val database = remember { DatabaseProvider.getInstance(context) }
    val mainViewModel = MainViewModel(navController = navController, dataBase = database)
    mainViewModel.setScreen(ChatRoute.javaClass.name)
    mainViewModel.setAllUserDetails()
    Auth(mainViewModel = mainViewModel)
}

@Composable
fun Auth(mainViewModel: MainViewModel) {
    val userState by mainViewModel.userState.collectAsState()
    if (userState.getOrDefault("logged_in", "false") == "loading") {
        Loading()
    } else if (userState.getOrDefault("logged_in", "false") == "true") {
        MainScreen(mainViewModel = mainViewModel)
    } else {
        Authentication(mainViewModel = mainViewModel)
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
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
            navController = mainViewModel.navController,
            startDestination = ChatRoute,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(1000)) },
        ) {
            composable<ChatRoute> {
                BackHandler {
                    handleBack()
                }
                Chat(
                    modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel,
                )
            }
            composable<GroupsRoute> {
                BackHandler {
                    handleBack()
                }
                Group(
                    modifier = Modifier.padding(innerPadding),
                )
            }
            composable<CallsRoute> {
                BackHandler {
                    handleBack()
                }
                Call(
                    modifier = Modifier.padding(innerPadding),
                )
            }
            composable<StoriesRoute> {
                BackHandler {
                    handleBack()
                }
                Stories(
                    modifier = Modifier.padding(innerPadding),
                )
            }
            composable<SettingsRoute> {
                BackHandler {
                    handleBack()
                }
                Settings(
                    mainViewModel = mainViewModel,
                )
            }
            composable<ProfileRoute> {
                BackHandler {
                    handleBack()
                }
                Profile(
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
