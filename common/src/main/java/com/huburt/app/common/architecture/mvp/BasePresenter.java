package com.huburt.app.common.architecture.mvp;

public interface BasePresenter {

    /**
     * 开启订阅关系，view加载完成后自动调用
     */
    void subscribe();

    /**
     * 解除订阅关系，通常在view被销毁前调用
     */
    void unSubscribe();

}
