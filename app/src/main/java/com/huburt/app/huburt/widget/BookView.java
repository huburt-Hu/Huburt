package com.huburt.app.huburt.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.huburt.app.huburt.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;

/**
 * Created by hubert on 2018/5/25.
 */
public class BookView extends View {

    public static final int TYPE_MOVE = 1;
    public static final int TYPE_EXTEND = 2;
    public static final int TYPE_CLICK = 3;
    public static final int TYPE_SLIDE = 4;

    private static String[] titles = {"09:00", "09:30", "10:00", "10:30", "11:00",
            "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00",
            "16:30", "17:00", "17:30", "18:00"};


    private static final int DEFAULT_HEIGHT = dp2px(100);//wrap_content高度

    private int space = dp2px(40);//刻度间隔
    private int lineWidth = dp2px(1);//刻度线的宽度
    private int textSize = dp2px(12);
    private int textMargin = dp2px(8);//文字与长刻度的margin值
    private int rate = 1;   //短刻度与长刻度数量的比例(>=1)
    private float lineRate = 0.4f;//短刻度与长刻度长度的比例(0.0-1.0)
    private float areaRate = 0.7f;//选择区域高度占整体高度比例(0.0-1.0)
    private int selectedBgColor = Color.parseColor("#654196F5");
    private int selectedStrokeColor = Color.parseColor("#4196F5");
    private int overlappingBgColor = Color.parseColor("#65FF6666");
    private int overlappingStrokeColor = Color.parseColor("#FF6666");
    private int selectedStrokeWidth = dp2px(2);
    private int extendRadius = dp2px(7);//扩展圆的半径
    private float touchRate = 1.5f;//扩展触摸区域与视图的比率

    private int width;
    private int height;
    private int maxWidth;//最大内容宽度
    private int totalWidth;//控件整体宽度
    private int minOffset = 0;
    private int maxOffset;
    private int offset = minOffset;//可视区域左边界与整体内容左边界的偏移量
    private int minvelocity;//最小惯性滑动速度
    private float lastX;//滑动上一个位置
    private int lastFling;//惯性滑动上一个位置
    private boolean overlapping;//是否覆盖unselectable
    private int touchType;

    private Paint mPaint;
    private VelocityTracker velocityTracker;
    private OverScroller scroller;
    private List<RectF> unselectable = new ArrayList<>();
    private RectF tempRect = new RectF();
    private RectF selectedRectF;
    private RectF extendPointRectF;
    private BitmapShader bitmapShader;
    private Bitmap bitmap;
    private float areaTop;

    public BookView(Context context) {
        this(context, null);
    }

    public BookView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new OverScroller(context);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg);
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        minvelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        width = widthSize;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = DEFAULT_HEIGHT;//wrap_content的高
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        totalWidth = titles.length * space;
        maxWidth = totalWidth - space;
        maxOffset = totalWidth - width;
        areaTop = (1 - areaRate) * height;

        Timber.i("width:" + width);
        Timber.i("height:" + height);
        Timber.i("maxWidth:" + maxWidth);
        Timber.i("offset:" + offset);
        Timber.i("maxOffset:" + maxOffset);

        unselectable.add(new RectF(3 * space, areaTop, 4 * space, height));
        setSelectedRectF(6, 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawUnselectable(canvas);
        drawSelected(canvas);
    }

    private void drawLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(0, height, width, height, mPaint);
        for (int i = 0; i < titles.length; i++) {
            int positon = i * space;
            if (positon >= offset && positon <= offset + width) {
                int x = positon - offset;
                if (i % (rate + 1) == 0) {
                    canvas.drawLine(x, 0, x, height, mPaint);

                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawText(titles[i], x + textMargin, textSize, mPaint);
                    mPaint.setStyle(Paint.Style.STROKE);
                } else {
                    canvas.drawLine(x, height * (1 - lineRate), x, height, mPaint);
                }
            }
        }
    }

    private void drawUnselectable(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(bitmapShader);
        for (RectF rectF : unselectable) {
            float left = Math.max(rectF.left, offset) - offset;
            float right = Math.min(rectF.right, offset + width) - offset;
            tempRect.set(left, rectF.top, right, rectF.bottom);
            canvas.drawRect(tempRect, mPaint);
        }
        mPaint.setShader(null);
    }

    private void drawSelected(Canvas canvas) {
        if (selectedRectF == null) {
            return;
        }
        float left = Math.max(selectedRectF.left, offset) - offset;
        float right = Math.min(selectedRectF.right, offset + width) - offset;
        tempRect.set(left, selectedRectF.top, right, selectedRectF.bottom);
        //填充
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(overlapping ? overlappingBgColor : selectedBgColor);
        canvas.drawRect(tempRect, mPaint);
        //边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(selectedStrokeWidth);
        mPaint.setColor(overlapping ? overlappingStrokeColor : selectedStrokeColor);
        canvas.drawRect(tempRect, mPaint);
        if ((selectedRectF.right - offset) == right) {
            //扩展圆边框
            mPaint.setColor(overlapping ? overlappingStrokeColor : selectedStrokeColor);
            canvas.drawCircle(tempRect.right, tempRect.centerY(), extendRadius, mPaint);
            //扩展圆填充
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(tempRect.right, tempRect.centerY(), extendRadius, mPaint);

            extendPointRectF = new RectF(selectedRectF.right - extendRadius * touchRate,
                    selectedRectF.centerY() - extendRadius * touchRate,
                    selectedRectF.right + extendRadius * touchRate,
                    selectedRectF.centerY() + extendRadius * touchRate);
        } else {
            extendPointRectF = null;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scroller.forceFinished(true);
                lastX = event.getX();
                float downY = event.getY();
                Timber.i("[ downX = " + lastX + ", downY = " + downY + " ]");
                RectF extend = null;
                if (extendPointRectF != null) {
                    extend = new RectF(extendPointRectF.left - offset, extendPointRectF.top,
                            extendPointRectF.right - offset, extendPointRectF.bottom);
                    Timber.i("extend:" + extend.toString());
                }
                RectF selected = null;
                if (selectedRectF != null) {
                    selected = new RectF(selectedRectF.left - offset, selectedRectF.top,
                            selectedRectF.right - offset, selectedRectF.bottom);
                    Timber.i("selected:" + selected.toString());
                }

                if (selectedRectF == null) {
                    touchType = TYPE_CLICK;
                } else if (extend != null && extend.contains(lastX, downY)) {
                    touchType = TYPE_EXTEND;
                } else if (selected != null && selected.contains(lastX, downY)) {
                    touchType = TYPE_MOVE;
                } else {
                    touchType = TYPE_SLIDE;
                }
                Timber.i("touchType:" + touchType);
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float dx = x - lastX;
                if (touchType == TYPE_EXTEND) {
                    float right = selectedRectF.right += dx * 2;
                    if (right > maxWidth) {
                        right = maxWidth;
                    }
                    if (right - selectedRectF.left < space) {
                        right = selectedRectF.left + space;
                    }
                    //联动
                    changeOffsetBy(dx);
                    selectedRectF.set(selectedRectF.left, selectedRectF.top, right, selectedRectF.bottom);
                } else if (touchType == TYPE_MOVE) {
                    float left = selectedRectF.left += dx * 2;
                    float right = selectedRectF.right += dx * 2;
                    //边界处理
                    if (left < 0) {
                        left = 0;
                        right = selectedRectF.right - selectedRectF.left;
                    }
                    if (right > maxWidth) {
                        right = maxWidth;
                        left = maxWidth - (selectedRectF.right - selectedRectF.left);
                    }
                    //联动
                    changeOffsetBy(dx);

                    selectedRectF.set(left, selectedRectF.top, right, selectedRectF.bottom);

                } else if (touchType == TYPE_SLIDE) {
                    changeOffsetBy(-dx);
                }
                lastX = x;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touchType == TYPE_CLICK) {
                    // TODO:hxb 2018/5/28 click
                } else if (touchType == TYPE_EXTEND) {
                    int right = Math.round(selectedRectF.right / space) * space;
                    selectedRectF.set(selectedRectF.left, selectedRectF.top, right, selectedRectF.bottom);
                    postInvalidate();
                } else if (touchType == TYPE_MOVE) {
                    int[] area = getSelectedArea();
                    if (area != null) {
                        setSelectedRectF(area[0], area[1]);
                        postInvalidate();
                    }
                } else if (touchType == TYPE_SLIDE) {
                    //处理惯性滑动
                    velocityTracker.computeCurrentVelocity(1000, 8000);
                    float xVelocity = velocityTracker.getXVelocity();
                    if (Math.abs(xVelocity) > minvelocity) {
                        scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                                Integer.MAX_VALUE, 0, 0);
                    }
                    velocityTracker.clear();
                }
                touchType = TYPE_SLIDE;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 改变offset，处理边界
     *
     * @param dx 变化量
     */
    private void changeOffsetBy(float dx) {
        offset += dx;
        if (offset < minOffset) {
            offset = minOffset;
        } else if (offset > maxOffset) {
            offset = maxOffset;
        }
    }

    /**
     * 将选择内容转换成区域
     *
     * @param start 开始位置
     * @param count 数量
     */
    private void setSelectedRectF(int start, int count) {
        int right = (start + count) * space;
        if (right > maxWidth) {
            right = maxWidth;
        }
        if (selectedRectF == null) {
            selectedRectF = new RectF(start * space, areaTop, right, height);
        } else {
            selectedRectF.set(start * space, areaTop, right, height);
        }
    }

    /**
     * 将选中区域转换成选择内容
     *
     * @return [start, count]
     */
    private int[] getSelectedArea() {
        if (selectedRectF == null) {
            return null;
        }
        int[] area = new int[2];
        float w = selectedRectF.right - selectedRectF.left;
        area[0] = Math.round(selectedRectF.left / space);
        area[1] = Math.round(w / space);
        return area;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int finalX = scroller.getFinalX();
            int currX = scroller.getCurrX();
            float dx = currX - lastFling;
            //已经在边界了，不在处理惯性
            if ((offset <= minOffset && dx > 0) || offset >= maxOffset && dx < 0) {
                scroller.forceFinished(true);
                return;
            }
            changeOffsetBy(-dx);
            lastFling = currX;
            postInvalidate();
        } else {
            lastFling = 0;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        velocityTracker.recycle();
        bitmap.recycle();
    }
}
