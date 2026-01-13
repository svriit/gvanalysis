//
//  ContentView.swift
//  CoalAnalyzer
//
//  Main View with Three Sections
//

import SwiftUI

struct ContentView: View {
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            ProximateAnalysisView()
                .tabItem {
                    Label("Proximate Analysis", systemImage: "chart.bar.doc.horizontal")
                }
                .tag(0)

            CoalAnalysisView()
                .tabItem {
                    Label("Coal Analysis", systemImage: "flame")
                }
                .tag(1)

            HeatValueView()
                .tabItem {
                    Label("Heat Value", systemImage: "thermometer")
                }
                .tag(2)
        }
        .accentColor(.orange)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
