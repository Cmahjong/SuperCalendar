package taotao.haoxiong.com.supercalendar.calendar

import java.util.*

/**
 * desc:Calendar工具类
 * time: 2018/1/23 0023
 * @author yinYin
 */
object CalendarUtil {

    fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month-1) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    fun getFirstDayWeek(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month-1, 1)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
}