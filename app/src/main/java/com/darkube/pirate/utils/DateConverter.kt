package com.darkube.pirate.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun utcToLocal(utcTime: String, format: String = "yyyy-MM-dd HH:mm:ss"): Pair<String, String> {
    val utcFormat = SimpleDateFormat(format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val localFormatDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val localFormatTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val date = utcFormat.parse(utcTime) ?: return Pair("", "")
    val localDate = localFormatDate.format(date)
    val localTime = localFormatTime.format(date)
    return Pair(localDate, localTime)
}

fun getMinutesDifference(prevTime: String, nextTime: String): Int {
    val hourDifference = prevTime.substring(11, 13).toInt() - nextTime.substring(11, 13).toInt()
    return hourDifference * 60 + prevTime.substring(14, 16).toInt() - nextTime.substring(14, 16).toInt()
}

fun getCurrentUtcTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(Date())
}
