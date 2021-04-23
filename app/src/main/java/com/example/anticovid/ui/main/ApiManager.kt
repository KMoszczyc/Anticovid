package com.example.anticovid.ui.main
import com.example.anticovid.data.model.CountryLiveDataModel
import com.example.anticovid.data.model.CountryRawDataModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.github.kittinunf.result.Result;

class ApiManager {
    var gson = Gson()
    lateinit var countryDataHistory: List<CountryRawDataModel>
    lateinit var currentCountry : CountryLiveDataModel

    //  another way
    fun getCovidDataJSON(countryName: String): String {
        var responseString = ""
        val httpAsync = "$COVID_API_COUNTRY_URL$countryName"
            .httpGet()
            .responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                    }
                    is Result.Success -> {
                        val data = result.get()
                        responseString = data
                    }
                }
            }
        httpAsync.join()

       return responseString
   }


//    add new fields like new cases, deaths etc
    fun createLiveCountryModel(): CountryLiveDataModel {
        val currentCountryData = countryDataHistory.last()
        val lastCountryData = countryDataHistory[countryDataHistory.size-2]

        val newDeaths = currentCountryData.Deaths - lastCountryData.Deaths
        val activeCasesChange = currentCountryData.Active - lastCountryData.Active
        val newConfirmedCases = currentCountryData.Confirmed - lastCountryData.Confirmed
        val newRecovered = currentCountryData.Recovered - lastCountryData.Recovered

        val indexLast7 = countryDataHistory.size-8
        val indexCurrent = countryDataHistory.size-1

        var deaths7DayAvg = 0
        var cases7DayAvg = 0

        deaths7DayAvg += (countryDataHistory[indexCurrent].Deaths - countryDataHistory[indexLast7].Deaths)/7
        cases7DayAvg += (countryDataHistory[indexCurrent].Confirmed - countryDataHistory[indexLast7].Confirmed)/7

        val countryLiveDataModel = CountryLiveDataModel(
            currentCountryData.Country,
            currentCountryData.CountryCode,
            currentCountryData.Lat,
            currentCountryData.Lon,
            newConfirmedCases,
            newDeaths,
            newRecovered,
            activeCasesChange,
            currentCountryData.Confirmed,
            currentCountryData.Deaths,
            currentCountryData.Recovered,
            currentCountryData.Active,
            deaths7DayAvg,
            cases7DayAvg,
            currentCountryData.Date)

        return countryLiveDataModel
    }

    //   some countries have covid data for specific provinces f.e germany, poland doesn't
    //  poland has aggregated data of all provinces per day
    fun aggregateCountryProvinceData(countryProvinceDataHistory : List<CountryRawDataModel>): List<CountryRawDataModel> {
        var countryDataList = mutableListOf<CountryRawDataModel>()
        val dateSet = mutableSetOf<String>()
        var aggregatedCountryRawDataModel = countryProvinceDataHistory[0]

        // Carefull here, this code is not simple -> group by date and add all provinces stats in that day
        val result = countryProvinceDataHistory
            .groupingBy { it.Date }
            .reduce { _, acc, element ->
                acc.copy(
                    ID = element.ID,
                    Country = element.Country,
                    CountryCode = element.CountryCode,
                    Province = "",
                    City = "",
                    CityCode = "",
                    Lat = element.Lat,
                    Lon = element.Lon,
                    Confirmed = acc.Confirmed + element.Confirmed,
                    Deaths = acc.Deaths + element.Deaths,
                    Recovered = acc.Recovered + element.Recovered,
                    Active = acc.Active + element.Active,
                    Date = element.Date)
            }.values.toList()
        return result
    }

    fun fetchCovidData(countryName: String): CountryLiveDataModel? {
        var responseString = getCovidDataJSON(countryName)

        println("response: $responseString")
        if(responseString!="") {
            var countryProvinceDataHistory = gson.fromJson(responseString, Array<CountryRawDataModel>::class.java).toList()
            countryDataHistory = aggregateCountryProvinceData(countryProvinceDataHistory)
            currentCountry = createLiveCountryModel()
        }
        else {
            println("Covid-19 api is not responding! :(")
            return null
        }


//        ___________________________FOR DEBUGGING___________________________
//        val indexLast7 = countryDataHistory.size-8
//        val indexCurrent = countryDataHistory.size-1
//
//        for(i in indexLast7..indexCurrent)
//            println(countryDataHistory[i])
//
//        var summedDeaths=0;
//        var summedCases=0;
//
//        for(provinceData in countryProvinceDataHistory) {
//            if(provinceData.Date == "2021-04-21T00:00:00Z"){
//                println("${provinceData.Province}  ${provinceData.Deaths}")
//                summedDeaths+=provinceData.Deaths
//                summedCases+=provinceData.Confirmed
//            }
//        }
//
//        println("summedDeaths: $summedDeaths , summedCases: $summedCases")

        return currentCountry;
    }

    companion object {
        const val BASE_URL_COVID_API = "https://api.covid19api.com/"
        const val COVID_API_COUNTRY_URL = "https://api.covid19api.com/live/country/"
    }
}