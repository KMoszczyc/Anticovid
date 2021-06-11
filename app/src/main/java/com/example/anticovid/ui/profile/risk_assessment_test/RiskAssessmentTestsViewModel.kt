package com.example.anticovid.ui.profile.risk_assessment_test

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anticovid.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class RiskAssessmentTestsViewModel(private val context: Context) : ViewModel() {
    private var database: FirebaseDatabase
    private var userId: String

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
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        database = FirebaseDatabase.getInstance("https://anticovid-93262-default-rtdb.europe-west1.firebasedatabase.app/")
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
        var test_infection_risk = 1
        when(result){
            "LowRisk" -> test_infection_risk = 10
            "MediumRisk" -> test_infection_risk = 30
            "HighRisk" -> test_infection_risk = 100
        }

        with (context.getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE).edit()) {
            putInt(SHARED_PREFERENCES_TEST_INFECTION_RISK, test_infection_risk)
            apply()
        }

        val sharedPrefContactInfectionRisk = context.getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE)
        val contact_infection_risk = sharedPrefContactInfectionRisk.getInt(SHARED_PREFERENCES_CONTACTS_INFECTION_RISK, 0)
        val infection_risk = Math.max(contact_infection_risk, test_infection_risk)

        database.reference.child("user-$userId").child("infectionRisk").setValue(infection_risk)

        with (context.getSharedPreferences(SHARED_PREFERENCES_MY_DATA, Context.MODE_PRIVATE).edit()) {
            putInt(SHARED_PREFERENCES_INFECTION_RISK, infection_risk)
            apply()
        }

        Log.wtf("test_result", "Test result: $result Test infection risk: $test_infection_risk, Contacts: $contact_infection_risk, Total: $infection_risk")
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