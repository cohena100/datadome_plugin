import 'dart:async';

import 'package:flutter/services.dart';

class DataDomePlugin {
  static const MethodChannel _channel = const MethodChannel('data_dome_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Map<String, dynamic>> getCall(
    String url,
    Map<String, String> headers,
    String key,
  ) async {
    final args = {'url': url, 'headers': headers, 'key': key};
    final Map<String, dynamic> response = await _channel.invokeMapMethod(
      'get',
      args,
    );
    return response;
  }
}
