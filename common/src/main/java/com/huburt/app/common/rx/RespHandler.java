package com.huburt.app.common.rx;


import com.huburt.app.common.R;
import com.huburt.app.common.architecture.mvp.BaseView;
import com.huburt.app.common.bean.BaseData;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;


/**
 * 网络结果处理类, 此类会判断网络错误与业务错误.
 */
public class RespHandler<T> {

    public static final String SUCCESS_STATUS = "0000";
    public static final String TOKEN_ERROR_STATUS = "9999";
    public static final String ACCOUNT_ERROR = "8001";//账号异地登录
    public static final String TOKEN_TIMEOUT = "8002";//token已过期
    public static final String EMPTY_TOKEN = "8003";//token为空

    private BaseView view;
    private CustomHandler<T> handler;

    public RespHandler(CustomHandler<T> handler) {
        this.handler = handler;
    }

    public RespHandler(CustomHandler<T> handler, BaseView view) {
        this.handler = handler;
        this.view = view;
    }

    public void onCompleted() {
        release();
    }

    public void onError(Throwable e) {
        e.printStackTrace();
        if (!handler.error(e)) {
            handleException(e);
        }
        release();
    }

    public void onNext(T t) {
        BaseData data;
        if (t instanceof BaseData) {
            data = (BaseData) t;
            if (SUCCESS_STATUS.equals(data.status)) {
                handler.success(t);
            } else {
                if (!handler.operationError(t, data.status, data.message)) {
                    handleOperationError(data.message);
                }
            }
        } else {
            handleSuccess(t);
        }
        release();
    }


    private void handleSuccess(T t) {
        try {
            handler.success(t);
        } catch (Exception e) {
            onError(e);
        }
    }

    public void release() {
        view = null;
        handler = null;
    }

    public void handleException(Throwable e) {
        if (view != null) {
            if (e instanceof ConnectException) {
                view.showToast(view.getContext().getString(R.string.network_error));
            } else if (e instanceof HttpException) {
                view.showToast(view.getContext().getString(R.string.network_server_error));
            } else if (e instanceof SocketTimeoutException) {
                view.showToast(view.getContext().getString(R.string.network_timeout));
            } else if (e instanceof UnknownHostException) {
                view.showToast(view.getContext().getString(R.string.network_error));
            } else {
                view.showToast(view.getContext().getString(R.string.network_other));
            }
        }
    }

    public void handleOperationError(String message) {
        if (view != null)
            view.showToast(message);
    }

    public interface CustomHandler<T> {
        /**
         * 请求成功同时业务成功的情况下会调用此函数
         */
        void success(T t);

        /**
         * 请求成功但业务失败的情况下会调用此函数.
         *
         * @return 是否需要自行处理业务错误.
         * <p>
         * true - 需要, 父类不会处理错误
         * </P>
         * false - 不需要, 交由父类处理
         */
        boolean operationError(T t, String status, String message);

        /**
         * 请求失败的情况下会调用此函数
         *
         * @return 是否需要自行处理系统错误.
         * <p>
         * true - 需要, 父类不会处理错误
         * </P>
         * false - 不需要, 交由父类处理
         */
        boolean error(Throwable e);

        /**
         * 是否展示异常对话框（主要是账号异常、或TOKEN为空）
         *
         * @return 需要自行处理结果
         * true 展示
         * false 不展示、重写此方法一般还需要重写operationError()在这里做业务处理
         * 默认展示
         * @Params 是否是账号异常
         */
        boolean isShowExceptionDialog(String status, String message);
    }
}
