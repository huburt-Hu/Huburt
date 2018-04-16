package com.huburt.app.common.rx;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by hubert on 2018/4/16.
 */

public abstract class DataResource<T> {

    private Flowable<T> flowable;

    public DataResource() {
        flowable = loadFromDb()
                .flatMap(new Function<T, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(T t) throws Exception {
                        onDbDataLoaded(t);
                        if (shouldFetch(t)) {
                            return getNetFlowable();
                        } else {
                            return Flowable.just(t);
                        }
                    }
                });

    }

    private Flowable<T> getNetFlowable() {
        return loadFromNet().map(new Function<T, T>() {
            @Override
            public T apply(T t) throws Exception {
                saveData(t);
                return t;
            }
        });
    }

    protected abstract boolean shouldFetch(T cache);

    protected abstract Flowable<T> loadFromDb();

    protected abstract Flowable<T> loadFromNet();

    protected abstract void saveData(T data);

    protected void onDbDataLoaded(T t) {

    }


    public Flowable<T> asFlowable() {
        return flowable;
    }

}
