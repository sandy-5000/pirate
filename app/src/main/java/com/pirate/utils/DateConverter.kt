package com.pirate.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getTimeStamp(): Long {
    val timestamp = System.currentTimeMillis()
    return timestamp
}

fun getMinutesDifference(prevTime: Long, currentTime: Long = getTimeStamp()): Long {
    val diffMillis = currentTime - prevTime
    val diffMinutes = diffMillis / (1000 * 60)
    return diffMinutes
}

fun timestampToLocal(timestamp: Long = getTimeStamp()): Pair<String, String> {
    val date = Date(timestamp)
    val localFormatDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val localFormatTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val localDate = localFormatDate.format(date)
    val localTime = localFormatTime.format(date)
    return Pair(localDate, localTime)
}
