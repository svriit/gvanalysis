# Coal Analyzer - Features Documentation

## Overview
Coal Analyzer is a comprehensive iOS application designed for coal industry professionals, analysts, and researchers. It provides three powerful tools for coal analysis and heat value conversion.

---

## 1. Proximate Analysis

### Purpose
Perform complete proximate analysis of coal samples with automatic grade determination.

### Input Fields

#### Sample Information
- **Sample Name** - Unique identifier for the coal sample
- **Date** - Date of analysis (uses iOS native date picker)
- **Rack Number** - Storage location identifier
- **Source** - Origin or supplier of the coal sample

#### Moisture Parameters (in percentage)
- **Total Moisture (TM)** - Total water content in the coal sample
- **Inherent Moisture (IM)** - Moisture content in the air-dried sample
- **Equilibrial Moisture (EM)** - Moisture content at equilibrium with atmospheric conditions

#### Composition Parameters (in percentage)
- **Volatile Matter** - Gaseous products released when coal is heated
- **Ash** - Inorganic residue remaining after complete combustion

### Calculations Performed

#### 1. GCV ADB (Gross Calorific Value - Air Derived Basis)
**Formula:**
```
GCV ADB = (154 × (100 - (1.1 × ash + IM)) - (108 × IM)) / 1.8
```
**Unit:** Kcal/Kg

**Purpose:** Determines the heating value of coal on an air-dried basis, which is the standard basis for coal trading and grading in many countries.

#### 2. Factor
**Formula:**
```
Factor = (100 - TM) / (100 - IM)
```
**Purpose:** Conversion factor to relate different moisture bases.

#### 3. GCV ARB (Gross Calorific Value - As Received Basis)
**Formula:**
```
GCV ARB = Factor × GCV ADB
```
**Unit:** Kcal/Kg

**Purpose:** Represents the actual heating value of coal as it is received, including all moisture.

#### 4. Equilibrial Factor
**Formula:**
```
Equilibrial Factor = (100 - EM) / (100 - IM)
```
**Purpose:** Used to convert calorific values to equilibrium moisture basis.

### Grade Determination
The app automatically determines coal grade (G4 to G15 or "Below G15") based on the calculated GCV ADB value:

| Grade | GCV Range (Kcal/Kg) | Quality |
|-------|---------------------|---------|
| G4    | ≥ 6100             | Highest |
| G5    | 5800 - 6099        | High    |
| G6    | 5500 - 5799        | High    |
| G7    | 5200 - 5499        | Medium-High |
| G8    | 4900 - 5199        | Medium |
| G9    | 4600 - 4899        | Medium |
| G10   | 4300 - 4599        | Medium-Low |
| G11   | 4000 - 4299        | Low |
| G12   | 3700 - 3999        | Low |
| G13   | 3400 - 3699        | Very Low |
| G14   | 3100 - 3399        | Very Low |
| G15   | 2800 - 3099        | Lowest |

### UI Features
- **Grouped Input Sections** - Organized by category for easy data entry
- **Icon-based Labels** - Visual indicators for each field type
- **Smart Keyboard** - Numeric keyboard for percentage fields
- **Validation** - Calculate button only enabled when required fields are filled
- **Results Card** - Prominent display of coal grade with detailed calculations
- **Color Coding** - Different colors for different result types
- **Smooth Animations** - Results appear with spring animation
- **Haptic Feedback** - Tactile response on calculation completion

---

## 2. Coal Analysis

### Purpose
Reference tool for coal grades and quick grade lookup from GCV values.

### Features

#### Coal Grades Reference Table
- **Complete Grade List** - All grades from G4 to G15
- **GCV Values** - Corresponding calorific values for each grade
- **Interactive Table** - Tap-to-highlight functionality
- **Clean Layout** - Easy-to-read tabular format with dividers

#### Grade Finder Tool
- **Input Field** - Enter any GCV value (Kcal/Kg)
- **Quick Calculation** - Instant grade determination
- **Visual Feedback** - Large, prominent display of determined grade
- **Table Highlighting** - Automatically highlights the determined grade in the reference table

#### Educational Content
- **About Coal Grades** - Explanation of grading system
- **Quality Indicators** - Visual indicators for highest and lowest grades
- **Information Cards** - Context about GCV and its importance

### Use Cases
1. **Quick Reference** - Check grade specifications during meetings
2. **Grade Verification** - Confirm supplier claims
3. **Quality Assessment** - Determine coal quality from laboratory reports
4. **Educational** - Learn about coal grading standards

---

## 3. Heat Value Converter

### Purpose
Convert heat values between different international units commonly used in coal industry.

### Supported Units

#### 1. Kcal/Kg (Kilocalories per Kilogram)
- **Region:** India, China, and most Asian countries
- **Usage:** Standard unit for coal trading in India
- **Icon:** Flame icon

#### 2. MJ/Kg (Megajoules per Kilogram)
- **Region:** International (SI unit)
- **Usage:** Scientific publications, international standards
- **Icon:** Bolt icon
- **Conversion:** 1 Kcal/Kg = 0.004184 MJ/Kg

#### 3. BTU/lb (British Thermal Units per Pound)
- **Region:** United States, UK
- **Usage:** Coal trading in North America
- **Icon:** Thermometer icon
- **Conversion:** 1 Kcal/Kg = 1.8 BTU/lb

#### 4. KJ/Kg (Kilojoules per Kilogram)
- **Region:** Europe, Australia
- **Usage:** Alternative SI unit, energy calculations
- **Icon:** Bolt circle icon
- **Conversion:** 1 Kcal/Kg = 4.184 KJ/Kg

### Features

#### Unit Conversion
- **Multi-directional** - Convert from any unit to all others
- **High Precision** - 4 decimal places for accuracy
- **Segmented Picker** - Easy unit selection
- **Color-coded Results** - Each unit has a distinct color

#### Conversion Reference
Built-in reference card showing standard conversion factors:
- 1 Kcal/Kg = 4.184 KJ/Kg
- 1 Kcal/Kg = 0.004184 MJ/Kg
- 1 Kcal/Kg = 1.8 BTU/lb

#### Educational Content
- **Unit Descriptions** - Where each unit is commonly used
- **Regional Information** - Geographic usage with flag icons
- **About Heat Values** - Explanation of calorific value concept

### Use Cases
1. **International Trading** - Convert between regional units
2. **Report Preparation** - Present data in required format
3. **Quality Comparison** - Compare coal from different regions
4. **Academic Use** - Convert units for research papers

---

## General App Features

### User Interface

#### Design Philosophy
- **Clean & Modern** - Contemporary iOS design language
- **Professional** - Suitable for industrial use
- **Intuitive** - Minimal learning curve
- **Consistent** - Uniform design across all sections

#### Visual Elements
- **Orange Accent Color** - Represents energy and coal industry
- **SF Symbols** - Native iOS icons throughout
- **Gradient Buttons** - Attractive call-to-action elements
- **Card-based Layout** - Information organized in cards
- **Groupboxes** - Sectioned content with headers

#### Typography
- **System Font** - Native iOS San Francisco font
- **Size Hierarchy** - Clear visual hierarchy
- **Semibold Headers** - Prominent section titles
- **Secondary Text** - Descriptive helper text

### User Experience

#### Interactions
- **Haptic Feedback** - Tactile response for actions
- **Smooth Animations** - Spring-based transitions
- **Keyboard Management** - Auto-dismiss with toolbar
- **Focus Management** - Smart field navigation
- **Scroll Views** - Smooth scrolling for all content

#### Accessibility
- **Large Touch Targets** - Easy to tap buttons and fields
- **Clear Labels** - Descriptive field names
- **Icon Support** - Visual reinforcement of text
- **Readable Text** - Appropriate font sizes

### Navigation

#### Tab-based Structure
Three main tabs at the bottom:
1. **Proximate Analysis** - Chart icon
2. **Coal Analysis** - Flame icon
3. **Heat Value** - Thermometer icon

#### Benefits
- **Quick Switching** - Instant access to any section
- **Persistent State** - Each tab maintains its state
- **Visual Indicators** - Active tab highlighted
- **Badge Support** - Ready for notifications (future)

---

## Technical Highlights

### Performance
- **Instant Calculations** - Real-time computation
- **Smooth 60 FPS** - Optimized animations
- **Low Memory** - Efficient resource usage
- **Quick Launch** - Fast app startup

### Reliability
- **Input Validation** - Prevents invalid calculations
- **Error Handling** - Graceful handling of edge cases
- **Division by Zero** - Protected calculations
- **Range Checking** - Appropriate value ranges

### Compatibility
- **iOS 16.0+** - Wide device support
- **iPhone & iPad** - Universal app
- **All Orientations** - Portrait and landscape
- **Light Mode** - Optimized for default iOS appearance

---

## Future Enhancement Ideas

### Data Management
- Save analysis results
- Export to PDF/Excel
- Analysis history
- Cloud sync

### Advanced Features
- Multiple sample comparison
- Trend analysis with charts
- Custom grade definitions
- Batch calculations

### Collaboration
- Share results via email/messages
- Generate professional reports
- Team workspaces
- Cloud storage integration

### Personalization
- Dark mode support
- Custom themes
- Favorite calculations
- Quick access shortcuts

---

## Conclusion

Coal Analyzer combines professional-grade calculations with modern iOS design to create a powerful tool for coal industry professionals. Whether you're performing proximate analysis, checking coal grades, or converting between international units, this app provides accurate, fast, and reliable results in an intuitive interface.

**Built with:** SwiftUI, Swift 5.0+, iOS 16.0+
**Last Updated:** January 2026
