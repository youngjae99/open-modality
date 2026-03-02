import SwiftUI
import Shared

struct DashboardView: View {
    @ObservedObject var viewModel: ServerViewModel

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    ServerStatusCard(
                        isRunning: viewModel.isRunning,
                        onToggle: { viewModel.toggleServer() }
                    )

                    if viewModel.isRunning {
                        ConnectionInfoCard(ipAddress: viewModel.ipAddress)

                        ActiveToolsCard(toolCount: viewModel.toolCount)
                    }
                }
                .padding()
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("Open Modality")
        }
    }
}

// MARK: - Server Status

struct ServerStatusCard: View {
    let isRunning: Bool
    let onToggle: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 8) {
                        Circle()
                            .fill(isRunning ? Color.green : Color(.systemGray3))
                            .frame(width: 10, height: 10)
                        Text(isRunning ? "Server Running" : "Server Stopped")
                            .font(.headline)
                    }
                    Text(isRunning
                         ? "Accepting MCP connections on port 8080"
                         : "Tap Start to begin accepting connections")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                Spacer()
            }

            Button(action: onToggle) {
                Text(isRunning ? "Stop Server" : "Start Server")
                    .font(.body.weight(.semibold))
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(isRunning ? .red : .accentColor)
            .controlSize(.large)
        }
        .padding()
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
    }
}

// MARK: - Connection Info

struct ConnectionInfoCard: View {
    let ipAddress: String
    @State private var copiedCmd = false
    @State private var copiedJson = false

    private var cliCommand: String {
        "claude mcp add --transport http open-modality http://\(ipAddress):8080/mcp"
    }

    private var configJSON: String {
        "{\n  \"mcpServers\": {\n    \"open-modality\": {\n      \"url\": \"http://\(ipAddress):8080/mcp\"\n    }\n  }\n}"
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Label("MCP Connection", systemImage: "link")
                .font(.headline)

            // CLI command section
            VStack(alignment: .leading, spacing: 8) {
                Text("Claude Code CLI:")
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(.secondary)

                Text(cliCommand)
                    .font(.system(.caption, design: .monospaced))
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.tertiarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 8))

                Button {
                    UIPasteboard.general.string = cliCommand
                    copiedCmd = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        copiedCmd = false
                    }
                } label: {
                    Label(
                        copiedCmd ? "Copied!" : "Copy Command",
                        systemImage: copiedCmd ? "checkmark" : "doc.on.doc"
                    )
                    .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }

            Divider()

            // JSON config section
            VStack(alignment: .leading, spacing: 8) {
                Text("Or add to MCP config file:")
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(.secondary)

                Text(configJSON)
                    .font(.system(.caption, design: .monospaced))
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.tertiarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 8))

                Button {
                    UIPasteboard.general.string = configJSON
                    copiedJson = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        copiedJson = false
                    }
                } label: {
                    Label(
                        copiedJson ? "Copied!" : "Copy Config JSON",
                        systemImage: copiedJson ? "checkmark" : "doc.on.doc"
                    )
                    .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }
        }
        .padding()
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
    }
}

// MARK: - Active Tools

struct ActiveToolsCard: View {
    let toolCount: Int

    var body: some View {
        HStack {
            Label("\(toolCount) MCP tools registered", systemImage: "wrench.and.screwdriver")
                .font(.subheadline)
                .foregroundStyle(.secondary)
            Spacer()
        }
        .padding()
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
    }
}
