package com.lindroid.pullrefreshloadmoredemo.activity

import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.lindroid.pullrefreshloadmoredemo.R
import com.lindroid.pullrefreshloadmoredemo.base.BaseActivity
import com.lindroid.pullrefreshloadmoredemo.utils.*
import com.lindroid.pullrefreshloadmoredemo.view.CustomLoadMoreView
import com.lindroid.utils.isNetworkConnect
import com.lindroid.utils.shortToast
import kotlinx.android.synthetic.main.activity_smart_quick.*

/**
 * @author Lin
 * @date 2019/2/26
 * @function SmartRefreshLayout+BaseQuickAdapter实现上拉刷新和下拉加载
 * @Description
 */
class SmartQuickActivity(override val contentViewId: Int = R.layout.activity_smart_quick) :
    BaseActivity() {

    override fun initView() {
        super.initView()
        initToolBar(R.string.refresh_load_smart_quick)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        rfMovie.setEnableLoadMore(false)
        rfMovie.setEnableLoadMoreWhenContentNotFull(false)
        initAdapter()
    }

    private fun initAdapter() {
        adapter.setLoadMoreView(CustomLoadMoreView())
        rvMovie.adapter = adapter
    }

    override fun initData() {
        super.initData()
        getDataList(isShowLoading = true)
    }

    override fun initOnClick() {
        super.initOnClick()
        rfMovie.setOnRefreshListener {
            pageNo = 1
            getDataList()
        }
        statusView.setOnRetryClickListener {
            pageNo = 1
            adapter.data.clear()
            getDataList(isShowLoading = true)
        }
        adapter.setOnLoadMoreListener({
            getDataList()
        }, rvMovie)

        btnFew.setOnClickListener {
            pageNo = 1
            adapter.data.clear()
            getDataList(5, isShowLoading = true, canEmptyRefresh = true)
        }

        btnEmptyRefresh.setOnClickListener {
            pageNo = 1
            adapter.data.clear()
            getDataList(0, isShowLoading = true, canEmptyRefresh = true)
        }
        btnEmptyNoRefresh.setOnClickListener {
            pageNo = 1
            adapter.data.clear()
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
            //加载时禁止下拉刷新
            rfMovie.setEnableRefresh(false)
        }
        val newData = createData(pageSize)
        Handler().postDelayed({
            when (isNetworkConnect()) {
                //请求成功
                true -> {
                    shortToast("请求成功")
//                    adapter.finishUpdateData(newData, pageNo)
                    /*  adapter.loadMoreDataSuccess(newData, pageNo, pageSize)
                      rfMovie.finishRefreshWithAdapterSuccess(
                          pageNo,
                          adapter.data.isEmpty(),
                          canEmptyRefresh
                      )*/
                    refreshLoadMoreSuccess(rfMovie, adapter, newData, pageNo, canEmptyRefresh)
                    statusView.showSuccessView(adapter.data.isEmpty())
                    pageNo++
                }
                //请求失败
                false -> {
                    shortToast("网络异常")
                    /* adapter.loadMoreDataFail(pageNo)
                     rfMovie.finishRefreshWithAdapterFail(
                         pageNo,
                         adapter.data.isEmpty(),
                         canEmptyRefresh
                     )*/
                    refreshLoadMoreFail(rfMovie, adapter, pageNo, canEmptyRefresh)
                    statusView.showFailedView(adapter.data.isEmpty())
                }
            }
        }, 1500)
    }
}
