package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.anticovid.R
import com.example.anticovid.data.model.RiskAssessmentTestResult
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_info.*
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_summary.*

class RiskAssessmentTestSummaryFragment(private val testResult: RiskAssessmentTestResult) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_assessment_test_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (testResult) {
            RiskAssessmentTestResult.LowRisk -> low_risk.visibility = View.VISIBLE
            RiskAssessmentTestResult.MediumRisk -> medium_risk.visibility = View.VISIBLE
            RiskAssessmentTestResult.HighRisk -> high_risk.visibility = View.VISIBLE
            else -> {}
        }
    }
}