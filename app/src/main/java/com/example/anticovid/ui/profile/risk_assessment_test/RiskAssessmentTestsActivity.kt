package com.example.anticovid.ui.profile.risk_assessment_test

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anticovid.R
import com.example.anticovid.data.model.*
import com.example.anticovid.ui.profile.risk_assessment_test.test.RiskAssessmentTestActivity
import kotlinx.android.synthetic.main.activity_risk_assessment_tests.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*
import java.util.*

class RiskAssessmentTestsActivity : AppCompatActivity(), RiskAssessmentTestRecyclerAdapter.OnRiskAssessmentTestClickListener {

    private lateinit var riskAssessmentTestsViewModel: RiskAssessmentTestsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_assessment_tests)
        this.supportActionBar?.hide()

        riskAssessmentTestsViewModel = ViewModelProvider(this, RiskAssessmentTestsViewModelFactory(this))
            .get(RiskAssessmentTestsViewModel::class.java).apply {
                isTodaysTestPerformed.observe(this@RiskAssessmentTestsActivity, androidx.lifecycle.Observer {
                    updateTestCard(it ?: false)
                })

                testsPerformed.observe(this@RiskAssessmentTestsActivity, androidx.lifecycle.Observer {
                    val testsPerformed = it ?: emptyList()

                    if (testsPerformed.isEmpty())
                        tests_performed_header.text = getString(R.string.no_tests_performed)
                    else
                        (tests_performed_recycler_view.adapter as RiskAssessmentTestRecyclerAdapter).updateTestsList(testsPerformed)
                })
            }

        initUI()
    }

    private fun updateTestCard(isTodaysTestPerformed: Boolean) {
        if (isTodaysTestPerformed) {
            test_date.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_24, 0)
            test_info.text = getString(R.string.rat_completed)
        }
        else {
            test_date.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_add_circle_24, 0)
            test_info.text = getString(R.string.complete_rat)
        }
    }

    private fun initUI() {
        back_bt.setOnClickListener {
            finish()
        }

        test_date.text = formatDate(Calendar.getInstance().time)

        perform_test.setOnClickListener {
            startRiskAssessmentTestActivity()
        }

        tests_performed_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@RiskAssessmentTestsActivity)
            adapter = RiskAssessmentTestRecyclerAdapter(this@RiskAssessmentTestsActivity)
        }
    }

    private fun startRiskAssessmentTestActivity(test: RiskAssessmentTest? = null) {
        val intent = Intent(this, RiskAssessmentTestActivity::class.java).apply {
            putExtra(RISK_ASSESSMENT_TEST_KEY, test)
        }
        startActivityForResult(intent, RISK_ASSESSMENT_TEST)
    }

    private fun startRiskAssessmentTestSummaryActivity(test: RiskAssessmentTest) {
        val intent = Intent(this, RiskAssessmentTestSummaryActivity::class.java).apply {
            putExtra(RISK_ASSESSMENT_TEST_KEY, test)
        }
        startActivityForResult(intent, RISK_ASSESSMENT_TEST)
    }

    override fun onRiskAssessmentTestClick(test: RiskAssessmentTest) {
        startRiskAssessmentTestSummaryActivity(test)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RISK_ASSESSMENT_TEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    (data!!.getSerializableExtra(RISK_ASSESSMENT_TEST_KEY) as? RiskAssessmentTest)?.let {
                        riskAssessmentTestsViewModel.saveTest(it)
                    }
                }
            }
        }
    }
}