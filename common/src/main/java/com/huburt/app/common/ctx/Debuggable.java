package com.huburt.app.common.ctx;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by hubert on 2018/1/9.
 * <p>
 * 通过application 节点的 android:debuggable 判断是否是debug版本。
 * 该属性默认跟随打包版本变化，若设置了该属性则是设置值。
 *
 * 用此类可以在Module中正确获取isDebug属性。
 */

public final class Debuggable {

    private static Boolean isDebug;

    private Debuggable() {

    }

    public static boolean init(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return isDebug;
    }

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug;
    }
}
