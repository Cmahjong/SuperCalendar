package com.yinjin.supercalendar

import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val selectedDate = ArrayList<DayBean>()
        selectedDate.add(DayBean(2018, 1, 1))
        selectedDate.add(DayBean(2018, 1, 2))
        selectedDate.add(DayBean(2018, 1, 3))

        assertEquals(true,selectedDate.contains(DayBean(2018, 1, 1)))
        assertEquals(true,selectedDate.contains(DayBean(2018, 1, 4)))
    }
}
