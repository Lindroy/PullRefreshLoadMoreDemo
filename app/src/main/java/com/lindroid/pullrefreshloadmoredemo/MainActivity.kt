package com.lindroid.pullrefreshloadmoredemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lindroid.pullrefreshloadmoredemo.utils.launchActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSmart.setOnClickListener {

            launchActivity<SmartRefreshActivity>()
        }
    }
}
