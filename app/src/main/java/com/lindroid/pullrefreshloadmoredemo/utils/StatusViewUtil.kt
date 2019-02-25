package com.lindroid.pullrefreshloadmoredemo.utils

import com.classic.common.MultipleStatusView
import com.lindroid.utils.isNetworkConnect

/**
 * @author Lin
 * @date 2019/2/22
 * @function MultipleStatusView工具类
 * @Description
 */

/**
 * 显示加载中视图
 */
fun MultipleStatusView.showLoadingView() {
    showLoading()
}

/**
 * 显示空视图
 */
fun MultipleStatusView.showEmptyView() {
    showEmpty()
}

/**
 * 显示内容视图
 */
fun MultipleStatusView.showContentView() {
    showContent()
}

/**
 * 请求成功，分为内容视图和空视图
 */
fun MultipleStatusView.showSuccessView(isEmptyData: Boolean) {
    if (isEmptyData) {
        showEmptyView()
    } else {
        showContentView()
    }
}

/**
 * 请求失败，分为错误和网络断开
 * @param isOldDataEmpty: 页面中是否已存在数据，传入true时不会显示失败视图
 */
@JvmOverloads
fun MultipleStatusView.showFailedView(isOldDataEmpty: Boolean = false) {
    if (isOldDataEmpty) {
        when (context.isNetworkConnect()) {
            true -> showError()
            false -> showNoNetwork()
        }
    }
}