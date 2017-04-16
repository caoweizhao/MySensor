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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-4-12.
 */

public class GyroScopeFragment extends Fragment {

    LineChart mLineChart;
    TextView mTextView;
    SensorManager sm;

    List<Entry> mEntries1 = new ArrayList<>();
    List<Entry> mEntries2 = new ArrayList<>();
    List<Entry> mEntries3 = new ArrayList<>();

    private static final int WIDTH = 10;
    private int CURRENT_OFFSET = 10;

    private long lastTime;


    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float[] angels = new float[3];
    public SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float xValue, yValue, zValue;
            /*if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                angels[0] += event.values[0] * dT;
                angels[1] += event.values[1] * dT;
                angels[2] += event.values[2] * dT;
            }
            timestamp = event.timestamp;
            xValue = (int) Math.toDegrees(angels[0]);
            yValue = (int) Math.toDegrees(angels[1]);
            zValue = (int) Math.toDegrees(angels[2]);*/

            xValue = event.values[0];
            yValue = event.values[1];
            zValue = event.values[2];


            StringBuilder sb = new StringBuilder();
            sb.append("当前陀螺仪数据为：");
            sb.append("\n");
            sb.append("X:").append(xValue).append(Unit.GYROSCOPE_UNIT).append("\n");
            sb.append("Y:").append(yValue).append(Unit.GYROSCOPE_UNIT).append("\n");
            sb.append("Z:").append(zValue).append(Unit.GYROSCOPE_UNIT);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 1000) {
                mTextView.setText(sb);

                /*if (mEntries1.size() > 10) {
                    mEntries1.remove(0);
                    mEntries2.remove(0);
                    mEntries3.remove(0);
                }

                mEntries1.add(new Entry(CURRENT_OFFSET, xValue));
                mEntries2.add(new Entry(CURRENT_OFFSET, yValue));
                mEntries3.add(new Entry(CURRENT_OFFSET, zValue));

                LineDataSet dataSetX = new LineDataSet(mEntries1, "X");
                dataSetX.setColor(Color.RED);
                dataSetX.setCircleColor(Color.RED);
                LineDataSet dataSetY = new LineDataSet(mEntries2, "Y");
                dataSetY.setColor(Color.BLUE);
                dataSetY.setCircleColor(Color.BLUE);
                LineDataSet dataSetZ = new LineDataSet(mEntries3, "Z");
                dataSetZ.setColor(Color.parseColor("#7fd919"));
                dataSetZ.setCircleColor(Color.parseColor("#7fd919"));
                List<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSetX);
                dataSets.add(dataSetY);
                dataSets.add(dataSetZ);
                LineData lineData = new LineData(dataSets);
                mLineChart.setData(lineData);
                mLineChart.invalidate();
                lastTime = currentTime;
                CURRENT_OFFSET += WIDTH;*/

                if (mEntries1.size() > 10) {
                    mEntries1.remove(0);
                    mEntries2.remove(0);
                    mEntries3.remove(0);


                    mLineChart.getXAxis().setAxisMinimum(mEntries1.get(1).getX());
                }

                mEntries1.add(new Entry(CURRENT_OFFSET, xValue));
                mEntries2.add(new Entry(CURRENT_OFFSET, yValue));
                mEntries3.add(new Entry(CURRENT_OFFSET, zValue));

                if (mLineChart.getLineData() != null && mLineChart.getLineData().getDataSetCount() > 0) {
                    LineDataSet dataSetX = (LineDataSet) mLineChart.getLineData().getDataSetByIndex(0);
                    LineDataSet dataSetY = (LineDataSet) mLineChart.getLineData().getDataSetByIndex(1);
                    LineDataSet dataSetZ = (LineDataSet) mLineChart.getLineData().getDataSetByIndex(2);

                    dataSetX.setValues(mEntries1);
                    dataSetY.setValues(mEntries2);
                    dataSetZ.setValues(mEntries3);

                    mLineChart.getLineData().notifyDataChanged();
                    mLineChart.notifyDataSetChanged();
                    mLineChart.invalidate();
                }else{
                    LineDataSet dataSetX = new LineDataSet(mEntries1, "X");
                    dataSetX.setColor(Color.RED);
                    dataSetX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSetX.setCubicIntensity(0.1f);
                    dataSetX.setDrawCircles(false);
                    LineDataSet dataSetY = new LineDataSet(mEntries2, "Y");
                    dataSetY.setColor(Color.BLUE);
                    dataSetY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSetY.setCubicIntensity(0.1f);
                    dataSetY.setDrawCircles(false);
                    LineDataSet dataSetZ = new LineDataSet(mEntries3, "Z");
                    dataSetZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSetZ.setCubicIntensity(0.1f);
                    dataSetZ.setDrawCircles(false);
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSetX);
                    dataSets.add(dataSetY);
                    dataSets.add(dataSetZ);
                    LineData lineData = new LineData(dataSets);
                    mLineChart.setData(lineData);
                    mLineChart.moveViewToX(CURRENT_OFFSET);
                    mLineChart.invalidate();
                }

                lastTime = currentTime;
                CURRENT_OFFSET += WIDTH;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public static GyroScopeFragment newInstance() {
        GyroScopeFragment lightFragment = new GyroScopeFragment();

        Bundle bundle = new Bundle();
        lightFragment.setArguments(bundle);
        return lightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        mLineChart = (LineChart) view.findViewById(R.id.chart);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChart();
        initSensor();
    }

    private void initSensor() {
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sm.registerListener(mListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void initChart() {
        mEntries1.add(new Entry(0,0));
        mEntries2.add(new Entry(0,0));
        mEntries3.add(new Entry(0,0));
        mLineChart.setAutoScaleMinMaxEnabled(true);

        Description description = new Description();
        description.setText("陀螺仪曲线图");
        mLineChart.setDescription(description);
        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setEnabled(true);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setDrawLabels(false);

        LineDataSet dataSetX = new LineDataSet(mEntries1, "X");
        dataSetX.setColor(Color.RED);
        dataSetX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetX.setCubicIntensity(0.1f);
        dataSetX.setDrawCircles(false);
        dataSetX.setDrawValues(false);

        LineDataSet dataSetY = new LineDataSet(mEntries2, "Y");
        dataSetY.setColor(Color.BLUE);
        dataSetY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetY.setCubicIntensity(0.1f);
        dataSetY.setDrawCircles(false);
        dataSetY.setDrawValues(false);

        LineDataSet dataSetZ = new LineDataSet(mEntries3, "Z");
        dataSetZ.setColor(Color.DKGRAY);
        dataSetZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetZ.setCubicIntensity(0.1f);
        dataSetZ.setDrawCircles(false);
        dataSetZ.setDrawValues(false);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetX);
        dataSets.add(dataSetY);
        dataSets.add(dataSetZ);
        LineData lineData = new LineData(dataSets);
        mLineChart.setData(lineData);
        mLineChart.invalidate();


    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(mListener);
        super.onDestroy();
    }


}
