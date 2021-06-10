package com.example.anticovid.data.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class HealthDiaryEntry(
    var header: String = formatDateTime(Calendar.getInstance().time),
    var date: Calendar = Calendar.getInstance(),
    var temperature: Double? = null,
    var runnyNose: SymptomState = SymptomState.None,
    var cough: SymptomState = SymptomState.None,
    var shivering: SymptomState = SymptomState.None,
    var achingMuscles: SymptomState = SymptomState.None,
    var placesAndContacts: String = ""
) : Serializable

fun formatDate(date: Date): String {
    return SimpleDateFormat("dd.MM.yyyy").format(date)
}

fun formatDateTime(date: Date): String {
    return SimpleDateFormat("EEE HH:mm, dd.MM.yyyy").format(date)
}
