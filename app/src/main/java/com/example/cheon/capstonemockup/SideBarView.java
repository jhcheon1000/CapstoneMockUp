package com.example.cheon.capstonemockup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.Toast;

import java.util.EventListener;

public class SideBarView extends RelativeLayout implements View.OnClickListener {

    public EventListener sideListener;

    public void setEventListener(EventListener i) {
        sideListener = i;
    }

    public interface EventListener {

        void onCancel();
        void onRegisterNewDevice();
        void onShowDeviceList();

        //장치 검색, 기기 목록 listener 등록할 것.
    }

    public SideBarView(Context context) {
        this(context, null);
        init();
    }

    public SideBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_side, this, true);

        findViewById(R.id.side_level_layout1).setOnClickListener(this);
        findViewById(R.id.side_level_layout2).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Toast.makeText(getContext(), "Cancel Button Clicked", Toast.LENGTH_LONG).show();
                sideListener.onCancel();
                break;
            case R.id.side_level_layout1:
                Toast.makeText(getContext(), "New Device Button Clicked", Toast.LENGTH_SHORT).show();
                sideListener.onRegisterNewDevice();
                break;
            case R.id.side_level_layout2:
                Toast.makeText(getContext(), "Device List Button Clicked", Toast.LENGTH_LONG).show();
                sideListener.onShowDeviceList();
                break;
            default:
                break;
        }
    }
}
