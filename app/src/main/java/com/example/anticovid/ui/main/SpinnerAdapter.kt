package com.example.anticovid.ui.main


//import com.example.anticovid.R

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.anticovid.R
import kotlinx.android.synthetic.main.spinner_item.view.*


class SpinnerAdapter(
    private val ctx: Context,
    private val contentArray: Array<String>,
    private val countryCodes: MutableList<String>,
    private val flagsCountryCodes: MutableList<String>,
    private val imageArray: MutableList<Drawable>
) : ArrayAdapter<String?>(ctx, R.layout.spinner_item, R.id.spinner_tv, contentArray) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val spinnerItem: View = inflater.inflate(R.layout.spinner_item, parent, false)
        val flagIndex = flagsCountryCodes.indexOf(countryCodes[position])

        spinnerItem.spinner_tv.text = contentArray[position]
        spinnerItem.spinner_image.setImageDrawable(imageArray[flagIndex])
        return spinnerItem
    }
}