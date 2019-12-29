package com.zeke.kangaroo.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * date 16/4/21
 * description 日期工具类
 */
@SuppressWarnings("unused")
@SuppressLint("SimpleDateFormat")
public class DateUtils {

    public static Date strToDate(String strDate) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(strDate);
    }

    public static Date objToDate(Object objDate) throws Exception {
        String dateString = objDate.toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(dateString);
    }

    public static Date objToFullDate(Object objDate) throws Exception {
        String dateString = objDate.toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(dateString);
    }

    public static Date strToDate(String strDate, String strFormat) {
        SimpleDateFormat formatSrc = new SimpleDateFormat(strFormat);
        Date date = null;
        try {
            date = formatSrc.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date formatDateToDate(Date iDate, String strFormat) throws ParseException {
        String dDate = DateUtils.format(iDate);
        return DateUtils.strToDate(dDate, strFormat);
    }

    public static Date formatToDefaultDate(Date iDate) throws ParseException {
        String dDate = DateUtils.format(iDate);
        return DateUtils.strToDate(dDate, "yyyy-MM-dd");
    }

    public static String format(Date iDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(iDate);
    }

    public static String format(Date iDate, String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        return format.format(iDate);
    }

    /**
     * 将CST的时间字符串转换成需要的日期格式字符串<br>
     *
     * @param cststr The source to be dealed with. <br>
     *               (exp:Fri Jan 02 00:00:00 CST 2009)
     * @param fmt    The format string
     * @return string or <code>null</code> if the cststr is unpasrseable or is
     * null return null,else return the string.
     * @author HitSnail
     */
    public static String getDateFmtStrFromCST(String cststr, String fmt) {
        if ((null == cststr) || (null == fmt)) {
            return null;
        }
        String str = null;
        SimpleDateFormat sdfy = new SimpleDateFormat(fmt.trim());
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss 'CST' yyyy", Locale.US);
        try {
            str = sdfy.format(sdf.parse(cststr.trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return str;
    }


    /**
     * 获得当年的第一天。
     *
     * @param yyyy 年份
     * @return Date数组。第一位是当年的第一天和第二位是当年的最后一天。
     */
    public static Date[] getFirstAndLastDays(String yyyy) {
        Date date = getDateFromPattern("yyyy-MM-dd", yyyy + "-01-01");
        String dateStr = formatDate(date);
        String year = dateStr.substring(0, 4);

        // 当年第一天的字符串形式。
        String firstDayStr = dateStr.replaceFirst(year + "-\\d{2}-\\d{2}", year + "-01-01");

        // 当年最后一天的字符串形式。
        String lastDayStr = dateStr.replaceFirst(year + "-\\d{2}-\\d{2}", year + "-12-31");

        Date firstDay = formatString(firstDayStr);
        Date lastDay = formatString(lastDayStr);
        return new Date[]{firstDay, lastDay};
    }

    /**
     * 通过格式化字符串得到时间
     *
     * @param parrern 格式化字符串 例如：yyyy-MM-dd
     * @param str     时间字符串 例如：2007-08-01
     * @return 出错返回null
     */
    public static Date getDateFromPattern(String parrern, String str) {
        if (str == null || ("").equals(str))
            // if (StringUtils.isEmpty(str))
            return null;
        SimpleDateFormat fmt = new SimpleDateFormat(parrern);
        try {
            return fmt.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串转化为JAVA时间类型。
     *
     * @param dateStr。字符串。
     * @return Date date。JAVA时间类型。
     */
    public static Date formatString(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将字符串转化为JAVA时间类型(精确到秒)。
     *
     * @param dateStr。字符串。
     * @return Date date。JAVA时间类型。
     */
    public static Date formatFullString(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将Date时间转为字符串。
     *
     * @param date。需要格式化的时间。
     * @return String。传入时间的格式化字符串。
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String formatDates(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 得到传入日期n天后的日期,如果传入日期为null，则表示当前日期n天后的日期
     *
     * @param dt   指定日期
     * @param days 可以为任何整数，负数表示前days天，正数表示后days天
     * @return Date
     */
    public static Date getAddDayDate(Date dt, int days) {
        if (dt == null)
            dt = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * @param dt   日期Date对象
     * @param days 可以为任何整数，负数表示前days天，正数表示后days天
     * @return Date
     * @author chensj
     * 得到传入日期n天后的日期,如果传入日期为null，则表示当前日期n天后的日期
     */
    public static Date getAddDayTime(Date dt, int days) {
        if (dt == null)
            dt = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + days);
        return cal.getTime();
    }

    /**
     * @param d1 date-one
     * @param d2 date-two
     * @return second
     * 根据传入的两个时间求时间间隔
     */
    public static int getDayBetween(Date d1, Date d2) {
        // return (int)(d1.getTime()-d2.getTime())/(1000*60*60*24);
        Date[] d = new Date[2];
        d[0] = d1;
        d[1] = d2;
        Calendar[] cal = new Calendar[2];
        for (int i = 0; i < cal.length; i++) {
            cal[i] = Calendar.getInstance();
            cal[i].setTime(d[i]);
            cal[i].set(Calendar.HOUR_OF_DAY, 0);
            cal[i].set(Calendar.MINUTE, 0);
            cal[i].set(Calendar.SECOND, 0);
        }
        long m = cal[0].getTime().getTime();
        long n = cal[1].getTime().getTime();
        return (int) ((long) (m - n) / 1000);
    }

    /**
     * 根据传入的两个时间求时间间隔(秒)
     *
     * @param d1 日期Date对象
     * @param d2 日期Date对象
     * @return second
     */
    public static int getSecondsBetween(Date d1, Date d2) {
        // return (int)(d1.getTime()-d2.getTime())/(1000*60*60*24);
        Date[] d = new Date[2];
        d[0] = d1;
        d[1] = d2;
        Calendar[] cal = new Calendar[2];
        for (int i = 0; i < cal.length; i++) {
            cal[i] = Calendar.getInstance();
            cal[i].setTime(d[i]);
        }
        long m = cal[0].getTime().getTime();
        long n = cal[1].getTime().getTime();
        return (int) ((m - n) / (1000 * 60 * 60 * 24));
    }

    /**
     * 根据传入的两个时间求时间间隔(分)
     *
     * @param d1 日期Date对象
     * @param d2 日期Date对象
     * @return second
     */
    public static int[] getDayMinuteBetween(Date d1, Date d2) {
        Date[] d = new Date[2];
        d[0] = d1;
        d[1] = d2;
        Calendar[] cal = new Calendar[2];
        for (int i = 0; i < cal.length; i++) {
            cal[i] = Calendar.getInstance();
            cal[i].setTime(d[i]);
        }
        long m = cal[0].getTime().getTime();
        long n = cal[1].getTime().getTime();
        int between[] = new int[4];
        between[0] = (int) ((m - n) / (1000 * 24 * 60 * 60));
        between[1] = (int) ((m - n) % (1000 * 24 * 60 * 60)) / (1000 * 60 * 60);
        between[2] = (int) ((m - n) % (1000 * 60 * 60)) / (1000 * 60);
        between[3] = (int) ((m - n) % (1000 * 60)) / (1000);
        return between;
    }

    /**
     * 根据传入的两个时间求时间间隔
     *
     * @param d1 日期Date
     * @param d2 日期Date
     * @return 返回时间间隔，如*秒钟，*分钟，*小时，*天
     * @author ChenKuan
     */
    public static String getTimeBetween(Date d1, Date d2) {
        Date[] d = new Date[2];
        d[0] = d1;
        d[1] = d2;
        Calendar[] cal = new Calendar[2];
        for (int i = 0; i < cal.length; i++) {
            cal[i] = Calendar.getInstance();
            cal[i].setTime(d[i]);
        }
        long m = cal[0].getTime().getTime();
        long n = cal[1].getTime().getTime();
        // 取间隔天数
        int daytime = (int) ((m - n) / (1000 * 60 * 60 * 24));
        if (Math.abs(daytime) > 0) {
            return Math.abs(daytime) + "天";
        }
        // 取间隔小时数
        int hourtime = (int) ((m - n) / (1000 * 60 * 60));
        if (Math.abs(hourtime) > 0) {
            return Math.abs(hourtime) + "小时";
        }
        // 取间隔分钟数
        int secondtime = (int) ((m - n) / (1000 * 60));
        if (Math.abs(secondtime) > 0) {
            return Math.abs(secondtime) + "分钟";
        }
        // 取间隔秒钟数
        int minuteime = (int) ((m - n) / (1000));
        if (Math.abs(minuteime) >= 0) {
            return Math.abs(minuteime) + "秒钟";
        }
        return null;
    }

    /*
     * 获取传入时间的当前月的第一天
     */
    @SuppressWarnings("deprecation")
    public static Date getFristDayOfMonth(Date sDate1) {
        Calendar cDay1 = Calendar.getInstance();
        cDay1.setTime(sDate1);
        final int fristDay = cDay1.getActualMinimum(Calendar.DAY_OF_MONTH);
        Date fristDate = cDay1.getTime();
        fristDate.setDate(fristDay);
        return fristDate;
    }

    /*
     * 获得传入时间的当月最后一天
     */
    @SuppressWarnings("deprecation")
    public static Date getLastDayOfMonth(Date sDate1) {
        Calendar cDay1 = Calendar.getInstance();
        cDay1.setTime(sDate1);
        final int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date lastDate = cDay1.getTime();
        lastDate.setDate(lastDay);
        return lastDate;
    }

    /*
     * 返回系统当前时间的前几个月的日期
     */
    public static Date getBeforDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        if (month > 5) {
            String aString = year + "-" + (month - 5) + "-" + day;
            Date date = formatString(aString);
            return date;
        }
        String bString = (year - 1) + "-" + (month + 12 - 5) + "-" + day;
        Date dates = formatString(bString);
        return dates;
    }

    // 增加或减少的天数
    public static Date addDay(int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(new Date());
        startDT.add(Calendar.DATE, num);
        return startDT.getTime();
    }

    // 增加或减少天数 某个日期
    public static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DATE, num);
        return startDT.getTime();
    }

    // 增加或减少月数 当天
    public static Date addMonth(int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(new Date());
        startDT.add(Calendar.MONTH, num);
        return startDT.getTime();
    }

    // 增加或减少年数 当天
    public static Date addYear(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.YEAR, num);
        return startDT.getTime();
    }

    // 返回java.sql.date
    public static java.sql.Date getFullSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    // 增加或减少月数 某个日期
    public static Date addMonth(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.MONTH, num);
        return startDT.getTime();
    }

    // 天数差
    public static int getQuot() {
        Calendar cc = Calendar.getInstance();
        cc.setTime(new Date());
        int currmum = cc.get(Calendar.DAY_OF_MONTH); // 当月的第几天
        int maxmum = cc.getActualMaximum(Calendar.DAY_OF_MONTH); // 当月最大天数
        return (maxmum - currmum);
    }

    // 百分比 (辅助算靓号的价格)
    public static float getPercent() {
        Calendar cc = Calendar.getInstance();
        cc.setTime(new Date());
        float currmum = cc.get(Calendar.DAY_OF_MONTH);
        float maxmum = cc.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(currmum / maxmum);
        return currmum / maxmum;
    }

    //
    public static Date getLastDay() {
        int quot = getQuot(); // 天数差
        return addDay(quot);
    }

    public static Date changeDateToUtil(java.sql.Date dt) {
        return new Date(dt.getTime());
    }

    public static java.sql.Date changeDateToSql(Date dt) {
        return new java.sql.Date(dt.getTime());
    }

    /**
     * @param date 日期
     * @return 月份的第一天
     * 获得本月的最后一天
     */
    public static Date getLastDateByMonth(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.MONTH, now.get(Calendar.MONTH) + 1);
        now.set(Calendar.DATE, 1);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
        now.set(Calendar.HOUR, 11);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        return now.getTime();
    }

    /**
     * @param date 月份所在的时间
     * @return 月份的最后一天
     * 获得所在月份的第一天
     */
    public static Date getFirstDateByMonth(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, 0);
        now.set(Calendar.HOUR, 12);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }

    /**
     * @param date   月份所在的时间
     * @param months 之后几个月
     * @return 当前月之后某月多少天
     * 获得当前月之后某月有多少天
     */
    public static int getDayByMonth(Date date, int months) {
        int tempMonth = date.getMonth() + 1 + months;
        int years = tempMonth / 12;
        int month = tempMonth % 12;
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, date.getYear() + years);
        time.set(Calendar.MONTH, month - 1);// Calendar对象默认一月为0
        int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
        return day;
    }

    public static Date getDateByAddDays(Date date, int days) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + days);
        return now.getTime();
    }


    /**
     * @param date   日期
     * @param Months 月数 1为本月
     * @return 获得指定日期所在的月之后某月的最后一天
     * @author zhangwt
     * 获得指定日期所在的月之后某月的最后一天
     */
    @SuppressWarnings("deprecation")
    public static Date getDateByMonth(Date date, int Months) {
        int tempMonth = date.getMonth() + 1 + Months;
        int years = tempMonth / 12;
        int month = tempMonth % 12;
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, date.getYear() + 1900 + years);
        time.set(Calendar.MONTH, month - 1);// Calendar对象默认一月为0
        time.set(Calendar.DATE, time.get(Calendar.DATE) - 1);
        time.set(Calendar.HOUR, 11);
        time.set(Calendar.MINUTE, 59);
        time.set(Calendar.SECOND, 59);
        return time.getTime();
    }


    public static boolean isLateTime(Date nowTime, String otime) {
        SimpleDateFormat sform = new SimpleDateFormat("yyyyMMddHHmmss");
        String snowTime = sform.format(nowTime);
        Long time1 = Long.valueOf(snowTime);
        Long time2 = Long.valueOf(otime);
        if (time1 < time2)
            return true;
        return false;
    }

    /**
     * 获得某月的剩余天数
     *
     * @param date   日期Date
     * @param Months 指定月份
     * @return 间隔天数
     */
    public static int getLastDayByMonth(Date date, int Months) {
        return getSecondsBetween(getDateByMonth(new Date(), Months), date) / 86400;
    }

    public static Date getFirstDayByMonth(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }

    // 获得周统计的统计时间
    public static Date getStatDateByWeek(Long year, Long month, Long week) throws Exception {
        Date date = strToDate(year + "-" + month, "yyyy-MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + (7 + (Calendar.MONDAY) - cal.get(Calendar.DAY_OF_WEEK)) % 7
                + ((Long) (7 * week)).intValue());
        return cal.getTime();
    }

    // 获得月统计的统计时间
    public static Date getStatDateByMonth(Long year, Long month) throws ParseException {
        Date date = strToDate(year + "-" + month, "yyyy-MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        return getFirstDayByMonth(cal.getTime());
    }

    // 获得季统计的统计时间
    public static Date getStatDateByQuarter(Long year, Long quarter) throws ParseException {
        Date date = strToDate(year + "-" + (quarter * 3), "yyyy-MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        return getFirstDayByMonth(cal.getTime());
    }

    // 获得半年统计的统计时间
    public static Date getStatDateBySemi(Long year, Long semi) throws ParseException {
        Date date = strToDate(year + "-" + (semi * 6), "yyyy-MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        return getFirstDayByMonth(cal.getTime());
    }

    // 获得年统计的统计时间
    public static Date getStatDateByYear(Long year) throws ParseException {
        Date date = strToDate((year + 1) + "-01-01", "yyyy-MM-dd");
        return getFirstDayByMonth(date);
    }

    // 根据推荐日期获得结算日期
    public static Date getRecommendFootDate(Date date) throws ParseException {
        Date dd = addMonth(date, 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        cal.set(Calendar.DATE, 10);
        if (cal.getTime().before(dd)) {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
        return cal.getTime();
    }

    /**
     * 获得本周的最后一天
     *
     * @param date 日期Date对象
     * @return 本周的最后一天日期Date
     */
    @SuppressWarnings("static-access")
    public static Date getLastDateOfWeek(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(now.DAY_OF_WEEK, 7);
        return now.getTime();
    }

    /**
     * 获取指定日期month月之后的所在日期;
     * 如3月5号 1月之后所在日期4月3号
     *
     * @param dt    指定日期
     * @param month 月份数
     * @return Date
     */
    public static Date getDateByDateAndMonth(Date dt, int month) {
        int day = 0;
        if (null == dt) return null;
        for (int i = 0; i < month; i++) {
            day += DateUtils.getDayByMonth(dt, i);
        }
        return DateUtils.getAddDayDate(dt, day - 1);
    }

    /**
     * @param dt    指定日期
     * @param month 月份数
     * @return 获取指定日期month月之前所在日期
     * 本月：9月28号，前一个月为8月29号
     */
    public static Date getDateBeforeNMonth(Date dt, int month) {
        int day = 0;
        if (null == dt) return null;
        int size = Math.abs(month);
        for (int i = 0; i < size; i++) {
            day -= DateUtils.getDayByMonth(dt, -i);
        }
        return DateUtils.getAddDayDate(dt, day);
    }

    /**
     * @param stype 返回值类型   0为多少天，1为多少个月，2为多少年
     * @param date1 开始日期
     * @param date2 结束日期
     * @return 比较的结果
     */
    public static int compareDate(String date1, String date2, int stype) {
        int n = 0;

        //String[] u = {"天","月","年"};
        String formatStyle = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(formatStyle);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(date1));
            c2.setTime(df.parse(date2));
        } catch (Exception e3) {
            System.out.println("wrong occured");
        }
        while (!c1.after(c2)) {
            n++;
            if (stype == 1) {
                c1.add(Calendar.MONTH, 1);// 比较月份，月份+1
            } else {
                c1.add(Calendar.DATE, 1); // 比较天数，日期+1
            }
        }

        n = n - 1;

        if (stype == 2) {
            int yushu = (int) n % 365;
            n = yushu == 0 ? (n / 365) : ((n / 365) - 1);
        }

        //   System.out.println(date1+" -- "+date2+" 相差多少"+u[stype]+":"+n);
        return n;
    }

    /**
     * 获取日期是星期几<br>
     *
     * @param dt 目标日期
     * @return 当前日期是星期几
     * @author onping
     * 想返回数字:1为周一2为周二，去掉数组weekDays,直接返回w
     * 想返回汉字周几见下
     */
    public static int getWeekOfDate(Date dt) {
        //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0 || w == 0) {
            w = 7;
        }
        return w;
        //return weekDays[w];
    }

    /**
     * 两个日期的差距(天数)
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 天数间隔
     */
    public static long getDistDates(Date startDate, Date endDate) {
        long totalDate = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        long timestart = calendar.getTimeInMillis();
        calendar.setTime(endDate);
        long timeend = calendar.getTimeInMillis();
        totalDate = Math.abs((timeend - timestart)) / (1000 * 60 * 60 * 24);
        return totalDate;
    }

    /**
     * 两个日期的差距(毫秒)
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return 相差的毫秒值
     */
    public static long getDistDatesInMillis(Date startDate, Date endDate) {
        long totalDate = 0;
        long timestart = 0;
        long timeend = 0;
        Calendar calendar = Calendar.getInstance();
        if (null != startDate) {
            calendar.setTime(startDate);
            timestart = calendar.getTimeInMillis();
        }
        if (null != endDate) {
            calendar.setTime(endDate);
            timeend = calendar.getTimeInMillis();
        }
        totalDate = Math.abs((timeend - timestart));
        return totalDate;
    }

    /**
     * @param dateU   为当前时间
     * @param minTime 为想减去的时间
     * @param flag    往前/后计算的标志
     * @return Date
     * @throws Exception dateU 往前推X小时X分钟 或者往后推
     */
    public static Date getMinDate(Date dateU, String minTime, Long flag) throws Exception {
        Date wantDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //转换为02:30 2小时30分钟
        wantDate = sdf.parse(minTime);
        String strDate = sdf.format(wantDate);
        int ss = 0;//转换后的毫秒数
        if (null != strDate) {
            int Hour = Integer.parseInt(strDate.split(":")[0]);
            int minute = Integer.parseInt(strDate.split(":")[1]);
            int second = Integer.parseInt(strDate.split(":")[2]);
            ss = Hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        }
        Long chaSec = 0L;
        if (flag == 1) {//往前推
            chaSec = dateU.getTime() - ss;
        } else if (flag == 2) {
            chaSec = dateU.getTime() + ss;
        }
        Date d = new Date(chaSec);
        return d;

    }

    /**
     * 当天日期加或减num年
     *
     * @param num 年数
     * @return Date
     * @throws Exception 转换异常
     */
    public static Date getYear(int num) throws Exception {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, num);
        return c.getTime();
    }
}