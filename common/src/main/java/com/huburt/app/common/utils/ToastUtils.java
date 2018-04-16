package com.huburt.app.common.utils;


import com.huburt.app.common.ctx.ContextProvider;

import es.dmoral.toasty.Toasty;

/**
 * Created by hubert on 2018/3/14.
 */

public class ToastUtils {

    public static void show(CharSequence msg) {
        Toasty.normal(ContextProvider.getContext(), msg).show();
    }
}
