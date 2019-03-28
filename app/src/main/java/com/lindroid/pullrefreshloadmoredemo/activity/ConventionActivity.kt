package com.lindroid.pullrefreshloadmoredemo.activity

import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.lindroid.pullrefreshloadmoredemo.R
import com.lindroid.pullrefreshloadmoredemo.base.BaseActivity
import com.lindroid.pullrefreshloadmoredemo.utils.showContentView
import com.lindroid.pullrefreshloadmoredemo.utils.showEmptyView
import com.lindroid.pullrefreshloadmoredemo.utils.showLoadingView
import com.lindroid.utils.isNetworkConnect
import com.lindroid.utils.shortToast
import com.scwang.smartrefresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.activity_convention.*

/**
 * @author Lin
 * @date 2019/3/27
 * @function 常规写法
 * @Description
 */
class ConventionActivity(override val contentViewId: Int = R.layout.activity_convention) :
    BaseActivity() {

    private val pageSize = 20

    override fun initView() {
        super.initView()
        initToolBar(R.string.refresh_load_convention)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        //数据没有填满一页时不能上拉加载更多，也可以在xml中设置
        rfMovie.setEnableLoadMoreWhenContentNotFull(false)
        rvMovie.adapter = adapter
    }

    override fun initData() {
        super.initData()
        getData(isShowLoading = true)
    }

    override fun initOnClick() {
        super.initOnClick()
        rfMovie.setOnRefreshListener {
            pageNo = 1
            getData()
        }
        rfMovie.setOnLoadMoreListener {
            getData()
        }
        statusView.setOnRetryClickListener {
            pageNo = 1
            getData(isShowLoading = true)
        }
    }

    private fun getData(isShowLoading: Boolean = false, canEmptyRefresh: Boolean = true) {
        if (isShowLoading) {
            statusView.showLoadingView()
            //加载时禁止下拉刷新
            rfMovie.setEnableRefresh(false)
        }
        val newData = createData(pageSize)
        Handler().postDelayed({
            when (isNetworkConnect()) {
                //网络请求数据成功
                true -> {
                    shortToast("请求成功")
                    if (pageNo == 1) {
                        adapter.setNewData(newData)
                        //是否可以下拉刷新，综合canEmptyRefresh和页面数据是否为空判断
                        val enableRefresh = canEmptyRefresh || adapter.data.isNotEmpty()
                        rfMovie.setEnableRefresh(enableRefresh)
                        if (rfMovie.state == RefreshState.Refreshing) {
                            rfMovie.finishRefresh()
                        }
                        //没有数据时禁止上拉加载
                        rfMovie.setEnableLoadMore(adapter.data.isNotEmpty())
                    } else {
                        adapter.addData(newData)
                        //请求数据成功后，根据数据大小确定是否显示“没有数据”的脚布局
                        if (newData.size != pageSize) {
                            rfMovie.finishLoadMoreWithNoMoreData()
                        } else {
                            rfMovie.finishLoadMore()
                        }
                    }
                    //处理多状态视图
                    if (adapter.data.isEmpty()) {
                        statusView.showEmptyView()
                    } else {
                        statusView.showContentView()
                    }
                    pageNo++
                }
                //网络请求数据失败
                false -> {
                    shortToast("网络异常")
                    if (adapter.data.isEmpty()) {
                        //页面没有数据应禁止上拉和下拉
                        rfMovie.setEnableRefresh(false)
                        rfMovie.setEnableLoadMore(false)
                        //页面没有数据才显示失败视图
                        when (isNetworkConnect()) {
                            true -> statusView.showError()
                            false -> statusView.showNoNetwork()
                        }
                    } else {
                        rfMovie.finishRefresh(false)
                        rfMovie.finishLoadMore(false)
                    }
                }
            }
        }, 1500)
    }
}
