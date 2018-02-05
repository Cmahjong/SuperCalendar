package com.yinjin.supercalendar

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * desc:
 * time: 2018/2/5 0005
 * @author yinYin
 */
class App : Application() {
    var mRefWatcher: RefWatcher? = null

    override fun onCreate() {
        super.onCreate()
        mRefWatcher = LeakCanary.install(this)
        LeakCanary.install(this)
    }

    companion object {
        var app: App? = App()
    }
}