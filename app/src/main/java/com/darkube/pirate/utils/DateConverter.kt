package com.darkube.pirate.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun utcToLocal(utcTime: String, format: String = "yyyy-MM-dd HH:mm:ss"): Pair<String, String> {
    val utcFormat = SimpleDateFormat(format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val localFormatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
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
