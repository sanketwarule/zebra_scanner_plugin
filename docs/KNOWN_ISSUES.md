# Known Issues and Resolution Steps

This document lists known issues found in the current codebase (v1.1.3) and how to resolve them.

## 1) Dart `disconnect()` is not implemented on Android
- Symptom: Calling `ZebraScannerPlugin.disconnect()` results in a `MissingPluginException` or `notImplemented` response.
- Cause: The Android plugin listens for the method name `disconnectScanner` while Dart calls `disconnect`.
- Resolution options:
  1. Change Android to handle `"disconnect"`:
     - In `android/.../ZebraScannerPlugin.java`, add a `case "disconnect":` that clears delegates and disconnects.
  2. Or change Dart to call `disconnectScanner`:
     - Update `lib/src/zebra_scanner_plugin.dart` to invoke `"disconnectScanner"`.
  3. Prefer option (1) to avoid a breaking change in the Dart API.

## 2) `connectToScanner(String bluetoothAddress)` is deprecated and not implemented
- Symptom: `connectToScanner` returns `notImplemented` on Android.
- Cause: Android implementation is commented out.
- Resolution:
  - Remove the API from Dart or keep it with an `@Deprecated` annotation (already added) and a clear doc comment.
  - If needed, re-implement native support for direct-address connection and un-comment the Android handler.

## 3) `initScanner` does not await the platform call
- Symptom: Potential race condition if `connect()` is called immediately after `initScanner` without giving native side time to register.
- Cause: Dart getter calls `_channel.invokeMethod("initScanner")` without `await`/`return`.
- Resolution:
  - Change to `return _channel.invokeMethod("initScanner");` so callers can `await ZebraScannerPlugin.initScanner` reliably.

## 4) iOS scanning is not implemented
- Symptom: On iOS only the platform version is returned; no scanning features are available.
- Resolution options:
  - Implement scanning using Zebra’s iOS SDK and bridge events via `EventChannel` analogous to Android.
  - Or explicitly document Android-only support and guard API usage by platform checks in examples.

## 5) `Foreground.java` TODO – lifecycle callbacks not implemented
- Symptom: Background/foreground detection may be incomplete.
- Resolution:
  - Implement Android `Application.ActivityLifecycleCallbacks` wiring in `Foreground` to correctly report background state.

## 6) `ConnectionActivity.java` TODO – screen size threshold
- Symptom: Code uses `if (getDeviceScreenSize() > 6)` with a TODO questioning the constant.
- Resolution:
  - Replace magic number with a constant and verify UI breakpoints across devices; consider using `sw<N>dp`-qualified resources.

## 7) Example app build.gradle TODOs
- Symptom: TODO comments about applicationId and release signing.
- Resolution:
  - Set a proper unique `applicationId` in example if publishing.
  - Configure a real signing config for release builds if distributing the example app.

## 8) Android permissions on newer SDKs
- Symptom: On Android 12+ Bluetooth permissions model changed.
- Resolution:
  - Add `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`, and conditional location permissions as required by targetSdk.

## 9) Packaging `BarcodeScannerLibrary`
- Symptom: Manual copying from pub-cache can be brittle.
- Resolution:
  - Consider publishing `BarcodeScannerLibrary` as a separate Maven AAR or include it as a Gradle module/submodule to avoid manual copying.

## 10) Logging controls
- Symptom: `Constants.DEBUG` is set to `true`.
- Resolution:
  - Tie DEBUG to build type or provide a runtime toggle.

## 11) Error propagation from native to Dart
- Symptom: Some native methods always `result.success(...)` without surfacing errors.
- Resolution:
  - Wrap native operations with try/catch and call `result.error(code, message, details)` for failures, then surface via Dart Futures.