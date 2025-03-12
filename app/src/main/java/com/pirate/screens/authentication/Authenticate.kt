package com.pirate.screens.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pirate.types.AuthenticatePage
import com.pirate.viewModels.MainViewModel

@Composable
fun Authenticate(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {

    var authenticatePage by remember { mutableStateOf(AuthenticatePage.LOGIN) }
    val setAuthenticatePage = { page: AuthenticatePage ->
        authenticatePage = page
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (authenticatePage) {
            AuthenticatePage.LOGIN -> Login(
                mainViewModel = mainViewModel,
                setAuthenticatePage = setAuthenticatePage,
            )

            AuthenticatePage.REGISTER -> Register(
                mainViewModel = mainViewModel,
                setAuthenticatePage = setAuthenticatePage,
            )
        }
    }

}
