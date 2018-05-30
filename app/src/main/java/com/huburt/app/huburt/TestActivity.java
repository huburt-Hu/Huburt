package com.huburt.app.huburt;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.huburt.app.common.base.BaseActivity;
import com.huburt.app.huburt.widget.SelectView;

import timber.log.Timber;

/**
 * Created by hubert on 2018/5/25.
 */
public class TestActivity extends BaseActivity {

    private SelectView selectView;
    private Button btnOk;
    private ViewGroup group;
    private TextView textView;
    private View btnAdd;
    private View btnReduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        group = findViewById(R.id.ll_operate);
        selectView = (SelectView) findViewById(R.id.select_view);
        selectView.addUnseletable(2,2);
        textView = findViewById(R.id.tv);
        selectView.setOverlappingStateListener(new SelectView.OverlappingStateChangeListener() {
            @Override
            public void onOverlappingStateChanged(boolean isOverlapping) {
                Timber.i("onOverlappingStateChanged, isOverlapping:" + isOverlapping);
                btnOk.setEnabled(!isOverlapping);
            }
        });

        selectView.setSelectChangeListener(new SelectView.SelectChangeListener() {
            @Override
            public void onSelected() {
                group.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSelectChanged(int start, int count) {
                textView.setText("start:" + start + ", count:" + count);
                btnReduce.setEnabled(count > 2);
                btnAdd.setEnabled(start + count < selectView.titles.length - 1);
            }
        });
        btnAdd = findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] selected = selectView.getSelected();
                selectView.setSelected(selected[0], selected[1] + 1);

            }
        });
        btnReduce = findViewById(R.id.reduce);
        btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] selected = selectView.getSelected();
                selectView.setSelected(selected[0], selected[1] - 1);
            }
        });
        btnOk = findViewById(R.id.ok);
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectView.clearSelected();
                group.setVisibility(View.GONE);
            }
        });
    }
}
