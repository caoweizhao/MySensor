package com.example.administrator.mysensor;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017-4-17.
 */

public class SnackBarUtil {

    public static Snackbar mSnackbar;
    private static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (mSnackbar != null) {
                    mSnackbar.show();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("SnackBarUtil", "stop");
                    mSnackbar = null;
                }
            }
        }
    };
    private static Thread mThread;

    public static void showSnackBar(View view, String msg) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            mSnackbar.setAction("关闭", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSnackbar.dismiss();
                    mThread.interrupt();
                }
            });
            mThread = new Thread(mRunnable);
            mThread.start();
        } else {
            mSnackbar.setText(msg);
        }
    }
}
