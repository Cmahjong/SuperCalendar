package com.yinjin.calendar

import android.content.Context
import android.graphics.*
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


/**
 * desc:月份控件
 * time: 2018/1/23 0023
 * @author yinYin
 */
class MonthView : View {
    /** 需要画的周末文字 */
    val weeks by lazy {
        arrayListOf(
                "日", "一", "二", "三", "四", "五", "六"
        )
    }

    /**选择过的日期过期的的图片*/
    val selectedDateOverdueBitmap: Bitmap by lazy {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_selected_date)
    }
    /**选择过的日期未过期的的图片*/
    val selectedDateBitmap: Bitmap  by lazy {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_selected_date_doing)
    }
    val selectingDateBitmap: Bitmap   by lazy {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_selecting)
    }
    /** view的宽度 */
    var viewWidth: Int = 1080
    /** view的高度 */
    /** 当前日期日份 */
    var currentDay: Int = 1
    /** 当前日期月份 */
    var currentMonth: Int = 1
    /** 当前日期年份 */
    var currentYear: Int = 2018
    /** 选择的月份 */
    var month: Int = 1
    /** 选择的年份 */
    var year: Int = 2018
    /** 画周末的画笔 */
    val weekPaint by lazy {
        Paint().apply {
            isFakeBoldText = false
            isAntiAlias = true
            textSize = weekTextSize
            typeface = Typeface.DEFAULT
            color = workingColor
            style = Paint.Style.FILL
        }
    }
    /**买的状态  //过期，未过期，当前时间*/
    lateinit var buyState: BuyState
    /**天的状态//不可用，当前，可用*/
    lateinit var dayState: DayState
    /** 画日期（天）的画笔 */
    val dayPaint by lazy {
        Paint().apply {
            isFakeBoldText = false
            isAntiAlias = true
            textSize = normalDayTextSize
            typeface = Typeface.DEFAULT
            color = normalDayTextColor
            style = Paint.Style.FILL
        }
    }
    /** 画圈的画笔 */
    val circlePaint by lazy {
        Paint().apply {
            isFakeBoldText = false
            isAntiAlias = true
            textSize = selectedDateRadius
            typeface = Typeface.DEFAULT
            color = selectedDateColor
            style = Paint.Style.FILL
        }
    }
    /** 画矩形的画笔 */
    val rectPaint by lazy {
        Paint().apply {
            isFakeBoldText = false
            isAntiAlias = true
            textSize = 10f
            typeface = Typeface.DEFAULT
            color = selectedDateColor
            style = Paint.Style.FILL
        }
    }
    /** 画圆弧的画笔 */
    val arcPaint by lazy {
        Paint().apply {
            isFakeBoldText = false
            isAntiAlias = true
            textSize = 1.0f
            typeface = Typeface.DEFAULT
            color = selectingLineColor
            style = Paint.Style.STROKE
        }
    }
    /** 画线的画笔 */
    val linePaint by lazy {
        Paint().apply {
            isFakeBoldText = false
            isAntiAlias = true
            textSize = 1.0f
            typeface = Typeface.DEFAULT
            color = selectingLineColor
            style = Paint.Style.FILL
        }
    }
    /**要绘制的位图的子集  */
    val src: Rect by lazy {
        Rect(0, 0, selectedDateOverdueBitmap.width, selectedDateOverdueBitmap.height)
    }


    /**  未选中文字大小*/
    var normalDayTextSize: Float = context.resources.getDimension(R.dimen.normalDayTextSize)
    /**未选中文字颜色 */
    var normalDayTextColor: Int = ContextCompat.getColor(context, R.color.normal_color)

    /**  选中文字大小*/
    var selectedDayTextSize: Float = context.resources.getDimension(R.dimen.selectedDayTextSize)
    /**  选中文字颜色*/
    var selectedDayColor: Int = ContextCompat.getColor(context, R.color.selected_color)

    /**  周末颜色*/
    var weekendColor: Int = ContextCompat.getColor(context, R.color.weekend_color)
    /** 工作日颜色 */
    var workingColor: Int = ContextCompat.getColor(context, R.color.working_color)
    /** 星期文字大小 */
    var weekTextSize: Float = context.resources.getDimension(R.dimen.weekTextSize)
    /** 天数距离星期天的距离 */
    var dayMarinWeekSize: Float = context.resources.getDimension(R.dimen.dayMarinWeekSize)
    /** 不能点击使用的颜色 */
    var unEnableColor: Int = ContextCompat.getColor(context, R.color.unEnable_color)
    /** 选择过的圈的未过期的背景颜色 */
    var selectedDateColor: Int = ContextCompat.getColor(context, R.color.selected_date_color)
    /** 选择中的背景的颜色 */
    var selectingDateColor: Int = ContextCompat.getColor(context, R.color.selecting_date_color)
    /** 选择过的圈的过期的背景颜色 */
    var selectedDateOverdueColor: Int = ContextCompat.getColor(context, R.color.selected_date_overdue_color)
    /** 当前日期的颜色 */
    var currentDayColor: Int = ContextCompat.getColor(context, R.color.current_day_color)
    /** 正在选中的线的颜色 */
    var selectingLineColor: Int = ContextCompat.getColor(context, R.color.selecting_line_color)
    /** 选择过的圈的半径大小 */
    var selectedDateRadius: Float = context.resources.getDimension(R.dimen.selectedDateRadius)

    /** 行高 */
    var lineHeight: Float = context.resources.getDimension(R.dimen.lineHeight)
    /** 标题的高度 */
    var titleHeight: Float = context.resources.getDimension(R.dimen.titleHeight)
    /** 图片距离圆心位置 */
    var bitmapMarginCircleCenter: Float = context.resources.getDimension(R.dimen.bitmapMarginCircleCenter)


    /** 当前的日期 */
    var currentDayBean: DayBean? = null
    /** touchEvent down的X位置 */
    var startX: Float = 0f
    /** touchEvent up的Y位置 */
    var startY: Float = 0f
    /** monthView的点击事件 */
    var monthViewClick: MonthViewClick? = null
    /** 每一个天的对象 */

    /** 画周的每一个对象 */
    val drawWeekList: ArrayList<WeekBean> by lazy { ArrayList<WeekBean>() }
    /** 画天的每一个对象 */
    val circleBitmapBeanListByDay: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanListByMonth: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanListBySelectingDay: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanListBySelectingMonth: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanDay: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    /**按断是否需要重新drawView，减少draw的次数，提升性能*/
    var isDraw = true
    /**添加刷新判断，防止异步情况下载进行drawView*/
    var isRefresh = false
    /** 判断是否可以出发touchEvent */
    var isEnableTouch = false
    /** view的高度 */
    var viewHeight: Int = 0

    constructor(context: Context) : super(context) {
        initData(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, @Nullable attrs: AttributeSet) {
        initData(context)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MonthView)
        normalDayTextSize = attributes.getDimension(R.styleable.MonthView_normalTextSize, context.resources.getDimension(R.dimen.normalDayTextSize))
        dayMarinWeekSize = attributes.getDimension(R.styleable.MonthView_normalTextSize, context.resources.getDimension(R.dimen.dayMarinWeekSize))
        normalDayTextColor = attributes.getColor(R.styleable.MonthView_normalTextColor, ContextCompat.getColor(context, R.color.normal_color))
        selectedDayTextSize = attributes.getDimension(R.styleable.MonthView_selectedTextSize, context.resources.getDimension(R.dimen.selectedDayTextSize))
        selectedDayColor = attributes.getColor(R.styleable.MonthView_selectedTextColor, ContextCompat.getColor(context, R.color.selected_color))
        selectedDateOverdueColor = attributes.getColor(R.styleable.MonthView_selectedDateOverdueColor, ContextCompat.getColor(context, R.color.selected_date_overdue_color))
        weekendColor = attributes.getColor(R.styleable.MonthView_weekendColor, ContextCompat.getColor(context, R.color.weekend_color))
        workingColor = attributes.getColor(R.styleable.MonthView_workingColor, ContextCompat.getColor(context, R.color.working_color))
        weekTextSize = attributes.getDimension(R.styleable.MonthView_weekTextSize, context.resources.getDimension(R.dimen.weekTextSize))
        unEnableColor = attributes.getColor(R.styleable.MonthView_unEnableTextColor, ContextCompat.getColor(context, R.color.unEnable_color))
        selectedDateColor = attributes.getColor(R.styleable.MonthView_selectedDateColor, ContextCompat.getColor(context, R.color.selected_date_color))
        selectedDateRadius = attributes.getDimension(R.styleable.MonthView_selectedDateRadius, context.resources.getDimension(R.dimen.selectedDateRadius))
        currentDayColor = attributes.getColor(R.styleable.MonthView_currentDayColor, ContextCompat.getColor(context, R.color.current_day_color))
        selectingLineColor = attributes.getColor(R.styleable.MonthView_selectingLineColor, ContextCompat.getColor(context, R.color.selecting_line_color))
        selectingDateColor = attributes.getColor(R.styleable.MonthView_selectingDateColor, ContextCompat.getColor(context, R.color.selecting_date_color))
        lineHeight = attributes.getDimension(R.styleable.MonthView_lineHeight, context.resources.getDimension(R.dimen.lineHeight))
        titleHeight = attributes.getDimension(R.styleable.MonthView_titleHeight, context.resources.getDimension(R.dimen.titleHeight))
        bitmapMarginCircleCenter = attributes.getDimension(R.styleable.MonthView_bitmapMarginCircleCenter, context.resources.getDimension(R.dimen.bitmapMarginCircleCenter))
    }

    /**
     * 初始化数据
     */
    private fun initData(context: Context) {
        val point = Point()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
        viewWidth = point.x
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
        year = currentYear
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        month = currentMonth
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        currentDayBean = DayBean(currentYear, currentMonth, currentDay)
        buyState = BuyState.OVERDUE
        dayState = DayState.NOT_ENABLE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isRefresh) {
            drawMonthTitle(canvas)
            drawDay(canvas)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    /**
     * 画月的title
     */
    private fun drawMonthTitle(canvas: Canvas?) {
        if (drawWeekList.isNotEmpty()) {
            this.drawWeekList.forEach {
                try {
                    weekPaint.color = it.weekPaintColor
                    canvas?.drawText(it.content, (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / weeks.size * (weeks.indexOf(it.content) + 0.5)) - weekPaint.measureText(it.content) / 2).toFloat(), y + weekTextSize, weekPaint)

                } catch (e: Exception) {
                    Log.e("MONTH_VIEW", "不知道为什么没有赋值", e)
                }
            }
        }
    }

    /**
     * 画里面的日期
     */
    private fun createMonthContentData() {

        //当天是星期几
        val firstDayWeek = CalendarUtil.getFirstDayWeek(year, month)
        //上一个月的总天数
        val daysLastMonth = if (month == 1) {
            CalendarUtil.getDaysInMonth(year, 12)
        } else {
            CalendarUtil.getDaysInMonth(year, month - 1)
        }
        //当前月的总天数
        val daysCurrentMonth = CalendarUtil.getDaysInMonth(year, month)
        //获取当月的行数（吧上个月的加上）
        var rowNumber = (daysCurrentMonth + firstDayWeek - 1) / 7
        val remainder = (daysCurrentMonth + firstDayWeek - 1) % 7
        if (remainder > 0) {
            rowNumber += 1
        }
        viewHeight = (rowNumber * lineHeight + weekTextSize + dayMarinWeekSize + 10).toInt()
        //每次进行onDraw的时候清空里面的数据，防止重叠
        TouchManager.monthDayBeanRect.clear()
        TouchManager.monthAllDayBean.clear()
        when (firstDayWeek) {
            1 -> {
                for (i in 1..rowNumber) {
                    for (k in firstDayWeek..7) {
                        val dayBean = DayBean()
                        //画日期
                        if ((k + (i - 1) * 7) > daysCurrentMonth) {
                            //画圈 当前月份为12 下一个月就是1月
                            if (month == 12) {
                                dayBean.year = year + 1
                                dayBean.month = 1
                            } else {
                                dayBean.year = year
                                dayBean.month = month + 1
                            }
                            dayBean.day = ((k + (i - 1) * 7) - daysCurrentMonth)
                            //设置画笔颜色 当前选择的月的下一个月
                            val circleBitmapBean = CircleBitmapBean()
                            createCalendarData(dayBean, circleBitmapBean, k, i, false, ((k + (i - 1) * 7) - daysCurrentMonth).toString())

                        } else {
                            //画圈
                            dayBean.year = year
                            dayBean.month = month
                            dayBean.day = ((k + (i - 1) * 7))
                            //设置画笔颜色 当前选择的月
                            val circleBitmapBean = CircleBitmapBean()
                            createCalendarData(dayBean, circleBitmapBean, k, i, true, (k + (i - 1) * 7).toString())
                        }
                    }

                }
            }
            else -> {
                //画第一行
                for (k in 1..7) {
                    val dayBean = DayBean()
                    if (firstDayWeek > k) {
                        //画圈 月份为1 上一个月就是12月
                        if (month == 1) {
                            dayBean.year = year - 1
                            dayBean.month = 12
                        } else {
                            dayBean.year = year
                            dayBean.month = month - 1
                        }
                        dayBean.day = (daysLastMonth + k - (firstDayWeek - 1))
                        //设置画笔颜色 当前选择的月的下一个月
                        val circleBitmapBean = CircleBitmapBean()
                        createCalendarData(dayBean, circleBitmapBean, k, 1, false, (daysLastMonth + k - (firstDayWeek - 1)).toString())
                    } else {
                        //画圈
                        dayBean.year = year
                        dayBean.month = month
                        dayBean.day = (k - firstDayWeek + 1)
                        //设置画笔颜色 当前选择的月
                        val circleBitmapBean = CircleBitmapBean()
                        createCalendarData(dayBean, circleBitmapBean, k, 1, true, (k - firstDayWeek + 1).toString())
                    }

                }
                //画剩下的几行
                for (i in 2..rowNumber) {
                    for (k in 1..7) {
                        val dayBean = DayBean()
                        //设置画笔颜色 当前选择的月的下一个月
                        if (((k - firstDayWeek + 1) + (i - 1) * 7) > daysCurrentMonth) {
                            //画圈 当前月份为12 下一个月就是1月
                            if (month == 12) {
                                dayBean.year = year + 1
                                dayBean.month = 1
                            } else {
                                dayBean.year = year
                                dayBean.month = month + 1
                            }
                            dayBean.day = (((k - firstDayWeek + 1) + (i - 1) * 7) - daysCurrentMonth)
                            //设置画笔颜色 当前选择的月的下一个月
                            val circleBitmapBean = CircleBitmapBean()
                            createCalendarData(dayBean, circleBitmapBean, k, i, false, (((k - firstDayWeek + 1) + (i - 1) * 7) - daysCurrentMonth).toString())
                        } else {
                            //画圈
                            dayBean.year = year
                            dayBean.month = month
                            dayBean.day = ((k - firstDayWeek + 1) + (i - 1) * 7)
                            //设置画笔颜色 当前选择的月的下一个月
                            val circleBitmapBean = CircleBitmapBean()
                            createCalendarData(dayBean, circleBitmapBean, k, i, true, ((k - firstDayWeek + 1) + (i - 1) * 7).toString())
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建日历控件数据
     */
    private fun createCalendarData(dayBean: DayBean, circleBitmapBean: CircleBitmapBean, k: Int, i: Int, isCurrentMonthDay: Boolean, textContent: String) {
        addManagerData(k, i, dayBean)
        setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, isCurrentMonthDay, circleBitmapBean)
        circleBitmapBean.content = textContent
        circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
        circleBitmapBean.textY = (y - (dayPaint.ascent() + dayPaint.descent()) / 2 + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
        createDayData(dayBean, k, i, circleBitmapBean)

    }

    /**
     * 添加管理数据
     */
    private fun addManagerData(k: Int, i: Int, dayBean: DayBean) {
        TouchManager.monthDayBeanRect.add(Rect((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k)), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt()))
        TouchManager.monthAllDayBean.add(dayBean)
    }

    /**
     * 画买的情况
     */
    private fun createDayData(dayBean: DayBean, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        circleBitmapBean.dayBean = dayBean
        //按天买
        if (DataManger.selectedDateByDay.isNotEmpty()) {
            createSelectedDateByDayData(dayBean, k, i, circleBitmapBean)
        }
        if (DataManger.selectingDateByDay.isNotEmpty()) {
            createSelectingDateByDayData(dayBean, k, i, circleBitmapBean)
        }
        //画买过的矩形框（按月和按季买）
        if (DataManger.selectedDayByMonthOrSeason.isNotEmpty()) {
            DataManger.selectedDayByMonthOrSeason.forEach {
                if (it.type != BuyType.DAY) {
                    createSelectedDateByMonthData(DateUtil.compareTwoDayBean(dayBean, it, currentDayBean, it.type!!), k, i, circleBitmapBean)
                }
            }

        }
        if (DataManger.selectingDayByMonthOrSeason.isNotEmpty()) {
            DataManger.selectingDayByMonthOrSeason.forEach {
                if (it.type != BuyType.DAY) {
                    createSelectingDateByMonthData(DateUtil.compareTwoDayBean(dayBean, it, currentDayBean, it.type!!), k, i, circleBitmapBean)
                }
            }
        }
        circleBitmapBeanDay.add(circleBitmapBean)
    }

    /**
     * 按月选择的时候画选中的日期
     *1:在区间并且过期 ；2在区间未过期且为当前日期；3在区间未过期且不为当前日期；
     * 4在起始位置并且过期，5在起始位置并且未过期且为当前日期，6在起始位置并且未过期且不为当前日期，
     * 7在结束位置并且过期，8在结束位置并且未过期且为当前时间，9在结束位置并且未过期且为不当前时间
     */
    private fun createSelectedDateByMonthData(type: Int, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        //根据原型，是先画圆在画图
        /*位图将被缩放/转换成合适的矩形  */
        val dst = Rect()
        /*每个天的矩形位置走边*/
        val leftDayRect = Rect()
        /*每个天的矩形位置右边2*/
        val rightDayRect = Rect()
        /*每个天的矩形位置中间*/
        val centerDayRect = Rect()
        /*每个天的园的所在矩形*/
        val circleDayRect = RectF()
        dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter - selectedDateOverdueBitmap.height).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap.width).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter).toInt())
        leftDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k)))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt())
        rightDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt())), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt())
        centerDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt())
        circleDayRect.set((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - selectedDateRadius).toInt().toFloat())), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt().toFloat(), ((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5) + selectedDateRadius)).toInt().toFloat()))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt().toFloat())
        circleBitmapBean.dst = dst
        circleBitmapBean.leftDayRect = leftDayRect
        circleBitmapBean.rightDayRect = rightDayRect
        circleBitmapBean.centerDayRect = centerDayRect
        circleBitmapBean.circleDayRect = circleDayRect

        circleBitmapBean.k = k
        when (type) {
            2 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = currentDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                circleBitmapBean.circlePaintColor = selectedDateColor
                circleBitmapBean.bitmap = selectedDateBitmap
                when (k) {
                    7 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    }
                }
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }
            3 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = selectedDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectedDateColor
                circleBitmapBean.bitmap = selectedDateBitmap
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }
            5 -> {
                circleBitmapBean.dayPaintColor = currentDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                circleBitmapBean.circlePaintColor = selectedDateColor
                circleBitmapBean.bitmap = selectedDateBitmap
                when (k) {
                    7 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    }
                }
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }
            6 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = selectedDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectedDateColor
                circleBitmapBean.bitmap = selectedDateBitmap
                when (k) {
                    7 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    }
                }
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }
            8 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = currentDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                circleBitmapBean.circlePaintColor = selectedDateColor
                circleBitmapBean.bitmap = selectedDateBitmap
                when (k) {
                    1 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circlePaint.color = circleBitmapBean.circlePaintColor!!
                    }
                }
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }
            9 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = selectedDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectedDateColor
                circleBitmapBean.bitmap = selectedDateBitmap
                when (k) {
                    1 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    }
                }
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }
            1, 4, 7 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = unEnableColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectedDateOverdueColor
                circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                circleBitmapBean.bitmap = selectedDateOverdueBitmap
                circleBitmapBeanListByMonth.add(circleBitmapBean)
            }

        }

    }

    /**
     * 按月选择的时候画选中的日期
     *1:在区间并且过期 ；2在区间未过期且为当前日期；3在区间未过期且不为当前日期；
     * 4在起始位置并且过期，5在起始位置并且未过期且为当前日期，6在起始位置并且未过期且不为当前日期，
     * 7在结束位置并且过期，8在结束位置并且未过期且为当前时间，9在结束位置并且未过期且为不当前时间
     */
    private fun createSelectingDateByMonthData(type: Int, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        //根据原型，是先画圆在画图
        val dst = Rect()
        /*每个天的矩形位置走边*/
        val leftDayRect = Rect()
        /*每个天的矩形位置右边2*/
        val rightDayRect = Rect()
        /*每个天的矩形位置中间*/
        val centerDayRect = Rect()
        /*每个天的园的所在矩形*/
        val circleDayRect = RectF()
        dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter - selectedDateOverdueBitmap.height).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap.width).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter).toInt())
        leftDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k)))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt())
        rightDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt())), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt())
        centerDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt())
        circleDayRect.set((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - selectedDateRadius).toInt().toFloat())), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - selectedDateRadius).toInt().toFloat(), ((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5) + selectedDateRadius)).toInt().toFloat()))), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize + selectedDateRadius).toInt().toFloat())
        circleBitmapBean.leftDayRect = leftDayRect
        circleBitmapBean.rightDayRect = rightDayRect
        circleBitmapBean.centerDayRect = centerDayRect
        circleBitmapBean.circleDayRect = circleDayRect

        circleBitmapBean.k = k
        when (type) {
            2 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = currentDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                circleBitmapBean.circlePaintColor = selectingDateColor
                when (k) {
                    7 -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                    }
                    1 -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                }
                circleBitmapBeanListBySelectingMonth.add(circleBitmapBean)
            }
            1, 3 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = selectedDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectingDateColor
                when (k) {
                    7 -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                    }
                    1 -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())

                    }
                }
                circleBitmapBeanListBySelectingMonth.add(circleBitmapBean)
            }
            5 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = Color.parseColor("#ffffff")
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                circleBitmapBean.circlePaintColor = selectingLineColor
                circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                circleBitmapBeanListBySelectingMonth.add(circleBitmapBean)
            }
            4, 6 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = Color.parseColor("#ffffff")
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectingLineColor
                when (k) {
                    7 -> {
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                }
                circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                circleBitmapBeanListBySelectingMonth.add(circleBitmapBean)
            }
            8 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = currentDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                circleBitmapBean.circlePaintColor = selectingDateColor
                when (k) {
                    1 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                    }
                }
                circleBitmapBeanListBySelectingMonth.add(circleBitmapBean)
            }
            7, 9 -> {
                circleBitmapBean.type = type
                circleBitmapBean.dayPaintColor = selectedDayColor
                circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                circleBitmapBean.circlePaintColor = selectingDateColor
                when (k) {
                    1 -> {
                        circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                        circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circlePaint.color = circleBitmapBean.circlePaintColor!!
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                    }
                }
                circleBitmapBeanListBySelectingMonth.add(circleBitmapBean)
            }

        }

    }


    /**
     * 画选择中右边的线
     */
    private fun drawSelectingLine(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawLine(circleBitmapBean.lineStartX!!, (circleBitmapBean.lineCenterY!! - selectedDateRadius), circleBitmapBean.lineEndX!!, (circleBitmapBean.lineCenterY!! - selectedDateRadius), linePaint)
        canvas?.drawLine(circleBitmapBean.lineStartX!!, (circleBitmapBean.lineCenterY!! + selectedDateRadius), circleBitmapBean.lineEndX!!, (circleBitmapBean.lineCenterY!! + selectedDateRadius), linePaint)
    }

    /**
     * 画中间矩形
     */
    private fun drawCenterRect(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawRect(circleBitmapBean.centerDayRect, rectPaint)
    }

    /**
     * 画最右边矩形
     */
    private fun drawRightRect(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawArc(circleBitmapBean.circleDayRect, 270f, 180f, true, rectPaint)
        canvas?.drawRect(circleBitmapBean.rightDayRect, rectPaint)
    }

    /**
     * 画最左边矩形
     */
    private fun drawLeftRect(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawArc(circleBitmapBean.circleDayRect, 90f, 180f, true, rectPaint)
        canvas?.drawRect(circleBitmapBean.leftDayRect, rectPaint)
    }

    /**
     * 画中间矩形
     */
    private fun drawSelectingCenterRect(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawRect(circleBitmapBean.centerDayRect, rectPaint)
    }

    /**
     * 画最右边矩形
     */
    private fun drawSelectingRightRect(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawArc(circleBitmapBean.circleDayRect, 270f, 180f, true, rectPaint)
        canvas?.drawArc(circleBitmapBean.circleDayRect, 270f, 180f, false, arcPaint)
        canvas?.drawRect(circleBitmapBean.rightDayRect, rectPaint)
    }

    /**
     * 画最左边矩形
     */
    private fun drawSelectingLeftRect(canvas: Canvas?, circleBitmapBean: CircleBitmapBean) {
        canvas?.drawArc(circleBitmapBean.circleDayRect, 90f, 180f, true, rectPaint)
        canvas?.drawArc(circleBitmapBean.circleDayRect, 90f, 180f, false, arcPaint)
        canvas?.drawRect(circleBitmapBean.leftDayRect, rectPaint)
    }

    /**
     * 按天选择的时候画选中的日期
     */
    private fun createSelectedDateByDayData(dayBean: DayBean, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        if (DataManger.selectedDateByDay.contains(dayBean)) {
            buyState = when {
                dayBean.year!! > currentYear -> BuyState.NOT_OVERDUE
                dayBean.year!! == currentYear -> when {
                    dayBean.month!! > currentMonth -> BuyState.NOT_OVERDUE
                    dayBean.month!! == currentMonth -> when {
                        dayBean.day!! > currentDay -> BuyState.NOT_OVERDUE
                        dayBean.day!! == currentDay -> BuyState.CURRENT
                        else -> BuyState.OVERDUE
                    }
                    else -> BuyState.OVERDUE
                }
                else -> BuyState.OVERDUE
            }
            //根据原型，是先画圆在画图
            val dst = Rect()
            dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter - selectedDateOverdueBitmap.height).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap.width).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter).toInt())
            circleBitmapBean.dst = dst
            when (buyState) {
                BuyState.NOT_OVERDUE -> {
                    circleBitmapBean.dayPaintColor = selectedDayColor
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                    circleBitmapBean.circlePaintColor = selectedDateColor
                    circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                    circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    circleBitmapBean.bitmap = selectedDateBitmap
                }
                BuyState.CURRENT -> {
                    circleBitmapBean.dayPaintColor = currentDayColor
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                    circleBitmapBean.circlePaintColor = selectedDateColor
                    circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                    circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    circleBitmapBean.bitmap = selectedDateBitmap
                }
                BuyState.OVERDUE -> {
                    circleBitmapBean.dayPaintColor = unEnableColor
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                    circleBitmapBean.circlePaintColor = selectedDateOverdueColor
                    circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                    circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
                    circleBitmapBean.bitmap = selectedDateOverdueBitmap
                }
            }
            circleBitmapBeanListByDay.add(circleBitmapBean)
        }

    }

    /**
     * 按天选择的时候画选中的日期
     */
    private fun createSelectingDateByDayData(dayBean: DayBean, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        if (DataManger.selectingDateByDay.contains(dayBean)) {
            //根据原型，是先画圆在画图
            val dst = Rect()
            dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter - selectedDateOverdueBitmap.height).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap.width).toInt()), (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize - bitmapMarginCircleCenter).toInt())
            circleBitmapBean.dst = dst
            circleBitmapBean.dayPaintColor = Color.parseColor("#ffffff")
            circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
            circleBitmapBean.circlePaintColor = selectingLineColor
            circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
            circleBitmapBean.circleY = (y + lineHeight * (i - 0.5) + dayMarinWeekSize + weekTextSize).toFloat()
            circleBitmapBean.bitmap = selectingDateBitmap
            circleBitmapBeanListBySelectingDay.add(circleBitmapBean)
        }
    }


    /**
     * 画bitmap
     */
    private fun drawBitmap(canvas: Canvas?, dst: Rect, bitmap: Bitmap?) {
        canvas?.drawBitmap(bitmap, src, dst, null)
    }


    /**
     * 画天
     */
    private fun drawDay(canvas: Canvas?) {
        try {
            //按天买
            if (DataManger.selectedDateByDay.isNotEmpty() && circleBitmapBeanListByDay.isNotEmpty()) {
                circleBitmapBeanListByDay.forEach {
                    if (DataManger.selectedDateByDay.contains(it.dayBean)) {
                        circlePaint.color = it.circlePaintColor!!
                        drawCircle(canvas, it.circleX!!, it.circleY!!, selectedDateRadius, circlePaint)
                        drawBitmap(canvas, it.dst!!, it.bitmap)
                    }
                }

            }
            if (DataManger.selectingDateByDay.isNotEmpty() && circleBitmapBeanListBySelectingDay.isNotEmpty()) {
                circleBitmapBeanListBySelectingDay.forEach {
                    if (DataManger.selectingDateByDay.contains(it.dayBean)) {
                        circlePaint.color = it.circlePaintColor!!
                        drawCircle(canvas, it.circleX!!, it.circleY!!, selectedDateRadius, circlePaint)
                        drawBitmap(canvas, it.dst!!, it.bitmap)
                    }
                }
            }
            //画买过的矩形框（按月和按季买）
            if (DataManger.selectedDayByMonthOrSeason.isNotEmpty() && circleBitmapBeanListByMonth.isNotEmpty()) {
                circleBitmapBeanListByMonth.forEach {
                    drawSelectedDateByMonthOrSeason(it, canvas)
                }

            }
            if (DataManger.selectingDayByMonthOrSeason.isNotEmpty() && circleBitmapBeanListBySelectingMonth.isNotEmpty()) {
                circleBitmapBeanListBySelectingMonth.forEach {
                    drawSelectingDateByMonthOrSeason(it, canvas)
                }
            }
            circleBitmapBeanDay.forEach {
                try {
                    dayPaint.color = it.dayPaintColor ?: Color.parseColor("#000000")
                    dayPaint.typeface = it.dayPaintTypeface ?: Typeface.DEFAULT
                    canvas?.drawText(it.content, it.textX!!, it.textY!!, dayPaint)
                } catch (e: Exception) {
                    Log.e("MONTH_VIEW", "不知道为什么没有赋值", e)
                }

            }
        } catch (e: Exception) {
            Log.e("...", "产生了ConcurrentModificationException异常")
        }
    }

    private fun drawSelectingDateByMonthOrSeason(circleBitmapBean: CircleBitmapBean, canvas: Canvas?) {
        when (circleBitmapBean.type) {
            2, 1, 3 -> {
                when (circleBitmapBean.k) {
                    7 -> {
                        drawSelectingRightRect(canvas, circleBitmapBean)
                    }
                    1 -> {
                        drawSelectingLeftRect(canvas, circleBitmapBean)
                    }
                    else -> {
                        drawSelectingCenterRect(canvas, circleBitmapBean)
                    }
                }
                drawSelectingLine(canvas, circleBitmapBean)
            }

            5 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    7 -> {
                    }
                    else -> {
                        drawSelectingLeftRect(canvas, circleBitmapBean)
                        drawSelectingLine(canvas, circleBitmapBean)
                    }
                }
                drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
            }
            4, 6 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    7 -> {
                    }
                    else -> {
                        drawSelectingLeftRect(canvas, circleBitmapBean)
                        drawSelectingLine(canvas, circleBitmapBean)
                    }
                }
                drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
            }
            8 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    1 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                        canvas?.drawArc(circleBitmapBean.circleDayRect, 0f, 360f, false, arcPaint)
                    }
                    else -> {
                        drawSelectingRightRect(canvas, circleBitmapBean)
                        drawSelectingLine(canvas, circleBitmapBean)
                    }
                }
            }
            7, 9 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    1 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                        canvas?.drawArc(circleBitmapBean.circleDayRect, 0f, 360f, false, arcPaint)
                    }
                    else -> {
                        drawSelectingRightRect(canvas, circleBitmapBean)
                        drawSelectingLine(canvas, circleBitmapBean)
                    }
                }
            }

        }
    }

    /**
     * 画选择过的日期（月和季）
     */
    private fun drawSelectedDateByMonthOrSeason(circleBitmapBean: CircleBitmapBean, canvas: Canvas?) {
        when (circleBitmapBean.type) {
            2 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    7 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                    }
                    else -> {
                        drawLeftRect(canvas, circleBitmapBean)
                    }
                }
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
            3 -> {

                when (circleBitmapBean.k) {
                    1 -> {
                        drawLeftRect(canvas, circleBitmapBean)
                    }
                    7 -> {
                        drawRightRect(canvas, circleBitmapBean)
                    }
                    else -> {
                        drawCenterRect(canvas, circleBitmapBean)
                    }
                }
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
            5 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    7 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                    }
                    else -> {
                        drawLeftRect(canvas, circleBitmapBean)
                    }
                }
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
            6 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    7 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                    }
                    else -> {
                        drawLeftRect(canvas, circleBitmapBean)

                    }
                }
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
            8 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    1 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                    }
                    else -> {
                        drawRightRect(canvas, circleBitmapBean)
                    }
                }
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
            9 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    1 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                    }
                    else -> {
                        drawRightRect(canvas, circleBitmapBean)
                    }
                }
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
            1, 4, 7 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
                drawBitmap(canvas, circleBitmapBean.dst!!, circleBitmapBean.bitmap)
            }
        }
    }

    /**
     * 画园
     */
    private fun drawCircle(canvas: Canvas?, x: Float, y: Float, radius: Float, paint: Paint) {
        canvas?.drawCircle(x, y, radius, paint)
    }

    /**
     * 创建画周的日期
     */
    private fun createDrawWeekData() {
        weeks.forEach {
            when (it) {
                "日", "六" -> {
                    drawWeekList.add(WeekBean(weekendColor, it))
                }
                else -> {
                    drawWeekList.add(WeekBean(workingColor, it))
                }
            }
        }
    }

    /**
     * 设置挡圈月份画笔颜色
     * @param year 当前年选择的年
     * @param month 当前年选择的月
     * @param day 天
     */
    private fun setMonthDayPaintColor(year: Int, month: Int, day: Int, isCurrentMonthDay: Boolean, circleBitmapBean: CircleBitmapBean) {
        //通过选择类型来获取选择时间的显示
        val limitDay = when (DataManger.useBuyType) {
            BuyType.DAY -> {
                3
            }
            BuyType.MONTH, BuyType.SEASON -> {
                9
            }
        }
        dayState = when {
            year > currentYear -> DayState.ENABLE
            year == currentYear -> when {
                month > currentMonth -> {
                    if (month - currentMonth == 1) {
                        if (day >= limitDay - (CalendarUtil.getDaysInMonth(currentYear, currentMonth) - currentDay)) {
                            DayState.ENABLE
                        } else {
                            DayState.NOT_ENABLE
                        }
                    } else {
                        DayState.ENABLE
                    }
                }
                month == currentMonth -> when {
                    day > currentDay + limitDay -> DayState.ENABLE
                    day == currentDay -> DayState.CURRENT
                    else -> DayState.NOT_ENABLE
                }
                else -> DayState.NOT_ENABLE
            }
            else -> DayState.NOT_ENABLE
        }
        if (isCurrentMonthDay) {
            when (dayState) {
                DayState.ENABLE -> {
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                    circleBitmapBean.dayPaintColor = normalDayTextColor
                }
                DayState.CURRENT -> {
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                    circleBitmapBean.dayPaintColor = currentDayColor
                }
                DayState.NOT_ENABLE -> {
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                    circleBitmapBean.dayPaintColor = unEnableColor
                }
            }
        } else {
            circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
            circleBitmapBean.dayPaintColor = unEnableColor
        }
    }

    /**
     * 刷新日历控件
     * 以异步操作进行处理，防止卡顿，以空间换时间
     */
    fun refreshView() {
        doAsync {
            isRefresh = false
            isEnableTouch = false
            circleBitmapBeanListByDay.clear()
            circleBitmapBeanDay.clear()
            circleBitmapBeanListBySelectingDay.clear()
            circleBitmapBeanListByMonth.clear()
            circleBitmapBeanListBySelectingMonth.clear()
            createDrawWeekData()
            createMonthContentData()
            uiThread {
                isRefresh = true
                isEnableTouch = true
                invalidate()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isEnableTouch) {
                    startX = event.x
                    startY = event.y
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                try {
                    //判断是否可以点击，方便进行回调
                    var isClick = true
                    //判断用户是点击还是滑动
                    if (isEnableTouch && Math.abs(event.x - startX) <= ViewConfiguration.get(context).scaledTouchSlop &&
                            Math.abs(event.y - startY) <= ViewConfiguration.get(context).scaledTouchSlop) {
                        //便利循环找到点击区域对应的日期
                        TouchManager.monthDayBeanRect.forEach {
                            if (it.contains(event.x.toInt(), event.y.toInt())) {
                                val dayBean = TouchManager.monthAllDayBean[TouchManager.monthDayBeanRect.indexOf(it)]
                                dayBean.type = DataManger.useBuyType
                                //判断日期是否在可点击的范围内
                                //通过选择类型来获取选择时间的显示
                                val limitDay = when (DataManger.useBuyType) {
                                    BuyType.DAY -> {
                                        3
                                    }
                                    BuyType.MONTH, BuyType.SEASON -> {
                                        9
                                    }
                                }
                                val dayBeanState =
                                        when (DateUtil.isCanSelect(currentDayBean!!, dayBean)) {
                                            true -> {
                                                when {
                                                    dayBean.year!! > currentYear -> DayState.ENABLE
                                                    dayBean.year!! == currentYear -> when {
                                                        dayBean.month!! > currentMonth -> {
                                                            if (dayBean.month!! - currentMonth == 1) {
                                                                if (dayBean.day!! >= limitDay - (CalendarUtil.getDaysInMonth(currentYear, currentMonth) - currentDay)) {
                                                                    DayState.ENABLE
                                                                } else {
                                                                    DayState.NOT_ENABLE
                                                                }
                                                            } else {
                                                                DayState.ENABLE
                                                            }
                                                        }
                                                        dayBean.month!! == currentMonth -> when {
                                                            dayBean.day!! > currentDay + limitDay -> DayState.ENABLE
                                                            dayBean.day!! == currentDay -> DayState.CURRENT
                                                            else -> DayState.NOT_ENABLE
                                                        }
                                                        else -> DayState.NOT_ENABLE
                                                    }
                                                    else -> DayState.NOT_ENABLE
                                                }
                                            }
                                            false -> {
                                                DayState.NOT_ENABLE
                                                isClick = false
                                                monthViewClick?.exceedClick(dayBean, DataManger.useBuyType, "所选日期的起始日不能超过90天")
                                                return@forEach
                                            }
                                        }


                                //日期不可点击
                                if (dayBeanState != DayState.ENABLE) {
                                    isClick = false
                                    monthViewClick?.noUnClick(dayBean, DataManger.useBuyType, "不能购买该日期的洗车券")
                                    return@forEach
                                }
                                //根据不同的type做相应的逻辑
                                when (DataManger.useBuyType) {
                                    BuyType.DAY -> {
                                        //按天选选中的日期（天）里面有这个日期
                                        if (DataManger.selectedDateByDay.isNotEmpty() && isClick) {
                                            if (DataManger.selectedDateByDay.contains(dayBean)) {
                                                isClick = false
                                            }
                                        }
                                        //按天选正在选的日期（月或者季）里面有这个日期
                                        if (DataManger.selectedDayByMonthOrSeason.isNotEmpty() && isClick) {
                                            if (!DateUtil.isSelect(dayBean)) {
                                                isClick = false
                                            }
                                        }
                                    }
                                    BuyType.MONTH, BuyType.SEASON -> {
                                        //按月或者季选正在选的日期（天）里面是否包含已选过的日期
                                        if (DataManger.selectedDateByDay.isNotEmpty() && isClick) {
                                            if (!DateUtil.isSelectByMonth(dayBean)) {
                                                isClick = false
                                            }
                                        }
                                        //按月或者季选正在选的日期（月或者季）里面是否包含已选过的日期
                                        if (DataManger.selectedDayByMonthOrSeason.isNotEmpty() && isClick) {
                                            if (!DateUtil.isSelect2ByMonth(dayBean)) {
                                                isClick = false
                                            }
                                        }
                                    }
                                }
                                //是否可以点击并进行回调
                                if (isClick) {
                                    when (DataManger.useBuyType) {
                                        BuyType.DAY -> {
                                            //先判断是否选中，没有选中就添加进去，选中了就去掉
                                            if (DataManger.selectingDateByDay.contains(dayBean)) {
                                                DataManger.selectingDateByDay.remove(dayBean)
                                            } else {
                                                DataManger.selectingDateByDay.add(dayBean)
                                            }
                                        }
                                        BuyType.MONTH, BuyType.SEASON -> {
                                            //直接清空里面数据，原因是只能选中一次，然后提交，然后才能再选
                                            DataManger.selectingDayByMonthOrSeason.clear()
                                            DataManger.selectingDayByMonthOrSeason.add(dayBean)
                                        }
                                    }
                                    monthViewClick?.click(dayBean, DataManger.useBuyType, id)
                                    refreshView()
                                } else {
                                    isEnableTouch = true
                                    monthViewClick?.unClick(dayBean, DataManger.useBuyType, "您已经选过该日期")
                                }

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("...", "产生了ConcurrentModificationException异常，让用户重新选")
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

data class WeekBean(val weekPaintColor: Int, val content: String)
data class CircleBitmapBean(var dayPaintColor: Int? = Color.parseColor("#000000"),
                            var dayPaintTypeface: Typeface? = Typeface.DEFAULT,
                            var circlePaintColor: Int? = Color.parseColor("#0f1C7FFD"),
                            var type: Int? = -1,
                            var circleX: Float? = 0F,
                            var circleY: Float? = 0f,
                            var textX: Float? = 0F,
                            var textY: Float? = 0f,
                            var k: Int? = 0,
                            var lineStartX: Float? = 0f,
                            var lineCenterY: Float? = 0f,
                            var lineEndX: Float? = 0f,
                            var content: String? = null,
                            var dayBean: DayBean? = null,
                            var bitmap: Bitmap? = null,
                            var dst: Rect? = null,
                            var leftDayRect: Rect? = null,
                            var rightDayRect: Rect? = null,
                            var centerDayRect: Rect? = null,
                            var circleDayRect: RectF? = null)

interface MonthViewClick {
    fun click(dayBean: DayBean, buyType: BuyType, position: Int)
    fun unClick(dayBean: DayBean, buyType: BuyType, message: String)
    fun noUnClick(dayBean: DayBean, buyType: BuyType, message: String)
    fun exceedClick(dayBean: DayBean, buyType: BuyType, message: String)
}


