package com.example.cheon.capstonemockup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AvailableDeviceActivity extends AppCompatActivity {
    private String TAG = "AvailableDeviceActivity";

//    FOR BLUETOOTH=================================================================================
    ListView listViewDetected;
    ListView listViewPaired;

    ArrayAdapter<String> adapter, detectedAdapter;
    static HandleSearch handleSearch;

    private BluetoothDevice bdDevice = null;
    private BluetoothSocket bdSocket = null;

    private UUID uuidSPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    ArrayList<String> arrayListpaired;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;



    ListItemClickedonPaired listItemClickedonPaired;
    ListItemClicked listItemClicked;

    BluetoothAdapter bluetoothAdapter = null;


    ConnectedTask mConnectedTask = null;

    Boolean isConnectionError = null;



    //==================================================================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("sibal", "onCreate start");
        setContentView(R.layout.activity_device_list);

        listViewDetected = (ListView) findViewById(R.id.listViewDetected);
        listViewPaired = (ListView) findViewById(R.id.listViewPaired);

        arrayListpaired = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handleSearch = new HandleSearch();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        /*
         * the above declaration is just for getting the paired bluetooth devices;
         * this helps in the removing the bond between paired devices.
         */
        listItemClickedonPaired = new ListItemClickedonPaired();
        listItemClicked = new ListItemClicked();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        adapter= new ArrayAdapter<String>(AvailableDeviceActivity.this, android.R.layout.simple_list_item_1, arrayListpaired);
        detectedAdapter = new ArrayAdapter<String>(AvailableDeviceActivity.this, android.R.layout.simple_list_item_single_choice);
        listViewDetected.setAdapter(detectedAdapter);

        detectedAdapter.notifyDataSetChanged();
        listViewPaired.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.i("sibal", "onStart start");

        listViewDetected.setOnItemClickListener(listItemClicked);
        listViewPaired.setOnItemClickListener(listItemClickedonPaired);
        isConnectionError = false;

    }

    private void getPairedDevices() {
        Log.i("sibal", "getPairedDevices() start");
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        Log.i("sibal", "getPairedDevices() size " + String.valueOf(pairedDevice.size()));
        if(pairedDevice.size()>0)
        {
            for(BluetoothDevice device : pairedDevice)
            {
                arrayListpaired.add(device.getName()+"\n"+device.getAddress());
                Log.i("sibal", "paired device " + device.getName());
                arrayListPairedBluetoothDevices.add(device);
            }
        }

        adapter.notifyDataSetChanged();

    }

    public void connected(BluetoothSocket soc, BluetoothDevice device) {
        mConnectedTask = new ConnectedTask(soc, device);
        mConnectedTask.execute();
    }

    private class PairedTask extends AsyncTask<Void, Void, Void> {

        PairedTask(View view, int position) {
            bdDevice = arrayListBluetoothDevices.get(position);
            Log.i("Log", "The dvice : "+bdDevice.toString());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /*
             * here below we can do pairing without calling the callthread(), we can directly call the
             * pairedDevice(). but for the safer side we must usethe threading object.
             */
            pairedDevice(bdDevice);

            Boolean isBonded = false;
            try {
                isBonded = createBond(bdDevice);
                if(isBonded) {
                    Log.i("sibal", "bonded true");

                    //isBonded 가 true를 반환했으면 size가 0일 수 없으므로 while을 통해 싱크 맞춤.
                    Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
                    while (pairedDevice.size() < 1) {
                        pairedDevice = bluetoothAdapter.getBondedDevices();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("Log", "The bond is created: "+isBonded);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            getPairedDevices();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }


    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice device) {
            mBluetoothDevice = device;

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuidSPP);
                Log.i(TAG, "create socket for " + mBluetoothDevice.getName());
            } catch (IOException e) {
                Log.e(TAG, "socket for " + mBluetoothDevice.getName() + "create failed " + e.getMessage());
            }

            Toast.makeText(getApplicationContext(), "success creating socket for " + mBluetoothDevice.getName(), Toast.LENGTH_SHORT);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            bluetoothAdapter.cancelDiscovery();

            try {
                mBluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();

                try {
                    mBluetoothSocket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "unable to close socket during connection failure", e1);
                }

                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isConnectSuccess) {
            super.onPostExecute(isConnectSuccess);

            if (isConnectSuccess) {
                connected(mBluetoothSocket, mBluetoothDevice);
            }
            else {
                //연결 실패 오류 처리
                isConnectionError = true;
                Log.d(TAG, "Unable to connect device");
                Toast.makeText(getApplicationContext(), "Unable to connect device", Toast.LENGTH_SHORT);
            }
        }
    }

    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {
        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket, BluetoothDevice device){

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e );
            }

            Log.i( TAG, "connected to "+ device.getName());

        }
//        Bluetooth 통신 실험. 서비스 등록 및 싹 다 바꿔야함. 실험만 하는 거임. 명심해라. 실험만 하는 거니까 열심히 해라.
        @Override
        protected Boolean doInBackground(Void... voids) {
            byte[] readBuffer = new byte[1024];
            int readBufferPosition = 0;

            while (true) {
//                while문 탈출 조건 추가하기
                if (isCancelled()) return false;

                try {
                    int bytesAvailable = mInputStream.available();

                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mInputStream.read(packetBytes);

                        for (int i =0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == '\n') {
                                byte[] encodeBytes = new byte[readBufferPosition];

                                System.arraycopy(readBuffer, 0, encodeBytes, 0, encodeBytes.length);
                                String recvMessage = new String(encodeBytes, "UTF-8");

                                readBufferPosition = 0;

                                Log.d(TAG, "recv message: " + recvMessage);
                            }
                            else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    return false;
                }
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (!aBoolean) {
                closeSocket();
                Log.i(TAG, "Device connection was lost");
                isConnectionError = true;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket() {
            try {
                mBluetoothSocket.close();
                Log.i(TAG, "close socket");
            } catch (IOException e) {
                Log.e(TAG, "unable to close() ", e);
            }
        }

        void write(String msg) {
            msg += "\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e );
            }
        }
    }


    class ListItemClicked implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
//            bdDevice = arrayListBluetoothDevices.get(position);
            //bdClass = arrayListBluetoothDevices.get(position);
            /*
             * here below we can do pairing without calling the callthread(), we can directly call the
             * connect(). but for the safer side we must usethe threading object.
             */
            //callThread();

            PairedTask pTask = new PairedTask(view, position);
            pTask.execute();

        }
    }
    class ListItemClickedonPaired implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            bdDevice = arrayListPairedBluetoothDevices.get(position);

//            try {
//                Boolean removeBonding = removeBond(bdDevice);
//                if(removeBonding)
//                {
//                    arrayListpaired.remove(position);
//                    adapter.notifyDataSetChanged();
//                }
//
//                Log.i("Log", "Removed"+removeBonding);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            ConnectTask mConnectTask = new ConnectTask(bdDevice);
            mConnectTask.execute();
        }
    }

    /*private void callThread() {
        new Thread(){
            public void run() {
                Boolean isBonded = false;
                try {
                    isBonded = createBond(bdDevice);
                    if(isBonded)
                    {
                        arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }//connect(bdDevice);
                Log.i("Log", "The bond is created: "+isBonded);
            }
        }.start();
    }*/

    private Boolean pairedDevice(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            Log.i("Log", "This is: "+bool.booleanValue());
            Log.i("Log", "devicesss: "+bdDevice.getName());
        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    }

    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (arrayListBluetoothDevices.isEmpty()) Log.d("Empty", "empty empty");
            Message msg = Message.obtain();
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){


                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() == null) return;
                try
                {
                    //device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                    //device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
                }
                catch (Exception e) {
                    Log.i("Log", "Inside the exception: ");
                    e.printStackTrace();
                }
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();
                if(arrayListBluetoothDevices.size()<1) // this checks if the size of bluetooth device is 0,then add the
                {                                           // device to the arraylist.
                    detectedAdapter.add(device.getName()+"\n"+device.getAddress());
                    Log.i("sibal","detected device " + device.getName());
                    arrayListBluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();
                }
                else
                {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for(int i = 0; i<arrayListBluetoothDevices.size();i++)
                    {
                        if(device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress()))
                        {
                            flag = false;
                        }
                    }
                    if(flag == true)
                    {
                        detectedAdapter.add(device.getName()+"\n"+device.getAddress());
                        arrayListBluetoothDevices.add(device);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }
    private void onBluetooth() {
        while(!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
            Log.i("Log", "Bluetooth is Enabled");
        }
    }
    private void offBluetooth() {
        while(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.disable();
        }
    }

    class HandleSearch extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("sibal", "onResume start");

        onBluetooth();

        arrayListBluetoothDevices.clear();
        getPairedDevices();
        startSearching();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                detectedAdapter.notifyDataSetChanged();
//                adapter.notifyDataSetChanged();
//            }
//        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("sibal", "onPause start");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("sibal", "onStop start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("sibal", "onDestroy start");
        offBluetooth();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
