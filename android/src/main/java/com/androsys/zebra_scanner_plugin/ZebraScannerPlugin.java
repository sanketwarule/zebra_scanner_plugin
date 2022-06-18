package com.androsys.zebra_scanner_plugin;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.androsys.zebra_scanner_plugin.helpers.ScannerAppEngine;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.SDKHandler;

import java.util.ArrayList;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * ZebraScannerPlugin
 */
public class ZebraScannerPlugin implements FlutterPlugin,
        MethodCallHandler, EventChannel.StreamHandler, ActivityAware, ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate {
    /// The MethodChannel that will the communication between Flutter and native Android
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private static final String TAG = ZebraScannerPlugin.class.getSimpleName();
    private MethodChannel channel;
    private EventChannel eventChannel;
    private Activity activity;

    public static SDKHandler sdkHandler;
    public static ArrayList<DCSScannerInfo> scannerInfoList = new ArrayList<DCSScannerInfo>();
    private EventChannel.EventSink sink = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "zebra_scanner_plugin");
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "barcode_stream");
        channel.setMethodCallHandler(this);
        eventChannel.setStreamHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
//            case "connectToScanner":
//                String bluetoothAddress = call.argument("bluetoothAddress");
//                launchBarcodeScanActivity(bluetoothAddress);
//                result.success("connectToScanner() method invoked");
//                break;
            case "connect":
                launchBarcodeScanActivity();
                final Map<String,Object> arguments = call.arguments();

                result.success("connectToScanner() method invoked");
                break;
            case "initScanner":
                ZebraScannerEngine.getInstance().clearConnectionDelegate();
                ZebraScannerEngine.getInstance().addDevConnectionsDelegate(this);
                break;
            case "disconnectScanner":
                ZebraScannerEngine.getInstance().clearConnectionDelegate();
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    public void launchBarcodeScanActivity(String bluetoothAddress) {
        Intent intent = new Intent(activity, ConnectionActivity.class);
        intent.putExtra("bluetoothAddress", bluetoothAddress);
        activity.startActivity(intent);
    }

    public void launchBarcodeScanActivity() {
        Intent intent = new Intent(activity, ConnectionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Log.d(TAG, "onAttachedToActivity");
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        sink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        sink = null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean scannerHasConnected(int scannerID) {
        try {
            if (scannerID != 0) {
                activity.runOnUiThread(() -> channel.invokeMethod("scannerHasConnected", true));
            }
        } catch (Exception e) {
            Log.e(TAG, "dcssdkEventBarcode: " + e.getLocalizedMessage());
        }
        return true;
    }

    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        return false;
    }

    @Override
    public void onScanDataReceived(String scanData) {
        try {
            if (scanData != null && !scanData.isEmpty()) {
                activity.runOnUiThread(() -> sink.success(scanData));
            }
        } catch (Exception e) {
            Log.e(TAG, "dcssdkEventBarcode: " + e.getLocalizedMessage());
        }
    }
}
