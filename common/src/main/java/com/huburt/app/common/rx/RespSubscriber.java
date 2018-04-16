package com.huburt.app.common.rx;

import com.huburt.app.common.architecture.mvp.BaseView;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by hubert
 * <p>
 * Created on 2017/6/14.
 */

public abstract class RespSubscriber<T> extends ResourceSubscriber<T> implements RespHandler.CustomHandler<T> {

    private RespHandler<T> respHandler;

    public RespSubscriber() {
        respHandler = new RespHandler<T>(this);
    }

    public RespSubscriber(BaseView baseView) {
        respHandler = new RespHandler<T>(this, baseView);
    }

    @Override
    public void onComplete() {
        respHandler.onCompleted();
        respHandler = null;
    }

    @Override
    public void onError(Throwable e) {
        respHandler.onError(e);
        respHandler = null;
    }

    @Override
    public void onNext(T t) {
        respHandler.onNext(t);
    }

    @Override
    public boolean operationError(T t, String status, String message) {
        return false;
    }

    @Override
    public boolean error(Throwable e) {
        return false;
    }

    @Override
    public boolean isShowExceptionDialog(String status, String message) {
        return true;
    }
}
