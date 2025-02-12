package com.darkube.pirate.utils

import kotlinx.serialization.Serializable

@Serializable
object EmptyRoute

@Serializable
object HomeRoute

@Serializable
object SettingsRoute

@Serializable
object ProfileRoute

@Serializable
data class ChatRoute(val pirateId: String)

