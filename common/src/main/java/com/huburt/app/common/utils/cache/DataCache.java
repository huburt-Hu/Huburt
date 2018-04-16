/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2017-03-12 02:50:16
 *
 * GitHub:  https://github.com/GcsSloop
 * Website: http://www.gcssloop.com
 * Weibo:   http://weibo.com/GcsSloop
 */

package com.huburt.app.common.utils.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.huburt.app.common.ctx.ContextProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据缓存工具,除了磁盘缓存外添加了内存缓存
 */
public class DataCache {

    private static final int M = 1024 * 1024;
    private static final String CACHE_NAME = "common_cache";

    private ACache mDiskCache;
    private LruCache<String, Object> mLruCache;

    private static DataCache dataCache;

    public static DataCache getInstance() {
        if (dataCache == null) {
            synchronized (DataCache.class) {
                dataCache = new DataCache(ContextProvider.getContext());
            }
        }
        return dataCache;
    }


    private DataCache(Context context) {
        mDiskCache = ACache.get(context.getApplicationContext(), CACHE_NAME);
        mLruCache = new LruCache<>(5 * M);
    }

    public <T extends Serializable> void saveListData(String key, List<T> data) {
        if (mDiskCache != null && mLruCache != null) {
            ArrayList<T> datas = (ArrayList<T>) data;
            mLruCache.put(key, datas);
            mDiskCache.put(key, datas, ACache.TIME_WEEK);     // 数据缓存时间为 1 周
        }
    }

    public <T extends Serializable> void saveListData(String key, List<T> data, int saveTime) {
        if (mDiskCache != null && mLruCache != null) {
            ArrayList<T> datas = (ArrayList<T>) data;
            mLruCache.put(key, datas);
            mDiskCache.put(key, datas, saveTime);     // 自定义保存时间,已秒为单位
        }
    }

    public <T extends Serializable> void saveData(@NonNull String key, @NonNull T data) {
        if (mDiskCache != null && mLruCache != null) {
            mLruCache.put(key, data);
            mDiskCache.put(key, data, ACache.TIME_WEEK);     // 数据缓存时间为 1 周
        }
    }

    public <T extends Serializable> void saveData(@NonNull String key, @NonNull T data, int saveTime) {
        if (mDiskCache != null && mLruCache != null) {
            mLruCache.put(key, data);
            mDiskCache.put(key, data, saveTime);     // 数据缓存时间为自定义,时间单位为秒,为-1时表示永久持久化
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getData(@NonNull String key) {
        if (mLruCache == null || mDiskCache == null)
            getInstance();
        T result = (T) mLruCache.get(key);
        if (result == null) {
            result = (T) mDiskCache.getAsObject(key);
            if (result != null) {
                mLruCache.put(key, result);
            }
        }
        return result;
    }

    public void removeDate(String key) {
        if (mDiskCache != null)
            mDiskCache.remove(key);
    }
    //使用示例
//    public void saveTopicRepliesList(int topic_id, List<TopicReply> replyList) {
//        ArrayList<TopicReply> replies = new ArrayList<>(replyList);
//        saveData("topic_reply_" + topic_id, replies);
//    }
//
//    public List<TopicReply> getTopicRepliesList(int topic_id) {
//        return getData("topic_reply_" + topic_id);
//    }

}
