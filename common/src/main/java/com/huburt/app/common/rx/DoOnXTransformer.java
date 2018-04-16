package com.huburt.app.common.rx;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by hubert
 * <p>
 * Created on 2017/6/14.
 */

public class DoOnXTransformer {

    public static <T> FlowableTransformer<T, T> doOnSubscribe(final Runnable runnable) {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(Subscription subscription) throws Exception {
                                Timber.i("doOnSubscribe:%s", Thread.currentThread().getName());
                                runnable.run();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> FlowableTransformer<T, T> doOnFinish(final Runnable runnable) {
        return new FlowableTransformer<T, T>() {

            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                Timber.i("doOnCompleted:%s", Thread.currentThread().getName());
                                runnable.run();
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Timber.i("doOnError:%s", Thread.currentThread().getName());
                                runnable.run();
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
