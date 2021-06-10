package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import com.example.anticovid.R
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_fever.*
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_info.*
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_symptoms_worsening.*

class RiskAssessmentTestSymptomsWorseningFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_assessment_test_symptoms_worsening, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        symptoms_worsening.setOnCheckedChangeListener { _, checkedId ->
            (activity as? RiskAssessmentTestActivity)?.riskAssessmentTestViewModel?.symptomsWorsening = checkedId == R.id.yes
        }
    }
}