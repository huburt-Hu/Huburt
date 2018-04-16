package com.huburt.app.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.huburt.app.common.ctx.ContextProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class description
 * 崩溃处理类，用于捕获全局crash，生成日志，并上传服务器
 *«
 * @author huburt
 * @date 2016-11-08
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //crash日志的存放位置
    private static String PATH = ContextProvider.getLogDir().getAbsolutePath();
    //crash日志文件的前缀
    private static final String FILE_NAME = "crash";
    //crash日志文件的后缀，本质是txt，可以改成任意后缀使用户无法阅读
    private static final String FILE_NAME_SUFFIX = ".txt";
    //默认的crashHandler，如没有则为null
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;


    private static CrashHandler sInstance = new CrashHandler();
    private int versionCode;
    private String versionName;

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        //设置异常处理为当前类
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        Context mContext = context.getApplicationContext();
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当程序出现未被捕获的异常，系统将会自动调用此方法
     *
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            dumpExceptionToSDCard(throwable);
            uploadExceptionToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则由自己结束
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        }
    }

    /**
     * 将crash日志写入sd卡
     *
     * @param throwable
     */
    private void dumpExceptionToSDCard(Throwable throwable) {
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            printWriter.print(getCrashHead(time));
            printWriter.println();
            throwable.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取崩溃头
     *
     * @param time
     * @return 崩溃头
     */
    private String getCrashHead(String time) {
        return "\n************* Crash Log Head ****************" +
                "\nCrash Time: " + time +// crash时间
                "\nDevice Manufacturer: " + Build.MANUFACTURER +// 设备厂商
                "\nDevice Model       : " + Build.MODEL +// 设备型号
                "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }

    /**
     * 将crash日志打包上传，此处省略
     */
    private void uploadExceptionToServer() {
        Log.d("tag", "upload");
    }
}