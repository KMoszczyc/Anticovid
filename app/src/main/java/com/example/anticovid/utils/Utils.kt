package com.example.anticovid.utils

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

fun readTextFile(context: Context, path: String): MutableList<String> {
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