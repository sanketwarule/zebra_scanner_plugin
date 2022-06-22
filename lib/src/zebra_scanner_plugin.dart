import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

import 'barcode.dart';

class ZebraScannerPlugin {
  static const MethodChannel _channel = MethodChannel('zebra_scanner_plugin');
  static const EventChannel _eventChannel = EventChannel('barcode_stream');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod("getPlatformVersion");
    return version;
  }

  static Future<void> get initScanner async {
    _channel.invokeMethod("initScanner");
  }

  static Future<String?> connectToScanner(String bluetoothAddress) async {
    final String? connectToScanner = await _channel.invokeMethod(
        "connectToScanner", {"bluetoothAddress": bluetoothAddress});
    return connectToScanner;
  }

  static Future<String?> connect() async {
    final String? connectToScanner = await _channel.invokeMethod("connect");
    return connectToScanner;
  }

  static Future<String?> disconnect() async {
    final String? connectToScanner = await _channel.invokeMethod("disconnect");
    return connectToScanner;
  }

  static Stream<Barcode> get barcodeStream {
    return _eventChannel.receiveBroadcastStream().map((event) {
      Map map = json.decode(event);
      // print("barcodeStream :: data : ${map["data"]} and type : ${map["type"].toString()}");
      Barcode barcode = Barcode(map["data"], map["type"]);
      return barcode;
    });
  }
}
