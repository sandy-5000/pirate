package com.darkube.pirate.utils

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object SettingsRoute

@Serializable
object ProfileRoute

@Serializable
object InviteFriendsRoute

@Serializable
data class ChatRoute(val pirateId: String, val username: String, val profileImage: Int)
