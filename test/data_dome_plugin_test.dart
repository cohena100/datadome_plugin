import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:data_dome_plugin/data_dome_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('data_dome_plugin');

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
  });
}
