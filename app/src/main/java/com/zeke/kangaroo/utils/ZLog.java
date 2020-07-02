package com.zeke.kangaroo.utils;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.MissingFormatArgumentException;

//Logcat统一管理类
@SuppressWarnings("WeakerAccess")
public class ZLog {
    private static final String TAG = "Zlog";

    private ZLog() {
        throw new Error("Do not need instantiate!");
    }

    // 是否允许输出日志
    public static boolean isDebug = true;
    private static LogType _logLevel = LogType.DEBUG;
    private static final String LINE_SEPARATOR;
    private static final int JSON_INDENT = 4;
    private static String realTag = TAG;

    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }

    public enum LogType {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, JSON
    }

    public static void i(String msg) {
        printLog(LogType.INFO, TAG, msg);
    }

    public static void i(String tag, String msg) {
        printLog(LogType.INFO, tag, msg);
    }

    public static void d(String msg) {
        printLog(LogType.DEBUG, TAG, msg);
    }

    public static void d(String tag, String msg) {
        printLog(LogType.DEBUG, tag, msg);
    }

    public static void e(String msg) {
        printLog(LogType.ERROR, TAG, msg);
    }

    public static void e(String tag, String msg) {
        printLog(LogType.ERROR, tag, msg);
    }


    public static void v(String msg) {
        printLog(LogType.VERBOSE, TAG, msg);
    }

    public static void v(String tag, String msg) {
        printLog(LogType.VERBOSE, tag, msg);
    }

    public static void w(String msg) {
        printLog(LogType.WARN, TAG, msg);
    }

    public static void w(String tag, String msg) {
        printLog(LogType.WARN, tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (isDebug) Log.i(tag, msg, tr);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isDebug) Log.d(tag, msg, tr);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isDebug) Log.e(tag, msg, tr);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (isDebug) Log.v(tag, msg, tr);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (isDebug) Log.w(tag, msg, tr);
    }

    public static void json(String jsonMsg) {
        printLog(LogType.JSON, TAG, jsonMsg);
    }

    public static void json(String tag, String jsonMsg) {
        printLog(LogType.JSON, tag, jsonMsg);
    }

    /**
     * 根据当前的参数log等级判断是否需要打印log
     *
     * @param type LogType
     * @return true/false
     */
    public static boolean needPrintLog(LogType type) {
        return isDebug && (type.ordinal() >= _logLevel.ordinal());
    }

    /**
     * 打印字符串
     */
    private static void printLog(LogType type, String tag, String msg, Object... args) {
        if (!needPrintLog(type)) {
            return;
        }

        if (args.length > 0) {
            msg = getFormatMsg(msg, args);
        }
        String logStr = createStackTraceInfo(tag, msg);

        switch (type) {
            case VERBOSE:
                Log.v(realTag, logStr);
                break;
            case DEBUG:
                Log.d(realTag, logStr);
                break;
            case INFO:
                Log.i(realTag, logStr);
                break;
            case WARN:
                Log.w(realTag, logStr);
                break;
            case ERROR:
                Log.e(realTag, logStr);
                break;
            case ASSERT:
                Log.wtf(realTag, logStr);
                break;
            case JSON:
                printJsonString(realTag, msg, logStr);
                break;
            default:
                break;
        }
    }

    private static void printJsonString(String tag, String msg, String logStr) {
        String jsonFormateStr = "";
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                jsonFormateStr = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                jsonFormateStr = jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException exception) {
            e(tag, exception.getCause().getMessage() + "\n" + msg);
            return;
        }

        // 打印原有格式日志 + 格式化后的日志
        String[] lines = jsonFormateStr.split(LINE_SEPARATOR);
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append(logStr).append(LINE_SEPARATOR);
        printLine(jsonContent, true);
        for (String lineMsg : lines) {
            jsonContent.append("║ ").append(lineMsg).append(LINE_SEPARATOR);
        }
        printLine(jsonContent, false);
        Log.d(tag, jsonContent.toString());
    }

    /**
     * 抽取当前堆栈信息
     *
     * @param tag TAG
     * @param msg 源日志内容
     * @return 返回带堆栈信息的日志信息字符串
     */
    private static String createStackTraceInfo(String tag, String msg) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
         /*
         * 外部调用方法堆栈的index
         * 5--- 外部调用点的堆栈信息
         * 4--- com.zeke.kangaroo.utils.ZLog.d
         * 3--- com.zeke.kangaroo.utils.ZLog.printLog
         * 2--- com.zeke.kangaroo.utils.ZLog.createStackTraceInfo
         * 1--- java.lang.Thread.getStackTrace
         * 0--- dalvik.system.VMStack.getThreadStackTrace(Native Method)
         */
        int index = 5;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        realTag = TAG.equals(tag) ? fileName : tag;
        // 大写第一个字母
        if(methodName.length() > 1){
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        }
        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(fileName)
                .append(":").append(lineNumber)
                .append(")#").append(methodName)
                .append(" ] ");
        stringBuilder.append(msg);
        return stringBuilder.toString();
    }

    private static String getFormatMsg(String msg, Object[] args) {
        String result = "";

        if (msg == null) {
            msg = "<null>";
        } else {
            try {
                result = String.format(msg, args);
            } catch (MissingFormatArgumentException ignored) {
            }
        }

        // 简单判断是否格式化正确
        if (TextUtils.isEmpty(result.trim()) || !result
                .contains(objectToString(args[args.length - 1]))) {
            StringBuilder builder = new StringBuilder(msg);
            for (Object arg : args) {
                builder.append(" ").append(objectToString(arg));
            }
            result = builder.toString();
        }

        return result;
    }

    // 基本数据类型
    private final static String[] types = {"int", "java.lang.String", "boolean", "char",
            "float", "double", "long", "short", "byte"};

    public static <T> String objectToString(T object) {
        if (object == null) {
            return "<null>";
        }
        if (object.toString().startsWith(object.getClass().getName() + "@")) {
            StringBuilder builder = new StringBuilder(object.getClass().getSimpleName() + "{");
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                boolean flag = false;
                for (String type : types) {
                    if (field.getType().getName().equalsIgnoreCase(type)) {
                        flag = true;
                        Object value = null;
                        try {
                            value = field.get(object);
                        } catch (IllegalAccessException e) {
                            value = e;
                        } finally {
                            builder.append(String.format("%s=%s, ", field.getName(),
                                    value == null ? "null"
                                            : value.toString()));
                            break;
                        }
                    }
                }
                if (!flag) {
                    builder.append(String.format("%s=%s, ", field.getName(), "Object"));
                }
            }
            return builder.replace(builder.length() - 2, builder.length() - 1, "}").toString();
        } else {
            return object.toString();
        }
    }

    private static void printLine(StringBuilder builder, boolean isTop) {
        if (isTop) {
            builder.append("╔═══════════════════════════════════════════════════════════════════════════════════════");
            builder.append(LINE_SEPARATOR);
        } else {
            builder.append("╚═══════════════════════════════════════════════════════════════════════════════════════");
        }

    }
}