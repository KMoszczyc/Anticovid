package com.example.anticovid.data.model

import java.io.Serializable
import java.util.*

data class RiskAssessmentTest(
    var header: String = "Test: ${formatDateTime(Calendar.getInstance().time)}",
    var date: Calendar = Calendar.getInstance(),
    var age: String = "",
    var diseases: List<String> = emptyList(),
    var symptoms: List<String> = emptyList(),
    var fever: String = "",
    var symptomsWorsening: Boolean? = null,
    var contact: Boolean = false,
    var result: RiskAssessmentTestResult = RiskAssessmentTestResult.None
) : Serializable