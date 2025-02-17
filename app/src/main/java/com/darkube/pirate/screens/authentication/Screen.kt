package com.darkube.pirate.screens.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.AuthenticatePage

@Composable
fun Authentication(
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

            AuthenticatePage.OTP -> OTPScreen(
                setAuthenticatePage = setAuthenticatePage,
            )

            AuthenticatePage.NEW_PASSWORD -> NewPassword(
                mainViewModel = mainViewModel,
                setAuthenticatePage = setAuthenticatePage,
            )
        }
    }
}
