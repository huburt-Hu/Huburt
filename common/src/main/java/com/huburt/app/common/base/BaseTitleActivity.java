package com.huburt.app.common.base;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huburt.app.common.R;
import com.huburt.app.common.widget.CommonTitleBar;


/**
 * Created by hubert on 2018/3/28.
 */

public class BaseTitleActivity extends BaseActivity {

    private CommonTitleBar titleBar;
    private FrameLayout flContainer;
    private ViewGroup llRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_title);
        titleBar = (CommonTitleBar) findViewById(R.id.title_bar);
        flContainer = (FrameLayout) findViewById(R.id.fl_container);
        llRoot = findViewById(R.id.ll_root);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, flContainer, false);
        setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        flContainer.removeAllViews();
        flContainer.addView(view, params);
    }

    @Override
    public void setContentView(View view) {
        flContainer.removeAllViews();
        flContainer.addView(view);
    }

    protected CommonTitleBar getTitleBar() {
        return titleBar;
    }

    @Override
    public void setTitle(CharSequence title) {
        titleBar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        titleBar.setTitle(titleId);
    }


    public void setBackgroundColor(@ColorInt int color) {
        llRoot.setBackgroundColor(color);
    }

    public void setBackgroundResource(@DrawableRes int resource) {
        llRoot.setBackgroundResource(resource);
    }
}
