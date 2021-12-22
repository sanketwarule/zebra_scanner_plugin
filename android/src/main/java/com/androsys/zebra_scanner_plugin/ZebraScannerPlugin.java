package com.androsys.zebra_scanner_plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.IDcsSdkApiDelegate;
import com.zebra.scannercontrol.SDKHandler;

import java.util.ArrayList;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import static com.androsys.zebra_scanner_plugin.ConnectionActivity.PERMISSIONS_ACCESS_COARSE_LOCATION;

/** ZebraScannerPlugin */
public class ZebraScannerPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware, IDcsSdkApiDelegate {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private static String TAG = ZebraScannerPlugin.class.getSimpleName();
  private MethodChannel channel;
  private EventChannel eventChannel;
  private EventChannel.EventSink sink = null;
  private Activity activity;

  public static SDKHandler sdkHandler;
  public static ArrayList<DCSScannerInfo> scannerInfos = new ArrayList<>();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "zebra_scanner_plugin");
    channel.setMethodCallHandler(this);
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(),"barcode_stream");
    eventChannel.setStreamHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("connectToScanner")) {
      Log.d(TAG, "connectToScanner() called...");
      connectToScanner();
      result.success("connectToScanner");
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    eventChannel.setStreamHandler(null);
  }

  private void connectToScanner(){
    activity.startActivity(new Intent(activity, ConnectionActivity.class));
  }
  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    sink = events;
  }

  @Override
  public void onCancel(Object arguments) {
    sink = null;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    initScanner();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  @Override
  public void dcssdkEventScannerAppeared(DCSScannerInfo dcsScannerInfo) {
    Log.d(TAG, "dcssdkEventScannerAppeared :: " +dcsScannerInfo.getScannerName());
  }

  @Override
  public void dcssdkEventScannerDisappeared(int i) {
    Log.d(TAG, "dcssdkEventScannerDisappeared :: " +i);

  }

  @Override
  public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo dcsScannerInfo) {
    Log.d(TAG, "dcssdkEventCommunicationSessionEstablished :: " +dcsScannerInfo.getScannerName());

  }

  @Override
  public void dcssdkEventCommunicationSessionTerminated(int i) {
    Log.d(TAG, "dcssdkEventCommunicationSessionTerminated");
  }

  @Override
  public void dcssdkEventBarcode(byte[] bytes, int i, int i1) {
    String barcodeData = new String(bytes);
    Log.d(TAG, "dcssdkEventBarcode :: " + barcodeData);
    try {
      if (!barcodeData.isEmpty()) {
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            sink.success(barcodeData);
          }
        });
      }
    } catch (Exception e) {
      Log.e(TAG, "dcssdkEventBarcode: " + e.getLocalizedMessage());
    }
  }

  @Override
  public void dcssdkEventImage(byte[] bytes, int i) {

  }

  @Override
  public void dcssdkEventVideo(byte[] bytes, int i) {

  }

  @Override
  public void dcssdkEventBinaryData(byte[] bytes, int i) {

  }

  @Override
  public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {

  }

  @Override
  public void dcssdkEventAuxScannerAppeared(DCSScannerInfo dcsScannerInfo, DCSScannerInfo dcsScannerInfo1) {

  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  void initScanner(){
    sdkHandler = new SDKHandler(activity,true);
    sdkHandler.dcssdkSetDelegate(this);

    int notifications_mask = 0;
    // We would like to subscribe to all scanner available/not-available events
    notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value;
    // We would like to subscribe to all scanner connection events
    notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value;
    // We would like to subscribe to all barcode events
    notifications_mask |= DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value;
    // subscribe to events set in notification mask
    sdkHandler.dcssdkSubsribeForEvents(notifications_mask);

    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // If permission has not been set previously , permission can be requested from the below code
      activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_ACCESS_COARSE_LOCATION);
    }
    else {
      initializeDcsSdk();
    }
  }

  private void initializeDcsSdk() {
    sdkHandler.dcssdkEnableAvailableScannersDetection(true);
    sdkHandler.dcssdkEnableBluetoothScannersDiscovery(true);
    sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
    sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_LE);
  }

}
