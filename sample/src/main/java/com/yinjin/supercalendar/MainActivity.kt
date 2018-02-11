package com.yinjin.supercalendar

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.yinjin.calendar.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    /** 记录上一次位置，判断是左滑还是右滑  */
    private var lastPosition = -1
    /** 月份的view  */
    private val views: ArrayList<MonthView> by lazy {
        ArrayList<MonthView>().apply {
            add(monthView)
        }
    }
    private val monthView: MonthView by lazy {
        MonthView(this).apply {
            id = 0
            layoutParams = ViewGroup.LayoutParams(month_route_view_pager.width, ViewGroup.LayoutParams.MATCH_PARENT)
            monthViewClick = object : MonthViewClick {
                override fun click(dayBean: DayBean, buyType: BuyType, position: Int) {
                    Toast.makeText(this@MainActivity, dayBean.month.toString() + "月" + dayBean.day + "号", Toast.LENGTH_SHORT).show()


                }

                override fun unClick(dayBean: DayBean, buyType: BuyType, message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
            refreshView()
        }
    }

    /** 适配器 */
    private val pageAdapter: PagerAdapter by lazy {
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
                    view.monthViewClick = object : MonthViewClick {
                        override fun click(dayBean: DayBean, buyType: BuyType, position: Int) {
                            Toast.makeText(this@MainActivity, dayBean.month.toString() + "月" + dayBean.day + "号", Toast.LENGTH_SHORT).show()

                        }

                        override fun unClick(dayBean: DayBean, buyType: BuyType, message: String) {
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    views.add(view)
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
//        DataManger.selectingDateByDay.add(DayBean(2018, 1, 11))
//        DataManger.selectingDateByDay.add(DayBean(2018, 1, 14))
//        DataManger.selectingDateByDay.add(DayBean(2018, 1, 13))
//        DataManger.selectedDateByDay.add(DayBean(2018, 1, 26))
//        DataManger.selectedDateByDay.add(DayBean(2018, 1, 28))
//        DataManger.selectedDateByDay.add(DayBean(2018, 2, 1))
//        DataManger.selectingDateByDay.add(DayBean(2018, 4, 12))
//        DataManger.selectingDateByDay.add(DayBean(2018, 4, 11))
//        DataManger.selectingDateByDay.add(DayBean(2018, 4, 14))
//        DataManger.selectingDateByDay.add(DayBean(2018, 4, 13))
//        DataManger.selectedDateByDay.add(DayBean(2018, 6, 26))
//        DataManger.selectedDateByDay.add(DayBean(2018, 6, 28))
//        DataManger.selectedDateByDay.add(DayBean(2018, 6, 1))
        DataManger.selectedDayByMonthOrSeason.add(DayBean(2018, 1, 14, BuyType.MONTH))
        DataManger.selectedDayByMonthOrSeason.add(DayBean(2018, 3, 14, BuyType.MONTH))
        DataManger.selectedDayByMonthOrSeason.add(DayBean(2018, 5, 14, BuyType.MONTH))

        tv_selected_time.text = Calendar.getInstance().get(Calendar.YEAR).toString() + "年" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString() + "月"
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

        rb_type_quarter.setOnClickListener {
            if (DataManger.useBuyType != BuyType.SEASON) {
                DataManger.useBuyType = BuyType.SEASON
                if (!DataManger.selectingDateByDay.isEmpty() || !DataManger.selectingDayByMonthOrSeason.isEmpty()) {
                    DataManger.selectingDateByDay.clear()
                    DataManger.selectingDayByMonthOrSeason.clear()
                }
            }
            views[lastPosition].refreshView()

        }
        rb_type_month.setOnClickListener {
            if (DataManger.useBuyType != BuyType.MONTH) {
                DataManger.useBuyType = BuyType.MONTH
                if (!DataManger.selectingDateByDay.isEmpty() || !DataManger.selectingDayByMonthOrSeason.isEmpty()) {
                    DataManger.selectingDateByDay.clear()
                    DataManger.selectingDayByMonthOrSeason.clear()
                }
            }
            views[lastPosition].refreshView()

        }
        rb_type_day.setOnClickListener {
            if (DataManger.useBuyType != BuyType.DAY) {
                DataManger.useBuyType = BuyType.DAY
                if (!DataManger.selectingDateByDay.isEmpty() || !DataManger.selectingDayByMonthOrSeason.isEmpty()) {
                    DataManger.selectingDateByDay.clear()
                    DataManger.selectingDayByMonthOrSeason.clear()
                }
            }
            views[lastPosition].refreshView()

        }
        down.setOnClickListener {
            DataManger.selectedDateByDay.addAll(DataManger.selectingDateByDay)
            DataManger.selectedDayByMonthOrSeason.addAll(DataManger.selectingDayByMonthOrSeason)
            DataManger.selectingDateByDay.clear()
            DataManger.selectingDayByMonthOrSeason.clear()
            views[lastPosition].refreshView()
        }
        img_month_pre.setOnClickListener {
            //为0，表示当前月份，不能点击上一个月的
            if (month_route_view_pager.currentItem == 0) {
                return@setOnClickListener
            }
            month_route_view_pager.currentItem = lastPosition - 1
        }
        //选择下一个月
        img_month_next.setOnClickListener {
            month_route_view_pager.currentItem = lastPosition + 1
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
        tv_selected_time.text = monthView2.year.toString() + "年" + monthView2.month.toString() + "月"
        //控制draw的频率

//            monthView2.selectingDateByDay.add(DayBean(2018, 1, 12))
        monthView2.refreshView()
//        if (position > lastPosition && monthView2.isDraw) {
//            monthView2.refreshView()
//            monthView2.isDraw = false
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        App.app?.mRefWatcher?.watch(this)
    }

}
