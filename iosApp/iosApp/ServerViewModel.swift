import Foundation
import Shared
import Combine

// MARK: - Display Models

struct SensorDisplayInfo: Identifiable {
    let id: String
    let name: String
    let permission: String
}

struct SensorCategoryInfo: Identifiable {
    let id: String
    let name: String
    let icon: String
    let sensors: [SensorDisplayInfo]
}

// MARK: - ViewModel

class ServerViewModel: ObservableObject {
    @Published var isRunning = false
    @Published var requestLog: [RequestLogEntry] = []
    @Published var ipAddress: String = "unknown"
    @Published var sensorCategories: [SensorCategoryInfo] = []
    @Published var toolCount: Int = 0

    private let platformSensors: PlatformSensors
    private var mcpServer: McpServer?
    private var timer: Timer?

    init() {
        platformSensors = PlatformSensors()
        let sessionManager = McpSessionManager()
        let json = Kotlinx_serialization_jsonJson.companion as! Kotlinx_serialization_jsonJson
        let toolRegistry = SensorToolRegistry(sensors: platformSensors, json: json)

        let tools = toolRegistry.registerAll()
        toolCount = Int(tools.count)

        mcpServer = McpServer(
            tools: tools,
            resources: toolRegistry.registerResources(),
            sessionManager: sessionManager,
            port: 8080
        )

        ipAddress = getLocalIPAddress()
        loadSensors()
    }

    func toggleServer() {
        guard let server = mcpServer else { return }
        if isRunning {
            server.stop()
            isRunning = false
            timer?.invalidate()
            timer = nil
        } else {
            server.start()
            isRunning = true
            startPollingLog()
        }
    }

    // MARK: - Private

    private func startPollingLog() {
        timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
            guard let self = self, let server = self.mcpServer else { return }
            if let logs = server.sessions.requestLog.value as? [RequestLogEntry] {
                DispatchQueue.main.async {
                    self.requestLog = logs
                }
            }
        }
    }

    private func loadSensors() {
        let available = platformSensors.availableSensors()

        let categoryMeta: [(key: String, name: String, icon: String)] = [
            ("VISION", "Vision", "camera"),
            ("AUDIO", "Audio", "waveform"),
            ("LOCATION", "Location", "location"),
            ("MOTION", "Motion", "gyroscope"),
            ("ENVIRONMENT", "Environment", "cloud.sun"),
            ("CONNECTIVITY", "Connectivity", "antenna.radiowaves.left.and.right"),
            ("DEVICE", "Device", "iphone"),
        ]

        var grouped: [String: [SensorDisplayInfo]] = [:]

        for sensor in available {
            guard let sensorType = sensor as? SensorType else { continue }
            let catKey = sensorType.category.name
            let perm = platformSensors.permissionStatus(sensor: sensorType)
            let info = SensorDisplayInfo(
                id: sensorType.id,
                name: sensorType.displayName,
                permission: perm.name
            )
            grouped[catKey, default: []].append(info)
        }

        sensorCategories = categoryMeta.compactMap { meta in
            guard let sensors = grouped[meta.key], !sensors.isEmpty else { return nil }
            return SensorCategoryInfo(
                id: meta.key,
                name: meta.name,
                icon: meta.icon,
                sensors: sensors
            )
        }
    }

    private func getLocalIPAddress() -> String {
        var address = "unknown"
        var ifaddr: UnsafeMutablePointer<ifaddrs>?

        guard getifaddrs(&ifaddr) == 0, let firstAddr = ifaddr else {
            return address
        }
        defer { freeifaddrs(ifaddr) }

        for ptr in sequence(first: firstAddr, next: { $0.pointee.ifa_next }) {
            let interface = ptr.pointee
            let addrFamily = interface.ifa_addr.pointee.sa_family

            if addrFamily == UInt8(AF_INET) {
                let name = String(cString: interface.ifa_name)
                if name == "en0" || name == "en1" {
                    var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                    getnameinfo(
                        interface.ifa_addr,
                        socklen_t(interface.ifa_addr.pointee.sa_len),
                        &hostname,
                        socklen_t(hostname.count),
                        nil, 0,
                        NI_NUMERICHOST
                    )
                    address = String(cString: hostname)
                    break
                }
            }
        }
        return address
    }
}
