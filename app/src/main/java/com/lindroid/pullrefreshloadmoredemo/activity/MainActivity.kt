package com.lindroid.pullrefreshloadmoredemo.activity

import com.lindroid.pullrefreshloadmoredemo.R
import com.lindroid.pullrefreshloadmoredemo.base.BaseActivity
import com.lindroid.utils.launchActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity(override val contentViewId: Int = R.layout.activity_main) : BaseActivity() {

    override fun initView() {
        super.initView()
        initToolBar("目录", false)
    }

    override fun initOnClick() {
        super.initOnClick()
        btnSmart.setOnClickListener {
            launchActivity<SmartRefreshActivity>()
        }
        btnQuick.setOnClickListener {
            launchActivity<QuickAdapterActivity>()
        }
        btnSmartQuick.setOnClickListener {
            launchActivity<SmartQuickActivity>()
        }
    }

}
