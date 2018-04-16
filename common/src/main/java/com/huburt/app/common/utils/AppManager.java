package com.huburt.app.common.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import timber.log.Timber;

/**
 * Created by Administrator on 2016/1/7.
 */
public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager appManager;

    private AppManager() {
        activityStack = new Stack<Activity>();
    }

    public static AppManager getInstance() {
        if (appManager == null) {
            synchronized (AppManager.class) {
                if (appManager == null) {
                    appManager = new AppManager();
                }
            }
        }
        return appManager;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(@NonNull Activity activity) {
        activityStack.push(activity);
    }

    /**
     * activity出栈
     */
    public void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            if (activityStack.contains(activity)) {
                removeActivity(activity);
            }
            activity.finish();
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        Activity activity;
        while (!activityStack.empty()) {
            activity = activityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * finish指定的activity
     */
    public boolean finishActivity(Class<? extends Activity> actCls) {
        Activity act = findActivityByClass(actCls);
        if (null != act && !act.isFinishing()) {
            act.finish();
            return true;
        }
        return false;
    }

    /**
     * 通过字节码文件找到Activity
     */
    private Activity findActivityByClass(Class<? extends Activity> actCls) {
        Activity aActivity = null;
        for (Activity anActivityStack : activityStack) {
            aActivity = anActivityStack;
            if (null != aActivity
                    && aActivity.getClass().getName().equals(actCls.getName())
                    && !aActivity.isFinishing()) {
                break;
            }
            aActivity = null;
        }
        return aActivity;
    }

    /**
     * finish指定的activity之上的所有activity
     *
     * @param actCls        指定的Activity的字节码
     * @param isIncludeSelf 是否包含自己
     */
    public boolean finishToActivity(Class<? extends Activity> actCls, boolean isIncludeSelf) {
        List<Activity> buf = new ArrayList<Activity>();
        int size = activityStack.size();
        Activity activity = null;
        for (int i = size - 1; i >= 0; i--) {
            activity = activityStack.get(i);
            if (activity.getClass().isAssignableFrom(actCls)) {
                Timber.i("需要关闭的:" + activity.getClass().getSimpleName() + "总大小:" + size + "需要关闭的大小:" + buf.size());
                buf.add(activity);
                for (Activity a : buf) {
                    a.finish();
                }
                return true;
            } else if (i == size - 1 && isIncludeSelf) {
                buf.add(activity);
            } else if (i != size - 1) {
                buf.add(activity);
            }
        }
        return false;
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            System.exit(0);
            System.gc();
        } catch (Exception e) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 是否包含该Activity
     */
    public boolean isContains(Class cls) {
        for (Activity activity : activityStack) {
            if (activity != null && activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        if (activityStack != null) {
            return activityStack.size();
        }
        return 0;
    }

    /**
     * 结束指定Activity之外的所有Activity
     *
     * @param cls 不需要finish的Activity
     */
    public void finishCustomActivity(Class cls) {
        Timber.i("不需要结束的:" + cls + "总大小:" + activityStack.size());
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity next = iterator.next();
            if (!next.getClass().equals(cls)) {
                next.finish();
                iterator.remove();
            }
        }
        Timber.i("还剩大小:%s", activityStack.size());
    }
}
