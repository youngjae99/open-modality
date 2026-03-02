import SwiftUI
import Shared

struct MainScreen: View {
    @ObservedObject var viewModel: ServerViewModel

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                VStack(spacing: 4) {
                    Text("Open Modality")
                        .font(.system(size: 28, weight: .bold))
                    Text("Smartphone Sensor MCP Server")
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                }
                .padding(.top, 24)

                ServerStatusCard(
                    isRunning: viewModel.isRunning,
                    onToggle: { viewModel.toggleServer() }
                )

                if viewModel.isRunning {
                    ConnectionInfoCard(ipAddress: viewModel.ipAddress)
                }

                VStack(alignment: .leading, spacing: 8) {
                    Text("Request Log")
                        .font(.system(size: 16, weight: .semibold))

                    if viewModel.requestLog.isEmpty {
                        Text("No requests yet")
                            .font(.system(size: 13))
                            .foregroundColor(.secondary)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .padding(.vertical, 20)
                    } else {
                        ForEach(
                            Array(viewModel.requestLog.reversed().enumerated()),
                            id: \.offset
                        ) { _, entry in
                            RequestLogItem(entry: entry)
                        }
                    }
                }
            }
            .padding(.horizontal, 24)
        }
    }
}

struct ServerStatusCard: View {
    let isRunning: Bool
    let onToggle: () -> Void

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(isRunning ? "Server Running" : "Server Stopped")
                    .font(.system(size: 18, weight: .semibold))
                Text(isRunning ? "Accepting MCP connections" : "Tap to start")
                    .font(.system(size: 13))
                    .foregroundColor(.secondary)
            }
            Spacer()
            Button(action: onToggle) {
                Text(isRunning ? "Stop" : "Start")
                    .fontWeight(.medium)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(isRunning ? Color.red.opacity(0.8) : Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(isRunning ? Color.blue.opacity(0.1) : Color(.systemGray6))
        )
    }
}

struct ConnectionInfoCard: View {
    let ipAddress: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Connect from Claude Code:")
                .font(.system(size: 14, weight: .semibold))

            Text("""
            {
              "mcpServers": {
                "open-modality": {
                  "url": "http://\(ipAddress):8080/mcp"
                }
              }
            }
            """)
            .font(.system(size: 11, design: .monospaced))
            .padding(12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(.systemGray6))
            .cornerRadius(8)
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemBackground))
                .shadow(color: .black.opacity(0.05), radius: 2, y: 1)
        )
    }
}

struct RequestLogItem: View {
    let entry: SharedRequestLogEntry

    var body: some View {
        HStack {
            Text(entry.toolName ?? entry.method)
                .font(.system(size: 13, design: .monospaced))
            Spacer()
            if let duration = entry.durationMs {
                Text("\(duration)ms")
                    .font(.system(size: 12))
                    .foregroundColor(.secondary)
            }
        }
        .padding(8)
        .background(
            entry.success
                ? Color.clear
                : Color.red.opacity(0.1)
        )
        .cornerRadius(4)
    }
}
