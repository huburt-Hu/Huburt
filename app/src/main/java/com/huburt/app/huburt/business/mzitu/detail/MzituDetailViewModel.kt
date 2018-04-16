package com.huburt.app.huburt.business.mzitu.detail

import android.arch.lifecycle.*
import com.huburt.app.common.architecture.mvvm.RemoteRepository
import com.huburt.app.huburt.api.MzituService
import com.huburt.app.huburt.util.ParseMeiZiTu

/**
 * Created by hubert on 2018/4/14.
 *
 */
class MzituDetailViewModel : ViewModel() {
    private val controller: MutableLiveData<Int> = MutableLiveData()
    private val source: LiveData<String> = Transformations.switchMap(controller) {
        RemoteRepository.getService(MzituService::class.java).imageList(it)
    }
    val liveData: MediatorLiveData<List<String>> = MediatorLiveData()

    init {
        liveData.addSource(source) {
            if (it != null) {
                liveData.value = ParseMeiZiTu.parsePicturePage(it).data
            } else {
                liveData.value = null
            }
        }
    }

    fun getData(id: Int) {
        controller.value = id
    }
}