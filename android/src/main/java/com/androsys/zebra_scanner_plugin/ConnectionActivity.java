package com.androsys.zebra_scanner_plugin;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zebra.scannercontrol.BarCodeView;
import com.zebra.scannercontrol.DCSSDKDefs;

import static com.androsys.zebra_scanner_plugin.ZebraScannerPlugin.scannerInfos;
import static com.androsys.zebra_scanner_plugin.ZebraScannerPlugin.sdkHandler;

class ConnectionActivity extends Activity {
    int scannerId;
    static String bluetoothAddress;
    private FrameLayout barcodeDisplayArea;
    private EditText deviceBluetoothAddress;
    public static final int PERMISSIONS_ACCESS_COARSE_LOCATION = 10;
    private Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        barcodeDisplayArea = (FrameLayout) findViewById(R.id.scan_to_connect_barcode);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.enter_bluetooth_address);
        deviceBluetoothAddress = (EditText) dialog.findViewById(R.id.device_bluetooth_address);
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayStc(v);
            }
        });
        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToScanner();
            }
        });
    }

    public void connectToScanner(){
        if (sdkHandler != null) {
            sdkHandler.dcssdkGetAvailableScannersList(scannerInfos);
        }
        try{
            scannerId = scannerInfos.get(0).getScannerID();
            new ConnectScanner(scannerId).execute();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectScanner extends AsyncTask {
        int scannerId;

        public ConnectScanner(int scannerId){
            this.scannerId=scannerId;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Toast.makeText(getApplicationContext(),"connected",Toast.LENGTH_SHORT).show();
            return sdkHandler.dcssdkEstablishCommunicationSession(scannerId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        generatePairingBarcode();
    }

    //The method that is responsible for displaying the STC barcode according to the user provided bluetooth address
    public void displayStc(View view) {
        if(dialog.isShowing()) {
            dialog.dismiss();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        bluetoothAddress= deviceBluetoothAddress.getText().toString();
        Toast.makeText(getApplicationContext(),bluetoothAddress, Toast.LENGTH_SHORT).show();
        if(bluetoothAddress.equals("")) {
            barcodeDisplayArea.removeAllViews();
        }
        else {
            BarCodeView barcodeView;
            // User provided bluetooth address gets set
            sdkHandler.dcssdkSetBTAddress(bluetoothAddress);
            // Here a STC barcode corresponding to the bluetooth address provided will be generated
            barcodeView = sdkHandler.dcssdkGetPairingBarcode(DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_LE, DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.SET_FACTORY_DEFAULTS, bluetoothAddress);
            if (barcodeView != null) {
                updateBarcodeView(layoutParams, barcodeView);
            }
        }
    }

    private void generatePairingBarcode() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        BarCodeView barCodeView = sdkHandler.dcssdkGetPairingBarcode(DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_SSI_SLAVE, DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.SET_FACTORY_DEFAULTS, bluetoothAddress);
//        BarCodeView barCodeView = sdkHandler.dcssdkGetPairingBarcode(DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_SSI_SLAVE, DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.SET_FACTORY_DEFAULTS);

        //For Android versions  7 and below  bluetooth address of the device will be taken by the SDK automatically. But for versions
        //above that barcodeView will be null , as bluetooth address cannot be retrieved via the SDK due to security measures.
        if(barCodeView!=null) {
            updateBarcodeView(layoutParams, barCodeView);
        }
        else {
            //when the barcodeView is null, a popup window will be visible where the bluetooth address of the device has to be entered
            // in order for the STC barcode to be visible.
            dialog.show();
        }
    }



    // Once the correct bluetooth address is received this method proceed to display the barcode in the given frame layout
    private void updateBarcodeView(LinearLayout.LayoutParams layoutParams, BarCodeView barcodeView) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int orientation =this.getResources().getConfiguration().orientation;
        int x = width * 9 / 10;
        int y = x / 3;
        barcodeView.setSize(x, y);
        barcodeDisplayArea.addView(barcodeView, layoutParams);
    }
}
