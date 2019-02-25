package com.lindroid.pullrefreshloadmoredemo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lindroid.pullrefreshloadmoredemo.bean.MovieBean
import com.lindroid.pullrefreshloadmoredemo.request.MovieRequest
import com.lindroid.pullrefreshloadmoredemo.utils.*
import com.lindroid.pullrefreshloadmoredemo.utils.request.OnOkHttpCallback
import com.lindroid.pullrefreshloadmoredemo.utils.request.post
import com.lindroid.utils.isNetworkConnect
import com.lindroid.utils.shortToast
import kotlinx.android.synthetic.main.activity_quick_adapter.*

private const val TAG = "QuickAdapterActivity"

class QuickAdapterActivity : AppCompatActivity() {
    private var pageNo = 1
    private lateinit var adapter: BaseQuickAdapter<MovieBean.Subject, BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_adapter)
        rvMovie.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        initAdapter()
        initListener()
        getDataList(true)
    }

    private fun initAdapter() {
        adapter = BaseSimpleAdapter(android.R.layout.simple_list_item_1) { helper, item ->
            helper.setText(android.R.id.text1, item.title)
        }
        rvMovie.adapter = adapter
    }

    private fun initListener() {
        swMovie.setOnRefreshListener {
            pageNo = 1
            getDataList()
        }

        adapter.setOnLoadMoreListener({
            Log.d(TAG, "下拉刷新")
            getDataList()
        }, rvMovie)
    }

    private fun getDataList(isShowLoading: Boolean = false) {
        if (isShowLoading) {
            statusView.showLoadingView()
        }
        val newDatas: MutableList<MovieBean.Subject> = ArrayList()
        if (pageNo > 1 + 2) {
            newDatas.addAll(listOf())
        } else {
            for (i in (1..20)) {
                newDatas.add(MovieBean.Subject(title = "电影${(pageNo - 1) * 20 + i}"))
            }
        }

        Handler().postDelayed({
            when (isNetworkConnect()) {
                true -> {
                    swMovie.isRefreshing = false
                    adapter.finishLoadMore(newDatas, pageNo)
                    statusView.showSuccessView(adapter.data.isEmpty())
                    pageNo++
                }
                false -> {
                    statusView.showFailedView(adapter.data.isEmpty())
                    swMovie.isRefreshing = false
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
