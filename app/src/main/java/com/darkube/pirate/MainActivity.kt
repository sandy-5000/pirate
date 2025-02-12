package com.darkube.pirate

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darkube.pirate.components.AppFloatingActionButton
import com.darkube.pirate.components.AppTopBar
import com.darkube.pirate.components.BottomNavBar
import com.darkube.pirate.components.Loading
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.screens.authentication.Authentication
import com.darkube.pirate.screens.Conversation
import com.darkube.pirate.screens.Profile
import com.darkube.pirate.screens.Settings
import com.darkube.pirate.screens.home.Home
import com.darkube.pirate.ui.theme.PirateTheme
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.DatabaseProvider
import com.darkube.pirate.utils.EmptyRoute
import com.darkube.pirate.utils.HomeRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.SettingsRoute

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
    mainViewModel.setScreen(HomeRoute.javaClass.name)
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
        if (HomeRoute.javaClass.name != mainViewModel.currentScreen) {
            mainViewModel.navController.currentDestination?.let {
                mainViewModel.navigate(it)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        NavHost(
            navController = mainViewModel.navController,
            startDestination = HomeRoute,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 2 },
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 2 },
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                )
            },
        ) {
            composable<HomeRoute> {
                BackHandler {
                    handleBack()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppTopBar(mainViewModel = mainViewModel, isMainScreen = true) },
                    bottomBar = { BottomNavBar(mainViewModel = mainViewModel) },
                    floatingActionButton = { AppFloatingActionButton(mainViewModel = mainViewModel) }
                ) { innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding),
                        mainViewModel = mainViewModel,
                    )
                }
            }
            composable<SettingsRoute> {
                BackHandler {
                    handleBack()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            mainViewModel = mainViewModel,
                            displayTitle = "Settings",
                            isMainScreen = false
                        )
                    },
                ) { innerPadding ->
                    Settings(
                        mainViewModel = mainViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            composable<ProfileRoute> {
                BackHandler {
                    handleBack()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            mainViewModel = mainViewModel,
                            displayTitle = "Profile",
                            isMainScreen = false
                        )
                    },
                ) { innerPadding ->
                    Profile(
                        modifier = Modifier.padding(innerPadding),
                        mainViewModel = mainViewModel,
                    )
                }
            }
            composable<ChatRoute> {
                BackHandler {
                    handleBack()
                }
                val args = it.toRoute<ChatRoute>()
                val pirateId = args.pirateId
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            mainViewModel = mainViewModel,
                            displayTitle = pirateId,
                            isMainScreen = false
                        )
                    },
                ) { innerPadding ->
                    Conversation(
                        modifier = Modifier.padding(innerPadding),
                        mainViewModel = mainViewModel,
                        pirateId = pirateId,
                    )
                }
            }
            composable<EmptyRoute> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You Reached an Empty Route.")
                }
            }
        }
    }
}
