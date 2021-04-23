package com.example.anticovid.data.model

data class CountryLiveDataModel(
        val country: String,
        val countryCode: String,
        val lat: String,
        val lon: String,
        val newCases: Int,
        val newDeaths: Int,
        val newRecovered: Int,
        val activeCasesChange: Int,
        val casesTotal: Int,
        val deathsTotal: Int,
        val recoveredTotal: Int,
        val activeTotal: Int,
        val deaths7DayAvg : Int,
        val cases7DayAvg: Int,
        val date: String)