package com.zeke.kangaroo.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.TextUtils
import android.util.Log
import com.zeke.kangaroo.zlog.ZLog
import java.io.*
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Created by KingZ on 2015/11/4.
 * Discription:文件工具类
 */
class FileUtils private constructor() {
    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnReplaceListener {
        fun onReplaced(): Boolean
    }

    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {

        private const val TAG = "FileUtils"
        private val LINE_SEP = System.getProperty("line.separator")
        private val MIME_TYPES = arrayOf(
                arrayOf(".3gp", "video/3gpp"),
                arrayOf(".apk", "application/vnd.android.package-archive"),
                arrayOf(".asf", "video/x-ms-asf"),
                arrayOf(".avi", "video/x-msvideo"),
                arrayOf(".bin", "application/octet-stream"),
                arrayOf(".bmp", "image/bmp"),
                arrayOf(".c", "text/plain"),
                arrayOf(".class", "application/octet-stream"),
                arrayOf(".conf", "text/plain"),
                arrayOf(".cpp", "text/plain"),
                arrayOf(".doc", "application/msword"),
                arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
                arrayOf(".xls", "application/vnd.ms-excel"),
                arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                arrayOf(".exe", "application/octet-stream"),
                arrayOf(".gif", "image/gif"),
                arrayOf(".gtar", "application/x-gtar"),
                arrayOf(".gz", "application/x-gzip"),
                arrayOf(".h", "text/plain"),
                arrayOf(".htm", "text/html"),
                arrayOf(".html", "text/html"),
                arrayOf(".jar", "application/java-archive"),
                arrayOf(".java", "text/plain"),
                arrayOf(".jpeg", "image/jpeg"),
                arrayOf(".jpg", "image/jpeg"),
                arrayOf(".js", "application/x-javascript"),
                arrayOf(".log", "text/plain"),
                arrayOf(".m3u", "audio/x-mpegurl"),
                arrayOf(".m4a", "audio/mp4a-latm"),
                arrayOf(".m4b", "audio/mp4a-latm"),
                arrayOf(".m4p", "audio/mp4a-latm"),
                arrayOf(".m4u", "video/vnd.mpegurl"),
                arrayOf(".m4v", "video/x-m4v"),
                arrayOf(".mov", "video/quicktime"),
                arrayOf(".mp2", "audio/x-mpeg"),
                arrayOf(".mp3", "audio/x-mpeg"),
                arrayOf(".mp4", "video/mp4"),
                arrayOf(".mpc", "application/vnd.mpohun.certificate"),
                arrayOf(".mpe", "video/mpeg"),
                arrayOf(".mpeg", "video/mpeg"),
                arrayOf(".mpg", "video/mpeg"),
                arrayOf(".mpg4", "video/mp4"),
                arrayOf(".mpga", "audio/mpeg"),
                arrayOf(".msg", "application/vnd.ms-outlook"),
                arrayOf(".ogg", "audio/ogg"),
                arrayOf(".pdf", "application/pdf"),
                arrayOf(".png", "image/png"),
                arrayOf(".pps", "application/vnd.ms-powerpoint"),
                arrayOf(".ppt", "application/vnd.ms-powerpoint"),
                arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
                arrayOf(".prop", "text/plain"),
                arrayOf(".rc", "text/plain"),
                arrayOf(".rmvb", "audio/x-pn-realaudio"),
                arrayOf(".rtf", "application/rtf"),
                arrayOf(".sh", "text/plain"),
                arrayOf(".tar", "application/x-tar"),
                arrayOf(".tgz", "application/x-compressed"),
                arrayOf(".txt", "text/plain"),
                arrayOf(".wav", "audio/x-wav"),
                arrayOf(".wma", "audio/x-ms-wma"),
                arrayOf(".wmv", "audio/x-ms-wmv"),
                arrayOf(".wps", "application/vnd.ms-works"),
                arrayOf(".xml", "text/plain"),
                arrayOf(".z", "application/x-compress"),
                arrayOf(".zip", "application/x-zip-compressed"),
                arrayOf("", "*/*")
        )
// <editor-fold defaultstate="collapsed" desc="删除API">
        /**
         * Delete the directory.
         *
         * @param file The file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun delete(file: File): Boolean {
            return if (file.isDirectory) {
                deleteDir(file)
            } else {
                deleteFile(file)
            }
        }

        /**
         * Delete the directory.
         *
         * @param dir The directory.
         * @return `true`: success<br></br>`false`: fail
         */
        private fun deleteDir(dir: File): Boolean {
            // dir doesn't exist then return true
            if (!dir.exists()) return true
            // dir isn't a directory then return false
            if (!dir.isDirectory) return false
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file in files) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
            return dir.delete()
        }

        /**
         * Delete the file.
         *
         * @param file The file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteFile(file: File): Boolean {
            return !file.exists() || (file.isFile && file.delete())
        }

        /**
         * Delete the all in directory.
         *
         * @param dirPath The path of directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteAllInDir(dirPath: String): Boolean {
            return deleteAllInDir(getFile(dirPath))
        }

        /**
         * Delete the all in directory.
         *
         * @param dir The directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteAllInDir(dir: File): Boolean {
            return deleteFilesInDirWithFilter(dir) { true }
        }

        /**
         * Delete all files in directory.
         *
         * @param dirPath The path of directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteFilesInDir(dirPath: String): Boolean {
            return deleteFilesInDir(getFile(dirPath))
        }

        /**
         * Delete all files in directory.
         *
         * @param dir The directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteFilesInDir(dir: File): Boolean {
            return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile }
        }

        /**
         * Delete all files that satisfy the filter in directory.
         *
         * @param dirPath The path of directory.
         * @param filter  The filter.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteFilesInDirWithFilter(dirPath: String,
                                       filter: FileFilter): Boolean {
            return deleteFilesInDirWithFilter(getFile(dirPath), filter)
        }

        /**
         * Delete all files that satisfy the filter in directory.
         *
         * @param dir    The directory.
         * @param filter The filter.
         * @return `true`: success<br></br>`false`: fail
         */
        fun deleteFilesInDirWithFilter(dir: File, filter: FileFilter): Boolean {
            // dir doesn't exist then return true
            if (!dir.exists()) return true
            // dir isn't a directory then return false
            if (!dir.isDirectory) return false
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (filter.accept(file)) {
                        if (file.isFile) {
                            if (!file.delete()) return false
                        } else if (file.isDirectory) {
                            if (!deleteDir(file)) return false
                        }
                    }
                }
            }
            return true
        }

        /**
         * 删除指定目录文件夹下面的文件，如果文件超过指定时间未修改
         * @param path  目录路径
         * @param limitTime 限制时间
         * @return 是否删除成功
         */
        fun dealPathFilesWithOldDate(path: String, limitTime: Long): Boolean {
            if (TextUtils.isEmpty(path)) {
                return false
            }
            val file = File(path)
            val fl = file.listFiles() ?: return false
            ZLog.i(TAG, "deletePathFilesOldFile path:" + path + ", count:" + fl.size)
            for (curFile in fl) {
                if (curFile != null && curFile.lastModified() < limitTime) {
                    curFile.delete()
                }
            }
            return true
        }

        /**
         * 删除指定文件夹下的全部文件，如果文件超过某个数量
         * @param path 文件夹路径
         * @param count 文件总数上限
         * @return 是否删除成功
         */
        fun dealPathFilesOverCount(path: String, count: Long): Boolean {
            if (TextUtils.isEmpty(path)) {
                return false
            }
            val file = File(path)
            val fl = file.listFiles() ?: return true
            if (fl.size < count) {
                return false
            }
            ZLog.i(TAG, "deletePathFilesOldFile path:" + path + ", count:" + fl.size)
            for (curFile in fl) {
                curFile?.delete()
            }
            return true
        }

// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="储存API">
        /**
         * 储存bitMap的指定格式至指定目录
         * @param filePath 储存路径
         * @param bitmap   储存的bitmap
         * @param format   储存的格式
         * @param quality  储存的质量
         */
        fun saveBitmapWithPath(filePath: File, bitmap: Bitmap, format: CompressFormat?, quality: Int) {
            try {
                val out = FileOutputStream(filePath)
                if (filePath.canWrite()) {
                    bitmap.compress(format, quality, out)
                    out.flush()
                    out.close()
                } else {
                    throw IOException("The filePath is can not Write!")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun saveObjectWithPath(obj: Any?, filePath: File) {
            try {
                val out = ObjectOutputStream(FileOutputStream(filePath))
                if (filePath.canWrite()) {
                    out.writeObject(obj)
                    out.close()
                } else {
                    throw IOException("The filePath is can not Write!")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
// </editor-fold>

        fun readObjectWithPath(filePath: File): Any? {
            return try {
                val inputStream = ObjectInputStream(FileInputStream(filePath))
                if (filePath.canWrite()) {
                    val obj = inputStream.readObject()
                    inputStream.close()
                    obj
                } else {
                    throw IOException("The filePath is can not Write!")
                }
            } catch (e: IOException) {
                Log.e(TAG, "File not found :" + filePath.absolutePath)
                null
            } catch (e: ClassNotFoundException) {
                Log.e(TAG, "File not found :" + filePath.absolutePath)
                null
            }
        }

        /**
         * Get MIME type of file.
         *
         * @param file The file.
         * @return MIME value
         */
        fun getMIMEType(file: File): String {
            var type = ""
            val fileName = file.name
            val dotIndex = fileName.indexOf('.')
            if (dotIndex < 0) {
                return type
            }
            val end = fileName.substring(dotIndex, fileName.length).toLowerCase()
            if (TextUtils.equals("", end)) {
                return type
            }
            for (aMIME_MapTable in MIME_TYPES) {
                if (TextUtils.equals(aMIME_MapTable[0], end)) {
                    type = aMIME_MapTable[1]
                }
            }
            return type
        }

        /**
         * Return the file by path.
         *
         * @param path The path of file.
         * @return the file
         */
        private fun getFile(path: String): File {
            if (isSpace(path)){
                throw IllegalArgumentException("path is white space: [$path]")
            }
            return File(path)
        }

        // <editor-fold defaultstate="collapsed" desc="判断API">

        /**
         * Return whether it is a directory.
         *
         * @param file The file.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isDir(file: File): Boolean {
            return file.exists() && file.isDirectory
        }

        /**
         * Return whether it is a file.
         *
         * @param file The file.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isFile(file: File): Boolean {
            return file.exists() && file.isFile
        }
        // </editor-fold>

        /**
         * Rename the file.
         *
         * @param filePath The path of file.
         * @param newName  The new name of file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun rename(filePath: String, newName: String): Boolean {
            return rename(getFile(filePath), newName)
        }

        /**
         * Rename the file.
         *
         * @param file    The file.
         * @param newName The new name of file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun rename(file: File?, newName: String): Boolean {
            // file is null then return false
            if (file == null) return false
            // file doesn't exist then return false
            if (!file.exists()) return false
            // the new name is space then return false
            if (isSpace(newName)) return false
            // the new name equals old name then return true
            if (newName == file.name) return true
            val newFile = File(file.parent + File.separator + newName)
            // the new name of file exists then return false
            return (!newFile.exists()
                    && file.renameTo(newFile))
        }



        /**
         * Create a directory if it doesn't exist, otherwise do nothing.
         *
         * @param dirPath The path of directory.
         * @return `true`: exists or creates successfully<br></br>`false`: otherwise
         */
        fun createDir(dirPath: String): Boolean {
            return createDir(getFile(dirPath))
        }

        /**
         * Create a directory if it doesn't exist, otherwise do nothing.
         *
         * @param file The file.
         * @return `true`: exists or creates successfully<br></br>`false`: otherwise
         */
        fun createDir(file: File): Boolean {
            return if (file.exists()) {
                file.isDirectory
            } else {
                file.mkdirs()
            }
        }

        /**
         * Create a file if it doesn't exist, otherwise do nothing.
         *
         * @param file The file.
         * @return `true`: exists or creates successfully<br></br>`false`: otherwise
         */
        fun createFile(file: File): Boolean {
            if (file.exists()) return file.isFile
            return if (!createDir(file.parentFile)) {
                false
            } else {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            }
        }

        /**
         * Create a file if it doesn't exist, otherwise delete old file before creating.
         *
         * @param filePath The path of file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun createFileWithReplace(filePath: String): Boolean {
            return createFileWithReplace(getFile(filePath))
        }

        /**
         * Create a file if it doesn't exist, otherwise delete old file before creating.
         *
         * @param file The file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun createFileWithReplace(file: File): Boolean {
            // file exists and unsuccessfully delete then return false
            if (file.exists() && !file.delete()) return false
            return if (!createDir(file.parentFile)) false else try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        // <editor-fold defaultstate="collapsed" desc="复制&移动操作">
        /**
         * Copy the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyDir(srcDirPath: String, destDirPath: String): Boolean {
            return copyDir(getFile(srcDirPath), getFile(destDirPath))
        }

        /**
         * Copy the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @param listener    The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyDir(srcDirPath: String, destDirPath: String, listener: OnReplaceListener? = null): Boolean {
            return copyDir(getFile(srcDirPath), getFile(destDirPath), listener)
        }

        /**
         * Copy the directory.
         *
         * @param srcDir  The source directory.
         * @param destDir The destination directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyDir(srcDir: File?,
                    destDir: File?): Boolean {
            return copyOrMoveDir(srcDir, destDir, false)
        }

        /**
         * Copy the directory.
         *
         * @param srcDir   The source directory.
         * @param destDir  The destination directory.
         * @param listener The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyDir(srcDir: File?,
                    destDir: File?,
                    listener: OnReplaceListener?): Boolean {
            return copyOrMoveDir(srcDir, destDir, listener, false)
        }

        /**
         * Copy the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyFile(srcFilePath: String,
                     destFilePath: String): Boolean {
            return copyFile(getFile(srcFilePath), getFile(destFilePath))
        }

        /**
         * Copy the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @param listener     The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyFile(srcFilePath: String,
                     destFilePath: String,
                     listener: OnReplaceListener?): Boolean {
            return copyFile(getFile(srcFilePath), getFile(destFilePath), listener)
        }

        /**
         * Copy the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyFile(srcFile: File,
                     destFile: File): Boolean {
            return copyOrMoveFile(srcFile, destFile, false)
        }

        /**
         * Copy the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @param listener The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyFile(srcFile: File,
                     destFile: File,
                     listener: OnReplaceListener?): Boolean {
            return copyOrMoveFile(srcFile, destFile, listener, false)
        }

        /**
         * Move the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveDir(srcDirPath: String,
                    destDirPath: String): Boolean {
            return moveDir(getFile(srcDirPath), getFile(destDirPath))
        }

        /**
         * Move the directory.
         *
         * @param srcDirPath  The path of source directory.
         * @param destDirPath The path of destination directory.
         * @param listener    The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveDir(srcDirPath: String,
                    destDirPath: String,
                    listener: OnReplaceListener?): Boolean {
            return moveDir(getFile(srcDirPath), getFile(destDirPath), listener)
        }

        /**
         * Move the directory.
         *
         * @param srcDir  The source directory.
         * @param destDir The destination directory.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveDir(srcDir: File,
                    destDir: File): Boolean {
            return copyOrMoveDir(srcDir, destDir, true)
        }

        /**
         * Move the directory.
         *
         * @param srcDir   The source directory.
         * @param destDir  The destination directory.
         * @param listener The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveDir(srcDir: File,
                    destDir: File,
                    listener: OnReplaceListener? = null): Boolean {
            return copyOrMoveDir(srcDir, destDir, listener, true)
        }

        /**
         * Move the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveFile(srcFilePath: String,
                     destFilePath: String): Boolean {
            return moveFile(getFile(srcFilePath), getFile(destFilePath))
        }

        /**
         * Move the file.
         *
         * @param srcFilePath  The path of source file.
         * @param destFilePath The path of destination file.
         * @param listener     The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveFile(srcFilePath: String,
                     destFilePath: String,
                     listener: OnReplaceListener? = null): Boolean {
            return moveFile(getFile(srcFilePath), getFile(destFilePath), listener)
        }

        /**
         * Move the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveFile(srcFile: File?,
                     destFile: File?): Boolean {
            return copyOrMoveFile(srcFile, destFile, true)
        }

        /**
         * Move the file.
         *
         * @param srcFile  The source file.
         * @param destFile The destination file.
         * @param listener The replace listener.
         * @return `true`: success<br></br>`false`: fail
         */
        fun moveFile(srcFile: File?,
                     destFile: File?,
                     listener: OnReplaceListener?): Boolean {
            return copyOrMoveFile(srcFile, destFile, listener, true)
        }

        private fun copyOrMoveDir(srcDir: File?,
                                  destDir: File?,
                                  isMove: Boolean): Boolean {
            return copyOrMoveDir(srcDir, destDir, object : OnReplaceListener {
                override fun onReplaced(): Boolean {
                    return true
                }
            }, isMove)
        }

        private fun copyOrMoveDir(srcDir: File?,
                                  destDir: File?,
                                  listener: OnReplaceListener?,
                                  isMove: Boolean): Boolean {
            if (srcDir == null || destDir == null) return false
            // destDir's path locate in srcDir's path then return false
            val srcPath = srcDir.path + File.separator
            val destPath = destDir.path + File.separator
            if (destPath.contains(srcPath)) return false
            if (!srcDir.exists() || !srcDir.isDirectory) return false
            if (destDir.exists()) {
                if (listener == null || listener.onReplaced()) { // require delete the old directory
                    if (!deleteAllInDir(destDir)) { // unsuccessfully delete then return false
                        return false
                    }
                } else {
                    return true
                }
            }
            if (!createDir(destDir)) return false
            val files = srcDir.listFiles()
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
                } else if (file.isDirectory) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
                }
            }
            return !isMove || deleteDir(srcDir)
        }

        private fun copyOrMoveFile(srcFile: File?,
                                   destFile: File?,
                                   isMove: Boolean): Boolean {
            return copyOrMoveFile(srcFile, destFile, object : OnReplaceListener {
                override fun onReplaced(): Boolean {
                    return true
                }
            }, isMove)
        }

        private fun copyOrMoveFile(srcFile: File?,
                                   destFile: File?,
                                   listener: OnReplaceListener?,
                                   isMove: Boolean): Boolean {
            if (srcFile == null || destFile == null) return false
            // srcFile equals destFile then return false
            if (srcFile == destFile) return false
            // srcFile doesn't exist or isn't a file then return false
            if (!srcFile.exists() || !srcFile.isFile) return false
            if (destFile.exists()) {
                if (listener == null || listener.onReplaced()) { // require delete the old file
                    if (!destFile.delete()) { // unsuccessfully delete then return false
                        return false
                    }
                } else {
                    return true
                }
            }
            return if (!createDir(destFile.parentFile)) false else try {
                (writeFileFromIS(destFile, FileInputStream(srcFile))
                        && !(isMove && !deleteFile(srcFile)))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                false
            }
        }
        // </editor-fold>


        /**
         * Return the files in directory.
         *
         * @param dirPath     The path of directory.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files in directory
         */
        /**
         * Return the files in directory.
         *
         * Doesn't traverse subdirectories
         *
         * @param dirPath The path of directory.
         * @return the files in directory
         */
        @JvmOverloads
        fun listFilesInDir(dirPath: String, isRecursive: Boolean = false): List<File>? {
            return listFilesInDir(getFile(dirPath), isRecursive)
        }
        /**
         * Return the files in directory.
         *
         * @param dir         The directory.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files in directory
         */
        /**
         * Return the files in directory.
         *
         * Doesn't traverse subdirectories
         *
         * @param dir The directory.
         * @return the files in directory
         */
        @JvmOverloads
        fun listFilesInDir(dir: File, isRecursive: Boolean = false): List<File>? {
            return listFilesInDirWithFilter(dir, { true }, isRecursive)
        }

        /**
         * Return the files that satisfy the filter in directory.
         *
         * Doesn't traverse subdirectories
         *
         * @param dirPath The path of directory.
         * @param filter  The filter.
         * @return the files that satisfy the filter in directory
         */
        fun listFilesInDirWithFilter(dirPath: String,
                                     filter: FileFilter): List<File>? {
            return listFilesInDirWithFilter(getFile(dirPath), filter, false)
        }

        /**
         * Return the files that satisfy the filter in directory.
         *
         * @param dirPath     The path of directory.
         * @param filter      The filter.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files that satisfy the filter in directory
         */
        fun listFilesInDirWithFilter(dirPath: String,
                                     filter: FileFilter,
                                     isRecursive: Boolean): List<File>? {
            return listFilesInDirWithFilter(getFile(dirPath), filter, isRecursive)
        }
        /**
         * Return the files that satisfy the filter in directory.
         *
         * @param dir         The directory.
         * @param filter      The filter.
         * @param isRecursive True to traverse subdirectories, false otherwise.
         * @return the files that satisfy the filter in directory
         */
        /**
         * Return the files that satisfy the filter in directory.
         *
         * Doesn't traverse subdirectories
         *
         * @param dir    The directory.
         * @param filter The filter.
         * @return the files that satisfy the filter in directory
         */
        @JvmOverloads
        fun listFilesInDirWithFilter(dir: File,
                                     filter: FileFilter,
                                     isRecursive: Boolean = false): List<File>? {
            if (!isDir(dir)) return null
            val list: MutableList<File> = ArrayList()
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file in files) {
                    if (filter.accept(file)) {
                        list.add(file)
                    }
                    if (isRecursive && file.isDirectory) {
                        list.addAll(listFilesInDirWithFilter(file, filter, true)!!)
                    }
                }
            }
            return list
        }

        /**
         * Return the time that the file was last modified.
         *
         * @param file The file.
         * @return the time that the file was last modified
         */
        fun getFileLastModified(file: File): Long {
            return file.lastModified()
        }

        /**
         * Return the charset of file simply.
         *
         * @param file The file.
         * @return the charset of file simply
         */
        fun getFileCharset(file: File): String {
            var p = 0
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(file))
                p = (`is`.read() shl 8) + `is`.read()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return when (p) {
                0xefbb -> "UTF-8"
                0xfffe -> "Unicode"
                0xfeff -> "UTF-16BE"
                else -> "GBK"
            }
        }

        /**
         * Return the number of lines of file.
         *
         * @param file The file.
         * @return the number of lines of file
         */
        fun getFileLines(file: File?): Int {
            var count = 1
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(file))
                val buffer = ByteArray(1024)
                var readChars: Int
                if (LINE_SEP.endsWith("\n")) {
                    while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                        for (i in 0 until readChars) {
                            if (buffer[i].toChar() == '\n') ++count
                        }
                    }
                } else {
                    while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                        for (i in 0 until readChars) {
                            if (buffer[i].toChar() == '\r') ++count
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return count
        }

        /**
         * Return the size of directory.
         *
         * @param dir The directory.
         * @return the size of directory
         */
        fun getDirSize(dir: File): String {
            val len = getDirLength(dir)
            return if (len == -1L) "" else byte2FitMemorySize(len)
        }

        /**
         * Return the length of file.
         *
         * @param file The file.
         * @return the length of file
         */
        fun getFileSize(file: File): String {
            val len = getFileLength(file)
            return if (len == -1L) "" else byte2FitMemorySize(len)
        }

        /**
         * Return the deep of directory.
         *
         * @param dir The directory.
         * @return the length of directory
         */
        fun getDirLength(dir: File): Long {
            if (!isDir(dir)) return -1
            var len: Long = 0
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file in files) {
                    len += if (file.isDirectory) {
                        getDirLength(file)
                    } else {
                        file.length()
                    }
                }
            }
            return len
        }

        /**
         * Return the length of file.
         *
         * @param filePath The path of file.
         * @return the length of file
         */
        fun getFileLength(filePath: String): Long {
            val isURL = filePath.matches(Regex("[a-zA-z]+://[^\\s]*"))
            if (isURL) {
                try {
                    val conn = URL(filePath).openConnection() as HttpsURLConnection
                    conn.setRequestProperty("Accept-Encoding", "identity")
                    conn.connect()
                    return if (conn.responseCode == 200) {
                        conn.contentLength.toLong()
                    } else -1
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return getFileLength(getFile(filePath))
        }

        /**
         * Return the length of file.
         *
         * @param file The file.
         * @return the length of file
         */
        fun getFileLength(file: File): Long {
            return if (!isFile(file)) -1 else file.length()
        }

        /**
         * Return the MD5 of file.
         *
         * @param file The file.
         * @return the md5 of file
         */
        fun getFileMD5(file: File?): String {
            return bytes2HexString(getFileMD5Bytes(file))
        }

        private fun getFileMD5Bytes(file: File?): ByteArray? {
            if (file == null) return null
            var dis: DigestInputStream? = null
            try {
                val fis = FileInputStream(file)
                var md = MessageDigest.getInstance("MD5")
                dis = DigestInputStream(fis, md)
                val buffer = ByteArray(1024 * 256)
                while (true) {
                    if (dis.read(buffer) <= 0) break
                }
                md = dis.messageDigest
                return md.digest()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    dis?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

       // <editor-fold defaultstate="collapsed" desc="文件&目录名操作">
        /**
         * Return the file's path of directory.
         *
         * @param file The file.
         * @return the file's path of directory
         */
        fun getDirName(file: File): String {
            return getDirName(file.absolutePath)
        }

        /**
         * Return the file's path of directory.
         *
         * @param filePath The path of file.
         * @return the file's path of directory
         */
        fun getDirName(filePath: String): String {
            if (isSpace(filePath)) return ""
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
        }

        /**
         * Return the name of file.
         *
         * @param file The file.
         * @return the name of file
         */
        fun getFileName(file: File): String {
            return getFileName(file.absolutePath)
        }

        /**
         * Return the name of file.
         *
         * @param filePath The path of file.
         * @return the name of file
         */
        fun getFileName(filePath: String): String {
            if (isSpace(filePath)) return ""
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
        }

        /**
         * Return the name of file without extension.
         *
         * @param file The file.
         * @return the name of file without extension
         */
        fun getFileNameNoExtension(file: File?): String {
            return if (file == null) "" else getFileNameNoExtension(file.path)
        }

        /**
         * Return the name of file without extension.
         *
         * @param filePath The path of file.
         * @return the name of file without extension
         */
        fun getFileNameNoExtension(filePath: String): String {
            if (isSpace(filePath)) return ""
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            if (lastSep == -1) {
                return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
            }
            return if (lastPoi == -1 || lastSep > lastPoi) {
                filePath.substring(lastSep + 1)
            } else filePath.substring(lastSep + 1, lastPoi)
        }

        /**
         * Return the extension of file.
         *
         * @param file The file.
         * @return the extension of file
         */
        fun getFileExtension(file: File): String {
            return getFileExtension(file.path)
        }

        /**
         * Return the extension of file.
         *
         * @param filePath The path of file.
         * @return the extension of file
         */
        fun getFileExtension(filePath: String): String {
            if (isSpace(filePath)) return ""
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
        }
       // </editor-fold>

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////
        private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        private fun bytes2HexString(bytes: ByteArray?): String {
            if (bytes == null) return ""
            val len = bytes.size
            if (len <= 0) return ""
            val ret = CharArray(len shl 1)
            var i = 0
            var j = 0
            while (i < len) {
                ret[j++] = HEX_DIGITS[bytes[i].toInt() shr 4 and 0x0f]
                ret[j++] = HEX_DIGITS[bytes[i].toInt() and 0x0f]
                i++
            }
            return String(ret)
        }

        private fun byte2FitMemorySize(byteNum: Long): String {
            return if (byteNum < 0) {
                "shouldn't be less than zero!"
            } else if (byteNum < 1024) {
                String.format(Locale.getDefault(), "%.3fB", byteNum.toDouble())
            } else if (byteNum < 1048576) {
                String.format(Locale.getDefault(), "%.3fKB", byteNum.toDouble() / 1024)
            } else if (byteNum < 1073741824) {
                String.format(Locale.getDefault(), "%.3fMB", byteNum.toDouble() / 1048576)
            } else {
                String.format(Locale.getDefault(), "%.3fGB", byteNum.toDouble() / 1073741824)
            }
        }

        private fun isSpace(s: String): Boolean {
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        private fun writeFileFromIS(file: File,
                                    `is`: InputStream): Boolean {
            var os: OutputStream? = null
            return try {
                os = BufferedOutputStream(FileOutputStream(file))
                val data = ByteArray(8192)
                var len: Int
                while (`is`.read(data, 0, 8192).also { len = it } != -1) {
                    os.write(data, 0, len)
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * 读取Asset目录下的文件
         * @param fileName  文件名
         * @param assetManager  AssetManager
         * @return 文件字节数组
         * @throws IOException
         */
        @Throws(IOException::class)
        fun loadAssetFile(assetManager: AssetManager, fileName: String?): ByteArray? {
            var input: InputStream? = null
            return try {
                val output = ByteArrayOutputStream()
                input = assetManager.open(fileName)
                val buffer = ByteArray(1024)
                var size: Int
                while (-1 != input.read(buffer).also { size = it }) {
                    output.write(buffer, 0, size)
                }
                output.flush()
                output.toByteArray()
            } catch (e: FileNotFoundException) {
                null
            } finally {
                try {
                    input?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // <editor-fold defaultstate="collapsed" desc="Android 文件API">
        /**
         * 获取外部储存的文件对象
         */
        fun getExternalStorageDirectory(ctx: Context): File {
            return try {
                Environment.getExternalStorageDirectory()
            } catch (e: Throwable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        val storageManager = ctx.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                        val list = storageManager.storageVolumes
                        for (item in list) {
                            val mPath = StorageVolume::class.java.getDeclaredField("mPath")
                            mPath.isAccessible = true
                            val file = mPath[item] as File
                            if (file.exists()) {
                                return file
                            }
                        }
                    } catch (e1: Throwable) {
                        e1.printStackTrace()
                    }
                }
                try {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).parentFile
                } catch (ee: java.lang.Exception) {
                    File("/storage/emulated/0")
                }
            }
        }
        // </editor-fold>
    }
}