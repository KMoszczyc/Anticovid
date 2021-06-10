package com.example.anticovid.ui.profile.risk_assessment_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anticovid.R
import com.example.anticovid.data.model.RiskAssessmentTest

class RiskAssessmentTestRecyclerAdapter(private val onClickListener: OnRiskAssessmentTestClickListener) : RecyclerView.Adapter<RiskAssessmentTestRecyclerAdapter.HeaderViewHolder>() {

    private var tests: List<RiskAssessmentTest> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_risk_assessment_test, parent, false)

        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HeaderViewHolder, position: Int) {
        viewHolder.bind(tests[position])
    }

    override fun getItemCount() = tests.size

    fun updateTestsList(testsList: List<RiskAssessmentTest>) {
        tests = testsList
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val header: TextView = itemView.findViewById(R.id.test_header)

        fun bind(test: RiskAssessmentTest) {
            header.text = test.header
            itemView.setOnClickListener {
                onClickListener.onRiskAssessmentTestClick(test)
            }
        }
    }

    interface OnRiskAssessmentTestClickListener {
        fun onRiskAssessmentTestClick(test: RiskAssessmentTest)
    }
}