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
 * BaseQuickAdapter实现上拉加载更多功能
 * @param isGoneEndView:加载完所有的数据时是否去除“没有更多数据了”的底部布局
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.finishUpdateData(
    newData: List<T>,
    pageNo: Int,
    isGoneEndView: Boolean = false
) {
    if (pageNo == 1) {
        //下拉刷新，设置新数据
        setNewData(newData)
        //列表没有填满一页时不能上拉加载
        disableLoadMoreIfNotFullPage()
    } else {
        //上拉加载，添加新数据
        addData(newData)
        if (newData.isNotEmpty()) {
            //新数据不为空，本次加载完成
            loadMoreComplete()
        } else {
            //新数据为空，说明列表数据已经加载完毕，false表示会显示“没有更多数据了”的底部布局
            loadMoreEnd(isGoneEndView)
        }
    }
}

/**
 * BRAH上拉加载数据成功
 * @param newData: 新加载的数据
 * @param pageNo : 当前页码
 * @param pageSize: 每页加载的数据量
 * @param isGoneEndView: 加载完所有的数据时是否去除“没有更多数据了”的底部布局，默认为false
 *
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.loadMoreDataSuccess(
    newData: List<T>,
    pageNo: Int,
    pageSize: Int = 20,
    isGoneEndView: Boolean = false
) {
    when (pageNo == 1) {
        true -> {
            //下拉刷新，设置新数据
            setNewData(newData)
            //列表没有填满一页时不能上拉加载
            disableLoadMoreIfNotFullPage()
        }
        false -> {
            //上拉加载，添加新数据
            addData(newData)
            if (newData.size != pageSize) {
                //本次加载完成
                loadMoreComplete()
            } else {
                //列表数据已经加载完毕，false表示会显示“没有更多数据了”的底部布局
                loadMoreEnd(isGoneEndView)
            }
        }
    }
}

/**
 * BRAH上拉加载失败
 * @param pageNo: 当前页码
 */
fun <T : Any> BaseQuickAdapter<T, BaseViewHolder>.loadMoreDataFail(pageNo: Int) {
    if (pageNo > 1) {
        loadMoreFail()
    }
}


