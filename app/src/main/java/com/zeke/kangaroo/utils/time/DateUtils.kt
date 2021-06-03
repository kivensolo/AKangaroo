package com.zeke.kangaroo.utils.time

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * date 16/4/21
 * description 日期工具类
 */

val allFormat: SimpleDateFormat
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

val ymdFormat: SimpleDateFormat
    get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


/**
 * Date扩展属性，时间戳转为日期字符串,eg: 2021-05-31 22:12:25
 */
val Date.formatAll: String
    get() = allFormat.format(time)

val Date.formatYMD: String
    get() = ymdFormat.format(time)


/**
 * N天前
 */
val Int.daysAgo: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.DAY_OF_YEAR, -this@daysAgo)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N天后
 */
val Int.daysLater: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.DAY_OF_YEAR, +this@daysLater)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N周前
 */
val Int.weekAgo: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.WEEK_OF_YEAR, -this@weekAgo)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N周后
 */
val Int.weekLater: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.WEEK_OF_YEAR, +this@weekLater)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N月前
 */
val Int.monthAgo: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.MONTH, -this@monthAgo)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N月后
 */
val Int.monthLater: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.MONTH, +this@monthLater)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N年前
 */
val Int.yearAgo: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.YEAR, -this@yearAgo)
        ymdFormat.format(this.timeInMillis)
    }

/**
 * N年后
 */
val Int.yearLater: String
    get() = Calendar.getInstance().run {
        this.add(Calendar.YEAR, +this@yearLater)
        ymdFormat.format(this.timeInMillis)
    }

object DateUtils {
    @Throws(ParseException::class)
    fun strToDate(strDate: String): Date {
        return ymdFormat.parse(strDate)
    }

    @Throws(Exception::class)
    fun objToDate(objDate: Any): Date {
        val dateString = objDate.toString()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return format.parse(dateString)
    }

    @Throws(Exception::class)
    fun objToFullDate(objDate: Any): Date {
        val dateString = objDate.toString()
        return ymdFormat.parse(dateString)
    }

    fun strToDate(strDate: String?, strFormat: String?): Date? {
        val formatSrc = SimpleDateFormat(strFormat, Locale.ENGLISH)
        var date: Date? = null
        try {
            date = formatSrc.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    @Throws(ParseException::class)
    fun formatDateToDate(iDate: Date?, strFormat: String?): Date? {
        val dDate = format(iDate)
        return strToDate(dDate, strFormat)
    }

    @Throws(ParseException::class)
    fun formatToDefaultDate(iDate: Date?): Date? {
        val dDate = format(iDate)
        return strToDate(dDate, "yyyy-MM-dd")
    }

    fun format(iDate: Date?): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return format.format(iDate)
    }

    fun format(iDate: Date?, strFormat: String?): String {
        val format = SimpleDateFormat(strFormat, Locale.ENGLISH)
        return format.format(iDate)
    }

    /**
     * 将CST的时间字符串转换成需要的日期格式字符串<br></br>
     *
     * @param cststr The source to be dealed with. <br></br>
     * (exp:Fri Jan 02 00:00:00 CST 2009)
     * @param fmt    The format string
     * @return string or `null` if the cststr is unpasrseable or is
     * null return null,else return the string.
     * @author HitSnail
     */
    fun getDateFmtStrFromCST(cststr: String?, fmt: String?): String? {
        if (null == cststr || null == fmt) {
            return null
        }
        var str: String? = null
        val sdfy = SimpleDateFormat(fmt.trim { it <= ' ' })
        val ymdFormat = SimpleDateFormat(
                "EEE MMM dd HH:mm:ss 'CST' yyyy", Locale.US)
        str = try {
            sdfy.format(ymdFormat.parse(cststr.trim { it <= ' ' }))
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
        return str
    }

    /**
     * 获得当年的第一天。
     *
     * @param yyyy 年份
     * @return Date数组。第一位是当年的第一天和第二位是当年的最后一天。
     */
    fun getFirstAndLastDays(yyyy: String): Array<Date?> {
        val date = getDateFromPattern("yyyy-MM-dd", "$yyyy-01-01")
        val dateStr = formatDate(date)
        val year = dateStr.substring(0, 4)
        // 当年第一天的字符串形式。
        val firstDayStr = dateStr.replaceFirst(year + "-\\d{2}-\\d{2}".toRegex(), "$year-01-01")
        // 当年最后一天的字符串形式。
        val lastDayStr = dateStr.replaceFirst(year + "-\\d{2}-\\d{2}".toRegex(), "$year-12-31")
        val firstDay = formatString(firstDayStr)
        val lastDay = formatString(lastDayStr)
        return arrayOf(firstDay, lastDay)
    }

    /**
     * 通过格式化字符串得到时间
     *
     * @param parrern 格式化字符串 例如：yyyy-MM-dd
     * @param str     时间字符串 例如：2007-08-01
     * @return 出错返回null
     */
    fun getDateFromPattern(parrern: String?, str: String?): Date? {
        if (str == null || "" == str) // if (StringUtils.isEmpty(str))
            return null
        val fmt = SimpleDateFormat(parrern)
        try {
            return fmt.parse(str)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 将字符串转化为JAVA时间类型。
     *
     * @param dateStr。字符串。
     * @return Date date。JAVA时间类型。
     */
    fun formatString(dateStr: String?): Date? {
        val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
        return try {
            ymdFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 将字符串转化为JAVA时间类型(精确到秒)。
     *
     * @param dateStr。字符串。
     * @return Date date。JAVA时间类型。
     */
    fun formatFullString(dateStr: String?): Date? {
        val ymdFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return try {
            ymdFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 将Date时间转为字符串。
     *
     * @param date。需要格式化的时间。
     * @return String。传入时间的格式化字符串。
     */
    fun formatDate(date: Date?): String {
        val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
        return ymdFormat.format(date)
    }

    fun formatDates(date: Date?): String {
        val ymdFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return ymdFormat.format(date)
    }

    /**
     * 得到传入日期n天后的日期,如果传入日期为null，则表示当前日期n天后的日期
     *
     * @param dt   指定日期
     * @param days 可以为任何整数，负数表示前days天，正数表示后days天
     * @return Date
     */
    fun getAddDayDate(dt: Date?, days: Int): Date {
        var dt = dt
        if (dt == null) dt = Date(System.currentTimeMillis())
        val cal = Calendar.getInstance()
        cal.time = dt
        cal[Calendar.DAY_OF_MONTH] = cal[Calendar.DAY_OF_MONTH] + days
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.time
    }

    /**
     * @param dt   日期Date对象
     * @param days 可以为任何整数，负数表示前days天，正数表示后days天
     * @return Date
     * @author chensj
     * 得到传入日期n天后的日期,如果传入日期为null，则表示当前日期n天后的日期
     */
    fun getAddDayTime(dt: Date?, days: Int): Date {
        var dt = dt
        if (dt == null) dt = Date(System.currentTimeMillis())
        val cal = Calendar.getInstance()
        cal.time = dt
        cal[Calendar.DAY_OF_MONTH] = cal[Calendar.DAY_OF_MONTH] + days
        return cal.time
    }

    /**
     * @param d1 date-one
     * @param d2 date-two
     * @return second
     * 根据传入的两个时间求时间间隔
     */
    fun getDayBetween(d1: Date?, d2: Date?): Int { // return (int)(d1.getTime()-d2.getTime())/(1000*60*60*24);
        val d = arrayOfNulls<Date>(2)
        d[0] = d1
        d[1] = d2
        val cal = arrayOfNulls<Calendar>(2)
        for (i in cal.indices) {
            cal[i] = Calendar.getInstance()
            cal[i]?.apply {
                time = d[i]
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }
        val m = cal[0]!!.time.time
        val n = cal[1]!!.time.time
        return ((m - n) / 1000).toInt()
    }

    /**
     * 根据传入的两个时间求时间间隔(秒)
     *
     * @param d1 日期Date对象
     * @param d2 日期Date对象
     * @return second
     */
    fun getSecondsBetween(d1: Date?, d2: Date?): Int { // return (int)(d1.getTime()-d2.getTime())/(1000*60*60*24);
        val d = arrayOfNulls<Date>(2)
        d[0] = d1
        d[1] = d2
        val cal = arrayOfNulls<Calendar>(2)
        for (i in cal.indices) {
            cal[i] = Calendar.getInstance()
            cal[i]?.time = d[i]
        }
        val m = cal[0]!!.time.time
        val n = cal[1]!!.time.time
        return ((m - n) / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * 根据传入的两个时间求时间间隔(分)
     *
     * @param d1 日期Date对象
     * @param d2 日期Date对象
     * @return second
     */
    fun getDayMinuteBetween(d1: Date?, d2: Date?): IntArray {
        val d = arrayOfNulls<Date>(2)
        d[0] = d1
        d[1] = d2
        val cal = arrayOfNulls<Calendar>(2)
        for (i in cal.indices) {
            cal[i] = Calendar.getInstance()
            cal[i]?.time = d[i]
        }
        val m = cal[0]!!.time.time
        val n = cal[1]!!.time.time
        val between = IntArray(4)
        between[0] = ((m - n) / (1000 * 24 * 60 * 60)).toInt()
        between[1] = ((m - n) % (1000 * 24 * 60 * 60)).toInt() / (1000 * 60 * 60)
        between[2] = ((m - n) % (1000 * 60 * 60)).toInt() / (1000 * 60)
        between[3] = ((m - n) % (1000 * 60)).toInt() / 1000
        return between
    }

    /**
     * 根据传入的两个时间求时间间隔
     *
     * @param d1 日期Date
     * @param d2 日期Date
     * @return 返回时间间隔，如*秒钟，*分钟，*小时，*天
     * @author ChenKuan
     */
    fun getTimeBetween(d1: Date?, d2: Date?): String? {
        val d = arrayOfNulls<Date>(2)
        d[0] = d1
        d[1] = d2
        val cal = arrayOfNulls<Calendar>(2)
        for (i in cal.indices) {
            cal[i] = Calendar.getInstance()
            cal[i]?.time = d[i]
        }
        val m = cal[0]!!.time.time
        val n = cal[1]!!.time.time
        // 取间隔天数
        val daytime = ((m - n) / (1000 * 60 * 60 * 24)).toInt()
        if (Math.abs(daytime) > 0) {
            return Math.abs(daytime).toString() + "天"
        }
        // 取间隔小时数
        val hourtime = ((m - n) / (1000 * 60 * 60)).toInt()
        if (Math.abs(hourtime) > 0) {
            return Math.abs(hourtime).toString() + "小时"
        }
        // 取间隔分钟数
        val secondtime = ((m - n) / (1000 * 60)).toInt()
        if (Math.abs(secondtime) > 0) {
            return Math.abs(secondtime).toString() + "分钟"
        }
        // 取间隔秒钟数
        val minuteime = ((m - n) / 1000).toInt()
        return if (Math.abs(minuteime) >= 0) {
            Math.abs(minuteime).toString() + "秒钟"
        } else null
    }

    /*
     * 获取传入时间的当前月的第一天
     */
    fun getFristDayOfMonth(sDate1: Date?): Date {
        val cDay1 = Calendar.getInstance()
        cDay1.time = sDate1
        val fristDay = cDay1.getActualMinimum(Calendar.DAY_OF_MONTH)
        val fristDate = cDay1.time
        fristDate.date = fristDay
        return fristDate
    }

    /*
     * 获得传入时间的当月最后一天
     */
    fun getLastDayOfMonth(sDate1: Date?): Date {
        val cDay1 = Calendar.getInstance()
        cDay1.time = sDate1
        val lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH)
        val lastDate = cDay1.time
        lastDate.date = lastDay
        return lastDate
    }

    /*
     * 返回系统当前时间的前几个月的日期
     */
    val beforDate: Date?
        get() {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH] + 1
            val day = cal[Calendar.DATE]
            if (month > 5) {
                val aString = year.toString() + "-" + (month - 5) + "-" + day
                return formatString(aString)
            }
            val bString = (year - 1).toString() + "-" + (month + 12 - 5) + "-" + day
            return formatString(bString)
        }

    // 增加或减少的天数
    fun addDay(num: Int): Date {
        val startDT = Calendar.getInstance()
        startDT.time = Date()
        startDT.add(Calendar.DATE, num)
        return startDT.time
    }

    // 增加或减少天数 某个日期
    fun addDay(date: Date?, num: Int): Date {
        val startDT = Calendar.getInstance()
        startDT.time = date
        startDT.add(Calendar.DATE, num)
        return startDT.time
    }

    // 增加或减少月数 当天
    fun addMonth(num: Int): Date {
        val startDT = Calendar.getInstance()
        startDT.time = Date()
        startDT.add(Calendar.MONTH, num)
        return startDT.time
    }

    // 增加或减少年数 当天
    fun addYear(date: Date?, num: Int): Date {
        val startDT = Calendar.getInstance()
        startDT.time = date
        startDT.add(Calendar.YEAR, num)
        return startDT.time
    }

    // 返回java.sql.date
    fun getFullSqlDate(date: Date): Date {
        return Date(date.time)
    }

    // 增加或减少月数 某个日期
    fun addMonth(date: Date?, num: Int): Date {
        val startDT = Calendar.getInstance()
        startDT.time = date
        startDT.add(Calendar.MONTH, num)
        return startDT.time
    }// 当月最大天数// 当月的第几天

    // 天数差
    val quot: Int
        get() {
            val cc = Calendar.getInstance()
            cc.time = Date()
            val currmum = cc[Calendar.DAY_OF_MONTH] // 当月的第几天
            val maxmum = cc.getActualMaximum(Calendar.DAY_OF_MONTH) // 当月最大天数
            return maxmum - currmum
        }

    // 百分比 (辅助算靓号的价格)
    val percent: Float
        get() {
            val cc = Calendar.getInstance()
            cc.time = Date()
            val currmum = cc[Calendar.DAY_OF_MONTH].toFloat()
            val maxmum = cc.getActualMaximum(Calendar.DAY_OF_MONTH).toFloat()
            println(currmum / maxmum)
            return currmum / maxmum
        }// 天数差

    //
    val lastDay: Date
        get() {
            val quot = quot // 天数差
            return addDay(quot)
        }

    fun changeDateToUtil(dt: Date): Date {
        return Date(dt.time)
    }

    fun changeDateToSql(dt: Date): Date {
        return Date(dt.time)
    }

    /**
     * @param date 日期
     * @return 月份的第一天
     * 获得本月的最后一天
     */
    fun getLastDateByMonth(date: Date?): Date {
        val now = Calendar.getInstance()
        now.time = date
        now[Calendar.MONTH] = now[Calendar.MONTH] + 1
        now[Calendar.DATE] = 1
        now[Calendar.DATE] = now[Calendar.DATE] - 1
        now[Calendar.HOUR] = 11
        now[Calendar.MINUTE] = 59
        now[Calendar.SECOND] = 59
        return now.time
    }

    /**
     * @param date 月份所在的时间
     * @return 月份的最后一天
     * 获得所在月份的第一天
     */
    fun getFirstDateByMonth(date: Date?): Date {
        val now = Calendar.getInstance()
        now.time = date
        now[Calendar.DATE] = 0
        now[Calendar.HOUR] = 12
        now[Calendar.MINUTE] = 0
        now[Calendar.SECOND] = 0
        return now.time
    }

    /**
     * @param date   月份所在的时间
     * @param months 之后几个月
     * @return 当前月之后某月多少天
     * 获得当前月之后某月有多少天
     */
    fun getDayByMonth(date: Date, months: Int): Int {
        val tempMonth = date.month + 1 + months
        val years = tempMonth / 12
        val month = tempMonth % 12
        val time = Calendar.getInstance()
        time.clear()
        time[Calendar.YEAR] = date.year + years
        time[Calendar.MONTH] = month - 1 // Calendar对象默认一月为0
        return time.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getDateByAddDays(date: Date?, days: Int): Date {
        val now = Calendar.getInstance()
        now.time = date
        now[Calendar.DATE] = now[Calendar.DATE] + days
        return now.time
    }

    /**
     * @param date   日期
     * @param Months 月数 1为本月
     * @return 获得指定日期所在的月之后某月的最后一天
     * @author zhangwt
     * 获得指定日期所在的月之后某月的最后一天
     */
    fun getDateByMonth(date: Date, Months: Int): Date {
        val tempMonth = date.month + 1 + Months
        val years = tempMonth / 12
        val month = tempMonth % 12
        val time = Calendar.getInstance()
        time.clear()
        time[Calendar.YEAR] = date.year + 1900 + years
        time[Calendar.MONTH] = month - 1 // Calendar对象默认一月为0
        time[Calendar.DATE] = time[Calendar.DATE] - 1
        time[Calendar.HOUR] = 11
        time[Calendar.MINUTE] = 59
        time[Calendar.SECOND] = 59
        return time.time
    }

    fun isLateTime(nowTime: Date?, otime: String?): Boolean {
        val sform = SimpleDateFormat("yyyyMMddHHmmss")
        val snowTime = sform.format(nowTime)
        val time1 = java.lang.Long.valueOf(snowTime)
        val time2 = java.lang.Long.valueOf(otime)
        return if (time1 < time2) true else false
    }

    /**
     * 获得某月的剩余天数
     *
     * @param date   日期Date
     * @param Months 指定月份
     * @return 间隔天数
     */
    fun getLastDayByMonth(date: Date?, Months: Int): Int {
        return getSecondsBetween(getDateByMonth(Date(), Months), date) / 86400
    }

    fun getFirstDayByMonth(date: Date?): Date {
        val now = Calendar.getInstance()
        now.time = date
        now[Calendar.DAY_OF_MONTH] = 1
        now[Calendar.HOUR_OF_DAY] = 0
        now[Calendar.MINUTE] = 0
        now[Calendar.SECOND] = 0
        return now.time
    }

    // 获得周统计的统计时间
    @Throws(Exception::class)
    fun getStatDateByWeek(year: Long, month: Long, week: Long): Date {
        val date = strToDate("$year-$month", "yyyy-MM")
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.DATE] = cal[Calendar.DATE] + (7 + Calendar.MONDAY - cal[Calendar.DAY_OF_WEEK]) % 7 + (7 * week).toInt()
        return cal.time
    }

    // 获得月统计的统计时间
    @Throws(ParseException::class)
    fun getStatDateByMonth(year: Long, month: Long): Date {
        val date = strToDate("$year-$month", "yyyy-MM")
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1
        return getFirstDayByMonth(cal.time)
    }

    // 获得季统计的统计时间
    @Throws(ParseException::class)
    fun getStatDateByQuarter(year: Long, quarter: Long): Date {
        val date = strToDate(year.toString() + "-" + quarter * 3, "yyyy-MM")
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1
        return getFirstDayByMonth(cal.time)
    }

    // 获得半年统计的统计时间
    @Throws(ParseException::class)
    fun getStatDateBySemi(year: Long, semi: Long): Date {
        val date = strToDate(year.toString() + "-" + semi * 6, "yyyy-MM")
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1
        return getFirstDayByMonth(cal.time)
    }

    // 获得年统计的统计时间
    @Throws(ParseException::class)
    fun getStatDateByYear(year: Long): Date {
        val date = strToDate((year + 1).toString() + "-01-01", "yyyy-MM-dd")
        return getFirstDayByMonth(date)
    }

    // 根据推荐日期获得结算日期
    @Throws(ParseException::class)
    fun getRecommendFootDate(date: Date?): Date {
        val dd = addMonth(date, 1)
        val cal = Calendar.getInstance()
        cal.time = dd
        cal[Calendar.DATE] = 10
        if (cal.time.before(dd)) {
            cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1
        }
        return cal.time
    }

    /**
     * 获得本周的最后一天
     *
     * @param date 日期Date对象
     * @return 本周的最后一天日期Date
     */
    fun getLastDateOfWeek(date: Date?): Date {
        val now = Calendar.getInstance()
        now.time = date
        now[Calendar.DAY_OF_WEEK] = 7
        return now.time
    }

    /**
     * 获取指定日期month月之后的所在日期;
     * 如3月5号 1月之后所在日期4月3号
     *
     * @param dt    指定日期
     * @param month 月份数
     * @return Date
     */
    fun getDateByDateAndMonth(dt: Date?, month: Int): Date? {
        var day = 0
        if (null == dt) return null
        for (i in 0 until month) {
            day += getDayByMonth(dt, i)
        }
        return getAddDayDate(dt, day - 1)
    }

    /**
     * @param dt    指定日期
     * @param month 月份数
     * @return 获取指定日期month月之前所在日期
     * 本月：9月28号，前一个月为8月29号
     */
    fun getDateBeforeNMonth(dt: Date?, month: Int): Date? {
        var day = 0
        if (null == dt) return null
        val size = Math.abs(month)
        for (i in 0 until size) {
            day -= getDayByMonth(dt, -i)
        }
        return getAddDayDate(dt, day)
    }

    /**
     * @param stype 返回值类型   0为多少天，1为多少个月，2为多少年
     * @param date1 开始日期
     * @param date2 结束日期
     * @return 比较的结果
     */
    fun compareDate(date1: String?, date2: String?, stype: Int): Int {
        var n = 0
        //String[] u = {"天","月","年"};
        val formatStyle = "yyyy-MM-dd"
        val df: DateFormat = SimpleDateFormat(formatStyle)
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        try {
            c1.time = df.parse(date1)
            c2.time = df.parse(date2)
        } catch (e3: Exception) {
            println("wrong occured")
        }
        while (!c1.after(c2)) {
            n++
            if (stype == 1) {
                c1.add(Calendar.MONTH, 1) // 比较月份，月份+1
            } else {
                c1.add(Calendar.DATE, 1) // 比较天数，日期+1
            }
        }
        n = n - 1
        if (stype == 2) {
            val yushu = n % 365
            n = if (yushu == 0) n / 365 else n / 365 - 1
        }
        //   System.out.println(date1+" -- "+date2+" 相差多少"+u[stype]+":"+n);
        return n
    }

    /**
     * 获取日期是星期几<br></br>
     *
     * @param dt 目标日期
     * @return 当前日期是星期几
     * @author onping
     * 想返回数字:1为周一2为周二，去掉数组weekDays,直接返回w
     * 想返回汉字周几见下
     */
    fun getWeekOfDate(dt: Date?): Int { //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        val cal = Calendar.getInstance()
        cal.time = dt
        var w = cal[Calendar.DAY_OF_WEEK] - 1
        if (w < 0 || w == 0) {
            w = 7
        }
        return w
        //return weekDays[w];
    }

    /**
     * 两个日期的差距(天数)
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 天数间隔
     */
    fun getDistDates(startDate: Date?, endDate: Date?): Long {
        var totalDate: Long = 0
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val timestart = calendar.timeInMillis
        calendar.time = endDate
        val timeend = calendar.timeInMillis
        totalDate = Math.abs(timeend - timestart) / (1000 * 60 * 60 * 24)
        return totalDate
    }

    /**
     * 两个日期的差距(毫秒)
     *
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return 相差的毫秒值
     */
    fun getDistDatesInMillis(startDate: Date?, endDate: Date?): Long {
        var totalDate: Long = 0
        var timestart: Long = 0
        var timeend: Long = 0
        val calendar = Calendar.getInstance()
        if (null != startDate) {
            calendar.time = startDate
            timestart = calendar.timeInMillis
        }
        if (null != endDate) {
            calendar.time = endDate
            timeend = calendar.timeInMillis
        }
        totalDate = Math.abs(timeend - timestart)
        return totalDate
    }

    /**
     * @param dateU   为当前时间
     * @param minTime 为想减去的时间
     * @param flag    往前/后计算的标志
     * @return Date
     * @throws Exception dateU 往前推X小时X分钟 或者往后推
     */
    @Throws(Exception::class)
    fun getMinDate(dateU: Date, minTime: String?, flag: Long): Date {
        var wantDate: Date? = null
        val ymdFormat = SimpleDateFormat("HH:mm:ss") //转换为02:30 2小时30分钟
        wantDate = ymdFormat.parse(minTime)
        val strDate = ymdFormat.format(wantDate)
        var ss = 0 //转换后的毫秒数
        if (null != strDate) {
            val Hour = strDate.split(":".toRegex()).toTypedArray()[0].toInt()
            val minute = strDate.split(":".toRegex()).toTypedArray()[1].toInt()
            val second = strDate.split(":".toRegex()).toTypedArray()[2].toInt()
            ss = Hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000
        }
        var chaSec = 0L
        if (flag == 1L) { //往前推
            chaSec = dateU.time - ss
        } else if (flag == 2L) {
            chaSec = dateU.time + ss
        }
        return Date(chaSec)
    }

    /**
     * 当天日期加或减num年
     *
     * @param num 年数
     * @return Date
     * @throws Exception 转换异常
     */
    @Throws(Exception::class)
    fun getYear(num: Int): Date {
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, num)
        return c.time
    }
}