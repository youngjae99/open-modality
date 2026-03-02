import Foundation
import Shared
import Combine

class ServerViewModel: ObservableObject {
    @Published var isRunning = false
    @Published var requestLog: [RequestLogEntry] = []
    @Published var ipAddress: String = "unknown"

    private var mcpServer: McpServer?
    private var timer: Timer?

    init() {
        let sensors = PlatformSensors()
        let sessionManager = McpSessionManager()
        let json = Kotlinx_serialization_jsonJson.companion as! Kotlinx_serialization_jsonJson
        let toolRegistry = SensorToolRegistry(sensors: sensors, json: json)

        mcpServer = McpServer(
            tools: toolRegistry.registerAll(),
            resources: toolRegistry.registerResources(),
            sessionManager: sessionManager,
            port: 8080
        )

        ipAddress = getLocalIPAddress()
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
