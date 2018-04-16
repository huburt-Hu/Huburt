package com.huburt.app.common.architecture.mvp;


import android.content.Context;

import com.huburt.app.common.rx.DoOnXTransformer;
import com.huburt.app.common.rx.RetryWhenNetException;
import com.huburt.app.common.rx.SchedulersTransformer;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BasePresenterImpl<V extends BaseView<? extends BasePresenter>>
        implements BasePresenter {

    protected V mView;
    private CompositeDisposable disposables;

    public BasePresenterImpl(V view) {
        this.mView = view;
        disposables = new CompositeDisposable();
    }

    /**
     * @return true if the view is visible to user, else not.
     */
    protected boolean isViewActive() {
        return mView != null && mView.isActive();
    }

    /**
     * get the view which associated with this presenter,
     * calling {@link BasePresenterImpl#isViewActive()} before to make sure view is active,
     * or the return could be null.
     *
     * @return view or NULL.
     */
    protected V getView() {
        return mView;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {
        if (disposables != null) {
            disposables.clear();
        }
    }

    /**
     * 获取context，实际为activity
     *
     * @return context
     */
    public Context getContext() {
        return getView().getContext();
    }

    /**
     * 将rxjava订阅关系的subscription加入CompositeDisposable，用于管理rxjava的生命周期。
     *
     * @param d
     */
    protected void addToCompositeDisposable(Disposable d) {
        disposables.add(d);
    }


    /**
     * 获取网络请求的通用transformer，包括断网重试，切换线程，绑定生命周期
     * <p>使用方式observable.compose(getRequestTransformer()).subscribe(...)</p>
     *
     * @param <T>
     * @return transformer
     */
    protected <T> FlowableTransformer<T, T> getRequestTransformer(final int count) {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.retryWhen(new RetryWhenNetException(count))
                        .compose(SchedulersTransformer.<T>io_ui())
                        .compose(getView().<T>bindToRxLifecycle());
            }
        };
    }

    protected <T> FlowableTransformer<T, T> getRequestTransformer() {
        return getRequestTransformer(1);
    }

    /**
     * 为网络请求绑定默认的加载前显示loading，加载完成后消失
     *
     * @param <T>
     * @return transformer
     */
    protected <T> FlowableTransformer<T, T> getLoadingTransformer() {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.compose(DoOnXTransformer.<T>doOnSubscribe(new Runnable() {
                    @Override
                    public void run() {
                        getView().showLoading();
                    }
                })).compose(DoOnXTransformer.<T>doOnFinish(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewActive()) {
                            getView().closeLoading();
                        }
                    }
                }));
            }
        };
    }

}
