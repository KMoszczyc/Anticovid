package com.example.anticovid.ui.main
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.anticovid.R
import com.example.anticovid.data.model.CountryLiveDataModel
import kotlinx.android.synthetic.main.covid_data_item.*
import java.lang.Math.abs


internal class GridAdapter(private val context: Context, private val covid_data_model: CountryLiveDataModel?) :  BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var caseTypeTV: TextView
    private lateinit var newCasesNumTV: TextView
    private lateinit var avgCasesNumTV: TextView
    private lateinit var totalCasesNumTV: TextView
    private lateinit var avgCasesLayout: LinearLayout


    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?,parent: ViewGroup ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.covid_data_item, null)
        }

        caseTypeTV = convertView!!.findViewById(R.id.case_type_label_tv)
        newCasesNumTV = convertView.findViewById(R.id.new_cases_tv)
        avgCasesNumTV = convertView.findViewById(R.id.avg_cases_tv)
        totalCasesNumTV = convertView.findViewById(R.id.total_cases_tv)
        avgCasesLayout = convertView.findViewById(R.id.avg_cases_layout)


        if (covid_data_model!=null) {
            when(position) {
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

                    avgCasesLayout.visibility=View.GONE
                }
                3 -> {
                    val prefix = if (covid_data_model.activeCasesChange >= 0)  "+ " else "- "

                    caseTypeTV.text = context.getString(R.string.active_cases)
                    newCasesNumTV.text = prefix + prettyNumber(abs(covid_data_model.activeCasesChange))
                    totalCasesNumTV.text =  prettyNumber(covid_data_model.activeTotal)

                    avgCasesLayout.visibility=View.GONE
                }
            }
        }

        return convertView
    }

    fun prettyNumber(x: Int): String {
        return String.format("%,d", x)
    }
}