package com.lindroid.pullrefreshloadmoredemo.utils.request

/**
 * @author Lin
 * @date 2019/2/22
 * @function
 * @Description
 */
abstract class BaseRequestParams {
    abstract val url:String

    abstract val params:HashMap<String,String>
}