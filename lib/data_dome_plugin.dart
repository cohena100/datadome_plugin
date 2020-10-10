import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class DataDomePlugin {
  static const MethodChannel _channel = const MethodChannel('data_dome_plugin');

  static Future<Map<String, dynamic>> httpCall(
    HttpMethod method,
    String url,
    Map<String, String> headers,
    Uint8List body,
    String key,
  ) async {
    final args = {'url': url, 'headers': headers, 'body': body, 'key': key};
    final Map<String, dynamic> response = await _channel.invokeMapMethod(
      describeEnum(method),
      args,
    );
    return response;
  }
}

enum HttpMethod {
  get,
  delete,
  post,
  put,
}
