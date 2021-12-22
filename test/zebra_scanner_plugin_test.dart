import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:zebra_scanner_plugin/zebra_scanner_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('zebra_scanner_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await ZebraScannerPlugin.platformVersion, '42');
  });
}
