package taotao.haoxiong.com.supercalendar.calendar

import android.content.Context
import android.graphics.*
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import taotao.haoxiong.com.supercalendar.R
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
    val viewWidth by lazy {
        width
    }
    /** view的高度 */
    val viewHeight by lazy {
        height
    }
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
        Rect(0, 0, selectedDateOverdueBitmap!!.width, selectedDateOverdueBitmap!!.height)
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
    /**按天买已买的日期列表*/
    val selectedDateByDay: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /**按天买正在买的日期列表*/
    val selectingDateByDay: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /** 行高 */
    var lineHeight: Float = 260f
    /**  */
    var textContent: String? = null
    /** 行高 */
    var bitmapMarginCircleCenter: Float = 30f

    /**按月买已买的日期*/
    val selectedDayByMonthOrSeason: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /**按月买要买的日期*/
    val selectingDayByMonthOrSeason: ArrayList<DayBean> by lazy {
        ArrayList<DayBean>()
    }
    /** 当前的日期 */
    var currentDayBean: DayBean? = null
    /** touchEvent down的X位置 */
    var startX: Float = 0f
    /** touchEvent up的Y位置 */
    var startY: Float = 0f
    /** monthView的点击事件 */
    var monthViewClick: MonthViewClick? = null
    var useBuyType: BuyType = BuyType.MONTH
    /** 每一个天的对象 */

    /** 画周的每一个对象 */
    val drawWeekList: ArrayList<WeekBean> by lazy { ArrayList<WeekBean>() }
    /** 画天的每一个对象 */
    val circleBitmapBeanListByDay: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanListByMonth: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanListBySelectingDay: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanListBySelectingMonth: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    val circleBitmapBeanDay: ArrayList<CircleBitmapBean> by lazy { ArrayList<CircleBitmapBean>() }
    constructor(context: Context) : super(context) {
        initData()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, @Nullable attrs: AttributeSet) {
        initData()
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MonthView)
        normalDayTextSize = attributes.getDimension(R.styleable.MonthView_normalTextSize, 32F)
        normalDayTextColor = attributes.getColor(R.styleable.MonthView_normalTextColor, ContextCompat.getColor(context, R.color.normal_color))
        selectedDayTextSize = attributes.getDimension(R.styleable.MonthView_selectedTextSize, 32F)
        selectedDayColor = attributes.getColor(R.styleable.MonthView_selectedTextColor, ContextCompat.getColor(context, R.color.selected_color))
        selectedDateOverdueColor = attributes.getColor(R.styleable.MonthView_selectedDateOverdueColor, ContextCompat.getColor(context, R.color.selected_date_overdue_color))
        weekendColor = attributes.getColor(R.styleable.MonthView_weekendColor, ContextCompat.getColor(context, R.color.weekend_color))
        workingColor = attributes.getColor(R.styleable.MonthView_workingColor, ContextCompat.getColor(context, R.color.working_color))
        weekTextSize = attributes.getDimension(R.styleable.MonthView_weekTextSize, 32F)
        unEnableColor = attributes.getColor(R.styleable.MonthView_unEnableTextColor, ContextCompat.getColor(context, R.color.unEnable_color))
        selectedDateColor = attributes.getColor(R.styleable.MonthView_selectedDateColor, ContextCompat.getColor(context, R.color.selected_date_color))
        selectedDateRadius = attributes.getDimension(R.styleable.MonthView_selectedDateRadius, 29f)
        currentDayColor = attributes.getColor(R.styleable.MonthView_currentDayColor, ContextCompat.getColor(context, R.color.current_day_color))
        selectingLineColor = attributes.getColor(R.styleable.MonthView_selectingLineColor, ContextCompat.getColor(context, R.color.selecting_line_color))
        selectingDateColor = attributes.getColor(R.styleable.MonthView_selectingDateColor, ContextCompat.getColor(context, R.color.selecting_date_color))
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        currentYear = Calendar.getInstance().get(Calendar.YEAR)
        year = currentYear
        currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1
        month = currentMonth
        currentDay =  Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        buyState = BuyState.OVERDUE
        dayState = DayState.NOT_ENABLE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawMonthTitle(canvas)
        drawDay(canvas)
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
                weekPaint.color = it.weekPaintColor
                canvas?.drawText(it.content, (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / weeks.size * (weeks.indexOf(it.content) + 0.5)) - weekPaint.measureText(it.content) / 2).toFloat(), y + weekTextSize, weekPaint)
            }
        }
    }

    /**
     * 画里面的日期
     */
    private fun createMonthContentData() {
        circleBitmapBeanListByDay.clear()
        circleBitmapBeanDay.clear()
        circleBitmapBeanListBySelectingDay.clear()
        circleBitmapBeanListByMonth.clear()
        circleBitmapBeanListBySelectingMonth.clear()
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

        //每次进行onDraw的时候清空里面的数据，防止重叠
        TouchMannger.monthDayBeanRect.clear()
        when (firstDayWeek) {
            1 -> {
                for (i in 1..rowNumber) {
                    for (k in firstDayWeek..7) {
                        val dayBean = DayBean()
                        //画日期
                        if ((k + (i - 1) * 7) > daysCurrentMonth) {
                            textContent = ((k + (i - 1) * 7) - daysCurrentMonth).toString()
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
                            setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, false, circleBitmapBean)
                            circleBitmapBean.content = textContent
                            circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
                            circleBitmapBean.textY = y - (dayPaint.ascent() + dayPaint.descent()) / 2 + i * lineHeight / 2
                            createDayData(dayBean, k, i, circleBitmapBean)
                            addManagerData(k, i, dayBean)
                        } else {
                            textContent = (k + (i - 1) * 7).toString()
                            //画圈
                            dayBean.year = year
                            dayBean.month = month
                            dayBean.day = ((k + (i - 1) * 7))
                            //设置画笔颜色 当前选择的月
                            val circleBitmapBean = CircleBitmapBean()
                            setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, true, circleBitmapBean)
                            circleBitmapBean.content = textContent
                            circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
                            circleBitmapBean.textY = y - (dayPaint.ascent() + dayPaint.descent()) / 2 + i * lineHeight / 2
                            createDayData(dayBean, k, i, circleBitmapBean)
                            addManagerData(k, i, dayBean)
                        }
                    }

                }
            }
            else -> {
                //画第一行
                for (k in 1..7) {
                    val dayBean = DayBean()
                    if (firstDayWeek > k) {
                        textContent = (daysLastMonth + k - (firstDayWeek - 1)).toString()
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
                        setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, false, circleBitmapBean)
                        circleBitmapBean.content = textContent
                        circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
                        circleBitmapBean.textY = y - (dayPaint.ascent() + dayPaint.descent()) / 2 + 1 * lineHeight / 2
                        createDayData(dayBean, k, 1, circleBitmapBean)
                        addManagerData(k, 1, dayBean)
                    } else {
                        textContent = (k - firstDayWeek + 1).toString()
                        //画圈
                        dayBean.year = year
                        dayBean.month = month
                        dayBean.day = (k - firstDayWeek + 1)
                        //设置画笔颜色 当前选择的月
                        val circleBitmapBean = CircleBitmapBean()
                        setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, true, circleBitmapBean)
                        circleBitmapBean.content = textContent
                        circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
                        circleBitmapBean.textY = y - (dayPaint.ascent() + dayPaint.descent()) / 2 + 1 * lineHeight / 2
                        createDayData(dayBean, k, 1, circleBitmapBean)
                        addManagerData(k, 1, dayBean)
                    }

                }
                //画剩下的几行
                for (i in 2..rowNumber) {
                    for (k in 1..7) {
                        val dayBean = DayBean()
                        textContent = (((k - firstDayWeek + 1) + (i - 1) * 7) - daysCurrentMonth).toString()
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
                            setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, false, circleBitmapBean)
                            circleBitmapBean.content = textContent
                            circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
                            circleBitmapBean.textY = y - (dayPaint.ascent() + dayPaint.descent()) / 2 + i * lineHeight / 2
                            createDayData(dayBean, k, i, circleBitmapBean)
                            addManagerData(k, i, dayBean)
                        } else {
                            textContent = ((k - firstDayWeek + 1) + (i - 1) * 7).toString()
                            //画圈
                            dayBean.year = year
                            dayBean.month = month
                            dayBean.day = ((k - firstDayWeek + 1) + (i - 1) * 7)
                            //设置画笔颜色 当前选择的月的下一个月
                            val circleBitmapBean = CircleBitmapBean()
                            setMonthDayPaintColor(dayBean.year!!, dayBean.month!!, dayBean.day!!, true, circleBitmapBean)
                            circleBitmapBean.content = textContent
                            circleBitmapBean.textX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - dayPaint.measureText(circleBitmapBean.content) / 2).toFloat()
                            circleBitmapBean.textY = y - (dayPaint.ascent() + dayPaint.descent()) / 2 + i * lineHeight / 2
                            createDayData(dayBean, k, i, circleBitmapBean)
                            addManagerData(k, i, dayBean)
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加管理数据
     */
    private fun addManagerData(k: Int, i: Int, dayBean: DayBean) {
        TouchMannger.monthDayBeanRect.add(Rect((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))), (y + (i - 1) * lineHeight).toInt(), paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k)), (y + (i) * lineHeight).toInt()))
        TouchMannger.monthAllDayBean.add(dayBean)
    }

    /**
     * 画买的情况
     */
    private fun createDayData(dayBean: DayBean, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        circleBitmapBean.dayBean = dayBean
        //按天买
        if (selectedDateByDay.isNotEmpty()) {
            createSelectedDateByDayData(dayBean, k, i, circleBitmapBean)
        }
        if (selectingDateByDay.isNotEmpty()) {
            createSelectingDateByDayData(dayBean, k, i, circleBitmapBean)
        }
        //画买过的矩形框（按月和按季买）
        if (selectedDayByMonthOrSeason .isNotEmpty()) {
            selectedDayByMonthOrSeason.forEach {
                if (it.type != BuyType.DAY) {
                    createSelectedDateByMonthData(DateUtil.compareTwoDayBean(dayBean, it, currentDayBean, it.type!!), k, i, circleBitmapBean)
                }
            }

        }
        if (selectingDayByMonthOrSeason.isNotEmpty()) {
            selectingDayByMonthOrSeason.forEach {
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
        dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter - selectedDateOverdueBitmap?.height!!).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap?.width!!).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter).toInt())
        leftDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt()), (y + i * lineHeight / 2 - selectedDateRadius).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k)))), (y + i * lineHeight / 2 + selectedDateRadius).toInt())
        rightDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + i * lineHeight / 2 - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt())), (y + i * lineHeight / 2 + selectedDateRadius).toInt())
        centerDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + i * lineHeight / 2 - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))))), (y + i * lineHeight / 2 + selectedDateRadius).toInt())
        circleDayRect.set((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - selectedDateRadius).toInt().toFloat())), (y + i * lineHeight / 2 - selectedDateRadius).toInt().toFloat(), ((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5) + selectedDateRadius)).toInt().toFloat()))), (y + i * lineHeight / 2 + selectedDateRadius).toInt().toFloat())
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
        dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter - selectedDateOverdueBitmap?.height!!).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap?.width!!).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter).toInt())
        leftDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt()), (y + i * lineHeight / 2 - selectedDateRadius).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k)))), (y + i * lineHeight / 2 + selectedDateRadius).toInt())
        rightDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + i * lineHeight / 2 - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toInt())), (y + i * lineHeight / 2 + selectedDateRadius).toInt())
        centerDayRect.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1)))), (y + i * lineHeight / 2 - selectedDateRadius).toInt(), (((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))))), (y + i * lineHeight / 2 + selectedDateRadius).toInt())
        circleDayRect.set((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) - selectedDateRadius).toInt().toFloat())), (y + i * lineHeight / 2 - selectedDateRadius).toInt().toFloat(), ((((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5) + selectedDateRadius)).toInt().toFloat()))), (y + i * lineHeight / 2 + selectedDateRadius).toInt().toFloat())
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
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                    }
                    1 -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                    }
                    1 -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat())
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
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
                circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
                        circleBitmapBean.lineEndX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k))).toFloat())
                    }
                }
                circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                circleBitmapBean.circleY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
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
                        circleBitmapBean.circleY = (y + i * lineHeight / 2)
                        circlePaint.color = circleBitmapBean.circlePaintColor!!
                    }
                    else -> {
                        circleBitmapBean.lineStartX = ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 1))).toFloat())
                        circleBitmapBean.lineCenterY = (y + i * lineHeight / 2)
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
        if (selectedDateByDay.contains(dayBean)) {
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
            dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter - selectedDateOverdueBitmap?.height!!).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap?.width!!).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter).toInt())
            circleBitmapBean.dst = dst
            when (buyState) {
                BuyState.NOT_OVERDUE -> {
                    circleBitmapBean.dayPaintColor = selectedDayColor
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                    circleBitmapBean.circlePaintColor = selectedDateColor
                    circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                    circleBitmapBean.circleY = (y + i * lineHeight / 2)
                    circleBitmapBean.bitmap = selectedDateBitmap
                }
                BuyState.CURRENT -> {
                    circleBitmapBean.dayPaintColor = currentDayColor
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT_BOLD
                    circleBitmapBean.circlePaintColor = selectedDateColor
                    circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                    circleBitmapBean.circleY = (y + i * lineHeight / 2)
                    circleBitmapBean.bitmap = selectedDateBitmap
                }
                BuyState.OVERDUE -> {
                    circleBitmapBean.dayPaintColor = unEnableColor
                    circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
                    circleBitmapBean.circlePaintColor = selectedDateOverdueColor
                    circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
                    circleBitmapBean.circleY = (y + i * lineHeight / 2)
                    circleBitmapBean.bitmap = selectedDateOverdueBitmap
                }
            }
        }
        circleBitmapBeanListByDay.add(circleBitmapBean)

    }

    /**
     * 按天选择的时候画选中的日期
     */
    private fun createSelectingDateByDayData(dayBean: DayBean, k: Int, i: Int, circleBitmapBean: CircleBitmapBean) {
        if (selectingDateByDay.contains(dayBean)) {
            //根据原型，是先画圆在画图
            val dst = Rect()
            dst.set(((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter - selectedDateOverdueBitmap?.height!!).toInt(), ((paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5)) + bitmapMarginCircleCenter + selectedDateOverdueBitmap?.width!!).toInt()), (y + i * lineHeight / 2 - bitmapMarginCircleCenter).toInt())
            circleBitmapBean.dst = dst
            circleBitmapBean.dayPaintColor = Color.parseColor("#ffffff")
            circleBitmapBean.dayPaintTypeface = Typeface.DEFAULT
            circleBitmapBean.circlePaintColor = selectingLineColor
            circleBitmapBean.circleX = (paddingLeft + ((viewWidth - paddingLeft - paddingRight) / 7 * (k - 0.5))).toFloat()
            circleBitmapBean.circleY = (y + i * lineHeight / 2)
            circleBitmapBean.bitmap = selectingDateBitmap
        }
        circleBitmapBeanListBySelectingDay.add(circleBitmapBean)
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
        //按天买
        if (selectedDateByDay.isNotEmpty() && circleBitmapBeanListByDay.isNotEmpty()) {
            circleBitmapBeanListByDay.forEach {
                if (selectedDateByDay.contains(it.dayBean)) {
                    circlePaint.color = it.circlePaintColor!!

                    drawCircle(canvas, it.circleX!!, it.circleY!!, selectedDateRadius, circlePaint)
                    drawBitmap(canvas, it.dst!!, it.bitmap)
                }
            }

        }
        if (selectingDateByDay.isNotEmpty() && circleBitmapBeanListBySelectingDay.isNotEmpty()) {
            circleBitmapBeanListBySelectingDay.forEach {
                if (selectingDateByDay.contains(it.dayBean)) {
                    circlePaint.color = it.circlePaintColor!!
                    drawCircle(canvas, it.circleX!!, it.circleY!!, selectedDateRadius, circlePaint)
                    drawBitmap(canvas, it.dst!!, it.bitmap)
                }
            }
        }
        //画买过的矩形框（按月和按季买）
        if (selectedDayByMonthOrSeason.isNotEmpty() && circleBitmapBeanListByMonth.isNotEmpty()) {
            circleBitmapBeanListByMonth.forEach {
                drawSelectedDateByMonthOrSeason(it, canvas)
            }

        }
        if (selectingDayByMonthOrSeason.isNotEmpty()&&circleBitmapBeanListBySelectingMonth.isNotEmpty()) {
            circleBitmapBeanListBySelectingMonth.forEach {
                drawSelectingDateByMonthOrSeason(it, canvas)
            }
        }
        circleBitmapBeanDay.forEach {
            dayPaint.color = it.dayPaintColor!!
            dayPaint.typeface = it.dayPaintTypeface
            canvas?.drawText(it.content, it.textX!!, it.textY!!, dayPaint)
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
                drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
            }
            4, 6 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                drawSelectingLeftRect(canvas, circleBitmapBean)
                drawSelectingLine(canvas, circleBitmapBean)
                drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
            }
            8 -> {
                circlePaint.color = circleBitmapBean.circlePaintColor!!
                when (circleBitmapBean.k) {
                    1 -> {
                        drawCircle(canvas, circleBitmapBean.circleX!!, circleBitmapBean.circleY!!, selectedDateRadius, circlePaint)
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
     * @param isEnable 是否非当月的日期
     * @param paint 画笔
     */
    private fun setMonthDayPaintColor(year: Int, month: Int, day: Int, isCurrentMonthDay: Boolean, circleBitmapBean: CircleBitmapBean) {
        dayState = when {
            year > currentYear -> DayState.ENABLE
            year == currentYear -> when {
                month > currentMonth -> DayState.ENABLE
                month == currentMonth -> when {
                    day > currentDay + 3 -> DayState.ENABLE
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

    fun refreshView() {
        Observable.create(
                ObservableOnSubscribe<Int> {
                    createMonthContentData()
                    it.onNext(1)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    invalidate()
                }
        createDrawWeekData()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.x
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (Math.abs(event.x - startX) <= ViewConfiguration.get(context).scaledTouchSlop &&
                        Math.abs(event.y - startY) <= ViewConfiguration.get(context).scaledTouchSlop) {
                    TouchMannger.monthDayBeanRect.forEach {
                        if (it.contains(event.x.toInt(), event.y.toInt())) {
                            when (useBuyType) {
                                BuyType.DAY -> {
                                    val dayBean = TouchMannger.monthAllDayBean[TouchMannger.monthDayBeanRect.indexOf(it)]
                                    //按天选选中的日期里面有这个日期
                                    if (selectedDateByDay.isNotEmpty()) {
                                        if (selectedDateByDay.contains(dayBean)) {
                                            monthViewClick?.unClick(dayBean, BuyType.DAY, "您已经选过该日期")
                                        }
                                    }
                                    if (selectedDayByMonthOrSeason != null) {

                                    }
                                }
                                BuyType.MONTH -> {
                                }
                                BuyType.SEASON -> {
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }
}

data class WeekBean(val weekPaintColor: Int, val content: String)
data class CircleBitmapBean(var dayPaintColor: Int? = 0,
                            var dayPaintTypeface: Typeface? = Typeface.DEFAULT,
                            var circlePaintColor: Int? = 0,
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
    fun click(dayBean: DayBean, buyType: BuyType)
    fun unClick(dayBean: DayBean, buyType: BuyType, message: String)
}


