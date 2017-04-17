package com.example.administrator.mysensor;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Administrator on 2017-4-17.
 */

public class SnackBarUtil {

    public static Snackbar mSnackbar;

    public static void showSnackBar(View view, String msg) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
            mSnackbar.setAction("关闭", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSnackbar.dismiss();
                }
            });
        } else {
            mSnackbar.setText(msg);
        }
        mSnackbar.show();
    }
}
