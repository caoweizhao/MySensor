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
    AudioRecordDemo myRecordDemo;
    private int CURRENT_OFFSET = 10;

    public static SoundFragment newInstance() {
        SoundFragment lightFragment = new SoundFragment();

        Bundle bundle = new Bundle();
        lightFragment.setArguments(bundle);
        return lightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("LightFragment", "onCraeteView");
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        mLineChart = (LineChart) view.findViewById(R.id.chart);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("LightFragment", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        Description description = new Description();
        description.setText("分贝曲线图");
        mLineChart.setDescription(description);
        LineData lineData = new LineData();
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        LineDataSet dataSet = new LineDataSet(entries, "分贝曲线");
        lineData.addDataSet(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
        mLineChart.setAutoScaleMinMaxEnabled(true);

        XAxis myX = mLineChart.getXAxis();
        myX.setDrawGridLines(false);
        myX.setGridLineWidth(10);
        myX.setGranularity(10f);
        myX.setPosition(XAxis.XAxisPosition.BOTTOM);
        myX.setEnabled(true);
        myX.setDrawLabels(false);

        myRecordDemo = new AudioRecordDemo();
        myRecordDemo.getNoiseLevel();
    }

    @Override
    public void onDestroyView() {
        Log.d("LightFragment", "onDestroyView");
        myRecordDemo.stopRecord();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
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

    class AudioRecordDemo {
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
                    DecimalFormat df = new DecimalFormat("##.###");
                    final double value = Double.parseDouble(df.format(volume));

                    mTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(new StringBuilder("当前分贝值为：").append(value).append(Unit.SOUND_UNIT));
                            LineData data = mLineChart.getData();
                            if (data == null) {
                                data = new LineData();
                            }
                            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
                            if (dataSet == null) {
                                dataSet = new LineDataSet(null, "分贝值");
                            }

                            if (dataSet.getEntryCount() > 10) {
                                dataSet.removeEntry(0);
                            }
                            dataSet.addEntry(new Entry(CURRENT_OFFSET, (float) value));
                            dataSet.setCircleColor(Color.BLUE);
                            dataSet.setColor(Color.BLUE);
                            dataSet.notifyDataSetChanged();
                            data.notifyDataChanged();
                            mLineChart.invalidate();
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
                Log.d("AudioRecordDemo","quit record!");
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
