package com.zeke.kangaroo.zlog

import android.content.Context
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.ConsolePrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.zeke.kangaroo.utils.FileUtils
import java.io.File

/**
 * author：ZekeWang
 * date：2021/10/22
 * description：基于XLog扩展的新版ZLog
 * 增加简单的堆栈信息打印，便于IDE点击跳转
 *
 *  // 创建日志配置项
 *  LogConfiguration config = new LogConfiguration.Builder()
 *      .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
 *          : LogLevel.NONE)
 *      .tag("MY_TAG")                                         // 指定 TAG，默认为 "X-LOG"
 *      .enableThreadInfo()                                    // 允许打印线程信息，默认禁止
 *      .enableStackTrace(2)                                   // 允许打印深度为 2 的调用栈信息，默认禁止
 *      .enableBorder()                                        // 允许打印日志边框，默认禁止
 *      .jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
 *      .xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
 *      .throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
 *      .threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
 *      .stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
 *      .borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
 *      .addObjectFormatter(AnyClass.class,                    // 为指定类型添加对象格式化器
 *          new AnyClassObjectFormatter())                     // 默认使用 Object.toString()
 *      .addInterceptor(new BlacklistTagsFilterInterceptor(    // 添加黑名单 TAG 过滤器
 *          "blacklist1", "blacklist2", "blacklist3"))
 *      .addInterceptor(new MyInterceptor())                   // 添加一个日志拦截器
 *      .build();
 *
 *  //创建各种printer
 *  Printer androidPrinter = new AndroidPrinter(true);         // 通过 android.util.Log 打印日志的打印器
 *  Printer consolePrinter = new ConsolePrinter();             // 通过 System.out 打印日志到控制台的打印器
 *  Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
 *      .Builder("<日志目录全路径>")                             // 指定保存日志文件的路径
 *      .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
 *      .backupStrategy(new NeverBackupStrategy())             // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
 *      .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // 指定日志文件清除策略，默认为 NeverCleanStrategy()
 *      .flattener(new MyFlattener())                          // 指定日志平铺器，默认为 DefaultFlattener
 *      .writer(new MyWriter())                                // 指定日志写入器，默认为 SimpleWriter
 *      .build();
 *
 *  //初始化
 *  XLog.init(                                                 // 初始化 XLog
 *      config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
 *      androidPrinter,                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
 *      consolePrinter,
 *      filePrinter);
 */
class ZLog {

    companion object{
        private var enableGlobalBorder = false

        /**
         * 默认配置
         * TAG为ZLog, 不开启border和日志输出，
         *
         * @param writeToFile   日志写出到文件，默认关闭
         * @param enableLog     日志显示打印开关 默认开启
         * @param enableBorder  日志边框 默认关闭
         */
        fun init(
            ctx: Context,
            configuration: LogConfiguration? = null,
            writeToFile: Boolean = false,
            enableLog: Boolean = true,
            enableBorder: Boolean = false
        ) {
            var config = configuration
            enableGlobalBorder = enableBorder
            if (config == null) {
                val logConfigBuilder = LogConfiguration.Builder().apply {
                    logLevel(LogLevel.ALL)
                    tag("ZLog")
                    disableThreadInfo()
                    borderFormatter(ZBorderFormatter())

                    if (enableGlobalBorder) {
                        enableBorder()
                    } else {
                        disableBorder()
                    }
                }
                config = logConfigBuilder.build()
            }

            var androidPrinter: AndroidPrinter? = null
            var consolePrinter: ConsolePrinter? = null
            var filePrinter: FilePrinter? = null
            if (writeToFile) {
                //TODO 当前应用的data，目录是可以一次性获取的
                val logFolder = File(
                    FileUtils.Companion.getExternalStorageDirectory(ctx)
                        .toString() + "/Android/data/" + ctx.packageName, "zlog"
                )
                FileUtils.Companion.createDir(logFolder)

                filePrinter = FilePrinter.Builder(logFolder.absolutePath) // 指定保存日志文件的路径
                        .fileNameGenerator(DateFileNameGenerator()) // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                        .backupStrategy(FileSizeBackupStrategy2(5 * 1024 * 1024, 5)) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                        .flattener(ClassicFlattener())
                        .build()
            }

            if (enableLog) {
                androidPrinter = AndroidPrinter(true)
            }
//                consolePrinter = ConsolePrinter()
            val printers = mutableListOf<Printer>()
            if (androidPrinter != null) {
                printers.add(androidPrinter)
            }
            if (consolePrinter != null) {
                printers.add(consolePrinter)
            }
            if (filePrinter != null) {
                printers.add(filePrinter)
            }
            if (printers.isNotEmpty()) {
                XLog.init(config, *printers.toTypedArray())
            } else {
                XLog.init(config)
            }
        }

        //------ 以下方法，不绘制border时，都用全局的logger，不重新创建logger对象

        @JvmStatic @JvmOverloads
        fun d(tag: String, content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val builder = XLog.tag(tag)
            if (enableBorder || enableGlobalBorder) {
                builder.enableBorder().d(content, tr)
            } else {
                builder.d(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun d(msg: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val content = appendStackTraceInfo(msg)
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().d(content, tr)
            } else {
                XLog.d(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun d(any: Any, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().d(any)
            } else {
                XLog.d(any)
            }
        }

        @JvmStatic @JvmOverloads
        fun d(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().d(array)
            } else {
                XLog.d(array)
            }
        }

        @JvmStatic @JvmOverloads
        fun i(tag: String, msg: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val content = appendStackTraceInfo(msg)
            val customLogger = XLog.tag(tag)
            if (enableBorder || enableGlobalBorder) {
                customLogger.enableBorder().i(content, tr)
            } else {
                customLogger.i(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun i(msg: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val content = appendStackTraceInfo(msg)
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().i(content, tr)
            } else {
                XLog.i(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun i(any: Any, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().i(any)
            } else {
                XLog.i(any)
            }
        }

        @JvmStatic @JvmOverloads
        fun i(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().i(array)
            } else {
                XLog.i(array)
            }
        }

        @JvmStatic @JvmOverloads
        fun w(tag: String, content: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().tag(tag).w(content, tr)
            } else {
                XLog.tag(tag).w(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun w(msg: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val content = appendStackTraceInfo(msg)
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().w(content, tr)
            } else {
                XLog.w(content, tr)
            }
        }

        @JvmStatic
        fun w(any: Any, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().w(any)
            } else {
                XLog.w(any)
            }
        }

        @JvmStatic
        fun w(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().w(array)
            } else {
                XLog.w(array)
            }
        }

        @JvmStatic @JvmOverloads
        fun e(tag: String, msg: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val content = appendStackTraceInfo(msg)
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().tag(tag).e(content, tr)
            } else {
                XLog.tag(tag).e(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun e(msg: String, tr: Throwable? = null, enableBorder: Boolean = false) {
            val content = appendStackTraceInfo(msg)
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().e(content, tr)
            } else {
                XLog.e(content, tr)
            }
        }

        @JvmStatic @JvmOverloads
        fun e(any: Any, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().e(any)
            } else {
                XLog.e(any)
            }
        }

        @JvmStatic @JvmOverloads
        fun e(array: Array<Any>, enableBorder: Boolean = false) {
            if (enableBorder || enableGlobalBorder) {
                XLog.enableBorder().e(array)
            } else {
                XLog.e(array)
            }
        }

        @JvmStatic
        fun json(tag: String, json: String) {
            XLog.enableBorder().tag(tag).json(json)
        }

        @JvmStatic
        fun json(json: String) {
            XLog.enableBorder().json(json)
        }

        @JvmStatic
        fun xml(tag: String, xml: String) {
            XLog.enableBorder().tag(tag).xml(xml)
        }

        @JvmStatic
        fun xml(xml: String) {
            XLog.enableBorder().json(xml)
        }


        /**
         * 抽取当前堆栈信息
         *
         * @param msg 源日志内容
         * @return 返回带堆栈信息的日志信息字符串
         */
        private fun appendStackTraceInfo(msg: String): String {
            val stackTrace = Thread.currentThread().stackTrace
            /*
             * 外部调用方法堆栈的index
             * 5--- 外部调用点的堆栈信息
             * 4--- com.zeke.kangaroo.zlog.ZLogV2$Companion.d$default(ZLogV2.kt:150)
             * 3--- com.zeke.kangaroo.zlog.ZLog$Companion.d/i/v
             * 2--- com.zeke.kangaroo.zlog.ZLog$Companion.appendStackTraceInfo(ZLogV2.kt:319)
             * 1--- java.lang.Thread.getStackTrace
             * 0--- dalvik.system.VMStack.getThreadStackTrace(Native Method)
             */
            val index = 5
            val fileName = stackTrace[index].fileName

            var methodName = stackTrace[index].methodName
            val lineNumber = stackTrace[index].lineNumber
            methodName = toUpperCase(methodName)
            val stringBuilder = StringBuilder()
            stringBuilder.append("[($fileName:$lineNumber)#$methodName] ")
            stringBuilder.append(msg)
            return stringBuilder.toString()
        }

        private fun toUpperCase(string: String): String {
            val methodName = string.toCharArray()
            methodName[0] = toUpperCase(methodName[0])
            return String(methodName)
        }

        private fun toUpperCase(src: Char): Char {
            var chars = src
            if (chars.toInt() in 97..122) {//a---z
                chars = (chars.toInt() xor 32).toChar()
            }
            return chars
        }
    }
}