package com.lindroid.pullrefreshloadmoredemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lindroid.pullrefreshloadmoredemo.bean.MovieBean
import com.lindroid.pullrefreshloadmoredemo.request.MovieRequest
import com.lindroid.pullrefreshloadmoredemo.utils.BaseSimpleAdapter
import com.lindroid.pullrefreshloadmoredemo.utils.request.OnOkHttpCallback
import com.lindroid.pullrefreshloadmoredemo.utils.request.post
import kotlinx.android.synthetic.main.activity_smart_refresh.*

class SmartRefreshActivity : AppCompatActivity() {
    private lateinit var adapter: BaseQuickAdapter<MovieBean.Subject, BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_refresh)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        //数据没有填满一页时不能上拉加载更多，也可以在xml中设置
        rfMovie.setEnableLoadMoreWhenContentNotFull(false)
        initAdapter()
        getMovies()
    }

    private fun initAdapter() {
        adapter = BaseSimpleAdapter(android.R.layout.simple_list_item_1) { helper, item ->
            helper.setText(android.R.id.text1, item.title)
        }
        rvMovie.adapter = adapter
    }

    private fun getMovies() {
        MovieRequest(1, 5).post<MovieBean>(object : OnOkHttpCallback {
            override fun onFailure(code: Int, msg: String) {

            }

            override fun onSuccess(result: Any) {
                val movieBean = result as MovieBean
                adapter.setNewData(movieBean.subjects)
            }

        })
    }
}
