package com.example.administrator.mysensor;

import android.os.Environment;
import android.support.annotation.IntDef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017-4-24.
 */

public class FileHelper {

    public static final int ACCELERATE = 0;
    public static final int GPS = 1;
    public static final int GYROSCOPE = 2;
    public static final int LIGHT = 3;
    public static final int MAGNETIC = 4;
    public static final int ORIENTATION = 5;
    public static final int SOUND = 6;
    public static String folderName = null;

    @IntDef(value = {ACCELERATE, GPS, GYROSCOPE, LIGHT, MAGNETIC, ORIENTATION, SOUND})
    public @interface FILE_TYPE {
    }

    public static final String mComma = ",";
    private static StringBuilder mStringBuilder = new StringBuilder();
    public static String mFileName = null;
    private static StringBuilder header = new StringBuilder();

    public static void open(@FILE_TYPE int type) {
        mStringBuilder.setLength(0);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            folderName = path + "/CSV/";
        }

        if (folderName == null) {
            return;
        }
        File fileRoot = new File(folderName);
        if (!fileRoot.exists()) {
            fileRoot.mkdir();
        }

        switch (type) {
            case ACCELERATE:
                mFileName = folderName + "accelerate.csv";
                // TODO: 2017-4-24
                header.setLength(0);
                header.append("XValue").append(mComma).append("YValue").append(mComma).append("ZValue").append(mComma).append("Time").append("\n");
                break;
            case GPS:
                mFileName = folderName + "gps.csv";
                header.setLength(0);
                header.append("Longitude").append(mComma).append("Latitude").append(mComma).append("Address").append(mComma).append("Time").append("\n");
                break;
            case GYROSCOPE:
                mFileName = folderName + "gyroscope.csv";
                header.setLength(0);
                header.append("XValue").append(mComma).append("YValue").append(mComma).append("ZValue").append(mComma).append("Time").append("\n");
                break;
            case ORIENTATION:
                mFileName = folderName + "orientation.csv";
                header.setLength(0);
                header.append("XValue").append(mComma).append("YValue").append(mComma).append("ZValue").append(mComma).append("Time").append("\n");
                break;
            case SOUND:
                mFileName = folderName + "sound.csv";
                header.setLength(0);
                header.append("Sound_Level").append(mComma).append("Time").append("\n");
                break;
            case MAGNETIC:
                mFileName = folderName + "magnetic.csv";
                header.setLength(0);
                header.append("XValue").append(mComma).append("YValue").append(mComma).append("ZValue").append(mComma).append("Time").append("\n");
                break;
            case LIGHT:
                mFileName = folderName + "light.csv";
                header.setLength(0);
                header.append("Light").append(mComma).append("Time").append("\n");
                break;
        }
    }

    public static void writeCsv(List<List<String>> datas) {
        for (List<String> mList : datas) {
            for (String data : mList) {
                mStringBuilder.append(data).append(mComma);
            }
            mStringBuilder.deleteCharAt(mStringBuilder.lastIndexOf(mComma));
            mStringBuilder.append("\n");
        }
    }

    public static void flush() {
        if (mFileName != null) {
            try {
                File file = new File(mFileName);
                FileOutputStream fos = new FileOutputStream(file, true);
                if (file.length() > 0) {
                    fos.write(mStringBuilder.toString().getBytes());
                } else {
                    fos.write(header.toString().getBytes());
                    fos.write(mStringBuilder.toString().getBytes());
                }

                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("You should call open() before flush()");
        }
    }
}
