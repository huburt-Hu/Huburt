package com.huburt.app.common.base;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huburt.app.common.ctx.BaseConstants;
import com.huburt.app.common.utils.SPUtils;
import com.huburt.app.common.utils.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxFragment;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by hubert
 * <p>
 * Created on 2017/6/5.
 * 实现摇一摇显示类名，方便定位
 */

public abstract class BaseFragment extends RxFragment implements LoadingShower {

    private SensorManager mSensorManager;
    private boolean shakeOpen;
    private SPUtils sp;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                //获取三个方向值
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];
                if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math
                        .abs(z) > 17)) {
                    String msg = "Activity:" + getActivity().getClass().getSimpleName() + "\n"
                            + "Fragment:" + this.getClass().getSimpleName();
                    ToastUtils.show(msg);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = SPUtils.getSp(BaseConstants.CONFIG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    public abstract int getLayoutId();

    @Override
    public void showLoading() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("fragment尚未添加到Activity中");
        }
        if (activity instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showLoading();
        } else {
            throw new IllegalStateException("Activity："
                    + activity.getClass().getSimpleName() + "请继承BaseActivity");
        }
    }

    @Override
    public void closeLoading() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("fragment尚未添加到Activity中");
        }
        if (activity instanceof BaseActivity) {
            ((BaseActivity) getActivity()).closeLoading();
        } else {
            throw new IllegalStateException("Activity："
                    + activity.getClass().getSimpleName() + "请继承BaseActivity");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sp.getBoolean(BaseConstants.SHAKE)) {
            //获取 SensorManager 负责管理传感器
            mSensorManager = ((SensorManager) getActivity().getSystemService(SENSOR_SERVICE));
        } else {
            mSensorManager = null;
        }
        if (mSensorManager != null) {
            //获取加速度传感器
            Sensor mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(sensorEventListener,
                        mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                shakeOpen = true;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (mSensorManager != null && shakeOpen) {
            mSensorManager.unregisterListener(sensorEventListener);
            shakeOpen = false;
        }
    }

    public void showToast(final String msg) {
        ToastUtils.show(msg);
    }
}
