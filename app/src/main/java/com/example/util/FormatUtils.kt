package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object CurrencyUtils {
    /**
     * Formats an amount according to the Indian numbering system:
     * e.g., ₹5,000 | ₹25,000 | ₹1,25,000 | ₹10,50,000
     */
    fun formatInr(amount: Double, showDecimals: Boolean = false, includeSign: Boolean = false): String {
        val isNegative = amount < 0
        val absAmount = abs(amount)
        val wholePart = absAmount.toLong()
        val decimalPart = ((absAmount - wholePart) * 100).toLong()

        val formattedWhole = formatIndianNumber(wholePart)
        val decimalStr = if (showDecimals && decimalPart > 0) {
            String.format(Locale.US, ".%02d", decimalPart)
        } else {
            ""
        }

        val prefix = when {
            isNegative -> "-₹"
            includeSign && amount > 0 -> "+₹"
            else -> "₹"
        }
        return "$prefix$formattedWhole$decimalStr"
    }

    private fun formatIndianNumber(number: Long): String {
        val str = number.toString()
        if (str.length <= 3) return str
        val lastThree = str.substring(str.length - 3)
        val remaining = str.substring(0, str.length - 3)
        val sb = StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            sb.append(remaining[i])
            count++
            if (count == 2 && i != 0) {
                sb.append(',')
                count = 0
            }
        }
        return sb.reverse().toString() + "," + lastThree
    }
}

enum class DateFilterOption(val label: String) {
    ALL_TIME("All Time"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This Week"),
    LAST_WEEK("Last Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    CUSTOM("Custom Range")
}

object DateUtils {
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val shortDateFormat = SimpleDateFormat("dd MMM", Locale.US)
    private val fullDateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun formatDate(millis: Long): String = displayDateFormat.format(Date(millis))
    fun formatShortDate(millis: Long): String = shortDateFormat.format(Date(millis))
    fun formatDateTime(millis: Long): String = fullDateTimeFormat.format(Date(millis))
    fun formatIsoDate(millis: Long): String = isoDateFormat.format(Date(millis))

    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun endOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun getRangeForFilter(
        option: DateFilterOption,
        customStart: Long? = null,
        customEnd: Long? = null,
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        return when (option) {
            DateFilterOption.ALL_TIME -> 0L to Long.MAX_VALUE
            DateFilterOption.TODAY -> startOfDay(nowMillis) to endOfDay(nowMillis)
            DateFilterOption.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                startOfDay(cal.timeInMillis) to endOfDay(cal.timeInMillis)
            }
            DateFilterOption.THIS_WEEK -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val start = startOfDay(cal.timeInMillis)
                cal.add(Calendar.DAY_OF_YEAR, 6)
                val end = endOfDay(cal.timeInMillis)
                start to end
            }
            DateFilterOption.LAST_WEEK -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.add(Calendar.DAY_OF_YEAR, -7)
                val start = startOfDay(cal.timeInMillis)
                cal.add(Calendar.DAY_OF_YEAR, 6)
                val end = endOfDay(cal.timeInMillis)
                start to end
            }
            DateFilterOption.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = startOfDay(cal.timeInMillis)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = endOfDay(cal.timeInMillis)
                start to end
            }
            DateFilterOption.LAST_MONTH -> {
                cal.add(Calendar.MONTH, -1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = startOfDay(cal.timeInMillis)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = endOfDay(cal.timeInMillis)
                start to end
            }
            DateFilterOption.CUSTOM -> {
                val s = customStart?.let { startOfDay(it) } ?: 0L
                val e = customEnd?.let { endOfDay(it) } ?: Long.MAX_VALUE
                s to e
            }
        }
    }

    fun getStartOfWeekMonday(referenceMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = referenceMillis
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        return startOfDay(cal.timeInMillis)
    }

    fun getMonthRange(year: Int, monthZeroIndexed: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthZeroIndexed)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val start = startOfDay(cal.timeInMillis)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = endOfDay(cal.timeInMillis)
        return start to end
    }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val weekDayNames = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )
}
