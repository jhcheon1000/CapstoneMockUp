package com.example.cheon.capstonemockup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity{
    private String TAG = "RegisterActivity";

    Button mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "RegisterActivity Start onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mConfirmButton = findViewById(R.id.btn_serial_next);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bleDeviceIntent = new Intent(getApplicationContext(), AvailableDeviceActivity.class);
                startActivity(bleDeviceIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
