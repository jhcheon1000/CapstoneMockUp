package com.example.cheon.capstonemockup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceActivity extends AppCompatActivity {
    private String TAG = "DeviceActivity";

    private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> input1 = new HashMap<>();
    private HashMap<String, String> input2 = new HashMap<>();
    ListView deviceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_status);

        deviceList = (ListView)findViewById(R.id.doorlock_enter);





//        List<String> dList = new ArrayList<>();
//
//        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dList);
//
//        deviceList.setAdapter(deviceAdapter);
//
//        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//
//        dList.add("CJH-1000");
//        dList.add("JKM-2015");
//        dList.add("KYJ-2014");

    }
}
