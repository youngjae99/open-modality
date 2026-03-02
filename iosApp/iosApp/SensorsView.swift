import SwiftUI
import Shared

struct SensorsView: View {
    @ObservedObject var viewModel: ServerViewModel

    var body: some View {
        NavigationStack {
            List {
                ForEach(viewModel.sensorCategories) { category in
                    Section {
                        ForEach(category.sensors) { sensor in
                            SensorRow(sensor: sensor)
                        }
                    } header: {
                        Label(category.name, systemImage: category.icon)
                    }
                }
            }
            .navigationTitle("Sensors")
        }
    }
}

// MARK: - Sensor Row

struct SensorRow: View {
    let sensor: SensorDisplayInfo

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(sensor.name)
                    .font(.body)
                Text(sensor.id)
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            Spacer()
            PermissionBadge(status: sensor.permission)
        }
    }
}

// MARK: - Permission Badge

struct PermissionBadge: View {
    let status: String

    var body: some View {
        Text(displayText)
            .font(.caption2.weight(.medium))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(badgeColor.opacity(0.15))
            .foregroundStyle(badgeColor)
            .clipShape(Capsule())
    }

    private var displayText: String {
        switch status {
        case "GRANTED": return "granted"
        case "DENIED": return "denied"
        case "RESTRICTED": return "restricted"
        default: return "not requested"
        }
    }

    private var badgeColor: Color {
        switch status {
        case "GRANTED": return .green
        case "DENIED": return .red
        case "RESTRICTED": return .orange
        default: return .gray
        }
    }
}
