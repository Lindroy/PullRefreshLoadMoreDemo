package com.lindroid.pullrefreshloadmoredemo.utils

import android.support.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author Lin
 * @date 2019/2/21
 * @function BaseRecyclerViewAdapter工具类
 * @Description
 */

/**
 * 获取真实的头布局个数
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.getRealHeaderCount() =
    if (headerLayout == null) 0 else headerLayout.childCount

/**
 * 获取真实的脚布局个数
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.getRealFooterCount() =
    if (footerLayout == null) 0 else footerLayout.childCount


/**
 * Lambda表达式形式的BaseQuickAdapter类
 * @param layoutId:列表item布局Id
 */
class BaseSimpleAdapter<T>(@LayoutRes layoutId: Int, private val convertListener: (helper: BaseViewHolder, item: T) -> Unit) :
    BaseQuickAdapter<T, BaseViewHolder>(layoutId) {
    override fun convert(helper: BaseViewHolder, item: T) {
        convertListener.invoke(helper, item)
    }
}

/**
 * BaseQuickAdapter实现下拉加载更多功能
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.finishLoadMore(newData: List<T>, pageNo: Int) {
    if (pageNo == 1) {
        data.clear()
        setNewData(newData)
    } else {
        addData(newData)
        if (newData.isNotEmpty()) {
            loadMoreComplete()
        } else {
            loadMoreEnd()
        }
    }
}


