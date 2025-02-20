package com.darkube.pirate.utils

import com.darkube.pirate.R

fun getProfileImage(num: Int): Int {
    return when(num) {
        0 -> R.drawable._profile_00
        1 -> R.drawable._profile_01
        2 -> R.drawable._profile_02
        3 -> R.drawable._profile_03
        4 -> R.drawable._profile_04
        5 -> R.drawable._profile_05
        6 -> R.drawable._profile_06
        7 -> R.drawable._profile_07
        8 -> R.drawable._profile_08
        9 -> R.drawable._profile_09
        10 -> R.drawable._profile_10
        11 -> R.drawable._profile_11
        12 -> R.drawable._profile_12
        else -> R.drawable._profile_08
    }
}
