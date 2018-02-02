package yinjin.calendar.com.calendar

import android.graphics.Rect
import yinjin.calendar.com.calendar.DayBean

/**
 * desc:点击的数据管理
 * time: 2018/1/30 0030
 * @author yinYin
 */
object TouchManager {
    /** 当前月所有的DayBean */
    val monthAllDayBean: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /** 当前月所有的Rect */
    val monthDayBeanRect: ArrayList<Rect> by lazy {
        ArrayList<Rect>()
    }

}