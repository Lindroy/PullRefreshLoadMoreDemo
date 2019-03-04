package com.lindroid.pullrefreshloadmoredemo.activity

import android.support.v7.widget.DividerItemDecoration
import android.widget.LinearLayout
import com.lindroid.pullrefreshloadmoredemo.R
import com.lindroid.pullrefreshloadmoredemo.base.BaseActivity
import com.lindroid.pullrefreshloadmoredemo.utils.*
import com.lindroid.utils.isNetworkConnect
import com.lindroid.utils.shortToast
import kotlinx.android.synthetic.main.activity_smart_refresh.*


/**
 * @author Lin
 * @date 2019/2/25
 * @function SmartRefreshLayout实现下拉刷新和上拉加载
 * @Description
 */
class SmartRefreshActivity(override val contentViewId: Int = R.layout.activity_smart_refresh) :
    BaseActivity() {

    override fun initView() {
        super.initView()
        initToolBar(R.string.refresh_load_smart)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        //数据没有填满一页时不能上拉加载更多，也可以在xml中设置
        rfMovie.setEnableLoadMoreWhenContentNotFull(false)
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
        rfMovie.setOnLoadMoreListener {
            getDataList()
        }
        statusView.setOnRetryClickListener {
            pageNo = 1
            getDataList(isShowLoading = true)
        }
        //数据较少没有填满页面时，只能下拉不能上拉加载
        btnFew.setOnClickListener {
            pageNo = 1
            getDataList(pageSize = 5, isShowLoading = true)
        }
        //数据为空，不能上拉加载，但可以下拉刷新
        btnEmptyRefresh.setOnClickListener {
            pageNo = 1
            getDataList(pageSize = 0, isShowLoading = true)
        }
        //数据为空，不能上拉加载，也不能下拉刷新
        btnEmptyNoRefresh.setOnClickListener {
            pageNo = 1
            getDataList(pageSize = 0, isShowLoading = true, canEmptyRefresh = false)
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
        android.os.Handler().postDelayed({
            when (isNetworkConnect()) {
                true -> {
                    rfMovie.refreshWhenSuccess(
                        adapter,
                        createData(pageSize),
                        pageNo,
                        canEmptyRefresh
                    )
                    statusView.showSuccessView(adapter.data.isEmpty())
                    pageNo++
                }
                false -> {
                    shortToast("网络异常")
                    rfMovie.refreshWhenFail(adapter.data.isEmpty())
                    statusView.showFailedView(adapter.data.isEmpty())
                }
            }
        }, 1500)
    }


}
