package com.huburt.app.common.rx;

import org.reactivestreams.Publisher;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * 网络异常重试
 */
public class RetryWhenNetException implements Function<Flowable<Throwable>, Publisher<?>> {
    private int count = 1;//retry count
    private long delay = 3000;//delay time

    public RetryWhenNetException() {

    }

    public RetryWhenNetException(int count) {
        this.count = count;
    }

    public RetryWhenNetException(int count, long delay) {
        this.count = count;
        this.delay = delay;
    }

    @Override
    public Publisher<?> apply(Flowable<Throwable> throwableFlowable) throws Exception {
        return throwableFlowable.zipWith(Flowable.range(1, count + 1), new BiFunction<Throwable, Integer, Wrapper>() {
            @Override
            public Wrapper apply(@NonNull Throwable throwable, @NonNull Integer integer) throws Exception {
                return new Wrapper(throwable, integer);
            }
        }).flatMap(new Function<Wrapper, Publisher<?>>() {
            @Override
            public Publisher<?> apply(@NonNull Wrapper wrapper) throws Exception {
                if ((wrapper.throwable instanceof ConnectException
                        || wrapper.throwable instanceof SocketTimeoutException
                        || wrapper.throwable instanceof TimeoutException)
                        && wrapper.index < count + 1) {
                    return Flowable.timer(delay + (wrapper.index - 1) * delay, TimeUnit.MILLISECONDS);
                }
                return Flowable.error(wrapper.throwable);
            }
        });
    }


    private class Wrapper {
        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }
}