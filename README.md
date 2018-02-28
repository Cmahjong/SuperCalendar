# SuperCalendar

日历控件
[![](https://jitpack.io/v/yinjinyj/SuperCalendar.svg)](https://jitpack.io/#yinjinyj/SuperCalendar)

使用方法

Step 1 Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.yinjinyj:SuperCalendar:1.0.11'
	}
  
Step 3 配合viewPager使用成滑动的日历控件

 使用方法请查看sample
 
 

常用的属性
  
         <!--未选中的text文字大小-->
        <attr name="normalTextSize" format="dimension"/>
        <!--未选中的text文字颜色-->
        <attr name="normalTextColor" format="color"/>
        <!--未选中的text背景颜色-->
        <attr name="normalTextBackground" format="color"/>
        <!--选中的text文字大小-->
        <attr name="selectedTextSize" format="dimension"/>
        <!--选中的text文字颜色-->
        <attr name="selectedTextColor" format="color"/>
        <!--选中的text背景颜色-->
        <attr name="selectedTextBackground" format="color"/>
        <!--周末颜色 周天和周六-->
        <attr name="weekendColor" format="color"/>
        <!--工作日颜色 周一到周五-->
        <attr name="workingColor" format="color"/>
        <!--周末文字大小-->
        <attr name="weekTextSize" format="dimension"/>
        <!--周末文字大小-->
        <attr name="unEnableTextColor" format="color"/>
        <!--选择过日期小于当前月的的圈的背景颜色-->
        <attr name="selectedDateOverdueColor" format="color"/>
        <!--选择过日期小于当前月的的圈的背景颜色-->
        <attr name="selectedDateColor" format="color"/>
        <!--选择过的圈的大小-->
        <attr name="selectedDateRadius" format="dimension"/>
        <!--选择过的圈的大小-->
        <attr name="currentDayColor" format="dimension"/>
        <!--正在选中时线的颜色-->
        <attr name="selectingLineColor" format="dimension"/>
        <!--选择中背景颜色-->
        <attr name="selectingDateColor" format="color"/>
        <!--每一行的高度-->
        <attr name="lineHeight" format="dimension"/>
        <!--标题的高度-->
        <attr name="titleHeight" format="dimension"/>
        <attr name="bitmapMarginCircleCenter" format="dimension"/>

    新增一个参数viewHeight，用来获取MonthView的高度
效果图

![img](/img/1.png)

![img](/img/2.png)

![img](/img/3.png)

![img](/img/4.png)
