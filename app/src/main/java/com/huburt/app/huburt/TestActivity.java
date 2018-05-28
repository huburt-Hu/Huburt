package com.huburt.app.huburt;

import android.os.Bundle;

import com.huburt.app.common.base.BaseActivity;
import com.huburt.app.huburt.widget.BooheeRulerView;

/**
 * Created by hubert on 2018/5/25.
 */
public class TestActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BooheeRulerView rulerView = (BooheeRulerView) findViewById(R.id.ruler);
        rulerView.init(0f, 100f, 50f, new BooheeRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {

            }
        });
    }
}
