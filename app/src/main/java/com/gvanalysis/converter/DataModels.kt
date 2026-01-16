package com.gvanalysis.converter

data class ProximateAnalysisData(
    val id: Long = System.currentTimeMillis(),
    val sample: String = "",
    val date: String = "",
    val rackNumber: String = "",
    val source: String = "",
    val totalMoisture: Double = 0.0,
    val inherentMoisture: Double = 0.0,
    val equilibrialMoisture: Double = 0.0,
    val volatileMatter: Double = 0.0,
    val ash: Double = 0.0,
    val gcvAdb: Double = 0.0,
    val factor: Double = 0.0,
    val gcvArb: Double = 0.0,
    val equilibrialFactor: Double = 0.0,
    val coalGrade: String = ""
)

data class CoalAnalysisData(
    val id: Long = System.currentTimeMillis(),
    val coalConsumption: Double = 0.0,
    val unitGeneration: Double = 0.0,
    val specificCoalConsumption: Double = 0.0,
    val date: String = ""
)

data class HeatValueData(
    val id: Long = System.currentTimeMillis(),
    val sampleName: String = "",
    val coalConsumption: Double = 0.0,
    val gcvArb: Double = 0.0,
    val heatValue: Double = 0.0,
    val date: String = ""
)
