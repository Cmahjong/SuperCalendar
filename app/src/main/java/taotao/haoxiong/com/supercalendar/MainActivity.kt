package taotao.haoxiong.com.supercalendar

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import taotao.haoxiong.com.supercalendar.calendar.BuyType
import taotao.haoxiong.com.supercalendar.calendar.DataManger
import taotao.haoxiong.com.supercalendar.calendar.DayBean
import taotao.haoxiong.com.supercalendar.calendar.MonthView
import java.util.*

class MainActivity : AppCompatActivity() {
    /** 记录上一次位置，判断是左滑还是右滑  */
    private var lastPosition = -1
    /** 月份的view  */
    private val views: ArrayList<MonthView> by lazy {
        ArrayList<MonthView>()
    }
    val currentYear by lazy {
        Calendar.getInstance().get(Calendar.YEAR)
    }
    val currentMonth by lazy {
        Calendar.getInstance().get(Calendar.MONTH) + 1
    }
    val currentDay by lazy {
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }
    var refreshMinPosition = 0

    /** 适配器 */
    val pageAdapter: PagerAdapter by lazy {
        object : PagerAdapter() {
            override fun getCount(): Int {
                return 100
            }

            override fun getItemPosition(`object`: Any?): Int {
                return super.getItemPosition(`object`)
            }

            override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
                container?.removeView(views[position])

            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                var view: MonthView?
                try {
                    view = views[position]
                } catch (e: Exception) {
                    view = MonthView(this@MainActivity)
                    if (views[position - 1].month == 12) {
                        view.year = views[position - 1].year + 1
                        view.month = 1
                    } else {
                        view.month = views[position - 1].month + 1
                    }
                    view.id = position
                    view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    views.add(view)
                    refreshMinPosition = position
                }
                if (view!!.parent === container) {
                    container.removeView(view)
                }
                container.addView(view)
                return view!!

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        assignView()
    }

    private fun assignView() {
        DataManger.selectedDayByMonthOrSeason.add(DayBean(2018, 1, 14, BuyType.MONTH))
        DataManger.selectedDayByMonthOrSeason.add(DayBean(2018, 3, 14, BuyType.MONTH))
        DataManger.selectedDayByMonthOrSeason.add(DayBean(2018, 5, 14, BuyType.MONTH))
        //预先加载三个
        val monthView = MonthView(this)
        monthView.id = 0
        monthView.layoutParams = ViewGroup.LayoutParams(month_route_view_pager.width, ViewGroup.LayoutParams.MATCH_PARENT)
        monthView.refreshView()
        views.add(monthView)
        textView.text = Calendar.getInstance().get(Calendar.YEAR).toString() + "年" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString() + "月"
        month_route_view_pager.apply {
            adapter = pageAdapter
            lastPosition = 0

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    switchMonth(lastPosition, position)
                    lastPosition = position
                }
            })
        }

        textView2.setOnClickListener {
            val monthView = views[lastPosition]
            DataManger.selectingDayByMonthOrSeason.clear()
            DataManger.selectingDayByMonthOrSeason.add(DayBean(2018, 7, 14, BuyType.MONTH))
            monthView.refreshView()
            for (i in lastPosition until views.size - 1) {
                views[i].isDraw=true
            }
        }
    }

    private fun switchMonth(lastPosition: Int, position: Int) {
        if (-1 == lastPosition) {
            return
        }
        val monthView1 = views[lastPosition]
        val monthView2 = views[position]
        if (lastPosition < position) {
            //说明往右滑动
            if (monthView1.month == 12) {
                monthView2.year = monthView1.year + 1
                monthView2.month = 1
            } else {
                monthView2.year = monthView1.year
                monthView2.month = monthView1.month + 1
            }
        } else if (lastPosition > position) {
            //说明往左滑动
            if (monthView1.month == 1) {
                monthView2.year = monthView1.year - 1
                monthView2.month = 12
            } else {
                monthView2.year = monthView1.year
                monthView2.month = monthView1.month - 1
            }
        }
        textView.text = monthView2.year.toString() + "年" + monthView2.month.toString() + "月"
        //控制draw的频率

//            monthView2.selectingDateByDay.add(DayBean(2018, 1, 12))
//            monthView2.selectingDateByDay.add(DayBean(2018, 1, 11))
//            monthView2.selectingDateByDay.add(DayBean(2018, 1, 14))
//            monthView2.selectingDateByDay.add(DayBean(2018, 1, 13))
//            monthView2.selectedDateByDay.add(DayBean(2018, 1, 26))
//            monthView2.selectedDateByDay.add(DayBean(2018, 1, 28))
//            monthView2.selectedDateByDay.add(DayBean(2018, 2, 1))
//        monthView2.selectingDateByDay.add(DayBean(2018, 4, 12))
//        monthView2.selectingDateByDay.add(DayBean(2018, 4, 11))
//        monthView2.selectingDateByDay.add(DayBean(2018, 4, 14))
//        monthView2.selectingDateByDay.add(DayBean(2018, 4, 13))
//        monthView2.selectedDateByDay.add(DayBean(2018, 6, 26))
//        monthView2.selectedDateByDay.add(DayBean(2018, 6, 28))
//        monthView2.selectedDateByDay.add(DayBean(2018, 6, 1))
        if (position > lastPosition && monthView2.isDraw) {
            monthView2.refreshView()
            monthView2.isDraw = false
        }
    }
}
