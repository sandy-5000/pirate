package com.darkube.pirate.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.screens.authenticate.Login
import com.darkube.pirate.screens.authenticate.NewPassword
import com.darkube.pirate.screens.authenticate.OTPScreen
import com.darkube.pirate.screens.authenticate.Register

enum class AuthenticatePage {
    LOGIN, REGISTER, OTP, NEW_PASSWORD
}

@Composable
fun Authentication(
    mainViewModel: MainViewModel
) {
    var authenticatePage by remember { mutableStateOf(AuthenticatePage.LOGIN) }
    val setAuthenticatePage = { page: AuthenticatePage ->
        authenticatePage = page
    }
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
