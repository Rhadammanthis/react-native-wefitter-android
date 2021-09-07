package com.reactnativewefitterandroid

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.wefitter.android.WeFitter

class WeFitterAndroidModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private val weFitter by lazy { WeFitter(reactContext) }

  override fun getName(): String {
    return "WeFitterAndroid"
  }

  @ReactMethod
  fun configure(config: ReadableMap) {
    val token = config.getString("token") ?: ""
    val apiUrl = config.getString("apiUrl") ?: ""
    val statusListener = object : WeFitter.StatusListener {
      override fun onConfigured(configured: Boolean) {
        sendEvent(
          reactContext,
          "onConfiguredWeFitterAndroid",
          Arguments.createMap().apply { putBoolean("configured", configured) })
      }

      override fun onConnected(connected: Boolean) {
        sendEvent(
          reactContext,
          "onConnectedWeFitterAndroid",
          Arguments.createMap().apply { putBoolean("connected", connected) })
      }

      override fun onError(error: String) {
        sendEvent(
          reactContext,
          "onErrorWeFitterAndroid",
          Arguments.createMap().apply { putString("error", error) })
      }
    }
    val notificationConfig = parseNotificationConfig(config)
    weFitter.configure(token, apiUrl, statusListener, notificationConfig)
  }

  @ReactMethod
  fun connect() {
    weFitter.connect()
  }

  @ReactMethod
  fun disconnect() {
    weFitter.disconnect()
  }

  @ReactMethod
  fun isConnected(callback: Callback) {
    callback(weFitter.isConnected())
  }

  @ReactMethod
  fun isSupported(callback: Callback) {
    callback(weFitter.isSupported())
  }

  private fun parseNotificationConfig(config: ReadableMap): WeFitter.NotificationConfig {
    return WeFitter.NotificationConfig().apply {
      config.getString("notificationTitle")?.let { title = it }
      config.getString("notificationText")?.let { text = it }
      config.getString("notificationIcon")?.let {
        val resourceId = getResourceId(it)
        if (resourceId != 0) iconResourceId = resourceId
      }
      config.getString("notificationChannelId")?.let { channelId = it }
      config.getString("notificationChannelName")?.let { channelName = it }
    }
  }

  private fun getResourceId(resourceName: String): Int {
    val resources = reactContext.resources
    val packageName = reactContext.packageName
    var resourceId = resources.getIdentifier(resourceName, "mipmap", packageName)
    if (resourceId == 0) {
      resourceId = resources.getIdentifier(resourceName, "drawable", packageName)
    }
    if (resourceId == 0) {
      resourceId = resources.getIdentifier(resourceName, "raw", packageName)
    }
    return resourceId
  }

  private fun sendEvent(reactContext: ReactContext, eventName: String, params: WritableMap?) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }
}
