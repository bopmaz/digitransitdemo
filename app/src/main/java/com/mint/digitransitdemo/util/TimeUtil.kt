package com.mint.digitransitdemo.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Int.toFormattedTime(): String {
    val time = LocalTime.ofSecondOfDay(this.toLong())
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return time.format(formatter)
}