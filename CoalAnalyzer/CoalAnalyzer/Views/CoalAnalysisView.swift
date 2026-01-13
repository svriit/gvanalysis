//
//  CoalAnalysisView.swift
//  CoalAnalyzer
//
//  Coal Analysis Section
//

import SwiftUI

struct CoalAnalysisView: View {
    @State private var selectedGrade: String = "G4"
    @State private var customGCV: String = ""
    @State private var calculatedGrade: String = ""
    @State private var showGradeResult = false

    let grades = CoalGrade.grades

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Card
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "flame.fill")
                                .font(.title2)
                                .foregroundColor(.orange)
                            Text("Coal Analysis")
                                .font(.title2)
                                .fontWeight(.bold)
                        }
                        Text("View coal grades and determine grade from GCV")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(Color.orange.opacity(0.1))
                    .cornerRadius(12)

                    // Coal Grades Table
                    GroupBox(label: Label("Coal Grades Reference", systemImage: "tablecells")) {
                        VStack(spacing: 0) {
                            // Header
                            HStack {
                                Text("Grade")
                                    .fontWeight(.bold)
                                    .frame(maxWidth: .infinity, alignment: .center)

                                Divider()

                                Text("GCV (Kcal/Kg)")
                                    .fontWeight(.bold)
                                    .frame(maxWidth: .infinity, alignment: .center)
                            }
                            .padding()
                            .background(Color.orange.opacity(0.2))

                            Divider()

                            // Rows
                            ForEach(grades) { grade in
                                HStack {
                                    Text(grade.grade)
                                        .fontWeight(.semibold)
                                        .frame(maxWidth: .infinity, alignment: .center)
                                        .foregroundColor(.orange)

                                    Divider()

                                    Text(formatGCVRange(grade))
                                        .frame(maxWidth: .infinity, alignment: .center)
                                }
                                .padding()
                                .background(selectedGrade == grade.grade ? Color.orange.opacity(0.1) : Color.clear)

                                if grade.id != grades.last?.id {
                                    Divider()
                                }
                            }
                        }
                    }

                    // GCV to Grade Converter
                    GroupBox(label: Label("Find Grade from GCV", systemImage: "arrow.left.arrow.right")) {
                        VStack(spacing: 16) {
                            HStack {
                                Image(systemName: "flame")
                                    .foregroundColor(.orange)

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("GCV Value")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                    TextField("Enter GCV (Kcal/Kg)", text: $customGCV)
                                        .keyboardType(.decimalPad)
                                }
                            }

                            Button(action: findGrade) {
                                HStack {
                                    Image(systemName: "magnifyingglass")
                                    Text("Find Grade")
                                        .fontWeight(.semibold)
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.orange)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                            .disabled(customGCV.isEmpty)
                            .opacity(customGCV.isEmpty ? 0.6 : 1.0)

                            if showGradeResult {
                                VStack(spacing: 8) {
                                    Text("Determined Grade")
                                        .font(.subheadline)
                                        .foregroundColor(.secondary)
                                    Text(calculatedGrade)
                                        .font(.system(size: 36, weight: .bold, design: .rounded))
                                        .foregroundColor(.orange)
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.orange.opacity(0.1))
                                .cornerRadius(10)
                                .transition(.scale.combined(with: .opacity))
                            }
                        }
                        .padding(.vertical, 8)
                    }

                    // Information Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Image(systemName: "info.circle.fill")
                                .foregroundColor(.blue)
                            Text("About Coal Grades")
                                .font(.headline)
                        }

                        Text("Coal grades are classified based on their Gross Calorific Value (GCV) measured in Kcal/Kg. Higher GCV values indicate better quality coal with greater energy content.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)

                        VStack(alignment: .leading, spacing: 6) {
                            InfoRow(icon: "arrow.up.circle.fill", text: "G4 is the highest grade (≥6100 Kcal/Kg)", color: .green)
                            InfoRow(icon: "arrow.down.circle.fill", text: "G15 is the lowest grade (2800-3099 Kcal/Kg)", color: .red)
                        }
                    }
                    .padding()
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(12)
                }
                .padding()
            }
            .navigationTitle("Coal Analysis")
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    private func formatGCVRange(_ grade: CoalGrade) -> String {
        if grade.maxGCV == Double.infinity {
            return "≥ \(Int(grade.minGCV))"
        } else {
            return "\(Int(grade.minGCV))"
        }
    }

    private func findGrade() {
        guard let gcvValue = Double(customGCV) else { return }

        withAnimation(.spring()) {
            calculatedGrade = CoalGrade.determineGrade(gcv: gcvValue)
            showGradeResult = true

            // Update selected grade for highlighting in table
            if let foundGrade = grades.first(where: { $0.grade == calculatedGrade }) {
                selectedGrade = foundGrade.grade
            }
        }

        // Haptic feedback
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)
    }
}

struct InfoRow: View {
    let icon: String
    let text: String
    let color: Color

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: icon)
                .foregroundColor(color)
            Text(text)
                .font(.caption)
                .foregroundColor(.secondary)
        }
    }
}

struct CoalAnalysisView_Previews: PreviewProvider {
    static var previews: some View {
        CoalAnalysisView()
    }
}
