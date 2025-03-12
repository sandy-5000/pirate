package com.pirate

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.pirate.screens.authentication.Authenticate
import com.pirate.screens.authentication.Login
import com.pirate.services.DatabaseProvider
import com.pirate.ui.theme.PirateTheme
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
                    Box(modifier = Modifier.padding(innerPadding)) {
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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Authenticate(mainViewModel = mainViewModel, modifier = Modifier.padding(innerPadding))
    }
}
