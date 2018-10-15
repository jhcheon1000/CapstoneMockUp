package com.example.cheon.capstonemockup;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;


public class DefaultActivity extends AppCompatActivity {
    private String TAG = "DefaultActivity";

    private Context mContext = DefaultActivity.this;

    private ViewGroup mainLayout; //사이드바 나왔을 때, 클릭 방지 영역
    private ViewGroup viewLayout; //전체 감싸는 영역
    private ViewGroup sideLayout; //사이드바만 감싸는 영역

    private Boolean isMenuShow = false;
    private Boolean isExitFlag = false;

    Toolbar defaultToolbar;

    private boolean powerOn = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if(isMenuShow){
            closeMenu();
        }else{

            if(isExitFlag){
                finish();
            } else {

                isExitFlag = true;
                Toast.makeText(this, "뒤로가기를 한번더 누르시면 앱이 종료됩니다.",  Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExitFlag = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Start DefaultActivity - OnCreate.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        defaultToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(defaultToolbar);

        Drawable rawDrawable = getResources().getDrawable(R.drawable.list);
        Bitmap dBitmap = ((BitmapDrawable) rawDrawable).getBitmap();
        Drawable mDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(dBitmap, 120, 120, true));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(mDrawable);

        getSupportActionBar().setTitle("");

        FloatingActionButton powerButton = (FloatingActionButton) findViewById(R.id.power_button);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!powerOn) {
                    powerOn = true;
                    v.setBackgroundTintList(new ColorStateList(new int[][]
                            {new int[]{0}}, new int[]{getResources().getColor(R.color.powerOn)}));
                    Toast.makeText(getApplicationContext(), "Power On", Toast.LENGTH_LONG).show();
                }
                else {
                    powerOn = false;
                    v.setBackgroundTintList(new ColorStateList(new int[][]
                            {new int[]{0}}, new int[]{getResources().getColor(R.color.powerOff)}));
                    Toast.makeText(getApplicationContext(), "Power Off", Toast.LENGTH_LONG).show();
                }
            }
        });

        mainLayout = findViewById(R.id.id_default_main);
        viewLayout = findViewById(R.id.fl_slide);
        sideLayout = findViewById(R.id.view_sildebar);

        addSideView();

        requestPermission();


    }

    private void addSideView() {

        SideBarView sidebar = new SideBarView(mContext);
        sideLayout.addView(sidebar);

        viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sidebar.setEventListener(new SideBarView.EventListener() {
            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel");
                closeMenu();
            }

            @Override
            public void onRegisterNewDevice() {
                Intent newDeviceIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(newDeviceIntent);
            }

            @Override
            public void onShowDeviceList() {

            }
        });
    }

    public void closeMenu() {
        isMenuShow = false;
        Animation slide = AnimationUtils.loadAnimation(mContext, R.anim.sidebar_hidden);
        sideLayout.startAnimation(slide);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewLayout.setVisibility(View.GONE);
                viewLayout.setEnabled(false);
                mainLayout.setEnabled(true);
            }
        }, 450);
    }

    public void showMenu() {
        isMenuShow = true;
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.sidebar_show);
        sideLayout.startAnimation(slide);
        viewLayout.setVisibility(View.VISIBLE);
        viewLayout.setEnabled(true);
        mainLayout.setEnabled(false);

        Log.i(TAG, "Side Menu Clicked");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Toast.makeText(getApplicationContext(), "Setting Button Clicked", Toast.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "Side Menu Button Clicked", Toast.LENGTH_LONG).show();
                showMenu();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission for Bluetooth")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(DefaultActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(DefaultActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
            Log.i(TAG, "Permission has been granted by user");


        } else {
            Log.i(TAG, "Permission has been denied by user");
        }

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
