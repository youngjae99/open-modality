<p align="center">
  <picture>
    <source srcset="assets/open-modality-dark.svg" media="(prefers-color-scheme: dark)">
    <img src="assets/open-modality.svg" width="300" alt="Open Modality Logo">
</picture>
</p>

<h1 align="center">Open Modality</h1>
<p align="center">Give AI senses. Your phone's sensors as MCP tools.</p>

<p align="center">
  <a href="#quickstart">Quickstart</a> ·
  <a href="#how-it-works">How it works</a> ·
  <a href="#available-tools">Tools</a> ·
  <a href="#building-from-source">Build</a> ·
  <a href="#contributing">Contributing</a>
</p>

---

Open Modality turns your smartphone into a sensor gateway for AI agents. It runs a local [MCP](https://modelcontextprotocol.io/) server on your phone, exposing every hardware sensor as a tool that Claude Code, Claude Desktop, or any MCP-compatible client can call over the network.

No cloud. No middleman. Your phone, your sensors, your data.

## Quickstart

1. **Install the app** on your Android or iOS device
2. **Tap Start** to launch the MCP server
3. **Add to your MCP client** — the app shows the config:

```json
{
  "mcpServers": {
    "open-modality": {
      "url": "http://<phone-ip>:8080/mcp"
    }
  }
}
```

That's it. Your AI can now see, hear, and feel the world through your phone.

## How it works

```
Claude Code / Desktop          Phone (Open Modality)
┌─────────────────┐           ┌──────────────────────┐
│                 │   MCP     │  Ktor HTTP Server    │
│  MCP Client     │◄─────────►│  :8080/mcp           │
│                 │  (WiFi)   │                      │
│  tools/call     │           │  SensorToolRegistry  │
│  "get_location" │──────────►│  → PlatformSensors   │
│                 │           │  → GPS / Accel / ... │
└─────────────────┘           └──────────────────────┘
```

- **Transport**: MCP Streamable HTTP — `POST /mcp` for requests, `GET /mcp/sse` for streaming
- **Protocol**: JSON-RPC 2.0 with standard MCP `tools/list`, `tools/call`, `resources/list`, `resources/read`
- **Architecture**: Kotlin Multiplatform — shared MCP server and sensor abstraction, native implementations per platform

## Available tools

| Tool | Description | Android | iOS |
|------|-------------|:-------:|:---:|
| `get_location` | GPS coordinates, altitude, speed, heading | Y | Y |
| `get_address` | Reverse geocoding (street, city, country) | Y | Y |
| `read_accelerometer` | 3-axis acceleration (m/s²) | Y | Y |
| `read_gyroscope` | 3-axis rotation rate (rad/s) | Y | Y |
| `read_magnetometer` | 3-axis magnetic field (μT) | Y | Y |
| `get_device_motion` | Fused attitude, gravity, user acceleration | Y | Y |
| `get_pedometer` | Step count, distance, floors | Y | Y |
| `read_barometer` | Atmospheric pressure (hPa), relative altitude | Y | Y |
| `read_ambient_light` | Ambient light level (lux) | Y | Y |
| `read_proximity` | Near/far proximity detection | Y | Y |
| `get_ambient_sound_level` | Ambient noise level (dB) | Y | - |
| `get_battery` | Battery level, charging state, thermal state | Y | Y |
| `get_device_info` | Model, OS, screen size, available sensors | Y | Y |
| `take_photo` | Capture photo from front/back camera | Soon | Soon |
| `record_audio` | Record audio with optional transcription | Soon | Soon |
| `scan_bluetooth` | Scan nearby BLE devices | Soon | Soon |
| `scan_wifi` | Scan nearby WiFi networks | Soon | Soon |
| `read_nfc` | Read NFC tags | Soon | Soon |
| `scan_lidar` | LiDAR depth scan (Pro models) | - | Soon |

## Building from source

### Prerequisites

- JDK 17+
- Android SDK (compileSdk 35, minSdk 28)
- Xcode 15+ (for iOS)
- [xcodegen](https://github.com/yonaskolb/XcodeGen) (for iOS project generation)

### Android

```sh
./gradlew :androidApp:assembleDebug
# APK → androidApp/build/outputs/apk/debug/
```

### iOS

```sh
# Generate Xcode project
cd iosApp && xcodegen generate && cd ..

# Build via xcodebuild
xcodebuild -project iosApp/iosApp.xcodeproj \
  -target iosApp -sdk iphonesimulator -arch arm64 \
  -configuration Debug build

# Or open in Xcode
open iosApp/iosApp.xcodeproj
```

### Shared module only

```sh
# Android target
./gradlew :shared:compileDebugKotlinAndroid

# iOS framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

## Project structure

```
open-modality/
├── shared/                          # Kotlin Multiplatform shared module
│   └── src/
│       ├── commonMain/              # MCP server, protocol, sensor abstraction
│       │   └── kotlin/com/openmodality/
│       │       ├── mcp/             # McpServer, McpProtocol, McpSession
│       │       ├── sensor/          # PlatformSensors (expect), SensorType
│       │       ├── tools/           # SensorToolRegistry, SchemaHelper
│       │       └── di/              # Koin DI module
│       ├── androidMain/             # Android sensor implementations
│       └── iosMain/                 # iOS sensor implementations
├── androidApp/                      # Android app (Jetpack Compose)
│   └── src/androidMain/
│       └── kotlin/com/openmodality/android/
│           ├── MainActivity.kt      # Permissions + service lifecycle
│           ├── ui/MainScreen.kt     # Server status UI
│           └── background/          # Foreground service
├── iosApp/                          # iOS app (SwiftUI)
│   ├── project.yml                  # xcodegen spec
│   └── iosApp/
│       ├── OpenModalityApp.swift
│       ├── ServerViewModel.swift
│       └── MainScreen.swift
└── mcp-config/                      # Example MCP client configs
```

## Tech stack

- **Kotlin 2.1.0** — Multiplatform shared logic
- **Ktor 3.0.3** — Embedded HTTP server (CIO engine)
- **kotlinx.serialization** — JSON-RPC 2.0 message encoding
- **Koin 4.0.0** — Dependency injection
- **Jetpack Compose** — Android UI
- **SwiftUI** — iOS UI
- **Google Play Services Location** — Android GPS
- **CoreMotion / CoreLocation** — iOS sensors

## Roadmap

- [x] MCP server with Streamable HTTP transport
- [x] Motion sensors (accelerometer, gyroscope, magnetometer, device motion)
- [x] Location (GPS, reverse geocoding)
- [x] Environment sensors (barometer, ambient light, proximity)
- [x] Device info and battery status
- [ ] Camera capture (photo/video)
- [ ] Audio recording + speech-to-text
- [ ] Bluetooth LE scanning
- [ ] WiFi network scanning
- [ ] NFC tag reading
- [ ] LiDAR depth scanning (iOS)
- [ ] Security layer (pairing code, access control, audit log)
- [ ] QR code for easy connection setup
- [ ] mDNS/Bonjour auto-discovery

## Contributing

Contributions are welcome. Open an issue or submit a PR.

If you're adding a new sensor:

1. Add the sensor type to `SensorType.kt`
2. Add result model to `SensorResults.kt`
3. Add `expect`/`actual` methods to `PlatformSensors.kt`
4. Register the MCP tool in `SensorToolRegistry.kt`
5. Build and test on both platforms

## License

MIT
