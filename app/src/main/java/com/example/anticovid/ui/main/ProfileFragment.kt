package com.example.anticovid.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.anticovid.R
import com.example.anticovid.ui.profile.health_diary.HealthDiaryActivity
import com.example.anticovid.ui.profile.MyDataActivity
import com.example.anticovid.ui.profile.risk_assessment_test.RiskAssessmentTestsActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        my_data.setOnClickListener {
            startActivity(Intent(activity, MyDataActivity::class.java))
        }

        health_diary.setOnClickListener {
            startActivity(Intent(activity, HealthDiaryActivity::class.java))
        }

        risk_assessment_test.setOnClickListener {
            startActivity(Intent(activity, RiskAssessmentTestsActivity::class.java))
        }
    }
}