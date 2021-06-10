package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import com.example.anticovid.R
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_diseases.*
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_diseases.none
import kotlinx.android.synthetic.main.fragment_risk_assessment_test_symptoms.*

class RiskAssessmentTestSymptomsFragment : Fragment() {

    private val symptoms = mutableListOf<String>()
    private val checkBoxes =  mutableListOf<CheckBox>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_assessment_test_symptoms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBoxes.apply {
            add(fever)
            add(cough)
            add(breathing_troubles)
            add(aching_muscles)
            add(shivering)
            add(headache)
            add(diarrhoea)
            add(sore_throat)
            add(impaired_smell_or_taste)
        }

        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            symptoms.apply {
                val value = buttonView.text.toString()
                if (isChecked) {
                    none.isChecked = false
                    add(value)
                }
                else
                    remove(value)
            }
            passAnswers()
        }.also { listener ->
            checkBoxes.forEach { it.setOnCheckedChangeListener(listener) }
        }

        none.setOnCheckedChangeListener { buttonView, isChecked ->
            symptoms.apply {
                if (isChecked) {
                    checkBoxes.forEach { it.isChecked = false }
                    add(buttonView.text.toString())
                }
                else
                    clear()
            }
            passAnswers()
        }
    }

    private fun passAnswers() {
        (activity as? RiskAssessmentTestActivity)?.riskAssessmentTestViewModel?.symptoms = symptoms
    }
}