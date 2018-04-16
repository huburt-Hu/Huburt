package com.huburt.app.huburt.test

import android.os.Bundle
import android.text.TextUtils
import com.huburt.app.common.base.BaseActivity
import com.huburt.app.common.rx.DataResource
import com.huburt.app.common.rx.SchedulersTransformer
import com.huburt.app.common.utils.SPUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.functions.Consumer
import timber.log.Timber

/**
 * Created by hubert on 2018/4/16.
 *
 */
class ResourceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = SPUtils.getSp("test")
        val resource = object : DataResource<String?>() {
            override fun shouldFetch(cache: String?): Boolean {
                Timber.i("shouldFetch:" + Thread.currentThread().name)
                return TextUtils.isEmpty(cache)
            }

            override fun loadFromDb(): Flowable<String?> {
                return Flowable.create(FlowableOnSubscribe {
                    Timber.i("loadFromDb:" + Thread.currentThread().name)
                    it.onNext(sp.getString("r2",""))
                }, BackpressureStrategy.ERROR)
            }

            override fun loadFromNet(): Flowable<String?> {
                return Flowable.create(FlowableOnSubscribe {
                    Timber.i("loadFromNet:" + Thread.currentThread().name)
                    it.onNext("data")
                }, BackpressureStrategy.ERROR)
            }

            override fun saveData(data: String?) {
                Timber.i("saveData:" + Thread.currentThread().name)
                sp.put("r2", data)
            }
        }
        resource.asFlowable()
                .compose(SchedulersTransformer.io_ui())
                .subscribe(Consumer {
                    Timber.i(it)
                })
    }
}