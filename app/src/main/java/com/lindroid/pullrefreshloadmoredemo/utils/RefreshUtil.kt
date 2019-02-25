package com.lindroid.pullrefreshloadmoredemo.utils

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lindroid.pullrefreshloadmoredemo.START_PAGE_NO
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * @author Lin
 * @date 2019/2/22
 * @function
 * @Description
 */

/**
 * 请求成功时刷新列表
 * @param canEmptyRefresh: 页面数据为空时是否允许上拉和下拉
 */
fun <T : Any> SmartRefreshLayout.refreshWhenSuccess(
    adapter: BaseQuickAdapter<T, BaseViewHolder>,
    newData: List<T>,
    pageNo: Int,
    canEmptyRefresh: Boolean = true,
    pageSize: Int = 20
) {
    when (pageNo == START_PAGE_NO) {
        true -> {
            adapter.data.clear()
            adapter.setNewData(newData)
            //是否可以下拉和上拉，综合canEmptyRefresh和页面数据是否为空判断
            val enableRefreshAndLoad = canEmptyRefresh || adapter.data.isNotEmpty()
            setEnableRefresh(enableRefreshAndLoad)
            setEnableLoadMore(adapter.data.isNotEmpty())
            finishRefresh()
        }
        false -> {
            adapter.addData(newData)
            finishLoadMoreWithResult(newData, pageSize)
        }
    }
}

/**
 * 请求失败
 * 页面没有数据时应禁止上拉和下拉
 */
fun SmartRefreshLayout.refreshWhenFail(isEmptyPage: Boolean = true) {
    if (isEmptyPage) {
        setEnableRefresh(false)
        setEnableLoadMore(false)
    } else {
        finishRefresh(false)
        finishLoadMore(false)
    }

}

/**
 * 请求数据成功后，根据数据大小确定是否显示“没有数据”的脚布局
 */
fun SmartRefreshLayout.finishLoadMoreWithResult(result: List<Any>, pageSize: Int = 20) {
    if (result.size != pageSize) {
        finishLoadMoreWithNoMoreData()
    } else {
        finishLoadMore()
    }
}