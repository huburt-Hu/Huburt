package com.huburt.app.common.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingUtils {

    private Context context;
    private ProgressDialog pd;

    public LoadingUtils(Context context) {
        this.context = context;
    }

    public void showProgressDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(context);
        }
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(msg);
        pd.show();
    }

    public void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public void onDestroy() {
        dismissProgressDialog();
        pd = null;
        context = null;
    }
}
