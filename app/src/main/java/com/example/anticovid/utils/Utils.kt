package com.example.anticovid.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import java.io.InputStream


fun readTxtFile(context: Context, path: String): MutableList<String> {
    val inputStream: InputStream = context.assets.open(path)
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    lineList.forEach{println(">  " + it)}
    return lineList
}

fun readCountries(context: Context): Pair<MutableList<String>, MutableList<String>> {
    val inputStream: InputStream = context.assets.open("countries_codes_tested.txt")
    val countries = mutableListOf<String>()
    val codes = mutableListOf<String>()

    inputStream.bufferedReader().forEachLine {
        val linesplit = it.split("_")
        countries.add(linesplit[0])
        codes.add(linesplit[1])
    }

    countries.forEach{println(">  " + it)}
    codes.forEach{println(">  " + it)}

    return Pair(countries, codes)
}

fun loadImages(context: Context): Pair<MutableList<Drawable>, MutableList<String>> {
    val img_path = "flags"
    val opts: BitmapFactory.Options = BitmapFactory.Options()
    opts.inDensity = DisplayMetrics.DENSITY_HIGH

    val images: Array<String> = context.getAssets().list(img_path) as Array<String>
    val drawables = mutableListOf<Drawable>()
    val countryCodes = mutableListOf<String>()

    for (image_name in images) {
        val inputstream: InputStream = context.getAssets().open("flags/" + image_name)
        val b = BitmapFactory.decodeStream(inputstream)
        b.density = Bitmap.DENSITY_NONE
        val drawable: Drawable = BitmapDrawable(b)

        drawables.add(drawable)
        val countryCode = image_name.split(".")[0].toUpperCase()
        countryCodes.add(countryCode)
        println(countryCode)
    }

    return Pair(drawables, countryCodes)
}