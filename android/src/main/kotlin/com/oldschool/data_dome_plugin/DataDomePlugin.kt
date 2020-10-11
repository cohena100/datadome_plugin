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
        @Suppress("UNCHECKED_CAST")
        val args = call.arguments as Map<String, Any>
        val url = args["url"] as String

        @Suppress("UNCHECKED_CAST")
        val headers = args["headers"] as? Map<String, String>
        val body = args["body"] as? ByteArray
        val key = args["key"] as String
        httpCall(call.method, url, headers, body, key, result)
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

    private fun httpCall(method: String, url: String, headers: Map<String, String>?, body: ByteArray?, key: String, result: Result) {
        dataDomeSDK = DataDomeSDK.with(activity!!.application, key, BuildConfig.VERSION_NAME)
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(DataDomeInterceptor(activity!!.application, dataDomeSDK))
        val client = builder.build()
        var requestBuilder = Request.Builder().url(url)
        if (headers != null) {
            requestBuilder = requestBuilder.headers(Headers.of(headers))
        }
        when (method) {
            "get" -> {
                requestBuilder = requestBuilder.get()
            }
            "delete" -> {
                requestBuilder = requestBuilder.delete()
            }
            "post" -> {
                requestBuilder = if (body != null) {
                    requestBuilder.post(RequestBody.create(null, body))
                } else {
                    requestBuilder.post(RequestBody.create(null, ByteArray(0)))
                }
            }
            "put" -> {
                requestBuilder = if (body != null) {
                    requestBuilder.put(RequestBody.create(null, body))
                } else {
                    requestBuilder.put(RequestBody.create(null, ByteArray(0)))
                }
            }
            else -> { // Note the block
                result.notImplemented()
                return
            }
        }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
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
