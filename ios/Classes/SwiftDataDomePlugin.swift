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
    let method =  call.method.uppercased()
    let args = call.arguments as! [String: Any]
    let url = args["url"] as! String
    let headers = args["headers"] as? [String: String]
    let body = args["body"] as? FlutterStandardTypedData
    httpCall(method, url, headers, body?.data, result)
  }
  
  private func httpCall(_ method: String, _ url: String, _ headers: [String: String]?, _ body: Data?,_ result: @escaping FlutterResult) {
    var request = URLRequest(url: URL(string: url)!)
    request.httpMethod = method
    if let headers = headers {
      for (field, value) in headers{
        request.addValue(value, forHTTPHeaderField: field)
      }
    }
    request.httpBody = body
    let task = URLSession.shared.protectedDataTask(withRequest: request, captchaDelegate: nil) { (data, response, error) in
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
          result([String: Any]())
        }
      }
    }
    task.resume()
  }
}
