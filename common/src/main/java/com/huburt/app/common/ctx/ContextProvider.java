package com.huburt.app.common.ctx;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by hubert on 2018/1/16.
 */

public final class ContextProvider {

    //正式
    public static final String URL_OL = "http://sapi.distrii.com/banbanbao-api/";
    //测试
    public static final String URL_DEBUG = "http://139.196.210.91:8090/banbanbao-api/";

    private static String URL;

    private static Context mApplication;

    private ContextProvider() {
    }

    public static void init(Context application) {
        mApplication = application;

    }

    public static Context getContext() {
        return mApplication;
    }


    public static String baseUrl() {
        return URL;
    }

    /**
     * 当SD卡存在或者SD卡不可被移除的时候
     *
     * @return 存储卡是否挂载(存在)
     */
    public static boolean isMountSdcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }

    /**
     * @return 项目文件存储根目录
     */
    public static File getRootDir() {
        File dir;
        if (isMountSdcard()) {
            dir = ContextProvider.getContext().getExternalFilesDir("");
        } else {
            dir = ContextProvider.getContext().getFilesDir();
        }
        return dir;
    }

    public static File getLogDir() {
        File file = new File(getRootDir(), "log");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getImageDir() {
        File file = new File(getRootDir(), "image");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getXXDir(String name) {
        File file = new File(getRootDir(), name);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getCacheDir() {
        return ContextProvider.getContext().getCacheDir();
    }

}
