package com.example.administrator.mysensor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GPSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GPSFragment extends Fragment {

    /**
     * 用于记录我的位置
     */
    private BDLocation mLocation;

    private LocationClient mLocationClient;
    private BaiduMap mBaiduMap;

    /**
     * 描述信息
     */
    private TextView mTextView;
    /**
     * 地图View
     */
    private MapView mMapView;
    /**
     * 用于导航至我的位置
     */
    private FloatingActionButton navToMyLocation;
    /**
     * 用于判定方向，更新我的位置图标方向
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 当前方向
     */
    private float myCurrentX;
    /**
     * 用于设置我的位置图标
     */
    private BitmapDescriptor myBitmapLocation;

    public GPSFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GPSFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GPSFragment newInstance() {
        GPSFragment fragment = new GPSFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gps_layout, container, false);
        mTextView = (TextView) view.findViewById(R.id.gps_text_view);
        navToMyLocation = (FloatingActionButton) view.findViewById(R.id.nav_to_my_location);
        navToMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMyLocation(mLocation);
            }
        });
        mMapView = (MapView) view.findViewById(R.id.gps_map_view);
        hideLogo();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        useLocationOrientationListener();
        startBaiDuLocation();
    }

    /**
     * 隐藏LOGO以及比例尺
     */
    private void hideLogo() {
        // 隐藏百度的LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        // 不显示地图上比例尺
        mMapView.showScaleControl(false);
    }

    /**
     * 开启百度地图服务
     */
    private void startBaiDuLocation() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        //设置需要获取地址描述信息（XX附近）
        option.setIsNeedLocationDescribe(true);
        option.setEnableSimulateGps(true);
        option.setIgnoreKillProcess(false);
        //设置坐标数据格式
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        //设置精度
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {

        /**
         * 百度地图地址刷新后的回调，注意此方法调用在子线程中
         * @param location
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocation = location;

            final StringBuilder sb = new StringBuilder();
            sb.append("地址:").append(location.getAddress().address).append("\n");
            sb.append("位置信息：").append(location.getLocationDescribe()).append("\n");
            sb.append("纬度：").append(location.getLatitude()).append(Unit.ORIENTATION_UNIT).append("\n");
            sb.append("经度：").append(location.getLongitude()).append(Unit.ORIENTATION_UNIT).append("\n");
            sb.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("网络");
            }

            //更新地址信息
            mTextView.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(sb);
                }
            });

            //显示我的位置
            showMyLocation();

            //第一次加载地图，自动导航至我的位置
            if (isFirstIn) {
                isFirstIn = false;
                navigateToMyLocation(location);
                //缩放地图
                MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(18f);
                mBaiduMap.animateMapStatus(update);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    //用于标志地图是否第一次加载
    private boolean isFirstIn = true;

    /**
     * 导航至我的位置
     * @param location
     */
    private void navigateToMyLocation(BDLocation location) {
        if (location == null) {
            return;
        }
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(update);
    }

    /**
     * 展示我的位置
     */
    private void showMyLocation() {
        BDLocation location = mLocation;
        if (location == null) {
            return;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        MyLocationData myLocationData = builder
                .direction(myCurrentX)  //开启方向指示
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        mBaiduMap.setMyLocationData(myLocationData);
        if (isFirstIn) {
            //(我的位置)设置自定义图标
            changeLocationIcon();
        }
    }

    /**
     * 定位结合方向传感器，从而可以实时监测到X轴坐标的变化，从而就可以检测到
     * 定位图标方向变化，只需要将这个动态变化的X轴的坐标更新myCurrentX值;
     */
    private void useLocationOrientationListener() {
        myOrientationListener = new MyOrientationListener(getActivity());
        myOrientationListener.setMyOrientationListener(new MyOrientationListener.onOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {//监听方向的改变，方向改变时，需要得到地图上方向图标的位置
                myCurrentX = x;
            }
        });
        myOrientationListener.start();
    }

    /**
     * 自定义定位图标
     */
    private void changeLocationIcon() {
        myBitmapLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_navigation_black_24dp);//引入自己的图标
        MyLocationConfiguration config = new
                MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, myBitmapLocation);
        mBaiduMap.setMyLocationConfiguration(config);
    }

    /**
     * -----------------------------------------------------------------------------------
     * 回调方法中对调用mapView的相应方法以释放内存
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        myOrientationListener.stop();
    }
}
