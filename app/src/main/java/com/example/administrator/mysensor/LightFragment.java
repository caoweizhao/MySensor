package com.example.administrator.mysensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.mysensor.R.id.chart;

/**
 * Created by Administrator on 2017-4-12.
 */

public class LightFragment extends Fragment {

    LineChart mLineChart;
    TextView mTextView;
    SensorManager sm;

    List<Entry> mEntries = new ArrayList<>();

    private static final int WIDTH = 10;
    private int CURRENT_OFFSET = 10;

    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float level = event.values[0];
            mTextView.setText(new StringBuilder("当前光照强度为：").append(level).append(Unit.LIGHT_UNIT));

            LineData data = mLineChart.getData();
            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSet.getEntryCount() > 10) {
                dataSet.removeEntry(0);

            }
            dataSet.addEntry(new Entry(CURRENT_OFFSET, level));

            dataSet.notifyDataSetChanged();
            data.notifyDataChanged();
            mLineChart.moveViewToX(CURRENT_OFFSET);
            mLineChart.notifyDataSetChanged();
            //mLineChart.setVisibleXRangeMaximum(120);
            // mLineChart.invalidate();
            CURRENT_OFFSET += WIDTH;
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
        Log.d("LightFragment", "onCraeteView");
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        mLineChart = (LineChart) view.findViewById(chart);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("LightFragment", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        initChart();
        initSensor();
    }

    private void initSensor() {
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(mListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void initChart() {

        mEntries.add(new Entry(0, 0));
        mLineChart.getAxisLeft().setAxisMinimum(-1);
        mLineChart.getAxisRight().setAxisMinimum(-1);
        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setGranularity(10f);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setEnabled(true);
        myX.setDrawLabels(false);



        /*Matrix matrix = new Matrix();
        //x轴缩放1.5倍
        matrix.postScale(1.5f, 1f);
        //在图表动画显示之前进行缩放
        mLineChart.getViewPortHandler().refresh(matrix, mLineChart, false);
        //x轴执行动画
        mLineChart.animateX(2000);*/

        Description description = new Description();
        description.setText("光照曲线图");
        mLineChart.setDescription(description);
        LineData lineData = new LineData();
        LineDataSet dataSet = new LineDataSet(mEntries, "光照曲线");


        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.1f);
        dataSet.setDrawCircles(false);
        dataSet.setColor(Color.BLUE);

        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.GRAY);
        dataSet.setFillAlpha(128);

        lineData.addDataSet(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
        mLineChart.setAutoScaleMinMaxEnabled(true);


    }

    @Override
    public void onDestroyView() {
        Log.d("LightFragment", "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(mListener);
        Log.d("LightFragment", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("LightFragment", "oncreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d("LightFragment", "onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.d("LightFragment", "onStart");
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("LightFragment", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        Log.d("LightFragment", "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("LightFragment", "onStop");
        super.onStop();
    }
}
