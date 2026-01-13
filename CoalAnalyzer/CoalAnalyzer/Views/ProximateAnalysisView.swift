//
//  ProximateAnalysisView.swift
//  CoalAnalyzer
//
//  Proximate Analysis Section
//

import SwiftUI

struct ProximateAnalysisView: View {
    // Input Fields
    @State private var sampleName: String = ""
    @State private var date = Date()
    @State private var rackNumber: String = ""
    @State private var source: String = ""
    @State private var totalMoisture: String = ""
    @State private var inherentMoisture: String = ""
    @State private var equilibrialMoisture: String = ""
    @State private var volatileMatter: String = ""
    @State private var ash: String = ""

    // Results
    @State private var result: ProximateAnalysisResult?
    @State private var showResults = false

    // UI State
    @FocusState private var focusedField: Field?

    enum Field: Hashable {
        case sampleName, rackNumber, source, totalMoisture, inherentMoisture
        case equilibrialMoisture, volatileMatter, ash
    }

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Card
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "doc.text.magnifyingglass")
                                .font(.title2)
                                .foregroundColor(.orange)
                            Text("Proximate Analysis")
                                .font(.title2)
                                .fontWeight(.bold)
                        }
                        Text("Enter coal sample details for analysis")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(Color.orange.opacity(0.1))
                    .cornerRadius(12)

                    // Input Section
                    VStack(spacing: 16) {
                        // Sample Information
                        GroupBox(label: Label("Sample Information", systemImage: "info.circle")) {
                            VStack(spacing: 12) {
                                CustomTextField(title: "Sample Name", text: $sampleName, icon: "tag")
                                    .focused($focusedField, equals: .sampleName)

                                DatePicker("Date", selection: $date, displayedComponents: .date)
                                    .padding(.vertical, 8)

                                CustomTextField(title: "Rack Number", text: $rackNumber, icon: "number")
                                    .focused($focusedField, equals: .rackNumber)

                                CustomTextField(title: "Source", text: $source, icon: "mappin.circle")
                                    .focused($focusedField, equals: .source)
                            }
                            .padding(.vertical, 8)
                        }

                        // Moisture Parameters
                        GroupBox(label: Label("Moisture Parameters (%)", systemImage: "drop")) {
                            VStack(spacing: 12) {
                                CustomTextField(title: "Total Moisture (TM)", text: $totalMoisture, icon: "drop.fill", keyboardType: .decimalPad)
                                    .focused($focusedField, equals: .totalMoisture)

                                CustomTextField(title: "Inherent Moisture (IM)", text: $inherentMoisture, icon: "drop.fill", keyboardType: .decimalPad)
                                    .focused($focusedField, equals: .inherentMoisture)

                                CustomTextField(title: "Equilibrial Moisture (EM)", text: $equilibrialMoisture, icon: "drop.fill", keyboardType: .decimalPad)
                                    .focused($focusedField, equals: .equilibrialMoisture)
                            }
                            .padding(.vertical, 8)
                        }

                        // Composition Parameters
                        GroupBox(label: Label("Composition Parameters (%)", systemImage: "chart.pie")) {
                            VStack(spacing: 12) {
                                CustomTextField(title: "Volatile Matter", text: $volatileMatter, icon: "flame", keyboardType: .decimalPad)
                                    .focused($focusedField, equals: .volatileMatter)

                                CustomTextField(title: "Ash", text: $ash, icon: "aqi.medium", keyboardType: .decimalPad)
                                    .focused($focusedField, equals: .ash)
                            }
                            .padding(.vertical, 8)
                        }
                    }

                    // Calculate Button
                    Button(action: calculateResults) {
                        HStack {
                            Image(systemName: "function")
                            Text("Calculate")
                                .fontWeight(.semibold)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(
                            LinearGradient(
                                gradient: Gradient(colors: [Color.orange, Color.red]),
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                    .disabled(!isFormValid)
                    .opacity(isFormValid ? 1.0 : 0.6)

                    // Results Section
                    if showResults, let result = result {
                        ResultsView(result: result)
                            .transition(.scale.combined(with: .opacity))
                    }
                }
                .padding()
            }
            .navigationTitle("Proximate Analysis")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItemGroup(placement: .keyboard) {
                    Spacer()
                    Button("Done") {
                        focusedField = nil
                    }
                }
            }
        }
    }

    // MARK: - Computed Properties

    private var isFormValid: Bool {
        !ash.isEmpty && !totalMoisture.isEmpty && !inherentMoisture.isEmpty && !equilibrialMoisture.isEmpty
    }

    // MARK: - Methods

    private func calculateResults() {
        guard let ashValue = Double(ash),
              let tmValue = Double(totalMoisture),
              let imValue = Double(inherentMoisture),
              let emValue = Double(equilibrialMoisture) else {
            return
        }

        withAnimation(.spring()) {
            result = CalculationService.performProximateAnalysis(
                ash: ashValue,
                totalMoisture: tmValue,
                inherentMoisture: imValue,
                equilibrialMoisture: emValue
            )
            showResults = true
            focusedField = nil
        }

        // Haptic feedback
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)
    }
}

// MARK: - Custom TextField

struct CustomTextField: View {
    let title: String
    @Binding var text: String
    let icon: String
    var keyboardType: UIKeyboardType = .default

    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(.orange)
                .frame(width: 24)

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.caption)
                    .foregroundColor(.secondary)
                TextField("Enter \(title.lowercased())", text: $text)
                    .keyboardType(keyboardType)
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Results View

struct ResultsView: View {
    let result: ProximateAnalysisResult

    var body: some View {
        VStack(spacing: 16) {
            // Header
            HStack {
                Image(systemName: "checkmark.seal.fill")
                    .font(.title2)
                    .foregroundColor(.green)
                Text("Analysis Results")
                    .font(.title3)
                    .fontWeight(.bold)
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            // Grade Display - Prominent
            VStack(spacing: 8) {
                Text("Coal Grade")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Text(result.grade)
                    .font(.system(size: 48, weight: .bold, design: .rounded))
                    .foregroundColor(.orange)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.orange.opacity(0.1))
            )

            Divider()

            // Calculations Grid
            VStack(spacing: 12) {
                ResultRow(
                    title: "GCV ADB",
                    value: CalculationService.formatDouble(result.gcvADB),
                    unit: "Kcal/Kg",
                    icon: "flame.fill",
                    color: .red
                )

                ResultRow(
                    title: "Factor",
                    value: CalculationService.formatDouble(result.factor, decimals: 4),
                    unit: "",
                    icon: "function",
                    color: .blue
                )

                ResultRow(
                    title: "GCV ARB",
                    value: CalculationService.formatDouble(result.gcvARB),
                    unit: "Kcal/Kg",
                    icon: "flame",
                    color: .orange
                )

                ResultRow(
                    title: "Equilibrial Factor",
                    value: CalculationService.formatDouble(result.equilibrialFactor, decimals: 4),
                    unit: "",
                    icon: "equal.circle",
                    color: .green
                )
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: 5)
    }
}

struct ResultRow: View {
    let title: String
    let value: String
    let unit: String
    let icon: String
    let color: Color

    var body: some View {
        HStack {
            HStack(spacing: 8) {
                Image(systemName: icon)
                    .foregroundColor(color)
                    .frame(width: 24)
                Text(title)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()

            HStack(spacing: 4) {
                Text(value)
                    .font(.headline)
                    .fontWeight(.semibold)
                if !unit.isEmpty {
                    Text(unit)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 12)
        .background(color.opacity(0.1))
        .cornerRadius(8)
    }
}

struct ProximateAnalysisView_Previews: PreviewProvider {
    static var previews: some View {
        ProximateAnalysisView()
    }
}
