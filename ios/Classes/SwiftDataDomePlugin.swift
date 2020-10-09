import Flutter
import UIKit
import DataDomeSDK

public class SwiftDataDomePlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "data_dome_plugin", binaryMessenger: registrar.messenger())
    let instance = SwiftDataDomePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
  
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {	
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)
    case "get":
      let args = call.arguments as! [String: Any]
      let url = args["url"] as! String
      let headers = args["headers"] as! [String: String]
      get(url, headers, result)
    default:
      result(nil)
    }
  }
  
  private func get(_ url: String, _ headers: [String: String], _ result: @escaping FlutterResult) {
    guard let theUrl = URL(string: url) else {
      return
    }
    let task = URLSession.shared.protectedDataTask(withURL: theUrl, captchaDelegate: nil) { (data, response, error) in
      DispatchQueue.main.async {
        if let response = response as? HTTPURLResponse {
          let statusCode = NSNumber(value: response.statusCode)
          if let data = data {
            let bytes = FlutterStandardTypedData(bytes: data)
            result(["code": statusCode, "data": bytes])
          } else {
            result(["code": statusCode])
          }
        } else {
          result([:] as! [String: Any])
        }
      }
    }
    task.resume()
  }
}
