package taotao.haoxiong.com.supercalendar.calendar

/**
 * desc:
 * time: 2018/1/30 0030
 * @author yinYin
 */
object DataManger {
    val selectedDayByMonthOrSeason: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    val selectingDayByMonthOrSeason: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
}