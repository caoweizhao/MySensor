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

public class AccelerateFragment extends Fragment {

    LineChart mLineChart;
    TextView mTextView;
    SensorManager sm;
    private float[] gravity = new float[3];

    List<Entry> mEntries1 = new ArrayList<>();
    List<Entry> mEntries2 = new ArrayList<>();
    List<Entry> mEntries3 = new ArrayList<>();

    private static final int WIDTH = 10;
    private static int CURRENT_OFFSET = 10;

    private long lastTime;
    public SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            StringBuilder stringBuilder = new StringBuilder();
            float x = 0, y = 0, z = 0;
            //判断传感器类别
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER: //加速度传感器
                    final float alpha = (float) 0.8;
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                    x = (event.values[0] - gravity[0]);
                    y = (event.values[1] - gravity[1]);
                    z = (event.values[2] - gravity[2]);

                    stringBuilder.append("各方向加速度为:").append("\n")
                            .append("X:").append(x).append(Unit.ACCELERATE_UNIT).append("\n")
                            .append("Y:").append(y).append(Unit.ACCELERATE_UNIT).append("\n")
                            .append("Z:").append(z).append(Unit.ACCELERATE_UNIT);
                    //重力加速度9.81m/s^2，只受到重力作用的情况下，自由下落的加速度
                    break;
                case Sensor.TYPE_GRAVITY://重力传感器
                    gravity[0] = event.values[0];//单位m/s^2
                    gravity[1] = event.values[1];
                    gravity[2] = event.values[2];
                    break;
                default:
                    break;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 1000) {
                mTextView.setText(stringBuilder);

                if (mEntries1.size() > 10) {
                    mEntries1.remove(0);
                    mEntries2.remove(0);
                    mEntries3.remove(0);
                }

                mEntries1.add(new Entry(CURRENT_OFFSET, x));
                mEntries2.add(new Entry(CURRENT_OFFSET, y));
                mEntries3.add(new Entry(CURRENT_OFFSET, z));

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
                CURRENT_OFFSET += WIDTH;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public static AccelerateFragment newInstance() {
        AccelerateFragment lightFragment = new AccelerateFragment();

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
        Sensor lightSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(mListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void initChart() {
        mEntries1.add(new Entry(0, 0));
        mEntries2.add(new Entry(0, 0));
        mEntries3.add(new Entry(0, 0));

        Description description = new Description();
        description.setText("加速度曲线图");
        mLineChart.setDescription(description);
        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setEnabled(true);
        myX.setDrawLabels(false);

        LineDataSet dataSetX = new LineDataSet(mEntries1, "X");
        dataSetX.setColor(Color.RED);
        LineDataSet dataSetY = new LineDataSet(mEntries2, "Y");
        dataSetY.setColor(Color.BLUE);
        LineDataSet dataSetZ = new LineDataSet(mEntries3, "Z");
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
