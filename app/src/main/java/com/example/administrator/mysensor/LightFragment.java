package com.example.administrator.mysensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.administrator.mysensor.R.id.chart;

/**
 * Created by Administrator on 2017-4-12.
 */

public class LightFragment extends Fragment {

    /**
     * 图表实例
     */
    LineChart mLineChart;
    /**
     * 描述信息
     */
    TextView mTextView;
    /**
     * 传感器管理类
     */
    SensorManager sm;

    /**
     * 当前显示坐标集，最多十个
     */
    List<Entry> mEntries = new ArrayList<>();

    /**
     * 保存所有坐标
     */
    List<Entry> mEntryList = new ArrayList<>();
    /**
     * 点的X轴间距
     */
    private static final int WIDTH = 10;
    /**
     * 当前点的X坐标
     */
    private int CURRENT_OFFSET = 10;

    private long lastTime;

    /**
     * 传感器监听器
     */
    private SensorEventListener mListener = new SensorEventListener() {
        /**
         * 传感器事件回调
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 1000) {
                float level = event.values[0];
                mTextView.setText(new StringBuilder("当前光照强度为：").append(level).append(Unit.LIGHT_UNIT));

                LineData data = mLineChart.getData();
                LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
                if (dataSet.getEntryCount() > 10) {
                    dataSet.removeEntry(0);
                }
                Entry e = new Entry(CURRENT_OFFSET, level);
                dataSet.addEntry(e);
                mEntryList.add(e);

                dataSet.notifyDataSetChanged();
                data.notifyDataChanged();
                mLineChart.moveViewToX(CURRENT_OFFSET);
                mLineChart.notifyDataSetChanged();
                //mLineChart.setVisibleXRangeMaximum(120);
                // mLineChart.invalidate();
                CURRENT_OFFSET += WIDTH;
                lastTime = currentTime;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public static LightFragment newInstance() {
        LightFragment lightFragment = new LightFragment();

        Bundle bundle = new Bundle();
        lightFragment.setArguments(bundle);
        return lightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        mLineChart = (LineChart) view.findViewById(chart);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        view.findViewById(R.id.saveToCSV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToCSV();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChart();
        initSensor();
    }

    /**
     * 初始化传感器
     */
    private void initSensor() {
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(mListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 初始化图表
     */
    private void initChart() {

        mEntries.add(new Entry(0, 0));
        mLineChart.getAxisLeft().setAxisMinimum(-1);
        mLineChart.getAxisRight().setAxisMinimum(-1);
        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setGranularity(10f);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);    //设置X轴位置
        myX.setEnabled(true);
        myX.setDrawLabels(false);   //设置是否显示X轴的值

        //设置图表描述信息
        Description description = new Description();
        description.setText("光照曲线图");
        mLineChart.setDescription(description);

        LineData lineData = new LineData();
        LineDataSet dataSet = new LineDataSet(mEntries, "光照曲线");

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.1f);    //设置平滑度
        dataSet.setDrawCircles(false);  //设置是否显示点集中的圆圈
        dataSet.setColor(Color.BLUE);   //设置折线颜色

        dataSet.setDrawFilled(true);    //设置折线下方是否显示阴影
        dataSet.setFillColor(Color.GRAY);   //设置阴影颜色
        dataSet.setFillAlpha(128);  //设置透明度

        lineData.addDataSet(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
        mLineChart.setAutoScaleMinMaxEnabled(true);     //设置XY轴是否自动缩放
    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(mListener);
        super.onDestroy();
    }

    public void saveToCSV() {
        int count = mEntryList.size();
        FileHelper.open(FileHelper.LIGHT);
        List<List<String>> mList = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String dateStr = sdf.format(date);
        for (int i = 0; i < count; i++) {
            List<String> datas = new ArrayList<>();
            Entry entry = mEntryList.get(i);
            datas.add(entry.getY() + "");
            datas.add(dateStr);
            mList.add(datas);
        }
        FileHelper.writeCsv(mList);
        FileHelper.flush();
        mList.clear();
        mEntryList.clear();
        Snackbar.make(mLineChart, "保存成功 " + FileHelper.mFileName, Snackbar.LENGTH_LONG).show();

    }
}
