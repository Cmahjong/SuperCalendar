package taotao.haoxiong.com.supercalendar.calendar

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * desc:
 * time: 2018/1/25 0025
 * @author yinYin
 */
object DateUtil {
    private const val FORMAT_SHORT = "yyyy-MM-dd"
    /**
     * templete: FORMAT_STR -> Date
     * @param str
     * 没时间写
     */
    fun str2Date(str: String): Date? {
        if (str.isEmpty()) {
            return null
        }
        val simpleDateFormat = SimpleDateFormat(FORMAT_SHORT)
        var date: Date? = null
        try {
            date = simpleDateFormat.parse(str)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     *日期是否在该区间
     * @return 1:在区间并且过期 ；2在区间未过期且为当前日期；3在区间未过期且不为当前日期；
     * 4在起始位置并且过期，5在起始位置并且未过期且为当前日期，6在起始位置并且未过期且不为当前日期，
     * 7在结束位置并且过期，8在结束位置并且未过期且为当前时间，9在结束位置并且未过期且为不当前时间
     */
    fun compareTwoDayBean(dayBean: DayBean, startDayBean: DayBean, currentDayBean: DayBean?, buyType: BuyType): Int {
        val dateTime = dayBean2Date(dayBean)!!.time
        val startDateTime = dayBean2Date(startDayBean)!!.time
        val currentDate = dayBean2Date(currentDayBean!!)!!.time
        var endTime: Long? = 100L
        when (buyType) {
            BuyType.MONTH -> {
                endTime = startDateTime + 29 * 24 * 60 * 60 * 1000L
            }
            BuyType.SEASON -> {
                endTime = startDateTime + 89 * 24 * 60 * 60 * 1000L
            }
        }

        return when (dateTime) {
            in (startDateTime + 1)..(endTime!! - 1) -> if (dateTime < currentDate) {
                //在区间并且过期
                1
            } else if (dateTime == currentDate) {
                //在区间未过期且为当前日期
                2
            } else {
                //在区间未过期且不为当前日期
                3
            }
            startDateTime -> if (startDateTime < currentDate) {
                //在起始位置并且过期
                4
            } else if (startDateTime == currentDate) {
                //在起始位置并且未过期且为当前日期
                5
            } else {
                //在起始位置并且未过期且不为当前日期
                6
            }
            endTime -> if (endTime < currentDate) {
                //在结束位置并且过期
                7
            } else if (endTime == currentDate) {
                //在结束位置并且未过期且为当前时间
                8
            } else {
                //在结束位置并且未过期且为不当前时间
                9
            }
            else -> 10
        }
    }

    /**
     *日期是否在该区间
     * @return 1:在区间并且过期 ；2在区间未过期且为当前日期；3在区间未过期且不为当前日期；
     * 4在起始位置并且过期，5在起始位置并且未过期且为当前日期，6在起始位置并且未过期且不为当前日期，
     * 7在结束位置并且过期，8在结束位置并且未过期且为当前时间，9在结束位置并且未过期且为不当前时间
     */
    fun compareTwoDayBean(dayBean: DayBean, startDayBean: DayBean, buyType: BuyType): Boolean {
        val dateTime = dayBean2Date(dayBean)!!.time
        val startDateTime = dayBean2Date(startDayBean)!!.time
        var endTime: Long? = 100L
        when (buyType) {
            BuyType.MONTH -> {
                endTime = startDateTime + 29 * 24 * 60 * 60 * 1000L
            }
            BuyType.SEASON -> {
                endTime = startDateTime + 89 * 24 * 60 * 60 * 1000L
            }
        }
        return dateTime < startDateTime || dateTime > endTime!!
    }

    fun dayBean2Date(dayBean: DayBean) = str2Date(dayBean.year.toString() + "-" + dayBean.month.toString() + "-" + dayBean.day.toString())
}