//
//  CoalGrade.swift
//  CoalAnalyzer
//
//  Model for Coal Grades
//

import Foundation

struct CoalGrade: Identifiable {
    let id = UUID()
    let grade: String
    let minGCV: Double
    let maxGCV: Double

    static let grades: [CoalGrade] = [
        CoalGrade(grade: "G4", minGCV: 6100, maxGCV: Double.infinity),
        CoalGrade(grade: "G5", minGCV: 5800, maxGCV: 6099),
        CoalGrade(grade: "G6", minGCV: 5500, maxGCV: 5799),
        CoalGrade(grade: "G7", minGCV: 5200, maxGCV: 5499),
        CoalGrade(grade: "G8", minGCV: 4900, maxGCV: 5199),
        CoalGrade(grade: "G9", minGCV: 4600, maxGCV: 4899),
        CoalGrade(grade: "G10", minGCV: 4300, maxGCV: 4599),
        CoalGrade(grade: "G11", minGCV: 4000, maxGCV: 4299),
        CoalGrade(grade: "G12", minGCV: 3700, maxGCV: 3999),
        CoalGrade(grade: "G13", minGCV: 3400, maxGCV: 3699),
        CoalGrade(grade: "G14", minGCV: 3100, maxGCV: 3399),
        CoalGrade(grade: "G15", minGCV: 2800, maxGCV: 3099)
    ]

    static func determineGrade(gcv: Double) -> String {
        for grade in grades {
            if gcv >= grade.minGCV && gcv <= grade.maxGCV {
                return grade.grade
            }
        }
        if gcv < 2800 {
            return "Below G15"
        }
        return "Unknown"
    }
}
