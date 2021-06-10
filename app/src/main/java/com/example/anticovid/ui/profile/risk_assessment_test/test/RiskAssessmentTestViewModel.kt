package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.R
import com.example.anticovid.data.model.RiskAssessmentTest
import com.example.anticovid.data.model.RiskAssessmentTestResult

class RiskAssessmentTestViewModel(private val context: Context) : ViewModel() {

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    private val _nextQuestionEnabled = MutableLiveData<Boolean>()
    val nextQuestionEnabled: LiveData<Boolean> = _nextQuestionEnabled

    private val questionsList = arrayListOf<Question>()
    private var currentQuestion = -1

    private val test = RiskAssessmentTest()
    var age = ""
        set(value) {
            field = value
            test.age = value
            enableNextQuestion(Question.Diseases)
        }
    var diseases: List<String> = emptyList()
        set(value) {
            field = value
            test.diseases = value
            enableNextQuestion(Question.Symptoms)
        }
    var symptoms: List<String> = emptyList()
        set(value) {
            field = value
            test.symptoms = value

            if (value.contains(context.getString(R.string.fever)))
                enableNextQuestion(Question.Fever)
            else if (!value.contains(context.getString(R.string.none)))
                enableNextQuestion(Question.SymptomsWorsening)
            else
                enableNextQuestion(Question.Contact)
        }
    var fever = ""
        set(value) {
            field = value
            test.fever = value
            enableNextQuestion(Question.SymptomsWorsening)
        }
    var symptomsWorsening: Boolean = false
        set(value) {
            field = value
            test.symptomsWorsening = value
            enableNextQuestion(Question.Contact)
        }
    var contact: Boolean = false
        set(value) {
            field = value
            test.contact = value
            calculateTestResult()
            enableNextQuestion(Question.Summary)
        }

    init {
        questionsList.apply {
            add(Question.Null)
            add(Question.Info)
            add(Question.Age)
        }

        updateQuestion(1)
    }

    fun nextQuestion() {
        if (currentQuestion < questionsList.lastIndex)
            updateQuestion(currentQuestion + 1)
    }

    fun previousQuestion() {
        if (currentQuestion > 0)
            updateQuestion(currentQuestion - 1)
    }

    private fun updateQuestion(questionIdx: Int) {
        currentQuestion = questionIdx
        _question.value = questionsList[questionIdx]
        _nextQuestionEnabled.value =
            questionIdx < questionsList.lastIndex || _question.value == Question.Summary
    }

    private fun enableNextQuestion(question: Question) {
        questionsList.apply {
            if (find { it == question } == null)
                add(question)
        }
        _nextQuestionEnabled.value = true
    }

    fun getTest() = test.copy()

    fun getTestResult() = test.result

    private fun calculateTestResult() {
        with(context) {
            val ageMultiplier = when (test.age) {
                getString(R.string.young_age) -> 1.0
                getString(R.string.middle_aged) -> 1.5
                getString(R.string.old_age) -> 2.0
                else -> { 1.0 }
            }

            val none = getString(R.string.none)

            var diseasesMultiplier = 1.0
            test.diseases.forEach {
                if (it != none)
                    diseasesMultiplier += 0.2
            }

            var symptomsMultiplier = 1.0
            test.symptoms.forEach {
                if (it != none)
                    symptomsMultiplier += 1
            }

            val feverMultiplier = when (test.fever) {
                getString(R.string.low_fever) -> 2.0
                getString(R.string.medium_fever) -> 2.5
                getString(R.string.high_fever) -> 3.0
                else -> { 1.0 }
            }

            val symptomsWorseningMultiplier =
                if (test.symptomsWorsening == true) 2
                else 1

            val contactMultiplier =
                if (test.contact) 2
                else 1

            val score = ageMultiplier *
                    diseasesMultiplier *
                    symptomsMultiplier *
                    feverMultiplier *
                    symptomsMultiplier *
                    contactMultiplier

            Log.d("Test_result", score.toString())

            test.result = when {
                score < 10 -> RiskAssessmentTestResult.LowRisk
                score < 50 -> RiskAssessmentTestResult.MediumRisk
                else -> RiskAssessmentTestResult.HighRisk
            }
        }
    }
}