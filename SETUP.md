# Coal Analyzer - Setup Guide

This guide will help you set up and run the Coal Analyzer iOS application.

## Prerequisites

Before you begin, ensure you have:
- **macOS** (Ventura 13.0 or later recommended)
- **Xcode 14.0+** installed from the Mac App Store
- **iOS 16.0+** device or simulator

## Installation Steps

### 1. Clone or Download the Repository

```bash
git clone <repository-url>
cd gvanalysis
```

### 2. Open the Project

Navigate to the CoalAnalyzer directory and open the Xcode project:

```bash
cd CoalAnalyzer
open CoalAnalyzer.xcodeproj
```

Alternatively, you can:
- Double-click `CoalAnalyzer.xcodeproj` in Finder
- Or open Xcode and use File → Open, then select the project

### 3. Configure Code Signing

1. In Xcode, select the **CoalAnalyzer** project in the navigator
2. Select the **CoalAnalyzer** target
3. Go to the **Signing & Capabilities** tab
4. Select your **Team** from the dropdown (you may need to add your Apple ID)
5. Xcode will automatically manage the provisioning profile

### 4. Select a Target Device

In the Xcode toolbar:
- Click on the device selector (next to the Run button)
- Choose either:
  - An iOS Simulator (e.g., iPhone 15, iPhone 15 Pro)
  - A connected physical iOS device

### 5. Build and Run

Press **Cmd + R** or click the **Run** button (▶️) in Xcode toolbar.

The app will:
1. Build (compile all Swift files)
2. Launch on your selected device/simulator
3. Be ready to use!

## Project Structure

```
CoalAnalyzer/
├── CoalAnalyzer.xcodeproj/         # Xcode project file
├── CoalAnalyzer/                   # Source code directory
│   ├── CoalAnalyzerApp.swift       # App entry point (@main)
│   ├── ContentView.swift           # Main tab view container
│   │
│   ├── Views/                      # UI Views
│   │   ├── ProximateAnalysisView.swift
│   │   ├── CoalAnalysisView.swift
│   │   └── HeatValueView.swift
│   │
│   ├── Models/                     # Data Models
│   │   └── CoalGrade.swift
│   │
│   ├── Services/                   # Business Logic
│   │   └── CalculationService.swift
│   │
│   ├── Assets.xcassets/            # Images, colors, icons
│   │   ├── AppIcon.appiconset/
│   │   └── AccentColor.colorset/
│   │
│   └── Info.plist                  # App configuration
│
└── README.md                       # Documentation
```

## Troubleshooting

### Issue: "No Developer Account"
**Solution:**
- Go to Xcode → Settings → Accounts
- Add your Apple ID
- Use this account for code signing

### Issue: "Unable to Boot Simulator"
**Solution:**
- Restart Xcode
- Or reset the simulator: Device → Erase All Content and Settings

### Issue: Build Errors
**Solution:**
- Clean build folder: Product → Clean Build Folder (Cmd + Shift + K)
- Rebuild: Product → Build (Cmd + B)

### Issue: Module Not Found
**Solution:**
- Ensure all files are added to the target
- Check target membership in File Inspector

## Testing the App

### Test Proximate Analysis
1. Open the app
2. Go to "Proximate Analysis" tab
3. Enter sample data:
   - Sample Name: "Sample 1"
   - Total Moisture: 10
   - Inherent Moisture: 5
   - Equilibrial Moisture: 7
   - Volatile Matter: 30
   - Ash: 15
4. Tap "Calculate"
5. Verify results are displayed with coal grade

### Test Coal Analysis
1. Go to "Coal Analysis" tab
2. View the grades table
3. Enter GCV: 5800
4. Tap "Find Grade"
5. Should show "G5"

### Test Heat Value Converter
1. Go to "Heat Value" tab
2. Enter value: 5000
3. Select unit: Kcal/Kg
4. Tap "Convert"
5. Verify conversions appear for all units

## Running on Physical Device

1. Connect your iPhone/iPad via USB
2. Trust the computer on your device if prompted
3. Select your device in Xcode's device selector
4. Run the app (Cmd + R)
5. On first run, go to Settings → General → VPN & Device Management
6. Trust your developer certificate

## Customization

### Change App Name
Edit `Info.plist`:
```xml
<key>CFBundleDisplayName</key>
<string>Your App Name</string>
```

### Change Bundle Identifier
1. Select project in navigator
2. Select target
3. Change "Bundle Identifier" in General tab

### Change Accent Color
1. Open `Assets.xcassets`
2. Select `AccentColor`
3. Modify color values

## Development

### Swift Version
- **Swift 5.0+**
- **SwiftUI** for UI
- **Combine** framework for reactive programming

### iOS Deployment Target
- **Minimum:** iOS 16.0
- **Recommended:** iOS 17.0+

### Supported Devices
- iPhone (all models running iOS 16.0+)
- iPad (all models running iOS 16.0+)

### Orientation Support
- Portrait
- Landscape Left
- Landscape Right
- iPad: All orientations including Portrait Upside Down

## Next Steps

After successful setup:
1. Explore the three main sections
2. Test all calculations with sample data
3. Verify coal grade determination
4. Test unit conversions
5. Try on different device sizes

## Additional Resources

- [Swift Documentation](https://swift.org/documentation/)
- [SwiftUI Tutorials](https://developer.apple.com/tutorials/swiftui)
- [Xcode Documentation](https://developer.apple.com/xcode/)

## Support

For issues or questions:
- Check the README.md for detailed feature documentation
- Review this setup guide
- Check Xcode console for error messages

---

**Happy Coding!** 🚀
