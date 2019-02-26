package com.lindroid.pullrefreshloadmoredemo.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lindroid.pullrefreshloadmoredemo.R
import com.lindroid.pullrefreshloadmoredemo.bean.MovieBean
import com.lindroid.pullrefreshloadmoredemo.utils.BaseSimpleAdapter
import kotlinx.android.synthetic.main.toolbar.view.*


/**
 * @author Lin
 * @date 2019/2/26
 * @function 基类Activity
 * @Description
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var pageNo = 1
    protected lateinit var adapter: BaseQuickAdapter<MovieBean.Subject, BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBefore()
        setContentView(contentViewId)
        initView()
        initData()
        initOnClick()
    }

    abstract val contentViewId: Int

    open fun initData() {

    }

    open fun initOnClick() {

    }

    open fun initBefore() {
        adapter = BaseSimpleAdapter(android.R.layout.simple_list_item_1) { helper, item ->
            helper.setText(android.R.id.text1, item.title)
        }
    }

    open fun initView() {

    }

    protected fun createData(pageSize: Int): List<MovieBean.Subject> {
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

    fun initToolBar(title: String = getString(R.string.app_name), isShowArrow: Boolean = true) {
        val toolView = window.decorView
        toolView.toolBar.title = title
        //ToolBar的属性设置要在setSupportActionBar方法之前调用
        setSupportActionBar(toolView.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(isShowArrow)
    }

    fun initToolBar(@StringRes strId: Int, isShowArrow: Boolean = true) {
        initToolBar(getString(strId), isShowArrow)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
