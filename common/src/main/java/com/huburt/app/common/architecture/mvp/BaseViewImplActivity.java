package com.huburt.app.common.architecture.mvp;

import android.content.Context;

import com.huburt.app.common.base.BaseActivity;

import io.reactivex.FlowableTransformer;


/**
 * Created by hubert
 * <p>
 * Created on 2017/9/30.
 */

public class BaseViewImplActivity<P extends BasePresenter> extends BaseActivity
        implements BaseView<P> {

    protected P mPresenter;

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
        return !isFinishing() && !getFragmentManager().isDestroyed();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public <X> FlowableTransformer<X, X> bindToRxLifecycle() {
        return this.<X>bindToLifecycle();
    }
}
