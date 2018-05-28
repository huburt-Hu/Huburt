/*
 * Created by w4lle 2016 .
 * Copyright (c) 2016 Boohee, Inc. All rights reserved.
 */

package com.huburt.app.huburt.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

/**
 * 刻度尺
 */
public class BooheeRulerView extends View {

    public static final int DEFAULT_LONG_LINE_HEIGHT = 38;
    public static final int DEFAULT_BOTTOM_PADDING = 8;
    public static final int DEFAULT_BG_COLOR = Color.parseColor("#FAE40B");
    public static final int DEFAULT_INDICATOR_HEIGHT = 18;
    public static final int DEFAULT_TEXT_SIZE = 17;
    public static final float DEFAULT_UNIT = 10;
    public static final int DEFAULT_WIDTH_PER_UNIT = 100;
    public static final int DEFAULT_LONG_LINE_STROKE_WIDTH = 2;
    public static final int DEFAULT_SHORT_LINE_STROKE_WIDTH = 1;

    private float density;
    private int width;
    private int height;
    /**
     * 当前值
     */
    private float currentValue;
    /**
     * 起点值
     */
    private float startValue;
    /**
     * 终点至
     */
    private float endValue;
    /**
     * 两条长线之间的间距
     */
    private float widthPerUnit;
    /**
     * 两条短线之间的距离
     */
    private float widthPerMicroUnit;
    /**
     * 单位，即每两条长线之间的数值
     */
    private float unit;
    /**
     * 短线单位
     */
    private float microUnit;
    /**
     * 每个单位之间的小单位数量，短线数量
     */
    private int microUnitCount;

    /**
     * 能够移动的最大值
     */
    private float maxRightOffset;
    private float maxLeftOffset;

    private int bgColor = DEFAULT_BG_COLOR;

    private float longLineHeight;
    private float shortLineHeight;

    private float bottomPadding;

    private float indicatorHeight;

    private float textSize;
    private int textColor = Color.WHITE;
    private int lineColor = Color.WHITE;

    private Paint bgPaint;
    private Paint indicatorPaint;
    private Paint linePaint;
    private Paint textPaint;
    private PaintFlagsDrawFilter pfdf;
    private float moveX;
    private VelocityTracker velocityTracker;
    private OverScroller scroller;
    /**
     * 剩余偏移量
     */
    private float offset;
    /**
     * 最小速度
     */
    private int minvelocity;

    public static final int MICRO_UNIT_ZERO = 0;
    public static final int MICRO_UNIT_ONE = 2;
    public static final int MICRO_UNIT_FIVE = 5;
    public static final int MICRO_UNIT_TEN = 10;
    private float originValue;

    @IntDef({MICRO_UNIT_ZERO, MICRO_UNIT_ONE, MICRO_UNIT_FIVE, MICRO_UNIT_TEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MicroUnitMode {
    }

    private OnValueChangeListener listener;

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public BooheeRulerView(Context context) {
        this(context, null);
    }

    public BooheeRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BooheeRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new OverScroller(context);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        density = getResources().getDisplayMetrics().density;

        longLineHeight = DEFAULT_LONG_LINE_HEIGHT * density;
        shortLineHeight = (DEFAULT_LONG_LINE_HEIGHT / 2) * density;
        bottomPadding = DEFAULT_BOTTOM_PADDING * density;
        indicatorHeight = DEFAULT_INDICATOR_HEIGHT * density;
        textSize = DEFAULT_TEXT_SIZE * density;
        widthPerUnit = DEFAULT_WIDTH_PER_UNIT * density;

//        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BooheeRulerView);
//        if (typedArray != null) {
//            bgColor = typedArray.getColor(R.styleable.BooheeRulerView_ruler_bg_color, DEFAULT_BG_COLOR);
//            textSize = typedArray.getDimension(R.styleable.BooheeRulerView_ruler_text_size, textSize);
//            textColor = typedArray.getColor(R.styleable.BooheeRulerView_ruler_text_color, Color.WHITE);
//            widthPerUnit = typedArray.getDimension(R.styleable.BooheeRulerView_ruler_width_per_unit, widthPerUnit);
//            lineColor = typedArray.getColor(R.styleable.BooheeRulerView_ruler_line_color, Color.WHITE);
//            typedArray.recycle();
//        }

        minvelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();
        initPaint();
    }

    /**
     * @param startValue 开始值
     * @param endValue 结束值
     * @param currentValue 保留一位小数
     * @param listener
     */
    public void init(float startValue, float endValue, float currentValue, OnValueChangeListener listener) {
        init(startValue, endValue, currentValue, DEFAULT_UNIT, MICRO_UNIT_ZERO, listener);
    }

    /**
     * 初始化
     *
     * @param startValue 开始值
     * @param endValue 结束值
     * @param currentValue   保留一位小数
     * @param unit  单位
     * @param microUnitCount 小单位
     * @param listener
     */
    public void init(float startValue, float endValue, float currentValue, float unit, @MicroUnitMode int microUnitCount, OnValueChangeListener listener) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.currentValue = currentValue;
        if (currentValue < startValue) {
            this.currentValue = startValue;
        }

        if (currentValue > endValue) {
            this.currentValue = endValue;
        }
        this.originValue = this.currentValue;
        this.unit = unit;
        this.microUnit = microUnitCount == MICRO_UNIT_ZERO ? 0 : unit / microUnitCount;
        this.microUnitCount = microUnitCount;
        this.listener = listener;
        caculate();
    }

    private void initPaint() {
        pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);
        bgPaint = new Paint();
        bgPaint.setColor(bgColor);

        indicatorPaint = new Paint();
        indicatorPaint.setColor(lineColor);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int measureMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int measureSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int result = getSuggestedMinimumWidth();
        switch (measureMode) {
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.EXACTLY:
                result = measureSize;
                break;
            default:
                break;
        }
        width = result;
        return result;
    }

    private int measureHeight(int heightMeasure) {
        int measureMode = View.MeasureSpec.getMode(heightMeasure);
        int measureSize = View.MeasureSpec.getSize(heightMeasure);
        int result = (int) (bottomPadding + longLineHeight * 2);
        switch (measureMode) {
            case View.MeasureSpec.EXACTLY:
                result = Math.max(result, measureSize);
                break;
            case View.MeasureSpec.AT_MOST:
                result = Math.min(result, measureSize);
                break;
            default:
                break;
        }
        height = result;
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(pfdf);
        drawBg(canvas);
        drawIndicator(canvas);
        drawRuler(canvas);
    }

    private void drawBg(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, bgPaint);
    }

    private void drawIndicator(Canvas canvas) {
        Path path = new Path();
        path.moveTo(width / 2 - indicatorHeight / 2, 0);
        path.lineTo(width / 2, indicatorHeight / 2);
        path.lineTo(width / 2 + indicatorHeight / 2, 0);
        canvas.drawPath(path, indicatorPaint);
    }

    private void drawRuler(Canvas canvas) {
        if (moveX < maxRightOffset) {
            moveX = maxRightOffset;
        }
        if (moveX > maxLeftOffset) {
            moveX = maxLeftOffset;
        }
        int halfCount = (int) (width / 2 / getBaseUnitWidth());
        float moveValue = (int) (moveX / getBaseUnitWidth()) * getBaseUnit();
        currentValue = originValue - moveValue;
        //剩余偏移量
        offset = moveX - (int) (moveX / getBaseUnitWidth()) * getBaseUnitWidth();

        for (int i = -halfCount - 1; i <= halfCount + 1; i++) {
            float value = ArithmeticUtil.addWithScale(currentValue, ArithmeticUtil.mulWithScale(i, getBaseUnit(), 2), 2);
            //只绘出范围内的图形
            if (value >= startValue && value <= endValue) {
                //画长的刻度
                float startx = width / 2 + offset + i * getBaseUnitWidth();
                if (startx > 0 && startx < width) {
                    if (microUnitCount != 0) {
                        if (ArithmeticUtil.remainder(value, unit) == 0) {
                            drawLongLine(canvas, i, value);
                        } else {
                            //画短线
                            drawShortLine(canvas, i);
                        }
                    } else {
                        //画长线
                        drawLongLine(canvas, i, value);
                    }
                }
            }
        }

        notifyValueChange();
    }

    private void notifyValueChange() {
        if (listener != null) {
            currentValue = ArithmeticUtil.round(currentValue, 2);
            listener.onValueChange(currentValue);
        }
    }

    private void drawShortLine(Canvas canvas, int i) {
        linePaint.setStrokeWidth(DEFAULT_SHORT_LINE_STROKE_WIDTH * density);
        canvas.drawLine(width / 2 + offset + i * getBaseUnitWidth(), 0,
                width / 2 + offset + i * getBaseUnitWidth(), 0 + shortLineHeight, linePaint);
    }

    private void drawLongLine(Canvas canvas, int i, float value) {
        linePaint.setStrokeWidth(DEFAULT_LONG_LINE_STROKE_WIDTH * density);
        //画长线
        canvas.drawLine(width / 2 + offset + i * getBaseUnitWidth(), 0,
                width / 2 + offset + i * getBaseUnitWidth(), 0 + longLineHeight, linePaint);
        //画刻度值
        canvas.drawText(String.valueOf(value), width / 2 + offset + i * getBaseUnitWidth() - textPaint.measureText(value + "") / 2,
                getHeight() - bottomPadding - calcTextHeight(textPaint, value + ""), textPaint);
    }

    private float getBaseUnitWidth() {
        if (microUnitCount != 0) {
            return widthPerMicroUnit;
        }
        return widthPerUnit;
    }

    private float getBaseUnit() {
        if (microUnitCount != 0) {
            return microUnit;
        }
        return unit;
    }

    private void caculate() {
        startValue = format(startValue);
        endValue = format(endValue);
        currentValue = format(currentValue);
        originValue = currentValue;
        if (unit == 0) {
            unit = DEFAULT_UNIT;
        }
        if (microUnitCount != MICRO_UNIT_ZERO) {
            widthPerMicroUnit = ArithmeticUtil.div(widthPerUnit, microUnitCount, 2);
        }
        maxRightOffset = -1 * ((endValue - originValue) * getBaseUnitWidth() / getBaseUnit());
        maxLeftOffset = ((originValue - startValue) * getBaseUnitWidth() / getBaseUnit());
        invalidate();
    }

    private float format(float vallue) {
        float result = vallue;
        if (getBaseUnit() < 0.1) {
            //0.01
            if (ArithmeticUtil.remainder(result, getBaseUnit()) != 0) {
                result += 0.01;
                result = format(result);
            }
        } else if (getBaseUnit() < 1) {
            //0.1
            if (ArithmeticUtil.remainder(result, getBaseUnit()) != 0) {
                result += 0.1;
                result = format(result);
            }
        } else if (getBaseUnit() < 10) {
            //1
            if (ArithmeticUtil.remainder(result, getBaseUnit()) != 0) {
                result += 1;
                result = format(result);
            }
        }
        return result;
    }


    private boolean isActionUp = false;
    private float mLastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float xPosition = event.getX();

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isActionUp = false;
                scroller.forceFinished(true);
                if (null != animator) {
                    animator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                float off = xPosition - mLastX;
                if ((moveX <= maxRightOffset) && off < 0 || (moveX >= maxLeftOffset) && off > 0) {
                } else {
                    moveX += off;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isActionUp = true;
                f = true;
                velocityTracker.computeCurrentVelocity(1000, 3000);
                float xVelocity = velocityTracker.getXVelocity();
                if (Math.abs(xVelocity) > minvelocity) {
                    scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                            Integer.MAX_VALUE, 0, 0);
                }
                return false;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private ValueAnimator animator;

    private boolean isCancel = false;

    private void startAnim() {
        isCancel = false;
        float neededMoveX = ArithmeticUtil.mul(ArithmeticUtil.div(moveX, getBaseUnitWidth(), 0), getBaseUnitWidth());
        animator = new ValueAnimator().ofFloat(moveX, neededMoveX);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isCancel) {
                    moveX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                isCancel = true;
            }
        });
        animator.start();
    }

    private boolean f = true;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            float off = scroller.getFinalX() - scroller.getCurrX();
            off = off * functionSpeed();
            if ((moveX <= maxRightOffset) && off < 0) {
                moveX = maxRightOffset;
            } else if ((moveX >= maxLeftOffset) && off > 0) {
                moveX = maxLeftOffset;
            } else {
                moveX += off;
                if (scroller.isFinished()) {
                    startAnim();
                } else {
                    postInvalidate();
                    mLastX = scroller.getFinalX();
                }
            }

        } else {
            if (isActionUp && f) {
                startAnim();
                f = false;

            }
        }
    }

    /**
     * 控制滑动速度
     *
     * @return
     */
    private float functionSpeed() {
        return 0.2f;
    }


    private int calcTextHeight(Paint paint, String demoText) {
        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }
}



/**
 * 浮点数精确计算工具类
 */
class ArithmeticUtil {

    /**
     * 对一个数字取精度
     *
     * @param v
     * @param scale
     * @return
     */
    public static float round(float v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 精确加法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static float add(float v1, float v2) {
        BigDecimal bigDecimal1 = new BigDecimal(v1);
        BigDecimal bigDecimal2 = new BigDecimal(v2);
        return bigDecimal1.add(bigDecimal2).floatValue();
    }

    /**
     * 精确加法
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static float addWithScale(float v1, float v2, int scale) {
        BigDecimal bigDecimal1 = new BigDecimal(v1);
        BigDecimal bigDecimal2 = new BigDecimal(v2);
        return bigDecimal1.add(bigDecimal2).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 精确减法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static float sub(float v1, float v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).floatValue();
    }

    /**
     * 精确乘法,默认保留一位小数
     *
     * @param v1
     * @param v2
     * @return
     */
    public static float mul(float v1, float v2) {
        return mulWithScale(v1, v2, 1);
    }

    /**
     * 精确乘法，保留scale位小数
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static float mulWithScale(float v1, float v2, int scale) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return round(b1.multiply(b2).floatValue(), scale);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static float div(float v1, float v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 取余数
     *
     * @param v1
     * @param v2
     * @return
     */
    public static int remainder(float v1, float v2) {
        return Math.round(v1 * 100) % Math.round(v2 * 100);
    }

    /**
     * 比较大小 如果v1 大于v2 则 返回true 否则false
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean strCompareTo(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        int bj = b1.compareTo(b2);
        boolean res;
        if (bj > 0)
            res = true;
        else
            res = false;
        return res;
    }
}