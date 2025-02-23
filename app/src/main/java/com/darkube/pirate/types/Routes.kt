package com.darkube.pirate.types

import com.darkube.pirate.utils.ChatRoute
import com.darkube.pirate.utils.HomeRoute
import com.darkube.pirate.utils.InviteFriendsRoute
import com.darkube.pirate.utils.ProfileRoute
import com.darkube.pirate.utils.SettingsRoute

enum class Routes(val value: String) {
    HOME(HomeRoute.javaClass.name),
    CHAT(ChatRoute.Companion::class.java.name.split("$").first()),
    SETTINGS(SettingsRoute.javaClass.name),
    PROFILE(ProfileRoute.javaClass.name),
    INVITE_FRIENDS(InviteFriendsRoute.javaClass.name),
}
