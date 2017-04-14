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

public class OrientationFragment extends Fragment {

    LineChart mLineChart;
    TextView mTextView;
    SensorManager sm;

    List<Entry> mEntries1 = new ArrayList<>();
    List<Entry> mEntries2 = new ArrayList<>();
    List<Entry> mEntries3 = new ArrayList<>();

    private static final int WIDTH = 10;
    private static int CURRENT_OFFSET = 10;

    private long lastTime;
    public SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float xValue = event.values[1];
            float yValue = event.values[2];
            float zValue = event.values[0];

            StringBuilder sb = new StringBuilder();
            if (Math.abs(yValue) > 45 && Math.abs(yValue) < 145) {
                sb.append("当前方向为侧躺");
            } else {
                if (Math.abs(xValue) < 90) {
                    sb.append("当前方向为向上");
                } else {
                    sb.append("当前方向为向下");
                }
            }

            sb.append("\n");
            sb.append("X:").append(xValue).append(Unit.ORIENTATION_UNIT).append("\n");
            sb.append("Y:").append(yValue).append(Unit.ORIENTATION_UNIT).append("\n");
            sb.append("Z:").append(zValue).append(Unit.ORIENTATION_UNIT);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 1000) {
                mTextView.setText(sb);

                if (mEntries1.size() > 10) {
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
                CURRENT_OFFSET += OrientationFragment.WIDTH;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public static OrientationFragment newInstance() {
        OrientationFragment lightFragment = new OrientationFragment();

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
        mEntries1.add(new Entry(0,0));
        mEntries2.add(new Entry(0,0));
        mEntries3.add(new Entry(0,0));

        Description description = new Description();
        description.setText("方向曲线");
        mLineChart.setDescription(description);
        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setEnabled(true);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setDrawLabels(false);

        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sm.registerListener(mListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(mListener);
        super.onDestroy();
    }


}
