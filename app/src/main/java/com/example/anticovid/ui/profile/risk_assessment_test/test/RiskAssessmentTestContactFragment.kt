package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.anticovid.R
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_contact.*

class RiskAssessmentTestContactFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_assessment_test_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contact_tv.setOnCheckedChangeListener { _, checkedId ->
            (activity as? RiskAssessmentTestActivity)?.riskAssessmentTestViewModel?.contact = checkedId == R.id.yes
        }
    }
}