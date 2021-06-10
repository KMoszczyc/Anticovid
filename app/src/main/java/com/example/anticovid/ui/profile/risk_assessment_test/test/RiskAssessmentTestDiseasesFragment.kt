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

class RiskAssessmentTestDiseasesFragment : Fragment() {

    private val diseases = mutableListOf<String>()
    private val checkBoxes =  mutableListOf<CheckBox>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_assessment_test_diseases, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBoxes.apply {
            add(chronic_lung_disease)
            add(cardiac_failure)
            add(diseases_or_drugs_that_reduce_immunity)
            add(chronic_liver_disease)
            add(diabetes)
            add(obesity)
        }

        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            diseases.apply {
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
            diseases.apply {
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
        (activity as? RiskAssessmentTestActivity)?.riskAssessmentTestViewModel?.diseases = diseases
    }
}