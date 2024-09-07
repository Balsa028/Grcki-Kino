package com.example.grckikino.utils

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.formatDrawingTimeForDisplay(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(date)
}

fun Long.formatRemainingTimeForDisplay(): String {
    val currentTime = System.currentTimeMillis()
    val remainingTime = this - currentTime

    val hours = (remainingTime / (1000 * 60 * 60)).toInt()
    val minutes = ((remainingTime / (1000 * 60)) % 60).toInt()
    val seconds = ((remainingTime / 1000) % 60).toInt()

    return if (hours > 0)
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    else
        String.format("%02d:%02d", minutes, seconds)
}

fun Int.toPx(): Float {
    return this * Resources.getSystem().displayMetrics.density
}