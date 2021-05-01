package com.example.anticovid.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.anticovid.R
import com.example.anticovid.data.model.CountryLiveDataModel
import kotlin.math.abs

class GridAdapter(private val context: Context, private val covid_data_model: CountryLiveDataModel?) :  BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var caseTypeTV: TextView
    private lateinit var newCasesNumTV: TextView
    private lateinit var avgCasesNumTV: TextView
    private lateinit var totalCasesNumTV: TextView
    private lateinit var avgCasesLayout: LinearLayout


    override fun getCount() = 4

    override fun getItem(position: Int) = null

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup ): View {
        val layoutInflaterCheck =
            layoutInflater ?: context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val convertViewCheck =
            convertView ?: layoutInflaterCheck.inflate(R.layout.covid_data_item, parent, false)

        caseTypeTV = convertViewCheck.findViewById(R.id.case_type_label_tv)
        newCasesNumTV = convertViewCheck.findViewById(R.id.new_cases_tv)
        avgCasesNumTV = convertViewCheck.findViewById(R.id.avg_cases_tv)
        totalCasesNumTV = convertViewCheck.findViewById(R.id.total_cases_tv)
        avgCasesLayout = convertViewCheck.findViewById(R.id.avg_cases_layout)


        if (covid_data_model != null) {
            when (position) {
                0 -> {
                    caseTypeTV.text = context.getString(R.string.infected)
                    newCasesNumTV.text = "+ " +  prettyNumber(covid_data_model.newCases)
                    avgCasesNumTV.text =  prettyNumber(covid_data_model.cases7DayAvg)
                    totalCasesNumTV.text =  prettyNumber(covid_data_model.casesTotal)
                }
                1 -> {
                    caseTypeTV.text = context.getString(R.string.deaths)
                    newCasesNumTV.text =  "+ " +  prettyNumber(covid_data_model.newDeaths)
                    avgCasesNumTV.text =  prettyNumber(covid_data_model.deaths7DayAvg)
                    totalCasesNumTV.text =  prettyNumber(covid_data_model.deathsTotal)
                }
                2 -> {
                    caseTypeTV.text = context.getString(R.string.recovered)
                    newCasesNumTV.text =  "+ " +  prettyNumber(covid_data_model.newRecovered)
                    totalCasesNumTV.text =  prettyNumber(covid_data_model.recoveredTotal)

                    avgCasesLayout.visibility = View.GONE
                }
                3 -> {
                    val prefix =
                       if (covid_data_model.activeCasesChange >= 0) "+ " else "- "

                    caseTypeTV.text = context.getString(R.string.active_cases)
                    newCasesNumTV.text = prefix + prettyNumber(abs(covid_data_model.activeCasesChange))
                    totalCasesNumTV.text =  prettyNumber(covid_data_model.activeTotal)

                    avgCasesLayout.visibility = View.GONE
                }
            }
        }

        return convertViewCheck
    }

    private fun prettyNumber(x: Int) = String.format("%,d", x)
}