package com.huburt.app.common.architecture.mvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.huburt.app.common.base.BaseActivity;
import com.huburt.app.common.utils.ToastUtils;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.FlowableTransformer;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by hubert on 2018/1/29.
 * <p>
 * 实现生命周期的view实现，可以直接放入Activity的xml实现声明周期的监听。
 * 但是在Fragment的xml中无法获取正确LifecycleOwner，需要调用init方法或者手动调用生命周期
 */

public class BaseLifecycleView<P extends BasePresenter> extends FrameLayout
        implements LifecycleObserver, BaseView<P> {

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    private boolean active;
    protected P mPresenter;
    private Fragment fragment;
    private Listener listener;

    public BaseLifecycleView(@NonNull Context context) {
        this(context, null);
    }

    public BaseLifecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLifecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        startObserver(context);
    }

    private void startObserver(Context context) {
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
        } else {
            IllegalArgumentException exception = new IllegalArgumentException(
                    String.format("inflate %s with wrong context, the right context should be instance of LifecycleOwner, the lifecycle will not callback",
                            getClass().getCanonicalName()));
            Timber.w(exception.getMessage());
        }
    }

    public void init(Fragment fragment) {
        this.fragment = fragment;
        listener = new Listener();
        fragment.getFragmentManager().registerFragmentLifecycleCallbacks(listener, false);
    }

    /**
     * 如果该View在依赖的Activity销毁之前就不再需要，移除view的同时调用此方法释放生命周期的观察
     */
    public void endObserver() {
        Context context = getContext();
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().removeObserver(this);
        } else if (fragment != null) {
            fragment.getFragmentManager().unregisterFragmentLifecycleCallbacks(listener);
        }
        onPause();
        onStop();
        onDestroy();
    }

    protected void setContentView(@LayoutRes int layout) {
        LayoutInflater.from(getContext()).inflate(layout, this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Timber.v("onCreate: %s", this);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Timber.v("onStart: %s", this);
        lifecycleSubject.onNext(ActivityEvent.START);
        active = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Timber.v("onResume: %s", this);
        lifecycleSubject.onNext(ActivityEvent.RESUME);
        active = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Timber.v("onPause: %s", this);
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Timber.v("onStop: %s", this);
        lifecycleSubject.onNext(ActivityEvent.STOP);
        active = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Timber.v("onDestroy: %s", this);
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        active = false;
    }

    @Override
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.show(msg);
    }

    @Override
    public void showLoading() {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showLoading();
        }
    }

    @Override
    public void closeLoading() {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).closeLoading();
        }
    }

    @Override
    public <X> FlowableTransformer<X, X> bindToRxLifecycle() {
        return RxLifecycle.bind(lifecycleSubject);
    }

    private class Listener extends FragmentManager.FragmentLifecycleCallbacks {
        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            if (fragment == f) onCreate();
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            if (fragment == f) onStart();
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            if (fragment == f) onResume();
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            if (fragment == f) onPause();
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            if (fragment == f) onStop();
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            if (fragment == f) onDestroy();
            fm.unregisterFragmentLifecycleCallbacks(this);
        }
    }
}
