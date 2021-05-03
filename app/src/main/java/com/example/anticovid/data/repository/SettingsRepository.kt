package com.example.anticovid.data.repository

import android.content.Context
import android.util.Log
import com.example.anticovid.data.model.SettingsData
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception

object SettingsRepository {

    fun loadSettingsData(context: Context?): SettingsData {
        var ret = SettingsData()

        if (context != null) {
            try {
                val inputStream = context.openFileInput("SettingsData.txt")
                BufferedReader(InputStreamReader(inputStream)).readLine()?.let {
                    ret = Gson().fromJson(it, SettingsData::class.java)
                }
                inputStream.close()
            } catch (e: Exception) {
                Log.d("SettingsData", "Cannot load data: $e")
            }
        }

        return ret
    }

    fun saveSettingsData(settingsData: SettingsData, context: Context?) {
        if (context != null) {
            try {
                OutputStreamWriter(
                    context.openFileOutput(
                        "SettingsData.txt",
                        Context.MODE_PRIVATE
                    )
                ).run {
                    write(Gson().toJson(settingsData))
                    close()
                }
            } catch (e: Exception) {
                Log.d("SettingsData", "Cannot save data: $e")
            }
        }
    }
}