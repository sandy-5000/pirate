package com.pirate.types

import com.pirate.utils.ChatRoute
import com.pirate.utils.HomeRoute
import com.pirate.utils.InviteFriendsRoute
import com.pirate.utils.ProfileRoute
import com.pirate.utils.SettingsRoute

enum class Routes(val value: String) {
    HOME(HomeRoute.javaClass.name),
    CHAT(ChatRoute.Companion::class.java.name.split("$").first()),
    SETTINGS(SettingsRoute.javaClass.name),
    PRIVACY(ProfileRoute.javaClass.name),
    PROFILE(ProfileRoute.javaClass.name),
    INVITE_FRIENDS(InviteFriendsRoute.javaClass.name),
}
