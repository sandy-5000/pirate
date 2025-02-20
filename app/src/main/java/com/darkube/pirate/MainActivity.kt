package com.darkube.pirate

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darkube.pirate.components.AppFloatingActionButton
import com.darkube.pirate.components.BasicTopBar
import com.darkube.pirate.components.BottomNavBar
import com.darkube.pirate.components.ChatInput
import com.darkube.pirate.components.ChatScreenTopBar
import com.darkube.pirate.components.Loading
import com.darkube.pirate.components.MainScreenBottomScaffold
import com.darkube.pirate.components.MainScreenTopBar
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.screens.authentication.Authentication
import com.darkube.pirate.screens.Conversation
import com.darkube.pirate.screens.Profile
import com.darkube.pirate.screens.Settings
import com.darkube.pirate.screens.home.Home
import com.darkube.pirate.ui.theme.PirateTheme
import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.DatabaseProvider
import com.darkube.pirate.utils.HomeRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.SettingsRoute
import com.darkube.pirate.utils.InviteFriendsRoute
import com.google.firebase.messaging.FirebaseMessaging
import com.darkube.pirate.screens.InviteFriends
import com.darkube.pirate.ui.theme.LightColor
import com.darkube.pirate.ui.theme.NavBarBackground
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        intent?.extras?.let { extras ->
            for (key in extras.keySet()) {
                val value = extras.getString(key)
                Log.d("token-extras", "Key: $key Value: $value")
            }
        }

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
    AuthenticatedScreen(mainViewModel = mainViewModel)
}

@Composable
fun AuthenticatedScreen(mainViewModel: MainViewModel) {
    val userState by mainViewModel.userState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        if (userState.getOrDefault("logged_in", "false") == "loading") {
            Loading(modifier = Modifier.padding(innerPadding), durationMillis = 1500)
        } else if (userState.getOrDefault("logged_in", "false") == "true") {
            MainScreen(mainViewModel = mainViewModel)
        } else {
            Authentication(
                mainViewModel = mainViewModel,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel) {
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

    fun handleBack() {
        mainViewModel.navController.popBackStack()
        if (HomeRoute.javaClass.name != mainViewModel.currentScreen) {
            mainViewModel.navController.currentDestination?.let {
                mainViewModel.navigate(it)
            }
        }
    }

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
            val sheetState = rememberBottomSheetScaffoldState()
            val scope = rememberCoroutineScope()
            val openBottomModel = {
                scope.launch {
                    sheetState.bottomSheetState.expand()
                }
            }

            BottomSheetScaffold(
                scaffoldState = sheetState,
                sheetShape = RectangleShape,
                sheetContent = {
                    MainScreenBottomScaffold(mainViewModel = mainViewModel)
                },
                sheetContainerColor = NavBarBackground,
                sheetPeekHeight = 0.dp,
                sheetDragHandle = {
                    Row(
                        modifier = Modifier.height(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(
                            modifier = Modifier
                                .width(28.dp)
                                .height(4.dp)
                                .clip(shape = RoundedCornerShape(2.dp))
                                .background(LightColor),
                        )
                    }
                },
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { MainScreenTopBar(mainViewModel = mainViewModel) },
                    bottomBar = { BottomNavBar(mainViewModel = mainViewModel) },
                    floatingActionButton = {
                        AppFloatingActionButton(
                            mainViewModel = mainViewModel,
                            onClick = openBottomModel,
                        )
                    }
                ) { innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding),
                        mainViewModel = mainViewModel,
                    )
                }
            }
        }
        composable<SettingsRoute> {
            BackHandler {
                handleBack()
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    BasicTopBar(
                        mainViewModel = mainViewModel,
                        pageTitle = "Settings",
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
                    BasicTopBar(
                        mainViewModel = mainViewModel,
                        pageTitle = "Profile",
                    )
                },
            ) { innerPadding ->
                Profile(
                    modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel,
                )
            }
        }
        composable<InviteFriendsRoute> {
            BackHandler {
                handleBack()
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    BasicTopBar(
                        mainViewModel = mainViewModel,
                        pageTitle = "InviteFriends",
                    )
                },
            ) { innerPadding ->
                InviteFriends(
                    modifier = Modifier.padding(innerPadding),
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
                    ChatScreenTopBar(
                        mainViewModel = mainViewModel,
                        pageTitle = pirateId,
                    )
                },
                bottomBar = {
                    ChatInput(pirateId = pirateId, mainViewModel = mainViewModel)
                }
            ) { innerPadding ->
                Conversation(
                    modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel,
                    pirateId = pirateId,
                )
            }
        }
    }
}
