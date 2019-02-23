package com.lindroid.pullrefreshloadmoredemo.utils.request

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @author Lin
 * @date 2019/2/22
 * @function
 * @Description
 */

 const val TAG = "OkHttp"


inline fun <reified T :Any> BaseRequestParams.post(callback: OnOkHttpCallback) {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .build()
    val form = FormBody.Builder()
    if (params.isNotEmpty()) {
        for (entry in params) {
            form.add(entry.key, entry.value)
        }
    }
    val body = form.build()
    val request = Request.Builder().url(url).post(body).build()
    val call = okHttpClient.newCall(request)
    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post {
                callback.onFailure(-100, "网络异常")
            }

        }

        override fun onResponse(call: Call, response: Response) {
            when (response.isSuccessful) {
                true -> {
                    val json = response.body()?.string()
                    Log.d(TAG, "json:$json")
//                    val res = Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)?.let { it }
                    Handler(Looper.getMainLooper()).post {
                        callback.onSuccess(Gson().fromJson<T>(json, object : TypeToken<T>() {}.type))
                    }
                }
                false -> {
                    Log.d(TAG, "请求失败")
                    callback.onFailure(response.code(), response.message())
                }
            }
        }

    })
}


interface OnOkHttpCallback {
    //失败
    fun onFailure(code: Int, msg: String)

    //成功
    fun onSuccess(result: Any)
}