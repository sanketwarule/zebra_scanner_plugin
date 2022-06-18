package com.androsys.zebra_scanner_plugin;

import android.content.Context;
import android.util.Log;

import com.androsys.zebra_scanner_plugin.application.Application;
import com.androsys.zebra_scanner_plugin.barcode.BarcodeTypes;
import com.androsys.zebra_scanner_plugin.helpers.Foreground;
import com.androsys.zebra_scanner_plugin.helpers.ScannerAppEngine;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.IDcsSdkApiDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZebraScannerEngine implements ScannerAppEngine, IDcsSdkApiDelegate {
    protected static String TAG;
    private static ArrayList<IScannerAppEngineDevConnectionsDelegate> mDevConnDelegates = new ArrayList<IScannerAppEngineDevConnectionsDelegate>();
    private static ArrayList<DCSScannerInfo> mScannerInfoList;
    private static ArrayList<DCSScannerInfo> mOfflineScannerInfoList;
    private static ZebraScannerEngine zebraScannerEngine;

    private ZebraScannerEngine() {
        initialize();
        onResume();
    }

    public static ZebraScannerEngine getInstance() {
        if (zebraScannerEngine == null) {
            zebraScannerEngine = new ZebraScannerEngine();
        }
        return zebraScannerEngine;
    }

    protected void clearConnectionDelegate() {
        if (mDevConnDelegates != null) {
            mDevConnDelegates.clear();
        }
    }

    protected void initialize() {
        mScannerInfoList = Application.mScannerInfoList;
        mOfflineScannerInfoList = new ArrayList<DCSScannerInfo>();
        TAG = getClass().getSimpleName();
        Application.sdkHandler.dcssdkSetDelegate(this);
        initializeDcsSdkWithAppSettings();
    }

    protected void onResume() {
        Application.sdkHandler.dcssdkSetDelegate(this);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void initializeDcsSdkWithAppSettings() {
        // Restore preferences
//        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
//        vibrator = new ManagedVibrator(getApplicationContext());
//        Application.MOT_SETTING_OPMODE = settings.getInt(Constants.PREF_OPMODE, DCSSDK_CONNTYPE_BT_NORMAL.value);
//
//        Application.MOT_SETTING_SCANNER_DETECTION = settings.getBoolean(Constants.PREF_SCANNER_DETECTION, true);
//        Application.MOT_SETTING_EVENT_IMAGE = settings.getBoolean(Constants.PREF_EVENT_IMAGE, true);
//        Application.MOT_SETTING_EVENT_VIDEO = settings.getBoolean(Constants.PREF_EVENT_VIDEO, true);
//        Application.MOT_SETTING_EVENT_BINARY_DATA = settings.getBoolean(Constants.PREF_EVENT_BINARY_DATA, true);
//
//        Application.MOT_SETTING_EVENT_ACTIVE = settings.getBoolean(Constants.PREF_EVENT_ACTIVE, true);
//        Application.MOT_SETTING_EVENT_AVAILABLE = settings.getBoolean(Constants.PREF_EVENT_AVAILABLE, true);
//        Application.MOT_SETTING_EVENT_BARCODE = settings.getBoolean(Constants.PREF_EVENT_BARCODE, true);
//
//        Application.MOT_SETTING_NOTIFICATION_AVAILABLE = settings.getBoolean(Constants.PREF_NOTIFY_AVAILABLE, false);
//        Application.MOT_SETTING_NOTIFICATION_ACTIVE = settings.getBoolean(Constants.PREF_NOTIFY_ACTIVE, false);
//        Application.MOT_SETTING_NOTIFICATION_BARCODE = settings.getBoolean(Constants.PREF_NOTIFY_BARCODE, false);
//
//        Application.MOT_SETTING_NOTIFICATION_IMAGE = settings.getBoolean(Constants.PREF_NOTIFY_IMAGE, false);
//        Application.MOT_SETTING_NOTIFICATION_VIDEO = settings.getBoolean(Constants.PREF_NOTIFY_VIDEO, false);
//        Application.MOT_SETTING_NOTIFICATION_BINARY_DATA = settings.getBoolean(Constants.PREF_NOTIFY_BINARY_DATA, false);

        int notifications_mask = 0;
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_IMAGE.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_VIDEO.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BINARY_DATA.value);

        Application.sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
    }

    /* ###################################################################### */
    /* ########## Utility functions ######################################### */
    /* ###################################################################### */

    /**
     * Checks if the application is being sent in the background (i.e behind
     * another application's Activity).
     *
     * @param context the context
     * @return <code>true</code> if another application will be above this one.
     */

    @Override
    public boolean isInBackgroundMode(final Context context) {
        return Foreground.get().isBackground();
    }

    /* ###################################################################### */
    /* ########## API calls for UI View Controllers ######################### */
    /* ###################################################################### */
    @Override
    public void addDevListDelegate(IScannerAppEngineDevListDelegate delegate) {
//        if (Application.mDevListDelegates == null)
//            Application.mDevListDelegates = new ArrayList<IScannerAppEngineDevListDelegate>();
//        Application.mDevListDelegates.add(delegate);
    }

    @Override
    public void addDevConnectionsDelegate(IScannerAppEngineDevConnectionsDelegate delegate) {
        if (mDevConnDelegates == null)
            mDevConnDelegates = new ArrayList<IScannerAppEngineDevConnectionsDelegate>();
        mDevConnDelegates.add(delegate);
    }

    @Override
    public void addDevEventsDelegate(IScannerAppEngineDevEventsDelegate delegate) {
//        if (mDevEventsDelegates == null)
//            mDevEventsDelegates = new ArrayList<IScannerAppEngineDevEventsDelegate>();
//        mDevEventsDelegates.add(delegate);
    }

    @Override
    public void removeDevListDelegate(IScannerAppEngineDevListDelegate delegate) {
//        if (Application.mDevListDelegates != null)
//            Application.mDevListDelegates.remove(delegate);
    }

    @Override
    public void removeDevConnectiosDelegate(IScannerAppEngineDevConnectionsDelegate delegate) {
//        if (mDevConnDelegates != null)
//            mDevConnDelegates.remove(delegate);
    }

    @Override
    public void removeDevEventsDelegate(ScannerAppEngine.IScannerAppEngineDevEventsDelegate delegate) {
//        if (mDevEventsDelegates != null)
//            mDevEventsDelegates.remove(delegate);
    }

    @Override
    public List<DCSScannerInfo> getActualScannersList() {
        return mScannerInfoList;
    }

    @Override
    public DCSScannerInfo getScannerInfoByIdx(int dev_index) {
        if (mScannerInfoList != null)
            return mScannerInfoList.get(dev_index);
        else
            return null;
    }

    @Override
    public DCSScannerInfo getScannerByID(int scannerId) {
        if (mScannerInfoList != null) {
            for (DCSScannerInfo scannerInfo : mScannerInfoList) {
                if (scannerInfo != null && scannerInfo.getScannerID() == scannerId)
                    return scannerInfo;
            }
        }
        return null;
    }

    @Override
    public void raiseDeviceNotificationsIfNeeded() {

    }

    /* ###################################################################### */
    /* ########## Interface for DCS SDK ##################################### */
    /* ###################################################################### */
    @Override
    public void updateScannersList() {
        if (Application.sdkHandler != null) {
            mScannerInfoList.clear();
            ArrayList<DCSScannerInfo> scannerTreeList = new ArrayList<DCSScannerInfo>();
            Application.sdkHandler.dcssdkGetAvailableScannersList(scannerTreeList);
            Application.sdkHandler.dcssdkGetActiveScannersList(scannerTreeList);
            createFlatScannerList(scannerTreeList);
        }
    }

    private void createFlatScannerList(ArrayList<DCSScannerInfo> scannerTreeList) {
        for (DCSScannerInfo s :
                scannerTreeList) {
            addToScannerList(s);
        }
    }

    private void addToScannerList(DCSScannerInfo s) {
        mScannerInfoList.add(s);
        if (s.getAuxiliaryScanners() != null) {
            for (DCSScannerInfo aux :
                    s.getAuxiliaryScanners().values()) {
                addToScannerList(aux);
            }
        }
    }


    @Override
    public DCSSDKDefs.DCSSDK_RESULT setAutoReconnectOption(int scannerId, boolean enable) {
        DCSSDKDefs.DCSSDK_RESULT ret;
        if (Application.sdkHandler != null) {
            ret = Application.sdkHandler.dcssdkEnableAutomaticSessionReestablishment(enable, scannerId);
            return ret;
        }
        return DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE;
    }

    @Override
    public void enableScannersDetection(boolean enable) {
        if (Application.sdkHandler != null) {
            Application.sdkHandler.dcssdkEnableAvailableScannersDetection(enable);
        }
    }

    @Override
    public void enableBluetoothScannerDiscovery(boolean enable) {
        if (Application.sdkHandler != null) {
            Application.sdkHandler.dcssdkEnableBluetoothScannersDiscovery(enable);
        }
    }

    @Override
    public boolean executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE opCode, String inXML, StringBuilder outXML, int scannerID) {
        if (Application.sdkHandler != null) {
            if (outXML == null) {
                outXML = new StringBuilder();
            }
            DCSSDKDefs.DCSSDK_RESULT result = Application.sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(opCode, inXML, outXML, scannerID);
            if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS)
                return true;
            else if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
                return false;
        }
        return false;
    }

    @Override
    public boolean executeSSICommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE opCode, String inXML, StringBuilder outXML, int scannerID) {
        if (Application.sdkHandler != null) {
            if (outXML == null) {
                outXML = new StringBuilder();
            }
            DCSSDKDefs.DCSSDK_RESULT result = Application.sdkHandler.dcssdkExecuteSSICommandOpCodeInXMLForScanner(opCode, inXML, outXML, scannerID);
            if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS)
                return true;
            else if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
                return false;
        }
        return false;
    }


    /* ###################################################################### */
    /* ########## IDcsSdkApiDelegate Protocol implementation ################ */
    /* ###################################################################### */
    @Override
    public void dcssdkEventScannerAppeared(DCSScannerInfo availableScanner) {
        Log.d("test", "scanner appeared");
    }

    @Override
    public void dcssdkEventScannerDisappeared(int scannerID) {
    }

    @Override
    public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo activeScanner) {
        ArrayList<DCSScannerInfo> activeScanners = new ArrayList<DCSScannerInfo>();
        Application.sdkHandler.dcssdkGetActiveScannersList(activeScanners);

        for (IScannerAppEngineDevConnectionsDelegate delegate : mDevConnDelegates) {
            if (delegate != null) {
                delegate.scannerHasConnected(activeScanner.getScannerID());
            }
        }
    }

    @Override
    public void dcssdkEventCommunicationSessionTerminated(int scannerID) {
        Application.isAnyScannerConnected = false;
        Application.currentConnectedScannerID = -1;
        Application.lastConnectedScanner = Application.currentConnectedScanner;
        Application.currentConnectedScanner = null;
    }

    @Override
    public void dcssdkEventBarcode(byte[] barcodeData, int barcodeType, int fromScannerID) {

        String barcode = new String(barcodeData);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", barcode);
            jsonObject.put("type", BarcodeTypes.getBarcodeTypeName(barcodeType));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (IScannerAppEngineDevConnectionsDelegate delegate : mDevConnDelegates) {
            if (delegate != null) {
                delegate.onScanDataReceived(jsonObject.toString());
            }
        }
    }

    @Override
    public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {
    }

    @Override
    public void dcssdkEventAuxScannerAppeared(DCSScannerInfo newTopology, DCSScannerInfo auxScanner) {
    }

    @Override
    public void dcssdkEventImage(byte[] imageData, int fromScannerID) {
    }

    @Override
    public void dcssdkEventVideo(byte[] videoFrame, int fromScannerID) {
    }

    @Override
    public void dcssdkEventBinaryData(byte[] binaryData, int fromScannerID) {
    }

}
