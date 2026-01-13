//
//  HeatValueView.swift
//  CoalAnalyzer
//
//  Heat Value Converter Section
//

import SwiftUI

struct HeatValueView: View {
    @State private var inputValue: String = ""
    @State private var inputUnit: HeatUnit = .kcalPerKg
    @State private var results: [ConversionResult] = []
    @State private var showResults = false

    enum HeatUnit: String, CaseIterable {
        case kcalPerKg = "Kcal/Kg"
        case mjPerKg = "MJ/Kg"
        case btuPerLb = "BTU/lb"
        case kjPerKg = "KJ/Kg"

        var icon: String {
            switch self {
            case .kcalPerKg: return "flame.fill"
            case .mjPerKg: return "bolt.fill"
            case .btuPerLb: return "thermometer"
            case .kjPerKg: return "bolt.circle.fill"
            }
        }
    }

    struct ConversionResult: Identifiable {
        let id = UUID()
        let unit: HeatUnit
        let value: Double
        let color: Color
    }

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Card
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "thermometer.sun.fill")
                                .font(.title2)
                                .foregroundColor(.orange)
                            Text("Heat Value Converter")
                                .font(.title2)
                                .fontWeight(.bold)
                        }
                        Text("Convert between different heat value units")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(Color.orange.opacity(0.1))
                    .cornerRadius(12)

                    // Input Section
                    GroupBox(label: Label("Input Heat Value", systemImage: "arrow.down.circle")) {
                        VStack(spacing: 16) {
                            // Value Input
                            HStack {
                                Image(systemName: inputUnit.icon)
                                    .foregroundColor(.orange)
                                    .frame(width: 24)

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Heat Value")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                    TextField("Enter value", text: $inputValue)
                                        .keyboardType(.decimalPad)
                                }
                            }

                            Divider()

                            // Unit Picker
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Unit")
                                    .font(.caption)
                                    .foregroundColor(.secondary)

                                Picker("Unit", selection: $inputUnit) {
                                    ForEach(HeatUnit.allCases, id: \.self) { unit in
                                        HStack {
                                            Image(systemName: unit.icon)
                                            Text(unit.rawValue)
                                        }
                                        .tag(unit)
                                    }
                                }
                                .pickerStyle(SegmentedPickerStyle())
                            }
                        }
                        .padding(.vertical, 8)
                    }

                    // Convert Button
                    Button(action: convertValue) {
                        HStack {
                            Image(systemName: "arrow.triangle.2.circlepath")
                            Text("Convert")
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
                    .disabled(inputValue.isEmpty)
                    .opacity(inputValue.isEmpty ? 0.6 : 1.0)

                    // Results Section
                    if showResults {
                        VStack(spacing: 12) {
                            HStack {
                                Image(systemName: "checkmark.seal.fill")
                                    .foregroundColor(.green)
                                Text("Conversion Results")
                                    .font(.headline)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)

                            ForEach(results) { result in
                                ConversionCard(result: result)
                            }
                        }
                        .transition(.scale.combined(with: .opacity))
                    }

                    // Conversion Reference
                    GroupBox(label: Label("Conversion Reference", systemImage: "book")) {
                        VStack(spacing: 12) {
                            ConversionReferenceRow(
                                from: "1 Kcal/Kg",
                                to: "4.184 KJ/Kg",
                                icon: "arrow.right"
                            )
                            Divider()
                            ConversionReferenceRow(
                                from: "1 Kcal/Kg",
                                to: "0.004184 MJ/Kg",
                                icon: "arrow.right"
                            )
                            Divider()
                            ConversionReferenceRow(
                                from: "1 Kcal/Kg",
                                to: "1.8 BTU/lb",
                                icon: "arrow.right"
                            )
                        }
                        .padding(.vertical, 8)
                    }

                    // Information Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Image(systemName: "info.circle.fill")
                                .foregroundColor(.blue)
                            Text("About Heat Values")
                                .font(.headline)
                        }

                        Text("Heat value (calorific value) represents the energy content of coal. Different units are used globally:")
                            .font(.subheadline)
                            .foregroundColor(.secondary)

                        VStack(alignment: .leading, spacing: 6) {
                            UnitInfoRow(unit: "Kcal/Kg", description: "Common in India & Asia", icon: "globe.asia.australia.fill")
                            UnitInfoRow(unit: "MJ/Kg", description: "SI unit (International)", icon: "globe")
                            UnitInfoRow(unit: "BTU/lb", description: "Common in USA", icon: "globe.americas.fill")
                            UnitInfoRow(unit: "KJ/Kg", description: "Alternative SI unit", icon: "globe.europe.africa.fill")
                        }
                    }
                    .padding()
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(12)
                }
                .padding()
            }
            .navigationTitle("Heat Value")
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    // MARK: - Conversion Logic

    private func convertValue() {
        guard let value = Double(inputValue) else { return }

        // Convert input to Kcal/Kg as base unit
        let kcalValue: Double
        switch inputUnit {
        case .kcalPerKg:
            kcalValue = value
        case .mjPerKg:
            kcalValue = value / 0.004184
        case .btuPerLb:
            kcalValue = value / 1.8
        case .kjPerKg:
            kcalValue = value / 4.184
        }

        // Convert to all units
        results = [
            ConversionResult(
                unit: .kcalPerKg,
                value: kcalValue,
                color: .orange
            ),
            ConversionResult(
                unit: .kjPerKg,
                value: kcalValue * 4.184,
                color: .blue
            ),
            ConversionResult(
                unit: .mjPerKg,
                value: kcalValue * 0.004184,
                color: .green
            ),
            ConversionResult(
                unit: .btuPerLb,
                value: kcalValue * 1.8,
                color: .red
            )
        ]

        withAnimation(.spring()) {
            showResults = true
        }

        // Haptic feedback
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(.success)
    }
}

// MARK: - Supporting Views

struct ConversionCard: View {
    let result: HeatValueView.ConversionResult

    var body: some View {
        HStack {
            Image(systemName: result.unit.icon)
                .font(.title2)
                .foregroundColor(result.color)
                .frame(width: 40)

            VStack(alignment: .leading, spacing: 4) {
                Text(result.unit.rawValue)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Text(String(format: "%.4f", result.value))
                    .font(.title3)
                    .fontWeight(.semibold)
            }

            Spacer()
        }
        .padding()
        .background(result.color.opacity(0.1))
        .cornerRadius(10)
    }
}

struct ConversionReferenceRow: View {
    let from: String
    let to: String
    let icon: String

    var body: some View {
        HStack {
            Text(from)
                .font(.subheadline)

            Spacer()

            Image(systemName: icon)
                .foregroundColor(.orange)

            Spacer()

            Text(to)
                .font(.subheadline)
                .fontWeight(.semibold)
        }
    }
}

struct UnitInfoRow: View {
    let unit: String
    let description: String
    let icon: String

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: icon)
                .foregroundColor(.blue)
                .frame(width: 20)
            Text(unit)
                .font(.caption)
                .fontWeight(.semibold)
                .frame(width: 60, alignment: .leading)
            Text(description)
                .font(.caption)
                .foregroundColor(.secondary)
        }
    }
}

struct HeatValueView_Previews: PreviewProvider {
    static var previews: some View {
        HeatValueView()
    }
}
