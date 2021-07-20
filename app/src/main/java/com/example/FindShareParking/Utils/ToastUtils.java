package com.example.FindShareParking.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    @SuppressLint("StaticFieldLeak")
    private static ToastUtils instance;
    private final Context context;

    private ToastUtils(Context context) {
        this.context = context;
    }

    public static ToastUtils getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new ToastUtils(context);
        }
    }


    public void ToastMsg(String msg) {
        Toast.makeText(context, msg,
                Toast.LENGTH_SHORT).show();
    }
}
