package com.yinjin.supercalendar

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import com.yinjin.calendar.MonthView

/**
 * desc:
 * time: 2018/2/3 0003
 * @author yinYin
 */
class MonthViewPager :ViewPager {
    constructor(context: Context):super(context)
    constructor(context: Context,attrs: AttributeSet):super(context,attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val position = currentItem
        val view = findViewById<MonthView>(position)
        view?.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, view.viewHeight)
    }

    /**
     * 计算高度
     *
     * @param measureSpec
     * measureSpec
     * @param view
     * view
     * @return 计算后的高度
     */
    private fun measureHeight(measureSpec: Int, view: MonthView?): Int {
        var result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        if (view != null) {
            result = view.viewHeight
        } else {
            if (specMode == View.MeasureSpec.EXACTLY) {
                result = specSize
            } else {
                result = view?.viewHeight!!
                if (specMode == View.MeasureSpec.AT_MOST) {
                    result = Math.min(result, specSize)
                }
            }
        }
        return result
    }
}