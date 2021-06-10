package com.example.anticovid.ui.profile.risk_assessment_test.test

enum class Question(val tag: String) {
    Null("null"),
    Info("info"),
    Age("age"),
    Diseases("diseases"),
    Symptoms("symptoms"),
    Fever("fever"),
    SymptomsWorsening("symptoms_worsening"),
    Contact("contact"),
    Summary("summary")
}