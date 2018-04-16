package com.huburt.app.common.architecture.mvp;


import android.content.Context;

import com.huburt.app.common.base.LoadingShower;

import io.reactivex.FlowableTransformer;

public interface BaseView<T extends BasePresenter> extends LoadingShower {

    void setPresenter(T presenter);

    boolean isActive();

    void showToast(String msg);

    Context getContext();

    <X> FlowableTransformer<X, X> bindToRxLifecycle();
}
