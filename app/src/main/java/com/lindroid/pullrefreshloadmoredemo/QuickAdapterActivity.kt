package com.lindroid.pullrefreshloadmoredemo

import android.os.Bundle
import android.os.Handler
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
class QuickAdapterActivity : AppCompatActivity() {
    private var pageNo = 1
    private lateinit var adapter: BaseQuickAdapter<MovieBean.Subject, BaseViewHolder>
//    protected val newDatas: MutableList<MovieBean.Subject> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_adapter)
        swMovie.setColorSchemeResources(R.color.colorPrimary)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        initAdapter()
        initListener()
        getDataList(isShowLoading = true)
    }

    private fun initAdapter() {
        adapter = BaseSimpleAdapter(android.R.layout.simple_list_item_1) { helper, item ->
            helper.setText(android.R.id.text1, item.title)
        }
        adapter.setLoadMoreView(CustomLoadMoreView())
        rvMovie.adapter = adapter
    }

    private fun initListener() {
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

    private fun createData(pageSize: Int): List<MovieBean.Subject> {
        val newDatas: MutableList<MovieBean.Subject> = ArrayList()
        if (pageSize == 0 || pageNo > 1 + 2) {
            newDatas.addAll(listOf())
        } else {
            for (i in (1..pageSize)) {
                newDatas.add(MovieBean.Subject(title = "电影${(pageNo - 1) * 20 + i}"))
            }
        }
        return newDatas
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
