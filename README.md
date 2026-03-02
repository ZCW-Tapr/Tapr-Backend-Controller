# Tapr-Backend-Controller: Device Control Service

The backend service for the Tapr gesture control system. **Tapr-Backend-Controller** receives gesture data from [Tapr-Trackpad](https://github.com/ZCW-Tapr/Tapr-Trackpad) and translates those gestures into control commands for smart devices via REST APIs.

## What It Does

Tapr-Backend-Controller acts as the bridge between gesture recognition and device control:
- **Consumes gesture data** from the trackpad module
- **Maps gestures to device commands** using configurable profiles
- **Translates to device APIs** (Govee, and extensible to others)
- **Executes device actions** in real-time with minimal latency

## Key Features

✓ Spring Boot REST API for gesture reception  
✓ Govee API integration (lights, switches, fans, thermostats, etc.)  
✓ Configurable gesture-to-action mappings  
✓ Async command execution  
✓ Error handling and retry logic  
✓ Support for complex device capabilities (brightness, color, temperature, etc.)  

### Tech Stack
- **Java 17**
- **Spring Boot 4**
- **Maven 3.9**
- **Govee API Key** (optional, required for Govee device control)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/ZCW-Tapr/Tapr-Backend-Controller.git
   cd Tapr-Backend-Controller
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

3. Configure your API keys (see **Configuration** section below)

4. Start the service:
   ```bash
   mvn spring-boot:run
   ```

   Service will listen on `http://localhost:8080`

## Configuration

### Govee API Setup

1. **Get your API key**:
   - For Govee devices: Open the [Govee Home App](https://www.goveeiot.com/)
   - Navigate to: Settings → API Key
   - Copy your API key
  
   - For other smart devices, you will have to acquire the devices and API keys.
     - For example, if you have smart devices from LIFX, you'll have to get an API key through either their app or reach out to
       their customer services for assistance.
     - For security purposes, you will want to put you API key in .gitignore. 

2. **Set environment variable**:
   ```bash
   export GOVEE_API_KEY="your-api-key-here"
   ```

   Or in `application.local.properties`:
   ```properties
   govee.api.key=your-api-key-here
   govee.api.host=https://openapi.api.govee.com
   ```

3. **Verify setup**:
   ```bash
   curl -X GET http://localhost:8080/api/devices \
     -H "Authorization: Bearer ${GOVEE_API_KEY}"
   ```

### Gesture-to-Action Mapping

Define gesture profiles in `config/gesture-mappings.json`:

```json
{
  "profiles": [
    {
      "name": "Bedroom Lights", <---- Or any other name you want to give your device to distinguish it from other devices you may have.
      "gestures": {
        "tap": {
          "device": "device-id-here",
          "action": "toggle_power"
        },
        "swipe_right": {
          "device": "device-id-here",
          "action": "increase_brightness",
          "value": 20
        },
        "swipe_left": {
          "device": "device-id-here",
          "action": "decrease_brightness",
          "value": 20
        }
      }
    }
  ]
}
```

## API Reference

### Receive Gesture Data

**POST** `/api/gestures/process`

Process a gesture and execute corresponding device actions.

```bash
curl -X POST http://localhost:8080/api/gestures/process \
  -H "Content-Type: application/json" \
  -d '{
    "gesture_type": "swipe_right",
    "start_x": 1000,
    "start_y": 800,
    "end_x": 1500,
    "end_y": 800,
    "duration_ms": 200
  }'
```

**Response** (200 OK):
```json
{
  "gesture_id": "uuid-here",
  "status": "success",
  "commands_executed": 1,
  "devices_affected": ["device-id-1"]
}
```

### Get Available Devices

**GET** `/api/devices`

Retrieve all Govee devices and their capabilities.

```bash
curl -X GET http://localhost:8080/api/devices
```

**Response** (200 OK):
```json
{
  "devices": [
    {
      "sku": "H605C", <- This is the SKU number for your device.
      "device_id": "00:00:00:00:00:00:00:00", <- For security reasons, I removed my ID. You will have your own ID
      "name": "Living Room Light",
      "type": "light",
      "capabilities": [
        "power_switch",
        "brightness",
        "color_rgb",
        "color_temperature"
      ],
      "online": true
    }
  ]
}
```

### Execute Device Command

**POST** `/api/devices/{device-id}/control`

Send a control command to a specific device.

```bash
curl -X POST http://localhost:8080/api/devices/00:00:00:00:00:00:00:00/control \
  -H "Content-Type: application/json" \
  -d '{
    "capability": "power_switch",
    "value": 1
  }'
```

**Response** (200 OK):
```json
{
  "status": "success",
  "device_id": "00:00:00:00:00:00:00:00",
  "command": "power_switch",
  "value": 1
}
```

## Supported Device Capabilities

### Govee Devices

| Capability | Values | Example |
|---|---|---|
| `power_switch` | 0 (off), 1 (on) | Toggle device power |
| `brightness` | 1-100 | Set brightness percentage |
| `color_rgb` | 0-16777215 | Set RGB color |
| `color_temperature` | 2000-9000 | Set Kelvin temperature |
| `toggle` | 0 (off), 1 (on) | Control toggles (oscillation, night light) |
| `mode` | enum | Change device mode |
| `work_mode` | struct | Complex mode with sub-values |

See [Govee API docs](https://developer.govee.com/) for full capability list.

## Architecture

```
Gesture Data (HTTP POST)
    ↓
GestureController
    ↓
GestureMapper (maps to device actions)
    ↓
DeviceService (translates to API calls)
    ↓
GoveeApiClient (REST calls to Govee)
    ↓
Device Command Execution
```

### Project Structure

```
src/main/
├── java/com/tapr/
│   ├── controller/
│   │   └── GestureController.java
│   ├── service/
│   │   ├── GestureService.java
│   │   └── DeviceService.java
│   ├── client/
│   │   └── GoveeApiClient.java
│   └── config/
│       └── GoveeConfig.java
└── resources/
    └── application.properties
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Test with Govee
```bash
# Ensure GOVEE_API_KEY is set, then:
mvn test -Dtest=GoveeApiClientTest
```

### Manual Testing

1. Start the service:
   ```bash
   mvn spring-boot:run
   ```

2. Get device list:
   ```bash
   curl http://localhost:8080/api/devices
   ```

3. Send a test gesture:
   ```bash
   curl -X POST http://localhost:8080/api/gestures/process \
     -H "Content-Type: application/json" \
     -d '{"gesture_type": "tap", "start_x": 1000, "start_y": 800}'
   ```

## Integration with Tapr-Trackpad

This service expects gesture data in the following format from [Tapr-Trackpad](https://github.com/ZCW-Tapr/Tapr-Trackpad):

```json
{
  "gesture_type": "swipe_left | swipe_right | tap | two_finger_tap",
  "start_x": int,
  "start_y": int,
  "end_x": int,
  "end_y": int,
  "duration_ms": int,
  "finger_count": int
}
```

Trackpad can send data via:
- HTTP POST to `/api/gestures/process`
- WebSocket (extensible)
- Kafka/RabbitMQ (future)

## Performance Notes

- Gesture processing: <100ms end-to-end
- API calls to Govee: ~200-500ms (network dependent)
- Concurrent gesture handling: Async/non-blocking
- Memory footprint: ~50-100MB
- CPU usage: <5% idle, ~10-20% under load

## Error Handling

The service includes robust error handling:

```json
{
  "status": "error",
  "error_code": "DEVICE_OFFLINE",
  "message": "Device is currently offline",
  "device_id": "00:00:00:00:00:00:00:00"
}
```

Common error codes:
- `DEVICE_NOT_FOUND`: Device ID doesn't exist
- `DEVICE_OFFLINE`: Device not reachable
- `CAPABILITY_NOT_SUPPORTED`: Device doesn't support the action
- `API_RATE_LIMIT`: Govee API rate limit exceeded
- `INVALID_PARAMETER`: Command parameters invalid

## Troubleshooting

### "401 Unauthorized" from Govee
- Verify API key is correct: `echo $GOVEE_API_KEY`
- Confirm key is for a Wi-Fi device (Bluetooth-only devices not supported)
- Check Govee Home App → Settings → API status

### "429 Too Many Requests"
- Govee limit is 10,000 requests/account/day
- Implement command queuing to batch gestures
- Add delays between rapid commands

### Service won't start
- Check Java version: `java -version` (need 11+)
- Verify port 8080 is available: `lsof -i :8080`
- Check logs: `mvn spring-boot:run 2>&1 | tail -50`

## Contributing

Contributions welcome! Areas for improvement:
- Additional device ecosystem support (IFTTT, Home Assistant, etc.)
- WebSocket support for real-time gesture streaming
- Advanced gesture patterns (pinch, rotate)
- Gesture recording and playback
- Web UI for gesture mapping configuration

## License

Part of the **Tapr** project. Licensed under **Commons Clause + MIT License**.

**Free for**: Personal use, education, non-profit projects  
**Requires license fee**: Commercial use, selling products/services based on Tapr  

See [Tapr organization LICENSE](https://github.com/ZCW-Tapr/blob/main/LICENSE) for full terms and commercial licensing contact.

---

**Questions?** Check the [Tapr Organization README](https://github.com/ZCW-Tapr) for system architecture overview.
