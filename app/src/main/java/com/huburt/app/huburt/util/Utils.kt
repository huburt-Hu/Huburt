package com.huburt.app.huburt.util

import android.text.TextUtils
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

/**
 * Created by hubert on 2018/4/13.
 *
 */

//自定义headers需要实现AppGlideModule
fun buildMzituUrl(url: String): GlideUrl? {
    return if (TextUtils.isEmpty(url)) {
        null
    } else {
        GlideUrl(url, LazyHeaders.Builder()
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                .addHeader("Host", "i.meizitu.net")
                .addHeader("Referer", "http://www.mzitu.com/")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .build())
    }
}