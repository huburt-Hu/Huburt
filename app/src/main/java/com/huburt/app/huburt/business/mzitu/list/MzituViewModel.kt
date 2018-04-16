package com.huburt.app.huburt.business.mzitu.list

import android.arch.lifecycle.*
import com.huburt.app.common.architecture.mvvm.RemoteRepository
import com.huburt.app.huburt.api.MzituService
import com.huburt.app.huburt.bean.BaseResult
import com.huburt.app.huburt.bean.MeiZiTu
import com.huburt.app.huburt.util.ParseMeiZiTu

/**
 * Created by hubert on 2018/4/13.
 *
 */
class MzituViewModel : ViewModel() {

    private val controller: MutableLiveData<Params> = MutableLiveData()
    private val resource = Transformations.switchMap(controller) {
        val service = RemoteRepository.getService(MzituService::class.java)
        when (it.type) {
            0 -> service.index(it.page)
            1 -> service.hot(it.page)
            2 -> service.best(it.page)
            3 -> service.sexy(it.page)
            4 -> service.japan(it.page)
            5 -> service.taiwan(it.page)
            6 -> service.mm(it.page)
            else -> throw IllegalArgumentException("wrong type")
        }

    }
    val result: MediatorLiveData<BaseResult<List<MeiZiTu>>> = MediatorLiveData<BaseResult<List<MeiZiTu>>>()

    init {
        result.addSource(resource, Observer {
            if (it != null) {
                result.value = ParseMeiZiTu.parseMeiZiTuList(it, 1)
            } else {
                result.value = null
            }
        })
    }

    fun getData(type: Int, page: Int) {
        controller.value = Params(type, page)
    }

    data class Params(val type: Int, val page: Int = 0)
}
