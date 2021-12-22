
import 'dart:async';

import 'package:flutter/services.dart';

class ZebraScannerPlugin {
  static const MethodChannel _channel = MethodChannel('zebra_scanner_plugin');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
