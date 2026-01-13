# GV Analysis Converter - Android App

An Android application for coal analysis calculations with three main sections: Proximate Analysis, Coal Analysis, and Heat Value.

## Features

### 1. Proximate Analysis (Implemented)
A comprehensive calculator for coal proximate analysis with the following features:

#### Input Parameters:
- Sample name
- Date (with date picker)
- Rack number
- Source
- Total Moisture (TM) %
- Inherent Moisture (IM) %
- Equilibrial Moisture (EM) %
- Volatile Matter (VM) %
- Ash %

#### Calculations:
- **GCV ADB (Gross Calorific Value - Air Dried Basis)**
  - Formula: `(154 * (100 - (1.1 * ash + IM)) - (108 * IM)) / 1.8`

- **Factor**
  - Formula: `(100 - TM) / (100 - IM)`

- **GCV ARB (As Received Basis)**
  - Formula: `Factor * GCV ADB`

- **Equilibrial Factor**
  - Formula: `(100 - EM) / (100 - IM)`

#### Coal Grading System:
The app automatically determines coal grade based on GCV ARB values:
- **Grade A**: > 6200 kcal/kg
- **Grade B**: 5600-6200 kcal/kg
- **Grade C**: 4940-5600 kcal/kg
- **Grade D**: 4200-4940 kcal/kg
- **Grade E**: 3360-4200 kcal/kg
- **Grade F**: 2400-3360 kcal/kg
- **Grade G**: 1300-2400 kcal/kg

Each grade is displayed with a color-coded indicator for easy identification.

### 2. Coal Analysis (Coming Soon)
Future section for additional coal analysis calculations.

### 3. Heat Value (Coming Soon)
Future section for heat value calculations.

## Technical Details

### Built With:
- **Language**: Kotlin
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Architecture**: Single Activity with Fragment-based navigation
- **UI Components**: Material Design 3

### Dependencies:
- AndroidX Core KTX
- AppCompat
- Material Design Components
- ConstraintLayout
- ViewPager2
- Fragment KTX
- Lifecycle Components

## UI/UX Features

- **Responsive Design**: Optimized for various screen sizes
- **Interactive UI**: Real-time input validation with error messages
- **Material Design**: Modern, clean interface following Material Design 3 guidelines
- **Tab Navigation**: Easy switching between different analysis sections
- **Date Picker**: User-friendly date selection
- **Clear Functionality**: Quick reset of all input fields
- **Results Display**: Clear, organized presentation of calculated values
- **Color-Coded Grades**: Visual indication of coal quality

## Project Structure

```
app/src/main/
├── java/com/gvanalysis/converter/
│   ├── MainActivity.kt
│   ├── ProximateAnalysisFragment.kt
│   ├── CoalAnalysisFragment.kt
│   └── HeatValueFragment.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── fragment_proximate_analysis.xml
│   │   ├── fragment_coal_analysis.xml
│   │   └── fragment_heat_value.xml
│   ├── values/
│   │   ├── colors.xml
│   │   ├── strings.xml
│   │   └── themes.xml
│   └── values-night/
│       └── themes.xml
└── AndroidManifest.xml
```

## How to Build

1. Clone this repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on an Android device or emulator

### Requirements:
- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK with API 34

## Usage

1. Launch the app
2. Navigate to the "Proximate Analysis" tab
3. Fill in all required fields:
   - Enter sample details (name, date, rack number, source)
   - Input moisture percentages (Total, Inherent, Equilibrial)
   - Enter volatile matter and ash percentages
4. Tap "Calculate" to see results
5. View calculated values and coal grade
6. Use "Clear" to reset all fields

## Input Validation

- All fields are required
- Numeric values must be between 0 and 100
- Date can be selected using the date picker
- Real-time error messages guide proper input

## Future Enhancements

- Implementation of Coal Analysis section
- Implementation of Heat Value section
- Data persistence and history
- Export results to PDF/CSV
- Database integration for storing analysis records
- Charts and graphs for trend analysis
- Multi-language support

## License

This project is part of GV Analysis tools.

## Contact

For questions or feedback, please contact the development team.
