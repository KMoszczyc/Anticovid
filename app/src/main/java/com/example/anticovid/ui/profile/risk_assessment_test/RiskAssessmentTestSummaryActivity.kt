package com.example.anticovid.ui.profile.risk_assessment_test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.anticovid.R
import com.example.anticovid.data.model.RISK_ASSESSMENT_TEST_KEY
import com.example.anticovid.data.model.RiskAssessmentTest
import com.example.anticovid.data.model.RiskAssessmentTestResult
import kotlinx.android.synthetic.main.activity_risk_assessment_test_summary.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*

class RiskAssessmentTestSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_assessment_test_summary)
        this.supportActionBar?.hide()

        back_bt.setOnClickListener {
            finish()
        }

        val test = intent.getSerializableExtra(RISK_ASSESSMENT_TEST_KEY) as? RiskAssessmentTest

        with(test!!) {
            when (result) {
                RiskAssessmentTestResult.LowRisk -> low_risk.visibility = View.VISIBLE
                RiskAssessmentTestResult.MediumRisk -> medium_risk.visibility = View.VISIBLE
                RiskAssessmentTestResult.HighRisk -> high_risk.visibility = View.VISIBLE
                else -> {}
            }

            age_tv.text = age

            diseases_tv.text = diseases.joinToString()

            symptoms_tv.text = symptoms.joinToString()

            if (fever != "") {
                fever_tv.text = fever
                fever_view.visibility = View.VISIBLE
            }

            symptomsWorsening?.let {
                symptoms_worsening_tv.text = if (it)
                    getString(R.string.yes)
                else
                    getString(R.string.no)
                symptoms_worsening_view.visibility = View.VISIBLE
            }

            contact_tv.text = if (contact)
                getString(R.string.yes)
            else
                getString(R.string.no)
        }
    }
}