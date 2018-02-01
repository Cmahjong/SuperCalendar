package taotao.haoxiong.com.supercalendar.calendar

/**
 * desc:数据管理类
 * time: 2018/1/30 0030
 * @author yinYin
 */
object DataManger {
    /**按月或者季买已买的日期列表（只存开始日期）*/
    val selectedDayByMonthOrSeason: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /**按月或者季买买正在买的日期列表（只存开始日期）*/
    val selectingDayByMonthOrSeason: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /**用户选择的类型*/
    var useBuyType: BuyType = BuyType.DAY
    /**按天买已买的日期列表*/
    val selectedDateByDay: java.util.ArrayList<DayBean> by lazy {
        java.util.ArrayList<DayBean>()
    }
    /**按天买正在买的日期列表*/
    val selectingDateByDay: java.util.ArrayList<DayBean> by lazy {
        java.util.ArrayList<DayBean>()
    }
}