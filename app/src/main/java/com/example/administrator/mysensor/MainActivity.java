package com.example.administrator.mysensor;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public static final int LIGHT = 0;
    public static final int ACCELERATE = 1;
    public static final int MAGNETIC = 2;
    public static final int GYROSCOPE = 3;
    public static final int ORIENTATION = 4;
    public static final int SOUND = 5;
    public static final int GPS = 6;

    private int currentPosition = 0;

    private TabLayout mTabLayout;
    private FragmentManager fm = getSupportFragmentManager();
    private FragmentTransaction ft;

    //用于监听电源键长按
    MyReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initBroadcastReceiver();

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        initTabLayout();
    }

    //初始化注册广播监听电源长按事件
    private void initBroadcastReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(myReceiver, intentFilter);
    }

    //初始化TabLayout
    private void initTabLayout() {
        TabLayout.Tab tab1 = mTabLayout.newTab();
        tab1.setText("光照");
        TabLayout.Tab tab2 = mTabLayout.newTab();
        tab2.setText("加速度");
        TabLayout.Tab tab3 = mTabLayout.newTab();
        tab3.setText("磁场");
        TabLayout.Tab tab4 = mTabLayout.newTab();
        tab4.setText("陀螺仪");
        TabLayout.Tab tab5 = mTabLayout.newTab();
        tab5.setText("方向");
        TabLayout.Tab tab6 = mTabLayout.newTab();
        tab6.setText("声音");
        TabLayout.Tab tab7 = mTabLayout.newTab();
        tab7.setText("GPS");
        mTabLayout.addTab(tab1);
        mTabLayout.addTab(tab2);
        mTabLayout.addTab(tab3);
        mTabLayout.addTab(tab4);
        mTabLayout.addTab(tab5);
        mTabLayout.addTab(tab6);
        mTabLayout.addTab(tab7);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                setFragment(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mTabLayout.setScrollPosition(0, 0, true);
        setFragment(0);
    }

    private void setFragment(int position) {
        currentPosition = position;
        switch (position) {
            case LIGHT:
                LightFragment lightFragment = LightFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, lightFragment);
                ft.commit();
                break;
            case ACCELERATE:
                AccelerateFragment accelerateFragment = AccelerateFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, accelerateFragment);
                ft.commit();
                break;
            case MAGNETIC:
                MagneticFragment magneticFragment = MagneticFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, magneticFragment);
                ft.commit();
                break;
            case GYROSCOPE:
                GyroScopeFragment gyroScopeFragment = GyroScopeFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, gyroScopeFragment);
                ft.commit();
                break;
            case ORIENTATION:
                OrientationFragment directionFragment = OrientationFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, directionFragment);
                ft.commit();
                break;
            case SOUND:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        tryRequestPermission(Manifest.permission.RECORD_AUDIO);
                    } else {
                        setSoundFrag();
                    }
                } else {
                    setSoundFrag();
                }

               /* BlankFragment blankFragment = BlankFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, blankFragment);
                ft.commit();*/
                break;
            case GPS:

                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(ACCESS_FINE_LOCATION);
                }
                if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
                    permissionList.add(READ_PHONE_STATE);
                }
                if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(WRITE_EXTERNAL_STORAGE);
                }

                if (!permissionList.isEmpty()) {
                    final String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                    if (shouldShowTips()) {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle("缺少权限")
                                .setMessage("APP需要定位权限来获取位置信息!")
                                .setPositiveButton("授予", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create();
                        dialog.show();
                    } else {
                        ActivityCompat.requestPermissions(this, permissions, 1);
                    }
                } else {
                    setGPSFrag();
                }
                break;

        }
    }

    private boolean shouldShowTips() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, READ_PHONE_STATE);
    }



    private void setGPSFrag() {
        GPSFragment gpsFragment = GPSFragment.newInstance();
        ft = fm.beginTransaction();
        ft.replace(R.id.container, gpsFragment);
        ft.commitAllowingStateLoss();
    }

    private void setSoundFrag() {
        SoundFragment soundFragment = SoundFragment.newInstance();
        ft = fm.beginTransaction();
        ft.replace(R.id.container, soundFragment);
        ft.commitAllowingStateLoss();
    }

    public void tryRequestPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(permission)) {
                if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("缺少权限")
                            .setMessage("APP需要录音权限来获取分贝值!")
                            .setPositiveButton("授予", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{permission}, 0);
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();
                    dialog.show();
                }
            } else {
                requestPermissions(new String[]{permission}, 0);
            }
        }
    }

    @Override
    protected void onPostResume() {
        Log.d("MainActivity", "onPostResume");
        super.onPostResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0) {
                if (permissions[0].equals(Manifest.permission.RECORD_AUDIO)) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "权限获取成功！", Toast.LENGTH_SHORT).show();
                        setSoundFrag();
                    } else {
                        finish();
                    }
                }
            } else {
                Toast.makeText(this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else if(requestCode == 1){
            if(grantResults.length >0){
                for(int result:grantResults){
                    if(result != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "必须要有相应权限才能获取位置信息！", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
                setGPSFrag();
            } else {
                Toast.makeText(this, "发生未知错误！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("MainActivity", "onSave");
        outState.putInt("position", currentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("MainActivity", "onRestore");
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt("position");
        setFragment(position);
        mTabLayout.setScrollPosition(position, 0, true);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this, "音量键下触发", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(this, "音量键上触发", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 监听电源长按事件
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("reason");
            if (data != null && data.equals("globalactions")) {
                Toast.makeText(context, "电源键长按", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
