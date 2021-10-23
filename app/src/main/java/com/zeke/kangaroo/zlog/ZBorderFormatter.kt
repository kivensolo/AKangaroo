package com.zeke.kangaroo.zlog

import com.elvishew.xlog.formatter.border.BorderFormatter
import com.elvishew.xlog.internal.SystemCompat

/**
 * String segments wrapped with borders look like:
 * <br>┌────────────────────────────────────────────────────────────────────────────
 * <br>│String segment 1
 * <br>├────────────────────────────────────────────────────────────────────────────
 * <br>│String segment 2
 * <br>├────────────────────────────────────────────────────────────────────────────
 * <br>│String segment 3
 * <br>└────────────────────────────────────────────────────────────────────────────
 */
class ZBorderFormatter : BorderFormatter {

    companion object {
        private const val VERTICAL_BORDER_CHAR = '│'

        // Length: 100.
        private const val TOP_HORIZONTAL_BORDER = "┌────────────────────────────────────────────────────────" +
                "────────────────────────────────────────────────────────"

        // Length: 99.
        private const val DIVIDER_HORIZONTAL_BORDER = "├────────────────────────────────────────────────────────" +
                "────────────────────────────────────────────────────────"

        // Length: 100.
        private const val BOTTOM_HORIZONTAL_BORDER = "└────────────────────────────────────────────────────────" +
                "────────────────────────────────────────────────────────"

        /**
         * Add {@value #VERTICAL_BORDER_CHAR} to each line of msg.
         *
         * @param msg the message to add border
         * @return the message with {@value #VERTICAL_BORDER_CHAR} in the start of each line
         */
        private fun appendVerticalBorder(msg: String?): String {
            val borderedMsgBuilder = StringBuilder(msg!!.length + 10)
            val lines = msg.split(SystemCompat.lineSeparator).toTypedArray()
            var i = 0
            val N = lines.size
            while (i < N) {
                if (i != 0) {
                    borderedMsgBuilder.append(SystemCompat.lineSeparator)
                }
                val line = lines[i]
                borderedMsgBuilder.append(VERTICAL_BORDER_CHAR).append(line)
                i++
            }
            return borderedMsgBuilder.toString()
        }
    }

    override fun format(segments: Array<String?>): String {
        if (segments.isEmpty()) {
            return ""
        }
        val nonNullSegments = arrayOfNulls<String>(segments.size)
        var nonNullCount = 0
        for (segment in segments) {
            if (segment != null) {
                nonNullSegments[nonNullCount++] = segment
            }
        }
        if (nonNullCount == 0) {
            return ""
        }
        val msgBuilder = StringBuilder()
        msgBuilder.append(TOP_HORIZONTAL_BORDER).append(SystemCompat.lineSeparator)
        for (i in 0 until nonNullCount) {
            msgBuilder.append(appendVerticalBorder(nonNullSegments[i]))
            if (i != nonNullCount - 1) {
                msgBuilder.append(SystemCompat.lineSeparator).append(DIVIDER_HORIZONTAL_BORDER)
                    .append(SystemCompat.lineSeparator)
            } else {
                msgBuilder.append(SystemCompat.lineSeparator).append(BOTTOM_HORIZONTAL_BORDER)
            }
        }
        return msgBuilder.toString()
    }

}
