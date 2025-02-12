package com.darkube.pirate.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.darkube.pirate.models.MainViewModel

@Composable
fun Conversation(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    pirateId: String,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Chat with pirate: $pirateId")
    }
}
