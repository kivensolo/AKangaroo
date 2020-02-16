package com.zeke.kangaroo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;

import java.lang.reflect.Method;

public final class UIUtils {
    private final static String TAG = "UIUtils";

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static View getFirstFocusableView(ViewParent parent) {
        try {
            ViewGroup group = (ViewGroup) parent;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                if (group.getChildAt(i).getVisibility() == View.VISIBLE &&
                        group.getChildAt(i).isEnabled() &&
                        group.getChildAt(i).isFocusable()) {
                    return group.getChildAt(i);
                }
            }
        } catch (Exception e) {
            ZLog.e(TAG, "getFirstFocusableView error:" + e.getLocalizedMessage());
        }
        return null;
    }

    public static void setViewSize(View view, int width, int height) {
        if (view == null) {
            ZLog.e(TAG, "setViewSize view==null");
            return;
        }
        if (view.getLayoutParams() == null) {
            ZLog.e(TAG, "setViewSize view.getLayoutParams()==null");
            return;
        }
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
    }

    public static void setViewMargin(View view, int leftMargin, int topMargin,
                                     int rightMargin, int bottomMargin) {
        if (view == null) {
            ZLog.e(TAG, "setViewMargin view==null");
            return;
        }
        if (view.getLayoutParams() == null) {
            ZLog.e(TAG, "setViewMargin view.getLayoutParams()==null");
            return;
        }
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = leftMargin;
        params.topMargin = topMargin;
        params.rightMargin = rightMargin;
        params.bottomMargin = bottomMargin;
    }

    public static void measureWidthAndHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    /**
     * 通过系统反射方法强制隐藏状态栏
     * 适用系统应该是:
     * Build.VERSION.SDK_INT 小于  Build.VERSION_CODES.LOLLIPOP
     * 5.0后的系统,把Context的STATUS_BAR_SERVICE弄成hide的了
     * @param context Context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void hindStatuBarByInvoke(Context context) {
        if(Build.VERSION.SDK_INT <  Build.VERSION_CODES.LOLLIPOP){
            return;
        }
        try {
            Class<?> localClass2 = Class.forName("android.os.SystemProperties");
            Method localMethod2 = localClass2.getMethod("set", String.class,
                    String.class);
            String arg1 = "sys.statusbar.forcehide";
            String arg2 = "true";
            localMethod2.invoke(null, arg1, arg2);
            @SuppressLint("WrongConstant")
            Object localObject = context.getSystemService("statusbar");
            Class<?> localClass1 = localObject.getClass();
            int i = localClass1.getField("DISABLE_MASK").getInt(null);
            Method localMethod1 = localClass1.getMethod("disable", int.class);
            localMethod1.invoke(localObject, i);
            ZLog.i(TAG, "App hideStatusBar OK");
        } catch (Exception localException2) {
            localException2.printStackTrace();
        }
    }

}
