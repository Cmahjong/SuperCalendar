package taotao.haoxiong.com.supercalendar.calendar

import android.graphics.Rect

/**
 * desc:点击的数据管理
 * time: 2018/1/30 0030
 * @author yinYin
 */
object TouchManager {
    val monthAllDayBean: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    val monthDayBeanRect: ArrayList<Rect> by lazy {
        ArrayList<Rect>()
    }

}