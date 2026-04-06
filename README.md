<p align="center">
  <picture>
    <source srcset="assets/open-modality-dark.svg" media="(prefers-color-scheme: dark)">
    <img src="assets/open-modality.svg" width="300" alt="Open Modality Logo">
</picture>
</p>

<h1 align="center">Open Modality</h1>
<p align="center">Give AI senses. Your phone's sensors over WebSocket.</p>

<p align="center">
  <a href="#quickstart">Quickstart</a> ·
  <a href="#how-it-works">How it works</a> ·
  <a href="#available-tools">Tools</a> ·
  <a href="#building-from-source">Build</a> ·
  <a href="#contributing">Contributing</a>
</p>

---

Open Modality turns your smartphone into a sensor gateway for AI agents. It runs a WebSocket server on your phone, exposing every hardware sensor as a callable tool. Any WebSocket client — Python scripts, AI agents, browser apps — can connect and use the sensors in real time.

No cloud. No middleman. Your phone, your sensors, your data.

## Quickstart

1. **Install the app** on your Android or iOS device
2. **Tap Start** to launch the sensor server
3. **Connect via WebSocket** using the PIN shown in the app:

```python
import websocket, json

ws = websocket.create_connection("ws://<phone-ip>:8080/ws?pin=<PIN>")

# List available tools
ws.send(json.dumps({"id": "1", "method": "list_tools"}))
tools = json.loads(ws.recv())

# Read a sensor
ws.send(json.dumps({"id": "2", "method": "get_location"}))
location = json.loads(ws.recv())
print(location)

ws.close()
```

Or use the HTTP endpoint for one-shot calls:

```bash
curl -X POST http://<phone-ip>:8080/call \
  -H "X-Pin: <PIN>" \
  -H "Content-Type: application/json" \
  -d '{"id":"1","method":"get_battery"}'
```

That's it. Your AI can now see, hear, and feel the world through your phone.

## How it works

```
AI Agent / Script              Phone (Open Modality)
┌─────────────────┐           ┌──────────────────────┐
│                 │    WS     │  Ktor WebSocket      │
│  WebSocket      │◄─────────►│  :8080/ws            │
│  Client         │  (WiFi)   │                      │
│                 │           │  SensorToolRegistry  │
│  "get_location" │──────────►│  → PlatformSensors   │
│                 │           │  → GPS / Accel / ... │
└─────────────────┘           └──────────────────────┘
```

### Endpoints

| Endpoint | Auth | Description |
|----------|------|-------------|
| `ws://<ip>:8080/ws?pin=<PIN>` | PIN (query param) | WebSocket — persistent connection for multiple calls |
| `POST http://<ip>:8080/call` | PIN (`X-Pin` header) | HTTP — single request-response |
| `GET http://<ip>:8080/info` | None | Discovery — server info, tool list, sensor list |
| `GET http://<ip>:8080/health` | None | Health check |

### Protocol

Simple JSON request-response over WebSocket:

```jsonc
// Request (client → phone)
{"id": "1", "method": "take_photo", "params": {"camera": "back"}}

// Response (phone → client)
{"id": "1", "result": {"content": [{"type": "image", "data": "base64...", "mimeType": "image/jpeg"}]}}

// Error response
{"id": "1", "error": {"code": -1, "message": "Camera not available"}}
```

Built-in methods: `list_tools`, `get_info`, `ping`. Any registered tool name (e.g. `take_photo`) is also a valid method.

### Architecture

- **Transport**: WebSocket (Ktor CIO) with PIN-based auth
- **Protocol**: Simple JSON request-response (no JSON-RPC overhead)
- **Code**: Kotlin Multiplatform — shared server and sensor abstraction, native implementations per platform

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
| `take_photo` | Capture photo from front/back camera | Y | Y |
| `record_audio` | Record audio with optional transcription | Y | Y |
| `scan_bluetooth` | Scan nearby BLE devices | Y | Y |
| `scan_wifi` | Scan nearby WiFi networks | Y | Y |
| `read_nfc` | Read NFC tags | Y | Y |
| `scan_lidar` | LiDAR depth scan (Pro models) | - | Y |

## Connecting an AI Agent

### Option 1: Direct WebSocket (recommended)

Any language/framework that supports WebSocket can connect directly:

```python
# Python AI agent example
import websocket, json

def call_tool(ws, method, params=None):
    msg = {"id": str(id(method)), "method": method}
    if params:
        msg["params"] = params
    ws.send(json.dumps(msg))
    return json.loads(ws.recv())["result"]

ws = websocket.create_connection("ws://192.168.1.100:8080/ws?pin=123456")
photo = call_tool(ws, "take_photo", {"camera": "back"})
location = call_tool(ws, "get_location")
```

### Option 2: MCP Bridge (for Claude Code / Claude Desktop)

Use a thin MCP bridge that translates between MCP and WebSocket:

```
Phone (WebSocket) ←→ MCP Bridge (Desktop) ←→ Claude Code (stdio)
```

See `bridge/` directory for the MCP bridge implementation (coming soon).

### Option 3: HTTP one-shot

For agents that work with simple HTTP function calling:

```python
import requests

response = requests.post(
    "http://192.168.1.100:8080/call",
    headers={"X-Pin": "123456", "Content-Type": "application/json"},
    json={"id": "1", "method": "get_location"}
)
print(response.json())
```

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
│       ├── commonMain/              # Server, protocol, sensor abstraction
│       │   └── kotlin/com/openmodality/
│       │       ├── server/          # OpenModalityServer, Protocol, SessionManager
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
```

## Tech stack

- **Kotlin 2.1.0** — Multiplatform shared logic
- **Ktor 3.0.3** — Embedded WebSocket server (CIO engine)
- **kotlinx.serialization** — JSON message encoding
- **Koin 4.0.0** — Dependency injection
- **Jetpack Compose** — Android UI
- **SwiftUI** — iOS UI
- **Google Play Services Location** — Android GPS
- **CoreMotion / CoreLocation** — iOS sensors

## Roadmap

- [x] WebSocket server with PIN auth
- [x] HTTP one-shot endpoint
- [x] Discovery endpoint (`GET /info`)
- [x] Motion sensors (accelerometer, gyroscope, magnetometer, device motion)
- [x] Location (GPS, reverse geocoding)
- [x] Environment sensors (barometer, ambient light, proximity)
- [x] Device info and battery status
- [x] Camera capture
- [x] Audio recording
- [x] Bluetooth LE scanning
- [x] WiFi network scanning
- [x] NFC tag reading
- [x] LiDAR depth scanning (iOS)
- [ ] MCP bridge for Claude Code / Claude Desktop
- [ ] mDNS/Bonjour auto-discovery
- [ ] Sensor streaming (continuous data push)
- [ ] QR code for easy connection setup

## Contributing

Contributions are welcome. Open an issue or submit a PR.

If you're adding a new sensor:

1. Add the sensor type to `SensorType.kt`
2. Add result model to `SensorResults.kt`
3. Add `expect`/`actual` methods to `PlatformSensors.kt`
4. Register the tool in `SensorToolRegistry.kt`
5. Build and test on both platforms

## License

MIT
