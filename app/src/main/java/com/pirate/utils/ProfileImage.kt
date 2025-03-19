package com.pirate.utils

import com.pirate.R

fun getProfileImage(num: Int): Int {
    return when(num) {
        0 -> R.drawable.profile_00
        1 -> R.drawable.profile_01
        2 -> R.drawable.profile_02
        3 -> R.drawable.profile_03
        4 -> R.drawable.profile_04
        5 -> R.drawable.profile_05
        6 -> R.drawable.profile_06
        7 -> R.drawable.profile_07
        8 -> R.drawable.profile_08
        9 -> R.drawable.profile_09
        10 -> R.drawable.profile_10
        11 -> R.drawable.profile_11
        12 -> R.drawable.profile_12
        else -> R.drawable.profile_08
    }
}
