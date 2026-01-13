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

    /// Perform proximate analysis with optional inputs
    /// Calculates only what's possible with available data
    static func performProximateAnalysis(
        ash: Double?,
        totalMoisture: Double?,
        inherentMoisture: Double?,
        equilibrialMoisture: Double?
    ) -> ProximateAnalysisResult {
        var gcvADB: Double? = nil
        var factor: Double? = nil
        var gcvARB: Double? = nil
        var equilibrialFactor: Double? = nil
        var grade: String? = nil

        // Calculate GCV ADB if we have ash and inherent moisture
        if let ash = ash, let im = inherentMoisture {
            gcvADB = calculateGCVADB(ash: ash, inherentMoisture: im)
            grade = CoalGrade.determineGrade(gcv: gcvADB!)
        }

        // Calculate Factor if we have total moisture and inherent moisture
        if let tm = totalMoisture, let im = inherentMoisture {
            factor = calculateFactor(totalMoisture: tm, inherentMoisture: im)
        }

        // Calculate GCV ARB if we have both factor and GCV ADB
        if let f = factor, let gcv = gcvADB {
            gcvARB = calculateGCVARB(factor: f, gcvADB: gcv)
        }

        // Calculate Equilibrial Factor if we have equilibrial moisture and inherent moisture
        if let em = equilibrialMoisture, let im = inherentMoisture {
            equilibrialFactor = calculateEquilibrialFactor(equilibrialMoisture: em, inherentMoisture: im)
        }

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
    let gcvADB: Double?
    let factor: Double?
    let gcvARB: Double?
    let equilibrialFactor: Double?
    let grade: String?

    var hasAnyResults: Bool {
        return gcvADB != nil || factor != nil || gcvARB != nil || equilibrialFactor != nil
    }
}
