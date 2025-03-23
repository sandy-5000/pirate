package com.pirate.utils

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class ChatRoute(val pirateId: String, val username: String, val profileImage: String)

@Serializable
object SettingsRoute

@Serializable
object ProfileRoute

@Serializable
object PrivacyRoute

@Serializable
object InviteFriendsRoute
