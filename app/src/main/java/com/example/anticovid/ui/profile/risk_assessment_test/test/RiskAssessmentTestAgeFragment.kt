package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import com.example.anticovid.R
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_age_info.*

class RiskAssessmentTestAgeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_assessment_test_age_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        age_tv.setOnCheckedChangeListener { _, checkedId ->
            (activity as? RiskAssessmentTestActivity)?.riskAssessmentTestViewModel?.age =
                (age_tv.children.first { it.id == checkedId } as RadioButton).text.toString()
        }
    }
}