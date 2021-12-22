
import 'dart:async';

import 'package:flutter/services.dart';

class ZebraScannerPlugin {
  static const MethodChannel _channel = MethodChannel('zebra_scanner_plugin');
  static const EventChannel _eventChannel = EventChannel('barcode_stream');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> get connect async {
    final String? connect = await _channel.invokeMethod('connectToScanner');
    return connect;
  }

  static Stream<String> get barcodeStream {
    return _eventChannel.receiveBroadcastStream().map((event) => event);
  }
}
