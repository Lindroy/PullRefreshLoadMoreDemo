package com.lindroid.pullrefreshloadmoredemo.app

import android.app.Application
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

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
        //初始化上拉加载下拉刷新控件，默认为经典样式
        private fun initRefreshLayout() {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(android.R.color.transparent)
                ClassicsHeader(context) as RefreshHeader
            }

            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                ClassicsFooter(context)
            }
        }

        init {
            initRefreshLayout()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}