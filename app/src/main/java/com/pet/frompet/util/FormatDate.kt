package com.pet.frompet.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class FormatDate {
    companion object {
        fun formatDate(timestamp: Long?): String {
            timestamp ?: return "알 수 없음"
            val now = System.currentTimeMillis()
            val diff = now - (timestamp ?: 0)

            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)

            return when {
                seconds < 60 -> "방금 전"
                minutes < 60 -> "${minutes}분 전"
                hours < 24 -> "${hours}시간 전"
                days == 1L -> "어제"
                else -> {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }
}
