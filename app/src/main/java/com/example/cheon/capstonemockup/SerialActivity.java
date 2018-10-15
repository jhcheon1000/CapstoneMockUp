package com.example.cheon.capstonemockup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SerialActivity extends AppCompatActivity {
    private String TAG = "SerialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "SerialActivity Start onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_serialnum);
    }
}
