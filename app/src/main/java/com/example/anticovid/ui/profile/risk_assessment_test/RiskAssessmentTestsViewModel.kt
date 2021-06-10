package com.example.anticovid.ui.profile.risk_assessment_test

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class RiskAssessmentTestsViewModel(private val context: Context) : ViewModel() {

    private val _isTodaysTestPerformed = MutableLiveData<Boolean>()
    val isTodaysTestPerformed: LiveData<Boolean> = _isTodaysTestPerformed

    private val _testsPerformed = MutableLiveData<List<RiskAssessmentTest>>()
    val testsPerformed: LiveData<List<RiskAssessmentTest>> = _testsPerformed

    init {
        loadTestsPerformed().also {
            _testsPerformed.value = it
            if (it.isNotEmpty())
                checkIfTestFromToday(it.first())
        }
    }

    private fun checkIfTestFromToday(test: RiskAssessmentTest) {
        with (SimpleDateFormat("ddMMyyyy")) {
            val today = format(Calendar.getInstance().time)
            _isTodaysTestPerformed.value = format(test.date.time) == today
        }
    }

    fun saveTest(test: RiskAssessmentTest) {
        _testsPerformed.value = (_testsPerformed.value?.toMutableList() ?: mutableListOf()).apply {
            removeIf { it.date.timeInMillis == test.date.timeInMillis }
            add(test)
            sortByDescending { it.date }
        }

        checkIfTestFromToday(test)
        saveTestsPerformed(Gson().toJson(_testsPerformed.value))
        saveLatestResult(test.result.name)
    }

    private fun saveLatestResult(result: String) {
        with (context.getSharedPreferences(SHARED_PREFERENCES_RISK_ASSESSMENT_TEST, Context.MODE_PRIVATE).edit()) {
            putString(SHARED_PREFERENCES_RISK_ASSESSMENT_TEST_LATEST_RESULT, result)
            apply()
        }

        Log.d("test_result", "Test result: $result")
    }

    private fun loadTestsPerformed(): List<RiskAssessmentTest> {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_RISK_ASSESSMENT_TEST, Context.MODE_PRIVATE)
        val testsPerformedString = sharedPreferences.getString(SHARED_PREFERENCES_RISK_ASSESSMENT_TESTS_PERFORMED, "")
        val listType = object : TypeToken<List<RiskAssessmentTest>>() {}.type

        return Gson().fromJson(testsPerformedString, listType) ?: emptyList()
    }

    private fun saveTestsPerformed(testsPerformedString: String) {
        with (context.getSharedPreferences(SHARED_PREFERENCES_RISK_ASSESSMENT_TEST, Context.MODE_PRIVATE).edit()) {
            putString(SHARED_PREFERENCES_RISK_ASSESSMENT_TESTS_PERFORMED, testsPerformedString)
            apply()
        }
    }
}