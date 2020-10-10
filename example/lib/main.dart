import 'dart:async';

import 'package:data_dome_plugin/data_dome_plugin.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      final headers = {
        'Content-type': 'application/json',
        'Accept': 'application/json',
      };
      //TODO: fill in url, DataDome key for android
      final key = '';
      final url = '';
      final body = null;
      final response = await DataDomePlugin.httpCall(
        HttpMethod.get,
        url,
        headers,
        body,
        key,
      );
      final int responseCode = response['code'];
      platformVersion = responseCode.toString();
    } on PlatformException {
      platformVersion = 'Failed HTTP call';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }
}
