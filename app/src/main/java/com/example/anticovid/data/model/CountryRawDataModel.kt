package com.example.anticovid.data.model

data class CountryRawDataModel(
        val ID: String,
        val Country: String,
        val CountryCode: String,
        val Province: String,
        val City: String,
        val CityCode: String,
        val Lat: String,
        val Lon: String,
        val Confirmed: Int,
        val Deaths: Int,
        val Recovered: Int,
        val Active: Int,
        var Date: String)