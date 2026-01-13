//
//  CalculationService.swift
//  CoalAnalyzer
//
//  Service for Coal Analysis Calculations
//

import Foundation

class CalculationService {

    // MARK: - Proximate Analysis Calculations

    /// Calculate GCV ADB (Air Derived Basis)
    /// Formula: (154*(100-(1.1*ash+IM))-(108*IM))/1.8
    static func calculateGCVADB(ash: Double, inherentMoisture: Double) -> Double {
        let gcvADB = (154 * (100 - (1.1 * ash + inherentMoisture)) - (108 * inherentMoisture)) / 1.8
        return gcvADB
    }

    /// Calculate Factor for ARB
    /// Formula: (100-TM)/(100-IM)
    static func calculateFactor(totalMoisture: Double, inherentMoisture: Double) -> Double {
        guard inherentMoisture != 100 else { return 0 }
        return (100 - totalMoisture) / (100 - inherentMoisture)
    }

    /// Calculate GCV ARB (As Received Basis)
    /// Formula: factor * GCV ADB
    static func calculateGCVARB(factor: Double, gcvADB: Double) -> Double {
        return factor * gcvADB
    }

    /// Calculate Equilibrial Factor
    /// Formula: (100-EM)/(100-IM)
    static func calculateEquilibrialFactor(equilibrialMoisture: Double, inherentMoisture: Double) -> Double {
        guard inherentMoisture != 100 else { return 0 }
        return (100 - equilibrialMoisture) / (100 - inherentMoisture)
    }

    /// Perform complete proximate analysis
    static func performProximateAnalysis(
        ash: Double,
        totalMoisture: Double,
        inherentMoisture: Double,
        equilibrialMoisture: Double
    ) -> ProximateAnalysisResult {
        let gcvADB = calculateGCVADB(ash: ash, inherentMoisture: inherentMoisture)
        let factor = calculateFactor(totalMoisture: totalMoisture, inherentMoisture: inherentMoisture)
        let gcvARB = calculateGCVARB(factor: factor, gcvADB: gcvADB)
        let equilibrialFactor = calculateEquilibrialFactor(equilibrialMoisture: equilibrialMoisture, inherentMoisture: inherentMoisture)
        let grade = CoalGrade.determineGrade(gcv: gcvADB)

        return ProximateAnalysisResult(
            gcvADB: gcvADB,
            factor: factor,
            gcvARB: gcvARB,
            equilibrialFactor: equilibrialFactor,
            grade: grade
        )
    }

    // MARK: - Formatting

    static func formatDouble(_ value: Double, decimals: Int = 2) -> String {
        return String(format: "%.\(decimals)f", value)
    }
}

// MARK: - Result Models

struct ProximateAnalysisResult {
    let gcvADB: Double
    let factor: Double
    let gcvARB: Double
    let equilibrialFactor: Double
    let grade: String
}
