# zebra_scanner_plugin – API Reference

## Overview
`zebra_scanner_plugin` is a Flutter plugin that integrates the Zebra Scanner Control SDK to receive barcode scan events from Zebra Bluetooth scanners.

- Android: Full implementation (scan, connect, event streaming)
- iOS: Placeholder implementation (returns platform version only)

Minimum Android SDK: 19

## Installation and Android Integration
1) Add the dependency:

```bash
flutter pub add zebra_scanner_plugin
```

2) Copy the `BarcodeScannerLibrary` folder from the plugin example into your Android app module that uses the plugin:

`$FLUTTER_HOME/.pub-cache/hosted/pub.dartlang.org/zebra_scanner_plugin-x.x.x+x/example/android/zebra_scanner_plugin`

3) Include the library in your app’s `settings.gradle`:

```gradle
include ':BarcodeScannerLibrary'
```

4) In your Android `AndroidManifest.xml`, under the `<application>` tag, add:

```xml
<application
    ...
    tools:replace="android:label">
    ...
</application>
```

This avoids a manifest label merge conflict introduced by the AAR.

5) Ensure required Bluetooth permissions are declared. Typical examples:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Note: Specific permissions can vary by Zebra SDK version and targetSdk. Consult Zebra SDK docs if you target Android 12+ for new Bluetooth permissions.

## Public Dart API
### Class: `Barcode`
- `String data`: Decoded barcode content
- `String type`: Symbology type name (e.g., CODE128, EAN13)

### Class: `ZebraScannerPlugin`
- `static Future<String?> get platformVersion`
  - Returns platform version from native code. Useful for smoke testing.

- `static Future<void> get initScanner`
  - Initializes the Zebra SDK and registers connection callbacks.
  - Call before `connect()`.

- `static Future<String?> connect()`
  - Launches a native Activity that scans/pairs and connects to a scanner.
  - Returns a simple confirmation string on success.

- `static Future<String?> disconnect()`
  - Attempts to disconnect the current scanner session.
  - Warning: On Android 1.1.3 this is not wired to a native implementation (native expects `disconnectScanner`). See Known Issues.

- `static Stream<Barcode> get barcodeStream`
  - Broadcast stream of barcode scan events.
  - Each event is sent from native as a JSON string `{ "data": "...", "type": "..." }` and is mapped into a `Barcode` instance.

- `@Deprecated static Future<String?> connectToScanner(String bluetoothAddress)`
  - Deprecated: Direct-address connection is not supported as of 1.1.3.
  - Use `connect()` instead.

## Quickstart Example
```dart
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:zebra_scanner_plugin/zebra_scanner_plugin.dart';

class ScannerPage extends StatefulWidget {
  const ScannerPage({Key? key}) : super(key: key);
  @override
  State<ScannerPage> createState() => _ScannerPageState();
}

class _ScannerPageState extends State<ScannerPage> {
  StreamSubscription<Barcode>? _sub;
  final List<Barcode> _scans = [];

  @override
  void initState() {
    super.initState();
    _initialize();
  }

  Future<void> _initialize() async {
    await ZebraScannerPlugin.initScanner; // prepare SDK
    _sub = ZebraScannerPlugin.barcodeStream.listen((barcode) {
      setState(() => _scans.add(barcode));
    }, onError: (err) {
      debugPrint('Scan error: $err');
    });
  }

  @override
  void dispose() {
    _sub?.cancel();
    super.dispose();
  }

  Future<void> _connect() async {
    await ZebraScannerPlugin.connect();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Zebra Scanner')),
      floatingActionButton: FloatingActionButton(
        onPressed: _connect,
        child: const Icon(Icons.bluetooth_searching),
      ),
      body: ListView.builder(
        itemCount: _scans.length,
        itemBuilder: (context, i) => ListTile(
          leading: const Icon(Icons.qr_code),
          title: Text(_scans[i].data),
          trailing: Text(_scans[i].type),
        ),
      ),
    );
  }
}
```

## Native Channels (for maintainers)
- MethodChannel: `zebra_scanner_plugin`
  - `getPlatformVersion`
  - `initScanner`
  - `connect`
  - `disconnectScanner` (Android handler) ← Dart currently calls `disconnect`
  - `connectToScanner` (commented-out in Android 1.1.3)

- EventChannel: `barcode_stream`
  - Payload: JSON string `{ data: string, type: string }`

## Platform Notes
- Android: Implemented using `SDKHandler` from Zebra Scanner Control SDK. Events are bridged via `EventChannel`.
- iOS: Only `platformVersion` is returned; scanning is not implemented.

See `docs/KNOWN_ISSUES.md` for current limitations and recommended fixes.