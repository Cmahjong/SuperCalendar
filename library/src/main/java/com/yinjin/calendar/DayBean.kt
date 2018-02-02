package com.yinjin.calendar

/**
 * desc:存储时间的
 * time: 2018/1/24 0024
 * @author yinYin
 */
class DayBean(var year: Int? = 0,
              var month: Int? = 0,
              var day: Int? = 0,
              var type: BuyType? = BuyType.DAY) {
    override fun equals(other: Any?): Boolean {
        val bean = other as DayBean
        return bean.year == year && bean.month == month && bean.day == day

    }
}
