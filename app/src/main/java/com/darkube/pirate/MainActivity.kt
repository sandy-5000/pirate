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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkube.pirate.components.AppFloatingActionButton
import com.darkube.pirate.components.AppTopBar
import com.darkube.pirate.components.BottomNavBar
import com.darkube.pirate.screens.Authentication
import com.darkube.pirate.screens.Home
import com.darkube.pirate.screens.Group
import com.darkube.pirate.screens.Call
import com.darkube.pirate.screens.Stories
import com.darkube.pirate.ui.theme.PirateTheme
import com.darkube.pirate.utils.CallsRoute
import com.darkube.pirate.utils.GroupsRoute
import com.darkube.pirate.utils.HomeRoute
import com.darkube.pirate.utils.StoriesRoute
import com.darkube.pirate.utils.getRouteId

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
    Authentication()
}

@Composable
fun MainScreen() {
    var activeTab by remember { mutableStateOf(HomeRoute.javaClass.name) }
    val navController = rememberNavController()

    fun handleBack() {
        navController.popBackStack()
        activeTab = getRouteId(navController.currentDestination)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar() },
        bottomBar = {
            BottomNavBar(
                tab = activeTab,
                navController = navController,
                onTabChange = { value ->
                    activeTab = value
                },
            )
        },
        floatingActionButton = { AppFloatingActionButton() }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> {
                BackHandler {
                    handleBack()
                }
                Home(
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