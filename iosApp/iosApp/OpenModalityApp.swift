import SwiftUI

@main
struct OpenModalityApp: App {
    @StateObject private var serverViewModel = ServerViewModel()

    var body: some Scene {
        WindowGroup {
            MainScreen(viewModel: serverViewModel)
        }
    }
}
