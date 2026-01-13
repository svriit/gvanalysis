# Coal Analyzer - iOS App

A comprehensive iOS application for coal analysis and heat value conversion, built with SwiftUI.

## Features

### 1. Proximate Analysis
Complete proximate analysis calculator with the following features:
- **Input Parameters:**
  - Sample Name
  - Date
  - Rack Number
  - Source
  - Total Moisture (TM) %
  - Inherent Moisture (IM) %
  - Equilibrial Moisture (EM) %
  - Volatile Matter %
  - Ash %

- **Calculations:**
  - GCV ADB (Air Derived Basis): `(154*(100-(1.1*ash+IM))-(108*IM))/1.8`
  - Factor: `(100-TM)/(100-IM)`
  - GCV ARB (As Received Basis): `factor * GCV ADB`
  - Equilibrial Factor: `(100-EM)/(100-IM)`
  - Automatic coal grade determination (G4 to G15)

### 2. Coal Analysis
- **Coal Grades Reference Table:** View all coal grades (G4-G15) with their corresponding GCV values
- **Grade Finder:** Input any GCV value to determine the corresponding coal grade
- Interactive table highlighting
- Educational information about coal grading system

### 3. Heat Value Converter
Convert between different heat value units:
- Kcal/Kg (Common in India & Asia)
- MJ/Kg (SI unit - International)
- BTU/lb (Common in USA)
- KJ/Kg (Alternative SI unit)

**Conversion Formulas:**
- 1 Kcal/Kg = 4.184 KJ/Kg
- 1 Kcal/Kg = 0.004184 MJ/Kg
- 1 Kcal/Kg = 1.8 BTU/lb

## Coal Grades Reference

| Grade | GCV (Kcal/Kg) |
|-------|---------------|
| G4    | ≥ 6100        |
| G5    | 5800          |
| G6    | 5500          |
| G7    | 5200          |
| G8    | 4900          |
| G9    | 4600          |
| G10   | 4300          |
| G11   | 4000          |
| G12   | 3700          |
| G13   | 3400          |
| G14   | 3100          |
| G15   | 2800          |

## Technical Details

### Requirements
- iOS 16.0+
- Xcode 14.0+
- Swift 5.0+

### Architecture
The app follows a clean SwiftUI architecture with:
- **Models:** Data structures for coal grades and analysis results
- **Views:** SwiftUI views for each section
- **Services:** Business logic and calculation services

### Project Structure
```
CoalAnalyzer/
├── CoalAnalyzer.xcodeproj/
│   └── project.pbxproj
├── CoalAnalyzer/
│   ├── CoalAnalyzerApp.swift       # App entry point
│   ├── ContentView.swift            # Main tab view
│   ├── Views/
│   │   ├── ProximateAnalysisView.swift
│   │   ├── CoalAnalysisView.swift
│   │   └── HeatValueView.swift
│   ├── Models/
│   │   └── CoalGrade.swift         # Coal grade data model
│   ├── Services/
│   │   └── CalculationService.swift # Calculation logic
│   ├── Assets.xcassets/            # App assets
│   └── Info.plist                  # App configuration
└── README.md
```

## How to Build

1. Open `CoalAnalyzer.xcodeproj` in Xcode
2. Select a target device or simulator (iPhone or iPad)
3. Press `Cmd + R` to build and run

## How to Use

### Proximate Analysis
1. Navigate to the "Proximate Analysis" tab
2. Fill in all required fields:
   - Sample information (name, date, rack number, source)
   - Moisture parameters (TM, IM, EM)
   - Composition parameters (volatile matter, ash)
3. Tap "Calculate" to see results
4. View calculated GCV values, factors, and coal grade

### Coal Analysis
1. Navigate to the "Coal Analysis" tab
2. Browse the coal grades reference table
3. To find a grade from GCV:
   - Enter a GCV value in the input field
   - Tap "Find Grade"
   - The determined grade will be displayed

### Heat Value Converter
1. Navigate to the "Heat Value" tab
2. Enter a heat value
3. Select the input unit (Kcal/Kg, MJ/Kg, BTU/lb, or KJ/Kg)
4. Tap "Convert"
5. View conversions in all available units

## UI Features

- **Modern Design:** Clean, professional interface with orange accent color
- **Interactive:** Smooth animations and haptic feedback
- **Responsive:** Works on iPhone and iPad in portrait and landscape
- **User-Friendly:** Clear labels, helpful icons, and informative descriptions
- **Keyboard Management:** Smart keyboard dismissal and toolbar
- **Color-Coded Results:** Easy-to-read results with color coding
- **Sectioned Input:** Grouped inputs for better organization

## Formulas Used

### GCV ADB (Air Derived Basis)
```
GCV ADB = (154 * (100 - (1.1 * ash + IM)) - (108 * IM)) / 1.8
```

### Factor (for ARB calculation)
```
Factor = (100 - TM) / (100 - IM)
```

### GCV ARB (As Received Basis)
```
GCV ARB = Factor * GCV ADB
```

### Equilibrial Factor
```
Equilibrial Factor = (100 - EM) / (100 - IM)
```

## Future Enhancements

Potential features for future versions:
- Data persistence (save analysis results)
- Export results to PDF/CSV
- History of calculations
- Dark mode support
- Offline documentation
- Multi-language support
- Charts and graphs for analysis trends

## License

This project is created for educational and professional use in coal analysis.

## Support

For issues or questions, please refer to the project documentation or contact the development team.

---

**Version:** 1.0
**Last Updated:** January 2026
**Platform:** iOS 16.0+
