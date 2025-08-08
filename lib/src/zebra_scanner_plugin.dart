import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

import 'barcode.dart';

/// Public Dart API for interacting with Zebra Bluetooth barcode scanners.
///
/// Notes
/// - Android: fully implemented via Zebra Scanner Control SDK.
/// - iOS: currently returns only platform version; scanning is not implemented.
class ZebraScannerPlugin {
  static const MethodChannel _channel = MethodChannel('zebra_scanner_plugin');
  static const EventChannel _eventChannel = EventChannel('barcode_stream');

  /// Returns the native platform version string.
  ///
  /// Intended primarily for smoke testing the platform channel wiring.
  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod("getPlatformVersion");
    return version;
  }

  /// Initializes the Zebra SDK on the host platform and registers connection callbacks.
  ///
  /// Must be called before invoking [connect].
  static Future<void> get initScanner async {
    _channel.invokeMethod("initScanner");
  }

  /// Connect to a scanner using a specific Bluetooth address.
  ///
  /// Deprecated: Direct-address connection is not supported as of Android plugin 1.1.3.
  /// Use [connect] to launch the native pairing/connect flow instead.
  @Deprecated('Use connect() instead. Direct address connection is not supported as of Android plugin 1.1.3.')
  static Future<String?> connectToScanner(String bluetoothAddress) async {
    final String? connectToScanner = await _channel.invokeMethod(
        "connectToScanner", {"bluetoothAddress": bluetoothAddress});
    return connectToScanner;
  }

  /// Launches the native connection flow to scan/pair and connect to a scanner.
  static Future<String?> connect() async {
    final String? connectToScanner = await _channel.invokeMethod("connect");
    return connectToScanner;
  }

  /// Disconnects the current scanner session if connected.
  ///
  /// Warning: On Android 1.1.3 this method is not wired to a native implementation
  /// (native expects method name "disconnectScanner"). See docs/KNOWN_ISSUES.md.
  static Future<String?> disconnect() async {
    final String? connectToScanner = await _channel.invokeMethod("disconnect");
    return connectToScanner;
  }

  /// Broadcast stream of scanned barcode events.
  ///
  /// Each event payload is a JSON string from the native side with keys:
  /// - `data`: the decoded barcode content
  /// - `type`: a string representing the symbology (e.g., CODE128)
  ///
  /// The stream maps each event into a [Barcode] instance.
  static Stream<Barcode> get barcodeStream {
    return _eventChannel.receiveBroadcastStream().map((event) {
      Map map = json.decode(event);
      Barcode barcode = Barcode(data: map["data"], type: map["type"]);
      return barcode;
    });
  }
}
