package com.gvanalysis.converter

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("GVAnalysisData", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Proximate Analysis Data
    fun saveProximateAnalysis(data: ProximateAnalysisData) {
        val allData = getAllProximateAnalysis().toMutableList()
        // Remove existing entry with same ID if it exists
        allData.removeAll { it.id == data.id }
        allData.add(0, data) // Add to beginning

        val json = gson.toJson(allData)
        prefs.edit().putString("proximate_analysis", json).apply()
    }

    fun getAllProximateAnalysis(): List<ProximateAnalysisData> {
        val json = prefs.getString("proximate_analysis", null) ?: return emptyList()
        val type = object : TypeToken<List<ProximateAnalysisData>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getProximateAnalysisById(id: Long): ProximateAnalysisData? {
        return getAllProximateAnalysis().find { it.id == id }
    }

    // Coal Analysis Data
    fun saveCoalAnalysis(data: CoalAnalysisData) {
        val allData = getAllCoalAnalysis().toMutableList()
        allData.removeAll { it.id == data.id }
        allData.add(0, data)

        val json = gson.toJson(allData)
        prefs.edit().putString("coal_analysis", json).apply()
    }

    fun getAllCoalAnalysis(): List<CoalAnalysisData> {
        val json = prefs.getString("coal_analysis", null) ?: return emptyList()
        val type = object : TypeToken<List<CoalAnalysisData>>() {}.type
        return gson.fromJson(json, type)
    }

    // Heat Value Data
    fun saveHeatValue(data: HeatValueData) {
        val allData = getAllHeatValues().toMutableList()
        allData.removeAll { it.id == data.id }
        allData.add(0, data)

        val json = gson.toJson(allData)
        prefs.edit().putString("heat_value", json).apply()
    }

    fun getAllHeatValues(): List<HeatValueData> {
        val json = prefs.getString("heat_value", null) ?: return emptyList()
        val type = object : TypeToken<List<HeatValueData>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
