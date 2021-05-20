package com.example.anticovid.data.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class HealthDiaryEntry(
    var header: String = formatDateTime(Calendar.getInstance().time),
    var date: Calendar = Calendar.getInstance()
) : Serializable

fun formatDate(date: Date): String {
    return SimpleDateFormat("dd.MM.yyyy").format(date)
}

fun formatDateTime(date: Date): String {
    return SimpleDateFormat("EEE HH:mm, dd.MM.yyyy").format(date)
}
