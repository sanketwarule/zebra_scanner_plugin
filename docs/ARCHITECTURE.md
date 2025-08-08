# Architecture and Components

## Overview
The plugin bridges Flutter (Dart) to native Zebra Scanner Control SDKs. Android is fully implemented, while iOS is a placeholder returning platform version only.

- Dart package exposes high-level API and a `barcodeStream`.
- Android uses a `MethodChannel` and `EventChannel` to drive scanner connection and relay scan events.
- iOS registers the plugin and responds to `getPlatformVersion`.

## Flutter (Dart)
- `ZebraScannerPlugin` (Dart):
  - MethodChannel: `zebra_scanner_plugin`
  - EventChannel: `barcode_stream`
  - Public methods: `initScanner`, `connect`, `disconnect` (see Known Issues), `platformVersion`
  - Public stream: `barcodeStream` → maps native JSON to `Barcode`
- `Barcode` (Dart model): `data`, `type`

## Android Components
- `ZebraScannerPlugin` (Java): Flutter plugin entry point
  - Registers MethodChannel and EventChannel
  - Handles methods: `getPlatformVersion`, `initScanner`, `connect`, `disconnectScanner` (see Known Issues)
  - Starts `ConnectionActivity` for scanner discovery/pair/connect flow
  - Implements `ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate` to receive connection and scan events and emit them to Dart

- `ZebraScannerEngine` (Java): Adapter around Zebra `SDKHandler`
  - Singleton; maintains scanner lists
  - Subscribes to Zebra SDK events via `IDcsSdkApiDelegate`
  - On barcode: constructs JSON `{ data, type }` and notifies delegates
  - Provides utility methods to execute Zebra commands and toggle discovery

- `ScannerAppEngine` (Java interface): contracts/delegates
  - `IScannerAppEngineDevConnectionsDelegate`: connection + scanned data callbacks
  - `IScannerAppEngineDevListDelegate`, `IScannerAppEngineDevEventsDelegate`: list and event contracts (mostly not used currently)

- `ConnectionActivity` (Java): UI/activity to initiate scanner connection
  - Launched by `ZebraScannerPlugin.connect()`
  - Manages SDK discovery/connect workflow and user prompts
  - Contains a TODO for screen-size threshold (see Known Issues)

- Helpers
  - `Foreground`: background/foreground detection (TODO to implement lifecycle callbacks)
  - `BarcodeTypes`: maps numeric Zebra symbology codes to human-readable names
  - `Application`: holds global SDK state (`SDKHandler`, current scanner, etc.)

## iOS Components
- `SwiftZebraScannerPlugin` / `ZebraScannerPlugin.h`
  - Registers `zebra_scanner_plugin` MethodChannel
  - Responds to `getPlatformVersion` only
  - Scanning not yet implemented

## Data Flow (Android)
1. Dart calls `await ZebraScannerPlugin.initScanner;`
   - Java registers delegates with `ZebraScannerEngine` and subscribes to SDK events
2. Dart calls `await ZebraScannerPlugin.connect();`
   - Java launches `ConnectionActivity` for pairing/connection
3. Zebra SDK establishes a session → `ZebraScannerEngine` notifies delegates
4. On scan → Zebra SDK emits barcode → `ZebraScannerEngine`:
   - Creates JSON `{ data, type }`
   - Notifies delegates → `ZebraScannerPlugin.onScanDataReceived`
   - Emits JSON over `EventChannel` → Dart maps to `Barcode`

## Error Handling
- Current implementation primarily logs errors and returns success; propagate errors via `result.error` on native and surface in Dart Futures/stream errors (see Known Issues).

## Extensibility
- Implement iOS scanning: mirror Android design using an `EventChannel`
- Add explicit disconnect flow and proper lifecycle handling
- Package `BarcodeScannerLibrary` as a proper Gradle/Maven artifact to avoid manual copying