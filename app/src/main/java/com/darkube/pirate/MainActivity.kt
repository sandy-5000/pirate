package com.darkube.pirate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.darkube.pirate.screens.Authentication
import com.darkube.pirate.ui.theme.PirateTheme
import com.darkube.pirate.utils.MainScreen

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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PirateTheme {
        Screen()
    }
}