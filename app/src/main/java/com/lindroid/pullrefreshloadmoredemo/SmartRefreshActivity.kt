package com.lindroid.pullrefreshloadmoredemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lindroid.pullrefreshloadmoredemo.bean.MovieBean
import com.lindroid.pullrefreshloadmoredemo.request.MovieRequest
import com.lindroid.pullrefreshloadmoredemo.utils.request.OnOkHttpCallback
import com.lindroid.pullrefreshloadmoredemo.utils.request.post

class SmartRefreshActivity : AppCompatActivity() {
    private val TAG = "tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_refresh)
        MovieRequest(1,5).post<MovieBean>(object : OnOkHttpCallback {
            override fun onFailure(code: Int, msg: String) {

            }

            override fun onSuccess(result: Any) {
                val movieBean = result as MovieBean
            }

        })
    }
}
