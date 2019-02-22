package com.lindroid.pullrefreshloadmoredemo.app

import android.app.Application

/**
 * @author Lin
 * @date 2019/2/21
 * @function
 * @Description
 */
class App:Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
    }
}