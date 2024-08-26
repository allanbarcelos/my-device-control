# MyDeviceControl

[Português](README.pt.md) | English

MyDeviceControl is a set of applications designed to remotely control Android devices from a Windows client using WebSocket for real-time communication. The project consists of an Android app that captures the screen and responds to commands received from a central server, and a Windows client that sends commands and displays the captured screen.

## Project Overview

### Project Components

1. **Central Server**: Receives data from the Android device and forwards commands from the Windows client to the Android device.
2. **Android App**: Captures the device screen, sends it to the server, and executes received commands.
3. **Windows App**: Connects to the server, sends commands to the Android device, and displays the captured screen.

### Technologies Used

- **Server**: Node.js, WebSocket, dotenv.
- **Android App**: Kotlin, WebSocket (via OkHttp), Screen Capture Services.
- **Windows App**: C#, WinForms, WebSocket (via WebSocketSharp).

## Environment Setup

### Central Server

1. Clone the central server repository.
2. Install the dependencies using `npm install`.
3. Create a `.env` file in the root of the project with the following contents:
    ```dotenv
    ANDROID_TOKEN=YOUR_ANDROID_TOKEN
    WINDOWS_TOKEN=YOUR_WINDOWS_TOKEN
    ```
4. Start the server with `node server.js`.

### Android App

1. Clone the Android app repository.
2. Configure the `build.gradle.kts` (Module: app) to include the necessary dependencies.
3. Add the following environment variables in the `build.gradle.kts`:
    ```kotlin
    defaultConfig {
        buildConfigField("String", "ANDROID_TOKEN", "\"YOUR_ANDROID_TOKEN\"")
        buildConfigField("String", "SERVER_API", "\"YOUR_SERVER_URL\"")
    }
    ```
4. Build and install the app on the Android device.

### Windows App

1. Clone the Windows client repository.
2. Set up the `TOKEN` and `SERVER_URL` environment variables in the Windows environment.
3. Build and run the application.

## Usage

### Android App

1. Open the Android app.
2. The app will automatically connect to the server, start capturing the screen, and send it to the server.

### Windows App

1. Open the Windows app.
2. The app will connect to the server, send the token for authentication, and display the captured screen from the Android device.
3. Use the keyboard and mouse to send commands to the Android device.

## Dependencies

### Server

- Node.js
- WebSocket
- dotenv

### Android App

- Kotlin
- OkHttp
- AndroidX

### Windows App

- .NET Framework
- WebSocketSharp

## Contributions

This project is a basic example of remote control between Android and Windows devices. Contributions to improve, add features, or fix bugs are welcome!