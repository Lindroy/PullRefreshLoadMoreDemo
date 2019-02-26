package com.lindroid.pullrefreshloadmoredemo.activity

import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.lindroid.pullrefreshloadmoredemo.R
import com.lindroid.pullrefreshloadmoredemo.START_PAGE_NO
import com.lindroid.pullrefreshloadmoredemo.base.BaseActivity
import com.lindroid.pullrefreshloadmoredemo.bean.MovieBean
import com.lindroid.pullrefreshloadmoredemo.request.MovieRequest
import com.lindroid.pullrefreshloadmoredemo.utils.*
import com.lindroid.pullrefreshloadmoredemo.utils.request.OnOkHttpCallback
import com.lindroid.pullrefreshloadmoredemo.utils.request.post
import com.lindroid.pullrefreshloadmoredemo.view.CustomLoadMoreView
import com.lindroid.utils.isNetworkConnect
import com.lindroid.utils.shortToast
import kotlinx.android.synthetic.main.activity_quick_adapter.*

private const val TAG = "QuickAdapterActivity"

/**
 * @author Lin
 * @date 2019/2/25
 * @function SwipeRefreshLayout+BaseQuickAdapter实现上拉加载下拉刷新
 * @Description
 */
class QuickAdapterActivity(override val contentViewId: Int = R.layout.activity_quick_adapter) :
    BaseActivity() {

    override fun initView() {
        super.initView()
        initToolBar(R.string.refresh_load_quick_adapter)
        swMovie.setColorSchemeResources(R.color.colorPrimary)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        initAdapter()

    }

    override fun initData() {
        super.initData()
        getDataList(isShowLoading = true)
    }

    private fun initAdapter() {
        adapter.setLoadMoreView(CustomLoadMoreView())
        rvMovie.adapter = adapter
    }

    override fun initOnClick() {
        swMovie.setOnRefreshListener {
            pageNo = 1
            getDataList()
        }
        statusView.setOnRetryClickListener {
            pageNo = 1
            getDataList(isShowLoading = true)
        }

        adapter.setOnLoadMoreListener({
            getDataList()
        }, rvMovie)
        btnFew.setOnClickListener {
            pageNo = 1
            getDataList(5, true, true)
        }
        btnEmptyRefresh.setOnClickListener {
            pageNo = 1
            getDataList(0, true, true)
        }
        btnEmptyNoRefresh.setOnClickListener {
            pageNo = 1
            getDataList(0, isShowLoading = true, canEmptyRefresh = false)
        }
    }

    private fun getDataList(
        pageSize: Int = 20,
        isShowLoading: Boolean = false,
        canEmptyRefresh: Boolean = true
    ) {
        if (isShowLoading) {
            statusView.showLoadingView()
        }

        Handler().postDelayed({
            when (isNetworkConnect()) {
                true -> {
                    shortToast("请求成功")
                    adapter.finishLoadMore(createData(pageSize), pageNo)
                    swMovie.finishRefresh(adapter.data.isEmpty(), canEmptyRefresh)
                    statusView.showSuccessView(adapter.data.isEmpty())
                    pageNo++
                }
                false -> {
                    shortToast("网络异常")
                    statusView.showFailedView(adapter.data.isEmpty())
                    swMovie.finishRefresh(adapter.data.isEmpty())
                    adapter.loadMoreFail()
                }
            }
        }, 1500)
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
                statusView.showFailedView(adapter.data.isEmpty())
                swMovie.isRefreshing = false
                adapter.loadMoreFail()
            }

            override fun onSuccess(result: Any) {
                swMovie.isRefreshing = false
                val movieBean = result as MovieBean
                val data = ArrayList<MovieBean.Subject>()
                data.addAll(movieBean.subjects)
                if (pageNo == START_PAGE_NO) {
                    adapter.setNewData(data)
                } else {
                    adapter.addData(data)
                    if (data.isNotEmpty()) {
                        adapter.loadMoreComplete()
                    } else {
                        adapter.loadMoreEnd()
                    }
                }
                statusView.showSuccessView(adapter.data.isEmpty())
                pageNo++
            }
        })
    }
}
