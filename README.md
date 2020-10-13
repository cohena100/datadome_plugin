# DataDome plugin for Flutter

[![Pub](https://img.shields.io/pub/v/data_dome_plugin.svg)](https://pub.dev/packages/mockito) ![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)


Flutter has it's own network implementation, but this plugin uses URLSession and OkHttp for iOS and Android, respectively.


#### Note
If your project doesn't support swift/kotlin then you can check [here](https://stackoverflow.com/questions/52244346/how-to-enable-swift-support-for-existing-project-in-flutter).

## iOS

1. Add the key `DataDomeKey` with your DataDome key string value to `../ios/Runner/Info.plist`.
2. Add the key `DataDomeProxyEnabled` with the boolean value `NO` to `../ios/Runner/Info.plist`.

[Forcing captcha display on iOS.](https://docs.datadome.co/docs/datadome-ios-sdk-20-and-earlier)

## Android

The method call has a key parameter that is used to pass the DataDome key.

[Forcing captcha display on Android](https://docs.datadome.co/docs/sdk-android) but could not get it to work.

## Usage

```dart
    try {
      final headers = {
        'Content-type': 'application/json',
        'Accept': 'application/json',
      };
      //TODO: fill in url, DataDome key for android
      final key = '';
      final url = '';
      final json = {
        "any": "body",
        "some": 1,
        "is": true,
      };
      final body = Uint8List.fromList(utf8.encode(jsonEncode(json)));
      final result = await DataDomePlugin.httpCall(
        HttpMethod.post,
        url,
        headers,
        body,
        key,
      );
      final response = _makeResponse(result);
      print(response.statusCode.toString());
    } on PlatformException {
      print('Failed HTTP call');
    }
.
.
.
  http.Response _makeResponse(Map<String, dynamic> resp) {
    final int code = resp['code'] ?? 500;
    final Uint8List data = resp['data'] ?? Uint8List(0);
    return http.Response.bytes(
      data.toList(),
      code,
      headers: {'content-type': 'application/json; charset=utf-8'},
    );
  }

```