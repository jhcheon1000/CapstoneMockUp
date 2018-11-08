package com.example.cheon.capstonemockup;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class SsgService extends Service {

//    for bluetooth
    private BluetoothDevice bdDevice = null;
    private BluetoothSocket bdSocket = null;

    private UUID uuidSPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ArrayList<String> arrayListpaired;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;

    BluetoothAdapter bluetoothAdapter = null;

    Boolean isConnectionError = null;

    @Override
    public void onCreate() {
        super.onCreate();

        arrayListpaired = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();

        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
