package com.lindroid.pullrefreshloadmoredemo.utils

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * @author Lin
 * @date 2019/3/28
 * @function
 * @Description
 */

/**
 * 上拉加载或下拉刷新成功
 * @param newData : 新请求的数据
 * @param pageNo : 当前页码
 * @param newData : 新请求的数据
 * @param canEmptyRefresh: 页面数据为空是否允许下拉刷新
 * @param pageSize: 每页请求的数据量
 */
fun <T : Any> refreshLoadMoreSuccess(
    refreshLayout: SmartRefreshLayout,
    adapter: BaseQuickAdapter<T, BaseViewHolder>,
    newData: List<T>,
    pageNo: Int,
    canEmptyRefresh: Boolean = true,
    pageSize: Int = 20
) {
    when (pageNo == 1) {
        true -> {
            //下拉刷新，设置新数据
            adapter.setNewData(newData)
            //列表没有填满一页时不能上拉加载
            adapter.disableLoadMoreIfNotFullPage()
            if (adapter.data.isEmpty() && !canEmptyRefresh) {
                refreshLayout.setEnableRefresh(false)
            } else {
                //显示加载视图时会禁止下拉，所以需要将下拉刷新打开
                refreshLayout.setEnableRefresh(true)
                refreshLayout.finishRefresh(true)
            }
        }
        false -> {
            //上拉加载，添加新数据
            adapter.addData(newData)
            if (newData.size != pageSize) {
                //列表数据已经加载完毕，false表示会显示“没有更多数据了”的底部布局
                adapter.loadMoreEnd(false)
            } else {
                //本次加载完成
                adapter.loadMoreComplete()
            }
        }
    }
}

/**
 * 上拉加载或下拉刷新失败
 * @param pageNo : 当前页码
 * @param canEmptyRefresh: 页面数据为空是否允许下拉刷新
 */
fun <T : Any> refreshLoadMoreFail(
    refreshLayout: SmartRefreshLayout,
    adapter: BaseQuickAdapter<T, BaseViewHolder>,
    pageNo: Int,
    canEmptyRefresh: Boolean = true
) {
    when (pageNo == 1) {
        true -> {
            if (adapter.data.isEmpty() && !canEmptyRefresh) {
                refreshLayout.setEnableRefresh(false)
            } else {
                //显示加载视图时会禁止下拉，所以需要将下拉刷新打开
                refreshLayout.setEnableRefresh(true)
                refreshLayout.finishRefresh(false)
            }
        }
        false -> {
            //上拉加载失败
            adapter.loadMoreFail()
        }
    }
}