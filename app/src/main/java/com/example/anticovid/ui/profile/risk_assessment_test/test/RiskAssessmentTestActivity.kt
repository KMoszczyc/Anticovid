package com.example.anticovid.ui.profile.risk_assessment_test.test

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.anticovid.R
import com.example.anticovid.data.model.HEALTH_DIARY_ENTRY_KEY
import com.example.anticovid.data.model.RISK_ASSESSMENT_TEST_KEY
import com.example.anticovid.ui.main.*
import kotlinx.android.synthetic.main.activity_health_diary_entry.*
import kotlinx.android.synthetic.main.activity_risk_assessment_test.*
import kotlinx.android.synthetic.main.app_title_bar_with_back_bt.*

class RiskAssessmentTestActivity : AppCompatActivity() {

    lateinit var riskAssessmentTestViewModel: RiskAssessmentTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_assessment_test)
        this.supportActionBar?.hide()

        riskAssessmentTestViewModel = ViewModelProvider(this, RiskAssessmentTestViewModelFactory(this))
            .get(RiskAssessmentTestViewModel::class.java).apply {
                question.observe(this@RiskAssessmentTestActivity, { question ->
                    question?.let {
                        if (it == Question.Null)
                            close()
                        else
                            showNextQuestion(it)
                    }
                })

                nextQuestionEnabled.observe(this@RiskAssessmentTestActivity, { nextQuestionEnabled ->
                    nextQuestionEnabled?.let { next_question_bt.isEnabled = it }
                })
            }

        initUI()
    }

    private fun initUI() {
        back_bt.setOnClickListener {
            hideCurrentQuestion()
            riskAssessmentTestViewModel.previousQuestion()
        }

        next_question_bt.setOnClickListener {
            hideCurrentQuestion()
            riskAssessmentTestViewModel.nextQuestion()
        }
    }

    private fun close() {
        finish()
    }

    private fun showNextQuestion(question: Question) {
        with (supportFragmentManager) {
            findFragmentByTag(question.tag).let { fragment ->
                if (fragment != null)
                    beginTransaction().show(fragment).commit()
                else beginTransaction().add(
                        R.id.fragment_container,
                        when (question) {
                            Question.Info -> RiskAssessmentTestInfoFragment()
                            Question.Age -> RiskAssessmentTestAgeFragment()
                            Question.Diseases -> RiskAssessmentTestDiseasesFragment()
                            Question.Symptoms -> RiskAssessmentTestSymptomsFragment()
                            Question.Fever -> RiskAssessmentTestFeverFragment()
                            Question.SymptomsWorsening -> RiskAssessmentTestSymptomsWorseningFragment()
                            Question.Contact -> RiskAssessmentTestContactFragment()
                            Question.Summary -> {
                                next_question_bt.apply {
                                    text = getString(R.string.close)
                                    setOnClickListener {
                                        setResult(
                                            Activity.RESULT_OK,
                                            Intent().apply { putExtra(RISK_ASSESSMENT_TEST_KEY, riskAssessmentTestViewModel.getTest()) }
                                        )
                                        finish()
                                    }
                                }
                                RiskAssessmentTestSummaryFragment(riskAssessmentTestViewModel.getTestResult())
                            }
                            else -> return
                        }, question.tag
                    ).commit()
            }
        }
    }

    private fun hideCurrentQuestion() {
        with (supportFragmentManager) {
            findFragmentByTag(riskAssessmentTestViewModel.question.value?.tag)?.let {
                beginTransaction().hide(it).commit()
            }
        }
    }
}