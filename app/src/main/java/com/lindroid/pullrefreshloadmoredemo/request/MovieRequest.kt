package com.lindroid.pullrefreshloadmoredemo.request

import com.lindroid.pullrefreshloadmoredemo.URL_DOUBAN_MOVIE
import com.lindroid.pullrefreshloadmoredemo.utils.request.BaseRequestParams

/**
 * @author Lin
 * @date 2019/2/22
 * @function
 * @Description
 */
class MovieRequest(pageNo: Int , pageSize: Int = 20) : BaseRequestParams() {
    override val url: String = URL_DOUBAN_MOVIE

    override val params: HashMap<String, String> =
        hashMapOf(
            "start" to ((pageNo - 1) * 20).toString(),
            "count" to pageSize.toString()
        )


}