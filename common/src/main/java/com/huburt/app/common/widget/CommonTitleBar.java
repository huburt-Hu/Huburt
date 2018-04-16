package com.huburt.app.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huburt.app.common.R;


/**
 * Created by hubert on 2018/3/26.
 * <p>
 * 通用title bar
 * <p>
 * {@code <com.distrii.common.widget.CommonTitleBar
 * android:id="@+id/title_bar"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * app:leftText="@string/cancel"
 * app:rightText="@string/finish"
 * app:title="@string/edit">
 * <p>
 * </com.distrii.common.widget.CommonTitleBar>}
 */

public class CommonTitleBar extends FrameLayout {

    private String leftText;
    private String rightText;
    private boolean showBack;
    private String title;
    private int rightResId;

    private ImageView ivBack;
    private TextView tvTitle;
    private FrameLayout flRight;
    private RelativeLayout rlTitle;
    private FrameLayout flLeft;

    public CommonTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public CommonTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        title = ta.getString(R.styleable.CommonTitleBar_title);
        leftText = ta.getString(R.styleable.CommonTitleBar_leftText);
        rightText = ta.getString(R.styleable.CommonTitleBar_rightText);
        rightResId = ta.getResourceId(R.styleable.CommonTitleBar_rightView, 0);
        showBack = TextUtils.isEmpty(leftText) &&
                ta.getBoolean(R.styleable.CommonTitleBar_showBack, true);
        ta.recycle();

        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.common_title_bar, this, true);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        flRight = (FrameLayout) findViewById(R.id.fl_right);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);

        flLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).onBackPressed();
                }
            }
        });
        setBackVisible(showBack);
        setTitle(title);

        if (rightResId != 0) {
            setRightView(rightResId);
        }
        if (!TextUtils.isEmpty(rightText)) {
            setRightText(rightText);
        }
        if (!TextUtils.isEmpty(leftText)) {
            setLeftText(leftText);
        }
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setTitle(@StringRes int resId) {
        tvTitle.setText(resId);
    }

    public void setBackVisible(boolean visible) {
        ivBack.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        rlTitle.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        rlTitle.setBackgroundResource(resid);
    }

    public void setLeftBack() {
        setLeftView(ivBack);
    }

    public void setLeftText(@StringRes int string) {
        setLeftText(getResources().getString(string));
    }

    public void setLeftText(CharSequence text) {
        TextView left = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.view_toolbar_right_text, flLeft, false);
        left.setText(text);
        setLeftView(left);
    }

    public void setLeftView(@LayoutRes int layoutId) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(layoutId, flRight, false);
        setRightView(contentView);
    }

    public void setLeftView(View view) {
        flLeft.removeAllViews();
        flLeft.addView(view);
    }

    public void setOnLeftClickListener(OnClickListener onClickListener) {
        flLeft.setOnClickListener(onClickListener);
    }

    public void setRightText(@StringRes int strings) {
        setRightText(getResources().getString(strings));
    }

    public void setRightText(CharSequence text) {
        TextView right = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.view_toolbar_right_text, flRight, false);
        right.setText(text);
        setRightView(right);
    }

    public void setRightView(@LayoutRes int layoutId) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(layoutId, flRight, false);
        setRightView(contentView);
    }

    public void setRightView(View view) {
        flRight.removeAllViews();
        flRight.addView(view);
    }

    public void setOnRightClickListener(OnClickListener listener) {
        flRight.setOnClickListener(listener);
    }
}
