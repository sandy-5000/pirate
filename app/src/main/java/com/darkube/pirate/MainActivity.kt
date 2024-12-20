package com.darkube.pirate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.darkube.pirate.components.AppFloatingActionButton
import com.darkube.pirate.components.AppTopBar
import com.darkube.pirate.components.BottomNavBar
import com.darkube.pirate.screens.Home
import com.darkube.pirate.screens.Group
import com.darkube.pirate.screens.Call
import com.darkube.pirate.screens.Stories
import com.darkube.pirate.ui.theme.PirateTheme

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
    var activeTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar() },
        bottomBar = {
            BottomNavBar(tab = activeTab, onTabChange = { value ->
                activeTab = value
            })
        },
        floatingActionButton = { AppFloatingActionButton() }
    ) { innerPadding ->
        when (activeTab) {
            0 -> Home(modifier = Modifier.padding(innerPadding))
            1 -> Group(modifier = Modifier.padding(innerPadding))
            2 -> Call(modifier = Modifier.padding(innerPadding))
            3 -> Stories(modifier = Modifier.padding(innerPadding))
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