package com.pirate

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.messaging.FirebaseMessaging
import com.pirate.components.DataLoading
import com.pirate.screens.InviteFriends
import com.pirate.screens.authentication.Authenticate
import com.pirate.screens.home.Conversation
import com.pirate.screens.home.Home
import com.pirate.screens.settings.Privacy
import com.pirate.screens.settings.Profile
import com.pirate.screens.settings.Settings
import com.pirate.services.DatabaseProvider
import com.pirate.services.SocketManager
import com.pirate.types.HomeScreen
import com.pirate.ui.theme.PirateTheme
import com.pirate.utils.ChatRoute
import com.pirate.utils.HomeRoute
import com.pirate.utils.InviteFriendsRoute
import com.pirate.utils.PrivacyRoute
import com.pirate.utils.ProfileRoute
import com.pirate.utils.SettingsRoute
import com.pirate.viewModels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(0))
        setContent {
            PirateTheme {
                Scaffold(
                    topBar = {
                        Box(
                            modifier = Modifier
                                .height(0.dp)
                                .safeContentPadding()
                        )
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .height(0.dp)
                                .safeContentPadding()
                        )
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(WindowInsets.statusBars.asPaddingValues()),
                    ) {
                        Screen(context = this@MainActivity)
                    }
                }
            }
        }
    }
}

@Composable
fun Screen(context: Context) {
    val mainViewModel = MainViewModel(
        navController = rememberNavController(),
        dataBase = DatabaseProvider.getInstance(context)
    )
    MainViewModel.init(mainViewModel)
    mainViewModel.setAllUserDetails()
    AuthenticatedScreen(mainViewModel = mainViewModel, context = context)
}

@Composable
fun AuthenticatedScreen(mainViewModel: MainViewModel, context: Context) {
    val userState by mainViewModel.userState.collectAsState()

    if (userState.getOrDefault("logged_in", "false") == "loading") {
        DataLoading(
            modifier = Modifier,
            durationMillis = 1200,
            message = "    Loading ...",
        )
    } else if (userState.getOrDefault("logged_in", "false") == "true") {
        val userId = userState.getOrDefault("_id", "")
        if (userId.isNotEmpty()) {
            LaunchedEffect(userId) {
                SocketManager.initialize(
                    application = context.applicationContext as Application,
                    userId = userId,
                )
            }
        }
        MainScreen(mainViewModel = mainViewModel, context = context)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Authenticate(mainViewModel = mainViewModel, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel, context: Context) {
    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                mainViewModel.updatePushToken(token)
            } else {
                Log.e("token-error", "Failed to get FCM token", task.exception)
            }
        }
    }

    NavHost(
        navController = mainViewModel.navController,
        startDestination = HomeRoute,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(stiffness = Spring.StiffnessLow),
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = spring(stiffness = Spring.StiffnessLow),
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(stiffness = Spring.StiffnessLow),
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = spring(stiffness = Spring.StiffnessLow),
            )
        },
    ) {
        composable<HomeRoute> {
            var bottomModel by remember { mutableStateOf(false) }
            val openModel = { bottomModel = true }
            val closeModel = { bottomModel = false }
            val homeScreenState by mainViewModel.homeScreenState.collectAsState()
            BackHandler {
                if (bottomModel) {
                    closeModel()
                } else if (homeScreenState != HomeScreen.CHATS) {
                    mainViewModel.setHomeScreen(HomeScreen.CHATS)
                } else {
                    (context as Activity).moveTaskToBack(true)
                }
            }

            Home(
                mainViewModel = mainViewModel,
                openModel = openModel,
                closeModel = closeModel,
                bottomModel = bottomModel
            )
        }

        composable<ChatRoute> {
            val args = it.toRoute<ChatRoute>()
            val pirateId = args.pirateId
            val username = args.username
            val profileImage = args.profileImage

            BackHandler {
                SocketManager.exitChatRoute(pirateId)
                mainViewModel.navController.popBackStack()
                mainViewModel.fetchChatsList()
            }

            Conversation(
                mainViewModel = mainViewModel,
                pirateId = pirateId,
                username = username,
                profileImage = profileImage,
            )
        }

        composable<ProfileRoute> {
            var bottomModel by remember { mutableStateOf(false) }
            val openModel = { bottomModel = true }
            val closeModel = { bottomModel = false }
            BackHandler {
                if (bottomModel) {
                    closeModel()
                } else {
                    mainViewModel.navController.popBackStack()
                }
            }

            Profile(
                mainViewModel = mainViewModel,
                bottomModel = bottomModel,
                openModel = openModel,
                closeModel = closeModel,
            )
        }

        composable<SettingsRoute> {
            var bottomModel by remember { mutableStateOf(false) }
            val openModel = { bottomModel = true }
            val closeModel = { bottomModel = false }
            BackHandler {
                if (bottomModel) {
                    closeModel()
                } else {
                    mainViewModel.navController.popBackStack()
                }
            }

            Settings(
                mainViewModel = mainViewModel,
                bottomModel = bottomModel,
                openModel = openModel,
                closeModel = closeModel,
            )
        }

        composable<PrivacyRoute> {
            BackHandler {
                mainViewModel.navController.popBackStack()
            }

            Privacy(mainViewModel = mainViewModel)
        }

        composable<InviteFriendsRoute> {
            BackHandler {
                mainViewModel.navController.popBackStack()
            }

            InviteFriends()
        }
    }
}
