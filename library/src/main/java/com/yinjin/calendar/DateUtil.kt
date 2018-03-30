package com.yinjin.calendar

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * desc:数据util主要用来时间对比
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
            in (startDateTime + 1)..(endTime!! - 1) -> when {
                dateTime < currentDate -> //在区间并且过期
                    1
                dateTime == currentDate -> //在区间未过期且为当前日期
                    2
                else -> //在区间未过期且不为当前日期
                    3
            }
            startDateTime -> when {
                startDateTime < currentDate -> //在起始位置并且过期
                    4
                startDateTime == currentDate -> //在起始位置并且未过期且为当前日期
                    5
                else -> //在起始位置并且未过期且不为当前日期
                    6
            }
            endTime -> when {
                endTime < currentDate -> //在结束位置并且过期
                    7
                endTime == currentDate -> //在结束位置并且未过期且为当前时间
                    8
                else -> //在结束位置并且未过期且为不当前时间
                    9
            }
            else -> 10
        }
    }

    /**
     *是否可以选择
     */
    fun isSelect(dayBean: DayBean): Boolean {
        val dateTime = dayBean2Date(dayBean)!!.time
        DataManger.selectedDayByMonthOrSeason.forEach {
            val startDateTime = dayBean2Date(it)!!.time
            var endTime: Long? = 100L
            when (it.type) {
                BuyType.MONTH -> {
                    endTime = startDateTime + 29 * 24 * 60 * 60 * 1000L
                }
                BuyType.SEASON -> {
                    endTime = startDateTime + 89 * 24 * 60 * 60 * 1000L
                }
            }
            if (dateTime >= startDateTime && dateTime <= endTime!!) {
                return false
            }
        }
        return true
    }

    /**
     *是否可以选择
     */
    fun isSelect2ByMonth(dayBean: DayBean): Boolean {
        val dateTime = dayBean2Date(dayBean)!!.time
        var dateEndTime = dayBean2Date(dayBean)!!.time
        when (DataManger.useBuyType) {
            BuyType.MONTH -> {
                dateEndTime = dateTime + 29 * 24 * 60 * 60 * 1000L
            }
            BuyType.SEASON -> {
                dateEndTime = dateTime + 89 * 24 * 60 * 60 * 1000L
            }
        }
        DataManger.selectedDayByMonthOrSeason.forEach {
            val startDateTime = dayBean2Date(it)!!.time
            var endTime: Long? = 100L
            when (it.type) {
                BuyType.MONTH -> {
                    endTime = startDateTime + 29 * 24 * 60 * 60 * 1000L
                }
                BuyType.SEASON -> {
                    endTime = startDateTime + 89 * 24 * 60 * 60 * 1000L
                }
            }
            if (dateTime >= startDateTime && dateTime <= endTime!!) {
                return false
            }
            if (dateEndTime >= startDateTime && dateEndTime <= endTime!!) {
                return false
            }
            if (dateTime < startDateTime && dateEndTime > endTime!!) {
                return false
            }

        }
        return true
    }

    /**
     *是否可以选择
     * @param dayBean 按月买起始日期
     */
    fun isSelectByMonth(dayBean: DayBean): Boolean {
        val startDateTime = dayBean2Date(dayBean)!!.time
        DataManger.selectedDateByDay.forEach {
            val dateTime = dayBean2Date(it)!!.time
            var endTime: Long? = 100L
            when (DataManger.useBuyType) {
                BuyType.MONTH -> {
                    endTime = startDateTime + 29 * 24 * 60 * 60 * 1000L
                }
                BuyType.SEASON -> {
                    endTime = startDateTime + 89 * 24 * 60 * 60 * 1000L
                }
            }
            if (dateTime >= startDateTime && dateTime <= endTime!!) {
                return false
            }
        }
        return true
    }

    /**
     *是否可以选择
     * @param dayBean 按月买起始日期
     */
    fun isCanSelect(currentDayBean: DayBean, clickBuyDayBean: DayBean): Boolean {
        val clickDateTime = dayBean2Date(clickBuyDayBean)!!.time
        var endTime = dayBean2Date(currentDayBean)!!.time + 90 * 24 * 60 * 60 * 1000L
        if (clickDateTime > endTime) {
            return false
        }
        return true
    }

    private fun dayBean2Date(dayBean: DayBean) = str2Date(dayBean.year.toString() + "-" + dayBean.month.toString() + "-" + dayBean.day.toString())
}