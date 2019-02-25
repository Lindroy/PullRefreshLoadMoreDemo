package com.lindroid.pullrefreshloadmoredemo.view

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.lindroid.pullrefreshloadmoredemo.R

/**
 * @author Lin
 * @date 2019/2/25
 * @function
 * @Description
 */
class CustomLoadMoreView : LoadMoreView() {
    /**
     * @return
     */
    override fun getLayoutId(): Int = R.layout.layout_load_more

    /**
     * loading view
     *
     * @return
     */
    override fun getLoadingViewId(): Int = R.id.load_more_loading

    /**
     * load end view, you can return 0
     *
     * @return
     */
    override fun getLoadEndViewId(): Int = R.id.load_more_load_end

    /**
     * load fail view
     *
     * @return
     */
    override fun getLoadFailViewId(): Int = R.id.load_more_load_fail
}