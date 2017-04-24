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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-4-12.
 */

public class MagneticFragment extends Fragment {

    List<Entry> mEntries1 = new ArrayList<>();
    List<Entry> mEntries2 = new ArrayList<>();
    List<Entry> mEntries3 = new ArrayList<>();

    List<Entry> mEntryList1 = new ArrayList<>();
    List<Entry> mEntryList2 = new ArrayList<>();
    List<Entry> mEntryList3 = new ArrayList<>();

    LineChart mLineChart;
    TextView mTextView;
    SensorManager sm;
    private long lastTime;

    private static final int WIDTH = 10;
    private int CURRENT_OFFSET = 10;
    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float xValue = 0, yValue = 0, zValue = 0;

            xValue = event.values[0];
            yValue = event.values[1];
            zValue = event.values[2];

            StringBuilder sb = new StringBuilder();
            sb.append("当前磁场大小为：");
            sb.append("\n");
            sb.append("X:").append(xValue).append(Unit.MAGNETIC_UNIT).append("\n");
            sb.append("Y:").append(yValue).append(Unit.MAGNETIC_UNIT).append("\n");
            sb.append("Z:").append(zValue).append(Unit.MAGNETIC_UNIT);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 1000) {
                mTextView.setText(sb);

                if (mEntries1.size() > 10) {
                    mEntries1.remove(0);
                    mEntries2.remove(0);
                    mEntries3.remove(0);
                    mLineChart.getXAxis().setAxisMinimum(mEntries1.get(1).getX());
                }

                Entry entry1 = new Entry(CURRENT_OFFSET, xValue);
                Entry entry2 = new Entry(CURRENT_OFFSET, yValue);
                Entry entry3 = new Entry(CURRENT_OFFSET, zValue);
                mEntries1.add(entry1);
                mEntries2.add(entry2);
                mEntries3.add(entry3);

                mEntryList1.add(entry1);
                mEntryList2.add(entry2);
                mEntryList3.add(entry3);

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
                } else {
                    LineDataSet dataSetX = new LineDataSet(mEntries1, "X");
                    dataSetX.setColor(Color.RED);
                    dataSetX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSetX.setCubicIntensity(0.1f);
                    //dataSetX.setDrawCircles(false);
                    LineDataSet dataSetY = new LineDataSet(mEntries2, "Y");
                    dataSetY.setColor(Color.BLUE);
                    dataSetY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSetY.setCubicIntensity(0.1f);
                    //dataSetY.setDrawCircles(false);
                    LineDataSet dataSetZ = new LineDataSet(mEntries3, "Z");
                    dataSetZ.setColor(Color.GRAY);
                    dataSetZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSetZ.setCubicIntensity(0.1f);
                    //dataSetZ.setDrawCircles(false);

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

    public static MagneticFragment newInstance() {
        MagneticFragment lightFragment = new MagneticFragment();

        Bundle bundle = new Bundle();
        lightFragment.setArguments(bundle);
        return lightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        view.findViewById(R.id.saveToCSV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToCSV();
            }
        });
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
        Sensor magneticSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(mListener, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void initChart() {
        mEntries1.add(new Entry(0, 0));
        mEntries2.add(new Entry(0, 0));
        mEntries3.add(new Entry(0, 0));
        mLineChart.setAutoScaleMinMaxEnabled(true);

        Description description = new Description();
        description.setText("磁场曲线图");
        mLineChart.setDescription(description);
        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setGranularity(10f);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setEnabled(true);
        myX.setDrawLabels(false);
        myX.setAxisMinimum(10);

        LineDataSet dataSetX = new LineDataSet(mEntries1, "X");
        dataSetX.setColor(Color.RED);
        dataSetX.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetX.setCubicIntensity(0.1f);
        dataSetX.setDrawCircles(false);
        //dataSetX.setDrawValues(false);

        LineDataSet dataSetY = new LineDataSet(mEntries2, "Y");
        dataSetY.setColor(Color.BLUE);
        dataSetY.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetY.setCubicIntensity(0.1f);
        dataSetY.setDrawCircles(false);
        //dataSetY.setDrawValues(false);

        LineDataSet dataSetZ = new LineDataSet(mEntries3, "Z");
        dataSetZ.setColor(Color.DKGRAY);
        dataSetZ.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetZ.setCubicIntensity(0.1f);
        dataSetZ.setDrawCircles(false);
        //dataSetZ.setDrawValues(false);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetX);
        dataSets.add(dataSetY);
        dataSets.add(dataSetZ);
        LineData lineData = new LineData(dataSets);
        mLineChart.setData(lineData);
        mLineChart.invalidate();

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

    public void saveToCSV() {
        int count = mEntryList1.size();
        FileHelper.open(FileHelper.MAGNETIC);
        List<List<String>> mList = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String dateStr = sdf.format(date);
        for (int i = 0; i < count; i++) {
            List<String> datas = new ArrayList<>();
            Entry entry1 = mEntryList1.get(i);
            Entry entry2 = mEntryList2.get(i);
            Entry entry3 = mEntryList3.get(i);

            datas.add(entry1.getY() + "");
            datas.add(entry2.getY() + "");
            datas.add(entry3.getY() + "");
            datas.add(dateStr);
            mList.add(datas);
        }
        FileHelper.writeCsv(mList);
        FileHelper.flush();
        mList.clear();
        mEntryList1.clear();
        mEntryList2.clear();
        mEntryList3.clear();
        Snackbar.make(mLineChart, "保存成功 " + FileHelper.mFileName, Snackbar.LENGTH_LONG).show();

    }

}
