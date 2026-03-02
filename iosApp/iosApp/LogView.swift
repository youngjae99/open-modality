import SwiftUI
import Shared

struct LogView: View {
    @ObservedObject var viewModel: ServerViewModel

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.requestLog.isEmpty {
                    emptyState
                } else {
                    logList
                }
            }
            .navigationTitle("Request Log")
            .toolbar {
                if !viewModel.requestLog.isEmpty {
                    ToolbarItem(placement: .topBarTrailing) {
                        Text("\(viewModel.requestLog.count)")
                            .font(.caption.weight(.medium))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.gray.opacity(0.2))
                            .clipShape(Capsule())
                    }
                }
            }
        }
    }

    private var emptyState: some View {
        VStack(spacing: 12) {
            Image(systemName: "tray")
                .font(.system(size: 40))
                .foregroundStyle(.tertiary)
            Text("No Requests Yet")
                .font(.headline)
            Text("MCP tool calls will appear here\nwhen clients connect to the server.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private var logList: some View {
        List {
            ForEach(
                Array(viewModel.requestLog.reversed().enumerated()),
                id: \.offset
            ) { _, entry in
                RequestLogRow(entry: entry)
            }
        }
    }
}

// MARK: - Log Row

struct RequestLogRow: View {
    let entry: RequestLogEntry

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(entry.toolName ?? entry.method)
                    .font(.system(.body, design: .monospaced))
                HStack(spacing: 8) {
                    if let duration = entry.durationMs {
                        Text("\(duration)ms")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                    if let error = entry.error {
                        Text(error)
                            .font(.caption)
                            .foregroundStyle(.red)
                            .lineLimit(1)
                    }
                }
            }
            Spacer()
            Image(systemName: entry.success ? "checkmark.circle.fill" : "xmark.circle.fill")
                .foregroundStyle(entry.success ? .green : .red)
                .imageScale(.small)
        }
    }
}
