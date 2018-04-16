package com.huburt.app.common.architecture.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.huburt.app.common.base.BaseFragment;

import io.reactivex.FlowableTransformer;


public abstract class BaseViewImplFragment<P extends BasePresenter> extends BaseFragment
        implements BaseView<P> {

    protected P mPresenter;
    protected Activity mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unSubscribe();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    @Override
    public Context getContext() {
        return mContext;
    }


    @Override
    public <T> FlowableTransformer<T, T> bindToRxLifecycle() {
        return this.<T>bindToLifecycle();
    }
}
