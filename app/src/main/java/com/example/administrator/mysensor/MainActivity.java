package com.example.administrator.mysensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int LIGHT = 0;
    public static final int ACCELERATE = 1;
    public static final int MAGNETIC = 2;
    public static final int GYROSCOPE = 3;
    public static final int ORIENTATION = 4;
    public static final int SOUND = 5;
    public static final int GPS = 6;

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
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        tryRequestPermission(Manifest.permission.RECORD_AUDIO);
                    } else {
                        setFrag();
                    }
                } else {
                    setFrag();
                }*/

                BlankFragment blankFragment = BlankFragment.newInstance("arg1","arg2");
                ft = fm.beginTransaction();
                ft.replace(R.id.container, blankFragment);
                ft.commit();
                break;
            case GPS:
                BlankFragment blankFragment1 = BlankFragment.newInstance("arg1","arg2");
                ft = fm.beginTransaction();
                ft.replace(R.id.container, blankFragment1);
                ft.commit();
                break;

        }
    }

    private void setFrag() {
        SoundFragment soundFragment = SoundFragment.newInstance();
        ft = fm.beginTransaction();
        ft.replace(R.id.container, soundFragment);
        ft.commitAllowingStateLoss();
    }

    public void tryRequestPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(permission)) {
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
            } else {
                requestPermissions(new String[]{permission}, 0);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d("MainActivity", "onSave");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onPostResume() {
        Log.d("MainActivity","onPostResume");
        super.onPostResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限获取成功！", Toast.LENGTH_SHORT).show();
                    setFrag();
                } else {
                    finish();
                }
            }
        }
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
