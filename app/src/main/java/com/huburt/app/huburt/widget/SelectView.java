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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.huburt.app.huburt.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Created by hubert on 2018/5/25.
 */
public class SelectView extends View {

    public static final int TYPE_MOVE = 1;
    public static final int TYPE_EXTEND = 2;
    public static final int TYPE_CLICK = 3;
    public static final int TYPE_SLIDE = 4;
    public static final int MESSAGE_MOVE = 11;
    public static final int MESSAGE_EXTEND = 22;

    private static String[] titles = {"09:00", "09:30", "10:00", "10:30", "11:00",
            "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00",
            "16:30", "17:00", "17:30", "18:00"};

    private final int DEFAULT_HEIGHT = dp2px(100);//wrap_content高度
    private final int CLICK_SPACE = 2;//点击默认选中区域范围

    private int space = dp2px(40);//刻度间隔
    private int lineWidth = dp2px(1);//刻度线的宽度
    private int textSize = dp2px(12);
    private int textMargin = dp2px(8);//文字与长刻度的margin值
    private int rate = 1;   //短刻度与长刻度数量的比例(>=1)
    private float lineRate = 0.4f;//短刻度与长刻度长度的比例(0.0~1.0)
    private float areaRate = 0.7f;//选择区域高度占整体高度比例(0.0~1.0)
    private int selectedBgColor = Color.parseColor("#654196F5");
    private int selectedStrokeColor = Color.parseColor("#4196F5");
    private int overlappingBgColor = Color.parseColor("#65FF6666");
    private int overlappingStrokeColor = Color.parseColor("#FF6666");
    private int selectedStrokeWidth = dp2px(2);
    private int extendRadius = dp2px(7);//扩展圆的半径
    private float touchRate = 1.5f;//扩展触摸区域与视图的比率(>=1)
    private int boundary = space / 2;//屏幕边界范围
    private float linkDx = 40;//联动速率(>=1)
    private int minSelect = 2;

    private int minFlingVelocity;//最小惯性滑动速度
    private int touchSlop;//最小滑动距离
    private int width;
    private int height;
    private int maxWidth;//最大内容宽度
    private int totalWidth;//控件整体宽度
    private int minOffset = 0;
    private int maxOffset;
    private int offset = minOffset;//可视区域左边界与整体内容左边界的偏移量
    private float downX, downY;
    private float lastX;//滑动上一个位置
    private int lastFling;//惯性滑动上一个位置
    private boolean overlapping;//是否覆盖unselectable
    private int touchType;//触摸类型
    private float areaTop;//选择区域top
    private boolean lastOverlappingState;//判断是否改变覆盖状态
    private int[] lastSelected = new int[2];//判断是否改变了选择区域

    private Paint mPaint;
    private VelocityTracker velocityTracker;
    private OverScroller scroller;
    private List<RectF> unselectableList = new ArrayList<>();
    private RectF tempRect = new RectF();
    private RectF selectedRectF;//选择区域位置
    private RectF extendPointRectF;//扩展点位置
    private Bitmap bitmap;//不可选区域贴图
    private BitmapShader bitmapShader;
    private boolean linking;//是否正在联动
    private Handler handler = new BookHandler(this);
    private OverlappingStateChangeListener overlappingStateListener;
    private SelectChangeListener selectChangeListener;

    private static class BookHandler extends Handler {
        private static final int DELAY_MILLIS = 10;//刷新率（0~16）
        private WeakReference<SelectView> selectViewWeakReference;

        BookHandler(SelectView selectView) {
            super();
            selectViewWeakReference = new WeakReference<>(selectView);
        }

        @Override
        public void handleMessage(Message msg) {
            SelectView view = selectViewWeakReference.get();
            if (view != null) {
                float dx = (float) msg.obj;
                view.changeOffsetBy(dx);
                if (msg.what == MESSAGE_EXTEND) {
                    float r = view.selectedRectF.right + dx;
                    view.resetSelectedRight(r);
                } else if (msg.what == MESSAGE_MOVE) {
                    float l = view.selectedRectF.left + dx;
                    float r = view.selectedRectF.right + dx;
                    view.resetSelectedRectF(l, r);
                }
                view.postInvalidate();
                if (view.linking) {
                    sendMessageDelayed(Message.obtain(msg), DELAY_MILLIS);
                }
            }
        }
    }

    public int dp2px(final float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new OverScroller(context);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg);
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        minFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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
        if (maxOffset < 0) {
            maxOffset = 0;
        }
        areaTop = (1 - areaRate) * height;

        Timber.i("width:" + width);
        Timber.i("height:" + height);
        Timber.i("maxWidth:" + maxWidth);
        Timber.i("offset:" + offset);
        Timber.i("maxOffset:" + maxOffset);

        unselectableList.add(new RectF(3 * space, areaTop, 4 * space, height));
//        setSelectedRectF(6, 4);
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
            int position = i * space;
            if (position >= offset && position <= offset + width) {
                int x = position - offset;
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
//        mPaint.setShader(bitmapShader);
        // TODO:hxb 2018/5/29 改回bitmap
        mPaint.setColor(Color.parseColor("#99878787"));
        for (RectF rectF : unselectableList) {
            float left = Math.max(rectF.left, offset) - offset;
            float right = Math.min(rectF.right, offset + width) - offset;
            tempRect.set(left, rectF.top, right, rectF.bottom);
            canvas.drawRect(tempRect, mPaint);
        }
//        mPaint.setShader(null);
    }

    private void drawSelected(Canvas canvas) {
        if (selectedRectF == null) {
            return;
        }
        overlapping = checkOverlapping();
        if (overlapping != lastOverlappingState && overlappingStateListener != null) {
            overlappingStateListener.onOverlappingStateChanged(overlapping);
            lastOverlappingState = overlapping;
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

    private boolean checkOverlapping() {
        if (selectedRectF != null) {
            for (RectF rectF : unselectableList) {
                if (rectF.intersects(selectedRectF.left, selectedRectF.top,
                        selectedRectF.right, selectedRectF.bottom)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        int action = event.getAction();
        Timber.i("action:" + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scroller.forceFinished(true);
                downX = event.getX();
                lastX = downX;
                downY = event.getY();
                Timber.i("[ downX = " + downX + ", downY = " + downY + " ]");

                checkTouchType();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float dx = x - lastX;
                if (touchType == TYPE_EXTEND) {
                    handleExtend(dx);
                } else if (touchType == TYPE_MOVE) {
                    handleMove(dx);
                } else if (touchType == TYPE_SLIDE) {
                    changeOffsetBy(-dx);
                }
                lastX = x;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float upX = event.getX();
                float upY = event.getY();
                if (Math.abs(upX - downX) < touchSlop && Math.abs(upY - downY) < touchSlop) {
                    touchType = TYPE_CLICK;
                    performClick();
                }

                handleActionUp(upX);
                touchType = TYPE_SLIDE;
                break;
            default:
                break;
        }
        return true;
    }

    private void checkTouchType() {
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
    }

    private void handleExtend(float dx) {
        //如果正在联动时，避免手指抖动造成不必要停止
        if (linking && Math.abs(dx) < touchSlop) {
            return;
        }
        float right = selectedRectF.right += dx;
        //下层联动
        Message message = null;
        if (dx > 0 && width - (right - offset) < boundary //选中区域滑到屏幕右边
                && offset < maxOffset) {
            message = handler.obtainMessage(MESSAGE_EXTEND, linkDx);
        } else if (dx < 0 && right > selectedRectF.left
                && right - offset < boundary && offset > minOffset) {
            message = handler.obtainMessage(MESSAGE_EXTEND, -linkDx);
        }
        if (message != null) {
            if (!linking) {
                linking = true;
                handler.sendMessage(message);
            }
        } else {
            stopLinking();
            resetSelectedRight(right);
        }
    }

    private void handleMove(float dx) {
        //如果正在联动时，避免手指抖动造成不必要停止
        if (linking && Math.abs(dx) < touchSlop) {
            return;
        }
        float left = selectedRectF.left += dx;
        float right = selectedRectF.right += dx;
        Message message = null;
        if ((dx < 0 && left - offset < boundary && offset > minOffset)) {//选中区域滑到屏幕左边并继续向左滑动
            message = handler.obtainMessage(MESSAGE_MOVE, -linkDx);
        } else if (dx > 0 && width - (right - offset) < boundary && offset < maxOffset) {//选中区域滑到屏幕右边并且继续向右滑动
            message = handler.obtainMessage(MESSAGE_MOVE, linkDx);
        }
        Timber.e("message:" + message);
        if (message != null) {//处在两边界，需要联动
            if (!linking) {
                linking = true;
                handler.sendMessage(message);
            }
        } else {
            stopLinking();
            resetSelectedRectF(left, right);
        }
    }

    private void stopLinking() {
        linking = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void handleActionUp(float upX) {
        if (touchType == TYPE_CLICK) {
            int start = (int) ((upX + offset) / space);
            int[] area = getSelectedArea();
            setSelectedRectF(start, area == null ? CLICK_SPACE : area[1]);
            postInvalidate();
        } else if (touchType == TYPE_EXTEND) {
            stopLinking();
            int right = Math.round(selectedRectF.right / space) * space;
            resetSelectedRight(right);
            postInvalidate();
        } else if (touchType == TYPE_MOVE) {
            stopLinking();
            int[] area = getSelectedArea();
            if (area != null) {
                setSelectedRectF(area[0], area[1]);
                postInvalidate();
            }
        } else if (touchType == TYPE_SLIDE) {
            //处理惯性滑动
            velocityTracker.computeCurrentVelocity(1000, 8000);
            float xVelocity = velocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > minFlingVelocity) {
                scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                        Integer.MAX_VALUE, 0, 0);
            }
            velocityTracker.clear();
        }
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
            linking = false;
        } else if (offset > maxOffset) {
            offset = maxOffset;
            linking = false;
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
            int cut = Math.round((right - maxWidth) * 1f / space);
            start -= cut;//整体向左移动
            right = maxWidth;
        }
        if (selectedRectF == null) {
            selectedRectF = new RectF(start * space, areaTop, right, height);
        } else {
            selectedRectF.set(start * space, areaTop, right, height);
        }
        notifySelectChangeListener(start, count);
    }

    private void notifySelectChangeListener(int start, int count) {
        if (selectChangeListener != null
                && (lastSelected[0] != start || lastSelected[1] != count)) {
            selectChangeListener.onSelectChanged(start, count);
            lastSelected[0] = start;
            lastSelected[1] = count;
        }
    }

    /**
     * 重置选择区域的位置
     *
     * @param left
     * @param right
     */
    private void resetSelectedRectF(float left, float right) {
        if (left < 0) {
            left = 0;
            right = selectedRectF.right - selectedRectF.left;
        }
        if (right > maxWidth) {
            right = maxWidth;
            left = maxWidth - (selectedRectF.right - selectedRectF.left);
        }
        int minSpace = minSelect * space;
        if (right - left < minSpace) {//最小值
            if (maxWidth - selectedRectF.left < minSpace) {
                right = maxWidth;
                left = maxWidth - minSpace;
            } else {
                right = selectedRectF.left + minSpace;
            }
        }
        selectedRectF.left = left;
        selectedRectF.right = right;
        int[] area = getSelectedArea();
        if (area != null) {
            notifySelectChangeListener(area[0], area[1]);
        }
    }

    /**
     * 重置选择区域的right
     *
     * @param right
     */
    private void resetSelectedRight(float right) {
        if (right > maxWidth) {
            right = maxWidth;
        }
        int minSpace = minSelect * space;
        if (right - selectedRectF.left < minSpace) {//最小值
            if (maxWidth - selectedRectF.left < minSpace) {
                right = maxWidth;
                selectedRectF.left = maxWidth - minSpace;
            } else {
                right = selectedRectF.left + minSpace;
            }
        }
        selectedRectF.right = right;
        int[] area = getSelectedArea();
        if (area != null) {
            notifySelectChangeListener(area[0], area[1]);
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
        handler.removeCallbacksAndMessages(null);
    }

    public OverlappingStateChangeListener getOverlappingStateListener() {
        return overlappingStateListener;
    }

    public void setOverlappingStateListener(OverlappingStateChangeListener overlappingStateListener) {
        this.overlappingStateListener = overlappingStateListener;
    }

    public SelectChangeListener getSelectChangeListener() {
        return selectChangeListener;
    }

    public void setSelectChangeListener(SelectChangeListener selectChangeListener) {
        this.selectChangeListener = selectChangeListener;
    }

    public interface OverlappingStateChangeListener {
        void onOverlappingStateChanged(boolean isOverlapping);
    }

    public interface SelectChangeListener {
        void onSelectChanged(int start, int count);
    }
}
