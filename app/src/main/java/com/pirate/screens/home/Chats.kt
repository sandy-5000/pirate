package com.pirate.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import com.pirate.viewModels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun Chats(
    mainViewModel: MainViewModel,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text( text="Chats", modifier = Modifier.clickable(onClick = {
            mainViewModel.viewModelScope.launch {
                mainViewModel.logout()
            }
        }))
    }
}
