package com.example.administrator.mysensor;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-4-12.
 */

public class SoundFragment extends Fragment {

    LineChart mLineChart;
    TextView mTextView;
    /**
     * 用户录音获取分贝值
     */
    AudioRecordDemo myRecordDemo;
    /**
     * 点的X轴间距
     */
    private static final int WIDTH = 10;
    /**
     * 当前点的X坐标
     */
    private int CURRENT_OFFSET = 10;
    /**
     * 点集
     */
    private List<Entry> mEntries = new ArrayList<>();

    public static SoundFragment newInstance() {
        SoundFragment lightFragment = new SoundFragment();

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
        myRecordDemo = new AudioRecordDemo();
        myRecordDemo.getNoiseLevel();
    }

    /**
     * 初始化图表
     */
    private void initChart() {
        mEntries.add(new Entry(0, 0));
        mLineChart.getAxisLeft().setAxisMinimum(0);
        mLineChart.getAxisRight().setAxisMinimum(0);
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
        description.setText("分贝曲线图");
        mLineChart.setDescription(description);
        LineData lineData = new LineData();
        LineDataSet dataSet = new LineDataSet(mEntries, "分贝曲线");


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
        myRecordDemo.stopRecord();
        super.onDestroyView();
    }

    /**
     * 录音获取分贝值
     */
    private class AudioRecordDemo {
        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                while (isGetVoiceRun) {
                    //r是实际读取的数据长度，一般而言r会小于buffersize
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                    long v = 0;
                    // 将 buffer 内容取出，进行平方和运算
                    for (int i = 0; i < buffer.length; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    // 平方和除以数据总长度，得到音量大小。
                    double mean = v / (double) r;
                    double volume = 10 * Math.log10(mean);
                    DecimalFormat df = new DecimalFormat("##.###"); //设置解析格式
                    final double value = Double.parseDouble(df.format(volume));

                    mTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(new StringBuilder("当前分贝值为：").append(value).append(Unit.SOUND_UNIT));

                            //更新图标数据
                            LineData data = mLineChart.getData();
                            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
                            if (dataSet.getEntryCount() > 10) {
                                dataSet.removeEntry(0);
                            }
                            dataSet.addEntry(new Entry(CURRENT_OFFSET, (float) value));

                            dataSet.notifyDataSetChanged();
                            data.notifyDataChanged();
                            mLineChart.moveViewToX(CURRENT_OFFSET);
                            mLineChart.notifyDataSetChanged();
                            //mLineChart.setVisibleXRangeMaximum(120);
                            // mLineChart.invalidate();
                            CURRENT_OFFSET += WIDTH;
                        }
                    });

                    CURRENT_OFFSET += 10;
                    // 大概一秒一次
                    synchronized (mLock) {
                        try {
                            mLock.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d("AudioRecordDemo", "quit record!");
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        };

        private Thread mThread = new Thread(mRunnable);

        private static final String TAG = "AudioRecord";
        static final int SAMPLE_RATE_IN_HZ = 8000;
        final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        boolean isGetVoiceRun;
        //用于线程延时
        Object mLock;

        public AudioRecordDemo() {
            mLock = new Object();
        }

        public void stopRecord() {
            isGetVoiceRun = false;
        }

        public void getNoiseLevel() {
            if (isGetVoiceRun) {
                Log.e(TAG, "还在录着呢");
                return;
            }

            if (mAudioRecord == null) {
                Log.e("sound", "mAudioRecord初始化失败");
            }
            isGetVoiceRun = true;
            mThread.start();
        }
    }
}
