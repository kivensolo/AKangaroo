package com.zeke.kangaroo.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {
    public static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private TimeUtils() {
        throw new AssertionError();
    }


    public static class ticketMarker {
        static final int _MarkersLimitation = 64;
        long[] _t = new long[_MarkersLimitation];
        String[] _n = new String[_MarkersLimitation];
        String _infoHdr;
        int _pos = 0;
        int _precision = 1000;

        public ticketMarker() {
            _infoHdr = "(ms) ";
        }

        public ticketMarker(String infoHdr, boolean ms) {
            if (infoHdr == null) {
                infoHdr = "";
            }
            if (ms) {
                _infoHdr = infoHdr + "(ms) ";
                _precision = 1000;
            } else {
                _infoHdr = infoHdr + "(us) ";
                _precision = 1;
            }
        }

        public void reset() {
            _pos = 0;
        }

        public void mark() {
            mark(null);
        }

        public void mark(String name) {
            if (name == null) {
                name = String.valueOf(_pos);
            }
            _n[_pos] = name;
            _t[_pos] = timestamp_us();
            ++_pos;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(_infoHdr);
            sb.append((_t[_pos - 1] - _t[0]) / _precision);
            sb.append(" - ");
            for (int i = 1; i < _pos; i++) {
                if (i > 1) {
                    sb.append(", ");
                }
                sb.append(_n[i]);
                sb.append(":");
                sb.append((_t[i] - _t[i - 1]) / _precision);
            }
            return sb.toString();
        }
    }

    public static long timestamp() {
        return System.nanoTime() / 1000000;
    }

    public static long timestamp_us() {
        return System.nanoTime() / 1000;
    }

    public static String seconds2time(long time) {
        int totalSeconds = (int) (time / 1000L);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        if (minutes <= 0 && hours <= 0) {
            return String.format("%02d", new Object[]{Integer.valueOf(seconds)});
        } else if (minutes > 0 && hours <= 0) {
            return String.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
        } else {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)});
        }
    }

    public static long getTime(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        Date date = null;
        try {
            date = sdf.parse(timeStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000L);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        if (minutes <= 0 && hours <= 0) {
            return String.format("00:00:%02d", seconds);
        } else if (minutes > 0 && hours <= 0) {
            return String.format("00:%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public static String formatSeekingPreviewTime(long time) {
        int totalSeconds = (int) (Math.abs(time) / 1000L);
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        String prefix = time >= 0 ? "+" : "-";
        if (minutes <= 0 && hours <= 0) {
            return String.format("%s 00:%02d", prefix, seconds);
        } else if (minutes > 0 && hours <= 0) {
            return String.format("%s %02d:%02d", prefix, minutes, seconds);
        } else {
            return String.format("%s %02d:%02d:%02d", prefix, hours, minutes, seconds);
        }
    }


    /**
     * 将后台返回的时间字符串转换为 HH:mm的格式
     *
     * @param date_str 时间字符串
     * @return HH:mm格式
     */
    public static String convertHourOfDay(String date_str) {
        Date date;
        try {
            date = new SimpleDateFormat("HHmmSS").parse(date_str);
        } catch (Exception e) {
            return "";
        }
        return new SimpleDateFormat("HH:mm").format(date);
    }

    /**
     * 获取 yyyyMMddHHmmSS 格式的时间
     *
     * @param dateStr yyyyMMddHHmmSS
     * @return 时间
     */
    public static long getTimeByDateStr(String dateStr) {
        long time = 0;
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmSS").parse(dateStr);
            return date.getTime();
        } catch (Exception e) {

        }
        return time;
    }

    /**
     * 传入yyyy-MM-dd HH:mm:SS返回yyyy/MM/dd
     *
     * @param dateStr yyyy-MM-dd HH:mm:SS
     * @return yyyy/MM/dd
     */
    public static String getYmdByDateStr(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        try {
            return dateStr.substring(0, 4) + "/" + dateStr.substring(5, 7) + "/" + dateStr.substring(8, 10);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String TimeZoneConverter(String old_date, String format, String from_zone, String to_zone) {
        TimeZone from = TimeZone.getTimeZone(from_zone);
        TimeZone to = TimeZone.getTimeZone(to_zone);
        SimpleDateFormat inputFormat = new SimpleDateFormat(format);
        inputFormat.setTimeZone(from);
        Date date = null;
        try {
            date = inputFormat.parse(old_date);
        } catch (ParseException e) {
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat(format);
        outputFormat.setTimeZone(to);
        return outputFormat.format(date);
    }

    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();

        String value = tz.getRawOffset() / 1000 / 60 / 60 + "";

        return value;
    }


    public static String convertWeekOfDay(String dayStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = dateFormat.parse(dayStr);
        } catch (Exception e) {
            return "";
        }
        if (dateFormat.format(new Date()).equals(dayStr)) {
            return "今天";
        } else {
            return new SimpleDateFormat("MM.dd").format(date);
        }
    }

    //毫秒转化天
    public static long ms2day(long ms) {
        long day = 0;
        if (ms > 0) {
            day = ms / 1000 / 60 / 60 / 24;
        }
        return day;
    }

    //毫秒转化天
    public static long ms2mm(long ms) {
        long day = 0;
        if (ms > 0) {
            day = ms / 1000 / 60;
        }
        return day;
    }

    public static void clearCalendarHMS(Calendar tmpC) {
        tmpC.set(Calendar.HOUR_OF_DAY, 0);
        tmpC.set(Calendar.MINUTE, 0);
        tmpC.set(Calendar.SECOND, 0);
        tmpC.set(Calendar.MILLISECOND, 0);
    }

    public static String timeDiffFormatByDay(Date chs) {
        if (TimeUtils.timeDiffByDay(chs, new Date()) == 0) {
            return "今天";
        } else if (TimeUtils.timeDiffByDay(chs, new Date()) == -1) {
            return "明天";
        } else if (TimeUtils.timeDiffByDay(chs, new Date()) == 1) {
            return "昨天";
        } else {
            return new SimpleDateFormat("MM.dd", Locale.getDefault()).format(chs);
        }
    }

    public static int timeDiffByDay(Date lhs, Date rhs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lhs);
        clearCalendarHMS(calendar);

        long diff = rhs.getTime() - calendar.getTimeInMillis();
        return (int) (diff / DAY_IN_MILLIS);
    }

    public static boolean isSameDate(Date lhs, Date rhs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lhs);
        clearCalendarHMS(calendar);

        long diff = rhs.getTime() - calendar.getTimeInMillis();
        return diff >= 0 && diff < DAY_IN_MILLIS;
    }
}
