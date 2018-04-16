package com.huburt.app.common.base;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.huburt.app.common.R;
import com.huburt.app.common.ctx.BaseConstants;
import com.huburt.app.common.utils.LoadingUtils;
import com.huburt.app.common.utils.SPUtils;
import com.huburt.app.common.utils.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;


public class BaseActivity extends RxAppCompatActivity implements LoadingShower {

    private SPUtils sp;
    private SensorManager mSensorManager;
    private boolean shakeOpen;
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
                    String msg = "Activity:" + getClass().getSimpleName();
                    ToastUtils.show(msg);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private LoadingUtils loadingUtils = new LoadingUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = SPUtils.getSp(BaseConstants.CONFIG);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initShake();
    }

    private void initShake() {
        if (sp.getBoolean(BaseConstants.SHAKE)) {
            boolean fragmentShake = false;//确保fragment没有开启摇一摇
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BaseFragment) {
                        fragmentShake = true;
                        break;
                    }
                }
            }
            if (!fragmentShake) {
                //获取 SensorManager 负责管理传感器
                mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
            }
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
    protected void onPause() {
        super.onPause();
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (mSensorManager != null && shakeOpen) {
            mSensorManager.unregisterListener(sensorEventListener);
            shakeOpen = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingUtils.onDestroy();
    }

    public void showToast(String message) {
        ToastUtils.show(message);
    }

    /**
     * 跳转到指定界面
     *
     * @param cla 要跳转界面的字节码
     */
    public void gotoActivity(Class<?> cla) {
        Intent intent = new Intent(this, cla);
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        loadingUtils.showProgressDialog(getString(R.string.loading));
    }

    @Override
    public void closeLoading() {
        loadingUtils.dismissProgressDialog();
    }
}
