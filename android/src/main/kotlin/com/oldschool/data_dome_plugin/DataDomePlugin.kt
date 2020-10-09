package com.oldschool.data_dome_plugin

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import co.datadome.sdk.DataDomeInterceptor
import co.datadome.sdk.DataDomeSDK
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import okhttp3.*
import java.io.IOException


/** DataDomePlugin */
class DataDomePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var dataDomeSDK: DataDomeSDK.Builder
    private var activity: Activity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "data_dome_plugin")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "get" -> {
                @Suppress("UNCHECKED_CAST")
                val args = call.arguments as Map<String, Any>
                val url = args["url"] as String
                @Suppress("UNCHECKED_CAST")
                val headers = args["headers"] as Map<String, String>
                val key = args["key"] as String
                get(url, headers, key, result)
            }
            else -> { // Note the block
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    fun get(url: String, headers: Map<String, String>, key: String, result: Result) {
        dataDomeSDK = DataDomeSDK.with(activity?.application, key, BuildConfig.VERSION_NAME).agent("BLOCKUA")
        dataDomeSDK.userAgent = "BLOCKUA"
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(DataDomeInterceptor(activity!!.application, dataDomeSDK))
        val client = builder.build()
        val headersBuild: Headers = Headers.of(headers)
        val request = Request.Builder().url(url).headers(headersBuild).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    result.success(emptyMap<String, Any>())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val statusCode = response.code()
                val bytes = response.body()?.bytes()
                Handler(Looper.getMainLooper()).post {
                    result.success(mapOf("code" to statusCode, "data" to bytes))
                }
            }
        })
    }
}
