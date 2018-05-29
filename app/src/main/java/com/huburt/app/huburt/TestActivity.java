package com.huburt.app.huburt;

import android.os.Bundle;

import com.huburt.app.common.base.BaseActivity;
import com.huburt.app.huburt.widget.SelectView;

/**
 * Created by hubert on 2018/5/25.
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        SelectView selectView = (SelectView) findViewById(R.id.select_view);
        selectView.setOverlappingStateListener(new SelectView.OverlappingStateChangeListener() {
            @Override
            public void onOverlappingStateChanged(boolean isOverlapping) {
//                Timber.i("onOverlappingStateChanged, isOverlapping:" + isOverlapping);
            }
        });

        selectView.setSelectChangeListener(new SelectView.SelectChangeListener() {
            @Override
            public void onSelectChanged(int start, int count) {
//                Timber.e("onSelectChanged, start:" + start + ", count:" + count);
            }
        });
    }
}
