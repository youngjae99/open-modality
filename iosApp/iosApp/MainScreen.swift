import SwiftUI
import Shared

struct MainScreen: View {
    @ObservedObject var viewModel: ServerViewModel

    var body: some View {
        TabView {
            DashboardView(viewModel: viewModel)
                .tabItem {
                    Label("Dashboard", systemImage: "gauge.open.with.lines.needle.33percent")
                }

            SensorsView(viewModel: viewModel)
                .tabItem {
                    Label("Sensors", systemImage: "sensor.fill")
                }

            LogView(viewModel: viewModel)
                .tabItem {
                    Label("Log", systemImage: "list.bullet.rectangle")
                }
        }
    }
}
