import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

import 'barcode.dart';

class ZebraScannerPlugin {
  static const MethodChannel _channel = MethodChannel('zebra_scanner_plugin');
  static const EventChannel _eventChannel = EventChannel('barcode_stream');

  // Sample to test the plugin with platform version
  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod("getPlatformVersion");
    return version;
  }

  // Initialize the scanner
  static Future<void> get initScanner async {
    _channel.invokeMethod("initScanner");
  }

  // @Deprecated
  // Connect to scanner with the specific bluetooth address
  static Future<String?> connectToScanner(String bluetoothAddress) async {
    final String? connectToScanner = await _channel.invokeMethod(
        "connectToScanner", {"bluetoothAddress": bluetoothAddress});
    return connectToScanner;
  }

  // Connecting the scanner to redirect to bluetooth barcode scanner activity
  static Future<String?> connect() async {
    final String? connectToScanner = await _channel.invokeMethod("connect");
    return connectToScanner;
  }

  // Disconnect the scanner
  static Future<String?> disconnect() async {
    final String? connectToScanner = await _channel.invokeMethod("disconnect");
    return connectToScanner;
  }

  // Listens to the stream of scanned barcode data
  static Stream<Barcode> get barcodeStream {
    return _eventChannel.receiveBroadcastStream().map((event) {
      Map map = json.decode(event);
      Barcode barcode = Barcode(data: map["data"], type: map["type"]);
      return barcode;
    });
  }
}
