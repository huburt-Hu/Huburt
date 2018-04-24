package com.huburt.app.common.rx;

import android.support.annotation.IntDef;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by hubert on 2018/4/23.
 */

public abstract class DataCacher<T> {

    public static final int NO_CACHE = 0;
    public static final int USE_CACHE = 1;
    public static final int CACHE_AND_NEW = 2;

    @IntDef({NO_CACHE, USE_CACHE, CACHE_AND_NEW})
    public @interface CacheMode {
    }

    private Observable observable;

    public DataCacher(@CacheMode final int mode) {
        observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                if (mode != NO_CACHE) {
                    T cache = loadFromCache();
                    if (cache != null) {
                        if (mode == USE_CACHE) {
                            emitter.onNext(cache);
                            emitter.onComplete();
                            return;
                        } else if (mode == CACHE_AND_NEW) {
                            emitter.onNext(cache);
                        }
                    }
                }
                T net = loadFromNet();
                if (net != null) {
                    saveData(net);
                }
                emitter.onNext(net);
                emitter.onComplete();
            }
        });
    }

    protected abstract T loadFromCache();

    protected abstract T loadFromNet();

    protected abstract void saveData(T data);

    public Observable asObservable() {
        return observable;
    }
}
