package com.huburt.app.common.architecture.mvp;

import java.util.List;

/**
 * Created by hubert
 * <p>
 * Created on 2017/7/6.
 */

public interface BaseRvView<B, P extends BasePresenter> extends BaseView<P> {
    /**
     * 获取数据后调用此方法刷新界面
     *
     * @param beans 数据
     */
    void onDataReceived(List<B> beans);

    void onGetDataFailed();
}
