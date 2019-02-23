package com.lindroid.pullrefreshloadmoredemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lindroid.pullrefreshloadmoredemo.bean.MovieBean
import com.lindroid.pullrefreshloadmoredemo.request.MovieRequest
import com.lindroid.pullrefreshloadmoredemo.utils.*
import com.lindroid.pullrefreshloadmoredemo.utils.request.OnOkHttpCallback
import com.lindroid.pullrefreshloadmoredemo.utils.request.post
import com.lindroid.utils.shortToast
import kotlinx.android.synthetic.main.activity_smart_refresh.*


class SmartRefreshActivity : AppCompatActivity() {
    private var pageNo = START_PAGE_NO

    private var enableEmptyRefresh = true

    private var isSetEmptyData = false

    private lateinit var adapter: BaseQuickAdapter<MovieBean.Subject, BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_refresh)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        //数据没有填满一页时不能上拉加载更多，也可以在xml中设置
        rfMovie.setEnableLoadMoreWhenContentNotFull(false)
        initAdapter()
        initListener()
        getMovies(isShowLoading = true)
    }

    private fun initListener() {
        rfMovie.setOnRefreshListener {
            isSetEmptyData = false
            pageNo = START_PAGE_NO
            getMovies()
        }
        rfMovie.setOnLoadMoreListener {
            getMovies()
        }
        statusView.setOnRetryClickListener {
            isSetEmptyData = false
            pageNo = START_PAGE_NO
            getMovies(isShowLoading = true)
        }
        //数据较少没有填满页面时，只能下拉不能上拉加载
        btnFew.setOnClickListener {
            isSetEmptyData = false
            pageNo = START_PAGE_NO
            getMovies(pageSize = 5, isShowLoading = true)
        }
        //数据为空，不能上拉加载，但可以下拉刷新
        btnEmptyRefresh.setOnClickListener {
            enableEmptyRefresh = true
            isSetEmptyData = true
            adapter.data.clear()
            pageNo = START_PAGE_NO
            getMovies(isShowLoading = true)
        }
        //数据为空，不能上拉加载，也不能下拉刷新
        btnEmptyNoRefresh.setOnClickListener {
            enableEmptyRefresh = false
            isSetEmptyData = true
            pageNo = START_PAGE_NO
            getMovies(isShowLoading = true)
        }
    }

    private fun initAdapter() {
        adapter = BaseSimpleAdapter(android.R.layout.simple_list_item_1) { helper, item ->
            helper.setText(android.R.id.text1, item.title)
        }
        rvMovie.adapter = adapter
    }

    /**
     * 获取电影数据
     */
    private fun getMovies(pageSize: Int = 20, isShowLoading: Boolean = false) {
        if (isShowLoading) {
            statusView.showLoadingView()
        }
        MovieRequest(pageNo, pageSize).post<MovieBean>(object : OnOkHttpCallback {
            override fun onFailure(code: Int, msg: String) {
                shortToast(msg)
                rfMovie.refreshWhenFail(adapter.data.isEmpty())
                statusView.showFailedView(adapter.data.isEmpty())
            }

            override fun onSuccess(result: Any) {
                val movieBean = result as MovieBean
                val data = ArrayList<MovieBean.Subject>()
                if (!isSetEmptyData) {
                    data.addAll(movieBean.subjects)
                }
                statusView.showSuccessView(data.isEmpty())
                rfMovie.refreshWhenSuccess(adapter, data, pageNo, enableEmptyRefresh)
                pageNo++
            }
        })
    }
}
