package com.lindroid.pullrefreshloadmoredemo.utils

import android.support.v4.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState

/**
 * @author Lin
 * @date 2019/2/22
 * @function
 * @Description
 */

/**
 * SmartRefreshLayout实现下拉刷新和上拉加载
 * 请求成功时刷新列表
 * @param adapter : 适配器
 * @param newData: 请求到的数据
 * @param pageNo: 当前页码
 * @param canEmptyRefresh: 页面数据为空时是否允许上拉和下拉
 * @param pageSize: 每页请求的数据数
 */
fun <T : Any> SmartRefreshLayout.refreshWhenSuccess(
    adapter: BaseQuickAdapter<T, BaseViewHolder>,
    newData: List<T>,
    pageNo: Int,
    canEmptyRefresh: Boolean = true,
    pageSize: Int = 20
) {
    when (pageNo == 1) {
        true -> {
            //进入页面或下拉刷新请求数据
            adapter.setNewData(newData)
            //是否可以下拉刷新，综合canEmptyRefresh和页面数据是否为空判断
            val enableRefresh = canEmptyRefresh || adapter.data.isNotEmpty()
            setEnableRefresh(enableRefresh)

            /* if (enableRefresh) {
                 //如果上一步没有禁止下拉刷新，此处应该停止下拉刷新
                 finishRefresh()
             }*/
            if (state == RefreshState.Refreshing) {
                finishRefresh()
            }
            //没有数据时禁止上拉加载
            setEnableLoadMore(adapter.data.isNotEmpty())
        }
        false -> {
            //上拉加载
            adapter.addData(newData)
            finishLoadMoreWithResult(newData, pageSize)
        }
    }
}

/**
 * SmartRefreshLayout实现下拉刷新和上拉加载
 * 请求失败，页面没有数据时应禁止上拉和下拉
 * @param isDataEmpty: 页面是否已有数据
 */
fun SmartRefreshLayout.refreshWhenFail(isDataEmpty: Boolean = true) {
    if (isDataEmpty) {
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


/**
 * SmartRefreshLayout+BaseQuickAdapter
 * SmartRefreshLayout负责下拉刷新，页面没有数据时默认不能下拉刷新，除非手动设置canEmptyRefresh为true
 * @param isSuccess: 是否刷新成功
 * @param isDataEmpty: 页面是否有数据
 * @param canEmptyRefresh: 页面数据为空是否允许下拉刷新
 */
private fun SmartRefreshLayout.finishRefreshWithAdapter(
    isSuccess: Boolean,
    pageNo: Int,
    isDataEmpty: Boolean,
    canEmptyRefresh: Boolean = false
) {
    if (pageNo > 1) {
        return
    }
    if (isDataEmpty && !canEmptyRefresh) {
        setEnableRefresh(false)
    } else {
        //显示加载视图时会禁止下拉，所以需要将下拉刷新打开
        setEnableRefresh(true)
        finishRefresh(isSuccess)
    }
}

fun SmartRefreshLayout.finishRefreshWithAdapterSuccess(
    pageNo: Int,
    isDataEmpty: Boolean,
    canEmptyRefresh: Boolean = false
) {
    finishRefreshWithAdapter(true, pageNo, isDataEmpty, canEmptyRefresh)
}


fun SmartRefreshLayout.finishRefreshWithAdapterFail(
    pageNo: Int,
    isDataEmpty: Boolean,
    canEmptyRefresh: Boolean = false
) {
    finishRefreshWithAdapter(false, pageNo, isDataEmpty, canEmptyRefresh)
}

/**
 * SwipeRefreshLayout控件结束下拉刷新
 * 页面没有数据时默认不能下拉刷新，除非手动设置canEmptyRefresh为true
 */
fun SwipeRefreshLayout.finishRefresh(isEmptyPage: Boolean, canEmptyRefresh: Boolean = false) {
    if (isEmptyPage && !canEmptyRefresh) {
        isEnabled = false
    } else {
        isEnabled = true
        if (isRefreshing) {
            isRefreshing = false
        }
    }
}

