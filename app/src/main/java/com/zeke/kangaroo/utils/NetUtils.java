package com.zeke.kangaroo.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: King.Z
 * date:  2016/3/3 14:48
 * description: 常用网络Tools
 */
public class NetUtils {
    private static final String TAG = "NetUtils";

    public static final String NETWORK_TYPE_LAN = "lan";
    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_3G = "eg";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isConnect(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 指定的网络连接类型是否可用
     * @param networkInfo NetworkInfo
     * @return true/false
     */
    public static boolean isConnect(NetworkInfo networkInfo){
        return networkInfo.isConnected();
    }

    /**
     * byte数组转为Mac
     * @param mac
     * @return
     */
    private static String MacString(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (byte v : mac) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(String.format("%02X", v));
        }
        return sb.toString();
    }

    /**
     * 获取第一个符合name_pattern的网卡的MAC地址 需要API Level 19,
     * <uses-permission android:name="android.permission.INTERNET"/>
     */
    private static String getMacLevel9(String name_pattern_rgx) {
        try {
            Method getHardwareAddress = NetworkInterface.class.getMethod("getHardwareAddress");
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface n = nis.nextElement();
                getHardwareAddress.invoke(n);
                byte[] hw_addr = (byte[]) getHardwareAddress.invoke(n);
                if (hw_addr != null) {
                    String name = n.getName().toLowerCase(Locale.CHINA);
                    String mac = MacString(hw_addr);
                    ZLog.d("NetUtils.getMacLevel9", name + " " + mac);
                    if (name.matches(name_pattern_rgx)) {
                        return mac;
                    }
                }
            }
        } catch (SocketException e) {
            ZLog.d("NetUtils.getMacLevel9", "Exception: SocketException.");
            e.printStackTrace();
        } catch (SecurityException e) {
            ZLog.d("NetUtils.getMacLevel9", "Exception: SecurityException.");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            ZLog.d("NetUtils.getMacLevel9", "Exception: NoSuchMethodException.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            ZLog.d("NetUtils.getMacLevel9", "Exception: IllegalArgumentException.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ZLog.d("NetUtils.getMacLevel9", "Exception: IllegalAccessException.");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            ZLog.d("NetUtils.getMacLevel9", "Exception: InvocationTargetException.");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取第一个符合name_pattern的网卡的MAC地址 需要netcfg工具
     */
    private static String getMacNetcfg(String name_pattern_rgx) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("netcfg");
            BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            Pattern result_pattern = Pattern.compile("^([a-z0-9]+)\\s+(UP|DOWN)\\s+([0-9./]+)\\s+.+\\s+([0-9a-f:]+)$", Pattern.CASE_INSENSITIVE);
            while (is.ready()) {
                String info = is.readLine();
                Matcher m = result_pattern.matcher(info);
                if (m.matches()) {
                    String name = m.group(1).toLowerCase(Locale.CHINA);
                    String status = m.group(2);
                    String addr = m.group(3);
                    String mac = m.group(4).toUpperCase(Locale.CHINA).replace(':', '-');
                    ZLog.d("NetUtils.getMacNetcfg", "match success:" + name + " " + status + " " + addr + " " + mac);
                    if (name.matches(name_pattern_rgx)) {
                        return mac;
                    }
                }
            }
        } catch (IOException e) {
            ZLog.d("NetUtils.getMacNetcfg", "Exception: IOException.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            ZLog.d("NetUtils.getMacNetcfg", "Exception: InterruptedException.");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取设备的有线网络的地址[机顶盒]
     *
     * @return AA-BB-CC-DD-EE-FF 格式 如果不存在，返回""
     */
    public static String getLANMac() {
        String mac = getMacLevel9("eth[0-9]+");
        if (mac.equals("")) {
            mac = getMacNetcfg("eth[0-9]+");
        }
        // MAC地址总是使用大写的。
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.CHINA);
        }
        if (mac.length() != 17) {
            ZLog.e(TAG, "getLANMac mac:" + mac);
        } else {
            ZLog.i(TAG, "getLANMac mac:" + mac);
        }
        return mac;
    }

    public static boolean isTypeNetAvailable(Context context,int netType) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            return false;
        }
        NetworkInfo typeWorkInfo = cm.getNetworkInfo(netType);
        return typeWorkInfo != null && typeWorkInfo.isAvailable();
    }

    /**
     * 获取当前网络状态
     * @param context Context
     * @return 已连接或者正在连接
     */
    public static int getCurrentNetState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            return -1;
        }
        NetworkInfo typeWorkInfo = cm.getActiveNetworkInfo();
        if(typeWorkInfo.isConnected()){
            return 0;
        }else if(typeWorkInfo.isConnectedOrConnecting()){
            return 1;
        }else{
            return -1;
        }
    }

    /**
     * 获取当前连接的网络类型
     * @param context Context
     * @return one of
     * {@link ConnectivityManager#TYPE_MOBILE},
     * {@link ConnectivityManager#TYPE_WIFI},
     * {@link ConnectivityManager#TYPE_WIMAX},
     * {@link ConnectivityManager#TYPE_ETHERNET},
     * {@link ConnectivityManager#TYPE_BLUETOOTH},
     * or other types defined by {@link ConnectivityManager}
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm == null ? null : cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable()) ? networkInfo.getType() : -1;
    }


    /****************************`***** WIFI Info Start*******************************/

    /**
     * Wifi是否连接
     * @param context Context
     * @return true/false
     */
    public static boolean isWifiConnect(Context context) {
        NetworkInfo sysNetworkInfo = getSysNetworkInfo(context);
        return sysNetworkInfo != null && sysNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isAvailable();
    }

    public static String getWifiMac() {
        String mac = getMacLevel9("wlan[0-9]+");
        if (mac.equals("")) {
            mac = getMacNetcfg("wlan[0-9]+");
        }
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.CHINA);
        }
        if (mac.length() != 17) {
            ZLog.e(TAG, "getWLANMac mac:" + mac);
        } else {
            ZLog.i(TAG, "getWLANMac mac:" + mac);
        }
        return mac;
    }

    public static String getWifiIp(Context context) {
        WifiManager wifimanage = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifimanage == null){
            return "0.0.0.0";
        }
        if (wifimanage.isWifiEnabled()) {
            wifimanage.setWifiEnabled(true);
        }
        WifiInfo wifiinfo = wifimanage.getConnectionInfo();
        return formatIpAddress(wifiinfo.getIpAddress());
    }


    public static String getWifiName(Context context) {
        @SuppressLint("WifiManagerPotentialLeak")
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getSSID();
    }
    /********************************* WIFI Info End*******************************/

    private static NetworkInfo getSysNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null ? cm.getActiveNetworkInfo() : null;
    }

    /**
     * 读取设备的默认网关IP地址
     * @return "000.000.000.000" 格式 如果不存在，返回""
     *
     * C:\\Users\\zhi.wang: adb shell ip route
     *   default via 10.0.2.2 dev eth0
     *   10.0.2.0/24 dev eth0  proto kernel  scope link  src 10.0.2.15
     */
    public static String getGatewayIp() {
        String ip = "";
        String info = "";
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("ip route");
            BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            Pattern result_pattern = Pattern.compile("default\\s+via\\s+(\\d+(?:\\.\\d+){3})\\s+dev.+", Pattern.CASE_INSENSITIVE);
            while (is.ready()) {
                info = is.readLine();
                ZLog.i(TAG, "getGatewayIp() ip route:" + info);
                Matcher m = result_pattern.matcher(info);
                if (m.matches()) {
                    ip = m.group(1).toLowerCase(Locale.CHINA);
                    ZLog.d(TAG, "getGatewayIp() m.group(1). ip:" + ip);
                    return ip;
                }
            }
        } catch (IOException e) {
            ZLog.d(TAG, "getGatewayIp() Exception: IOException.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            ZLog.d(TAG, "getGatewayIp() Exception: InterruptedException.");
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 读取网口工作状态
     * @return true: UP false:DOWN
     *
     * C:\\Users\\zhi.wang adb shell netcfg
     *   sit0     DOWN     0.0.0.0/0   0x00000080 00:00:00:00:00:00
     *   eth0     UP     10.0.2.15/24  0x00001043 52:54:00:12:34:56
     *   lo       UP     127.0.0.1/8   0x00000049 00:00:00:00:00:00
     */
    public static boolean getPortIsWork() {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("netcfg");
            BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            Pattern result_pattern = Pattern.compile("^([a-z0-9]+)\\s+(UP|DOWN)\\s+([0-9./]+)\\s+.+\\s+([0-9a-f:]+)$", Pattern.CASE_INSENSITIVE);
            while (is.ready()) {
                String info = is.readLine();
                Matcher m = result_pattern.matcher(info);
                if (m.matches()) {
                    String name = m.group(1).toLowerCase(Locale.CHINA);
                    String status = m.group(2);
                    String addr = m.group(3);
                    String mac = m.group(4).toUpperCase(Locale.CHINA).replace(':', '-');
                    ZLog.i(TAG, "getPortIsWork(), match success:" + name + " " + status + " " + addr + " " + mac);
                    if (name.matches("eth[0-9]+") && status.matches("UP") && (!addr.matches("0.0.0.0"))){
                        return true;
                    }
                    if (name.matches("wlan[0-9]+") && status.matches("UP") && (!addr.matches("0.0.0.0"))) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            ZLog.i(TAG, "getPortIsWork(), Exception: IOException.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            ZLog.i(TAG, "getPortIsWork(), Exception: InterruptedException.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取本地Ip地址
     *
     * @return IP地址
     */
    public static String getLocalIpAddress() {
        String ipaddress = "127.0.0.1";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipaddress = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ZLog.e("WifiPreferenceIpAddress", ex.toString());
        }
        return ipaddress;
    }

    private static String formatIpAddress(int ipAddress) {
        return String.format(Locale.ENGLISH,
                "%d.%d.%d.%d",
                (ipAddress & 0xFF),
                ((ipAddress >> 8) & 0xff),
                ((ipAddress >> 16) & 0xff),
                ((ipAddress >> 24) & 0xff));
    }

	public static void openNetSettingActivity(AppCompatActivity activity){
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

	 /*
      * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
      * @ip 目标ip地址
      * @return
      *
      * Usage: ping [-aAbBdDfhLnOqrRUvV] [-c count] [-i interval] [-I interface]
      *     [-m mark] [-M pmtudisc_option] [-l preload] [-p pattern] [-Q tos]
      *     [-s packetsize] [-S sndbuf] [-t ttl] [-T timestamp_option]
      *     [-w deadline] [-W timeout] [hop1 ...] destination
      */
     public static boolean isPingIpSucess(String ip) {
       String result = null;
       try {
           Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
           // 读取ping的内容
           InputStream input = p.getInputStream();
           BufferedReader in = new BufferedReader(new InputStreamReader(input));
           StringBuilder stringBuffer = new StringBuilder();
           String content = "";
           while ((content = in.readLine()) != null) {
               stringBuffer.append(content);
           }
           ZLog.d("------ping-----", "result content : " + stringBuffer.toString());
           // ping的状态
           int status = p.waitFor();
           if (status == 0) {
               result = "success";
               return true;
           } else {
               result = "failed";
           }
       } catch (IOException e) {
           result = "IOException";
       } catch (InterruptedException e) {
           result = "InterruptedException";
       } finally {
           ZLog.d("----result---", "result = " + result);
       }
       return false;
     }
}
