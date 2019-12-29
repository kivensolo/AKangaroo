package com.zeke.kangaroo.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by KingZ.
 * Data: 2016 2016/2/1
 * Discription:  Toast工具类
 */
public class ToastUtils {
    private static WeakReference<Toast> toast = null;

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void show(Context context, int id) {
        show(context, context.getString(id));
    }

    public static void show(Context context, String msg) {
        if (toast != null && toast.get() != null) {
            toast.get().cancel();
            toast = null;
        }
        toast = new WeakReference<>(Toast.makeText(context, msg, Toast.LENGTH_SHORT));
        toast.get().show();
    }
}
