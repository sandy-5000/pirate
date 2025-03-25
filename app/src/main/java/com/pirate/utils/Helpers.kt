package com.pirate.utils

import kotlin.random.Random

fun uniqId(): String {
    val timeStamp = System.currentTimeMillis().toString(36)
    val random = Random.nextInt(0, 1679616).toString(36).padStart(4, '0')
    return timeStamp + random
}
