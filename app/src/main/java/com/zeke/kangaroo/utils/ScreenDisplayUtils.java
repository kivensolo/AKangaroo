package com.zeke.kangaroo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;

/**
 * author: King.Z <br>
 * date:  2020/5/25 22:24 <br>
 * description: 屏幕显示工具类 <br>
 */
public class ScreenDisplayUtils {
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(wm == null){ return 0; }
        DisplayMetrics outMetrics = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(wm == null){ return 0; }
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context Context
     * @return 状态栏高度 px value
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        //原生系统支持的反射方式
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //上述反射方式在部分国产手机系统上无法获取高度，可通过以下方式获取
        if(statusHeight == -1){
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        Log.d("ScreenDisplayUtils","getStatusHeight:" + statusHeight);
        return statusHeight;
    }

    /**
     * 获取当前窗口的旋转角度
     *
     * @param activity  当前Activity
     * @return 旋转角度
     */
    public static int getDisplayRotation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_0:
            default:
                return 0;
        }
    }

    public static boolean isLandscape(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isLandscape(View view) {
        int orientation = view.getResources().getConfiguration().orientation;
        return  orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    public static boolean isPortrait(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isPortrait(View view) {
        int orientation = view.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 获得当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static int getScreenBrightnessMode(Context context) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception ignored) {}
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    public static int getScreenBrightnessValue(Context context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception ignored) {}
        return screenBrightness;
    }

    /**
     * 设置屏幕亮度值 0--255
     * 退出app也能保持该亮度值
     */
    public static void saveScreenBrightness(Context context,int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void setScreenBrightnessMode(Context context,int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 保存当前窗口的屏幕亮度值，并使之生效
     * @param brightness:  屏幕亮度值 0--255
     */
    public static void setScreenBrightness(Activity activity,int brightness) {
        float ratio = brightness / 255.0f;
        setScreenBrightness(activity,ratio);
    }

    /**
     * 保存当前窗口的屏幕亮度值，并使之生效
     * @param  ratio：0-1 之间，1代表最亮，0代表最暗
     */
    public static void setScreenBrightness(Activity activity,float ratio) {
        if(activity == null){
            return;
        }
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.screenBrightness = ratio;
        localWindow.setAttributes(localLayoutParams);
    }

}
