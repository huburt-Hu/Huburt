package com.huburt.app.huburt.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;

/**
 * Created by hubert
 * <p>
 * Created on 2017/6/7.
 */

public class TimeSectionPicker extends View implements View.OnTouchListener {

    public static final int TYPE_MOVE = 1;
    public static final int TYPE_EXTEND = 2;
    public static final int TYPE_CLICK = 3;

    private static String[] titles = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30"
            , "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"};

    private static String subTitle = "30";

    private int lineColor = Color.parseColor("#dedede");

    private int lightTitleColor = Color.parseColor("#71baff");
    private int titleColor = Color.parseColor("#666666");
    private int textSize = dp2px(12);

    private int textColor = Color.parseColor("#fefefe");
    private int bookColor = Color.parseColor("#71baff");
    private int bookStrokeColor = Color.parseColor("#71baff");
    private int usedColor = Color.parseColor("#f3928a");
    private int usedStrokeColor = Color.parseColor("#f3928a");

    private int overdueColor = Color.parseColor("#c7c7c7");

    private int overlappingColor = Color.parseColor("#ff9971");
    private float round = 10f;//区域圆角

    private float extendPointR = dp2px(8);//拉伸点半径
    private int space = dp2px(25);//刻度间隔
    private int offset = 100;//短线偏移量

    private boolean isFrist = true;//初始化padding和宽高值
    private int type;//移动.扩展拉伸.点击

    private Paint mPaint;
    private Point p1;
    private Point p2;
    private Rect titleBounds;
    private RectF bookRect;
    private RectF usedRect;
    private int paddingTop;
    private int paddingLeft;
    private int width;
    private float downY;
    private int bookStart = -1;
    private int bookCount = 0;
    private List<int[]> used = new ArrayList<>();
    private List<RectF> usedAreas = new ArrayList<>();
    private int lineNumber;
    private RectF extendPointRect;
    private float bottom;
    public int[] overdue;

    private OverlappingStateChangeListener listener;
    private boolean lastState;
    private OnBookChangeListener bookChangeListener;
    private String usedText;
    private String bookText;


    public TimeSectionPicker(Context context) {
        this(context, null);
    }

    public TimeSectionPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSectionPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(textSize);

        titleBounds = new Rect();
        mPaint.getTextBounds(titles[0], 0, titles[0].length(), titleBounds);

        p1 = new Point();
        p2 = new Point();
        bookRect = new RectF();
        usedRect = new RectF();
    }

    public void setOverdue(int[] overdue) {
        this.overdue = overdue;
    }

    public void setBookArea(int start, int count) {
        bookStart = start;
        bookCount = count;
        setBookRect(start, count);
        postInvalidate();
    }

    public void clearBookArea() {
        bookStart = -1;
        bookCount = 0;
        setBookRect(0, 0);
        postInvalidate();
    }

    public void addUsed(int[] area) {
        used.add(area);
        postInvalidate();
    }

    public List<int[]> getUsed() {
        return used;
    }

    public void clearUsed() {
        used.clear();
        overdue = null;
        postInvalidate();
    }

    public int getTimeNumber(int hour, int minute) {
        int result = (hour - 9) * 2;
        if (minute > 30) {
            result += 2;
        } else if (minute > 0) {
            result += 1;
        }
        if (result > titles.length - 1) {
            result = titles.length - 1;
        }
        return result;
    }

    public String[] getBookTime() {
        if (bookStart == -1) {
            return null;
        }
        String[] strings = new String[2];
        strings[0] = titles[bookStart];
        strings[1] = titles[bookStart + bookCount];
        return strings;
    }

    public int getBookCount() {
        return bookCount;
    }

    private String getTimeString(int start, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < titles.length; i++) {
            if (start == i) {
                sb.append(titles[i]);
                sb.append("~");
            }
            if (start + count == i) {
                sb.append(titles[i]);
            }
        }
        return sb.toString();
    }

    public void setOverlappingStateChangeListener(OverlappingStateChangeListener listener) {
        this.listener = listener;
    }

    public void setBookChangeListener(OnBookChangeListener bookChangeListener) {
        this.bookChangeListener = bookChangeListener;
    }

    public boolean isOverlapping() {
        if (bookCount == 0) {
            return false;
        }
        for (RectF usedArea : usedAreas) {
            if (usedArea.intersect(bookRect)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 800;//wrap_content的宽
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = space * titles.length;//wrap_content的高
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFrist) {//初始化参数
            //处理padding
            paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            width = getWidth() - paddingLeft - paddingRight;
            int height = getHeight() - paddingTop - paddingBottom;

            lineNumber = titles.length;
            bookRect.set(paddingLeft + 180, paddingTop + space * bookStart
                    , width - 30, paddingTop + space * (bookStart + bookCount));

            usedRect.set(paddingLeft + 180, paddingTop, width - 30, paddingTop);

            bottom = paddingTop + space * (titles.length - 1);
            isFrist = false;
        }

        //预定框与已预定是否交叠
        boolean overlapping = isOverlapping();
        if (overlapping != lastState && listener != null) {
            listener.onOverlappingStateChanged(overlapping);
            lastState = overlapping;
        }

        //画刻度线
        mPaint.setColor(lineColor);
        for (int i = 0; i < lineNumber; i++) {
            p1.set(i % 2 == 1 ? paddingLeft + offset : paddingLeft, paddingTop + space * i);
            p2.set(width, paddingTop + space * i);
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaint);
        }

        //画时间文字
        mPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < lineNumber; i++) {
            if (i >= bookStart && i <= bookStart + bookCount) {
                mPaint.setColor(lightTitleColor);
            } else {
                mPaint.setColor(titleColor);
            }
            if (i % 2 == 0) {
                canvas.drawText(titles[i], paddingLeft, paddingTop + titleBounds.height() * 1.3f + space * i, mPaint);
            } else {
                if (i == bookStart || i == bookStart + bookCount) {
                    canvas.drawText(subTitle, paddingLeft + titleBounds.width() / 2, paddingTop + titleBounds.height() * 1.2f + space * i, mPaint);
                }
            }
        }
        //画已使用区域
        usedAreas.clear();
        for (int[] ints : used) {
            RectF rectF = new RectF();
            rectF.set(usedRect.left, usedRect.top + space * ints[0]
                    , usedRect.right, usedRect.bottom + space * (ints[0] + ints[1]));
            usedAreas.add(rectF);
            drawUsedRect(rectF, canvas, mPaint, usedText);
        }
        //画过期的区域
        if (overdue != null) {
            RectF rectF = new RectF();
            rectF.set(usedRect.left, usedRect.top + space * overdue[0]
                    , usedRect.right, usedRect.bottom + space * (overdue[0] + overdue[1]));
            usedAreas.add(rectF);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(overdueColor);
            mPaint.setStrokeMiter(3);
            canvas.drawRoundRect(rectF, round, round, mPaint);
        }

        //画预定区域
        drawBookRect(canvas, mPaint, overlapping);
    }

    private void drawUsedRect(RectF rectF, Canvas canvas, Paint paint, String text) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeMiter(1);
        paint.setColor(usedStrokeColor);
        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(usedColor);
        canvas.drawRoundRect(rectF, round, round, paint);

        //不需要文字了
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setColor(textColor);
//        canvas.drawText(text, rectF.centerX(), rectF.centerY(), paint);
    }

    public void drawBookRect(Canvas canvas, Paint paint, boolean overlapping) {
        if (bookCount == 0) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(overlapping ? overlappingColor : bookStrokeColor);
        canvas.drawRoundRect(bookRect, round, round, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(overlapping ? overlappingColor : bookColor);
        canvas.drawRoundRect(bookRect, round, round, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(textColor);
        canvas.drawText(bookText, bookRect.centerX(), bookRect.centerY(), paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(bookRect.centerX(), bookRect.bottom, extendPointR, paint);
        paint.setColor(overlapping ? overlappingColor : bookStrokeColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(bookRect.centerX(), bookRect.bottom, extendPointR, paint);

        extendPointRect = new RectF(bookRect.centerX() - extendPointR * 2, bookRect.bottom - extendPointR * 2
                , bookRect.centerX() + extendPointR * 2, bookRect.bottom + extendPointR * 2);

        //查看扩展点触发区域
//        paint.setColor(Color.BLACK);
//        Log.i("tag", extendPointRect.toString());
//        canvas.drawRect(extendPointRect, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //view独享事件，即父view不可以获取后续事件，scrollview默认是false
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                downY = event.getY();
                Log.i("tag", "action down -- x,y:" + x + "," + downY);
                if (extendPointRect != null && extendPointRect.contains(x, downY)) {
                    type = TYPE_EXTEND;
                    return true;
                }
                if (bookRect.contains(x, downY)) {
                    type = TYPE_MOVE;
                    return true;
                }
                if (bookCount == 0 && checkClick(downY) && x > 150) {
                    type = TYPE_CLICK;
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
//                Log.i("tag", "action move -- y:" + currentY);
                float dY = currentY - downY;
                //外层联动
                ViewParent p = getParent();
                if (p instanceof ScrollView && type != TYPE_CLICK) {
                    ScrollView parent = (ScrollView) p;
                    parent.scrollBy(0, (int) dY / 2);
                }

                if (bookChangeListener != null) {
                    bookChangeListener.onBookChange();
                }

                switch (type) {
                    case TYPE_MOVE:
                        bookRect.set(bookRect.left, bookRect.top + dY, bookRect.right, bookRect.bottom + dY);
                        bookStart = Math.round((bookRect.top - paddingTop) / space);
                        //边缘修正
                        if (bookRect.top < paddingTop) {
                            bookStart = 0;
                            setBookRect(bookStart, bookCount);
                        }
                        if (bookRect.bottom > bottom) {
                            bookStart = titles.length - 1 - bookCount;
                            setBookRect(bookStart, bookCount);
                        }
                        break;
                    case TYPE_EXTEND:
                        bookRect.set(bookRect.left, bookRect.top, bookRect.right, bookRect.bottom + dY);
                        int end = (int) ((bookRect.bottom - paddingTop) / space);
                        bookCount = end - bookStart;
                        if (bookCount < 1) {
                            bookCount = 1;
                            setBookRect(bookStart, bookCount);
                        }
                        if (bookRect.bottom > bottom) {
                            end = titles.length - 1;
                            bookCount = end - bookStart;
                            setBookRect(bookStart, bookCount);
                        }
                        break;
                    case TYPE_CLICK:
                        break;
                }
                downY = currentY;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
//                Log.i("tag", "action up --");
                switch (type) {
                    case TYPE_MOVE:
                        if (bookRect.top < paddingTop) {
                            bookStart = 0;
                        }
                        break;
                    case TYPE_EXTEND:
                        int end = Math.round((bookRect.bottom - paddingTop) / space);
                        if (bookRect.bottom > bottom) {
                            end = titles.length - 1;
                        }
                        bookCount = end - bookStart;
                        break;
                    case TYPE_CLICK:
                        bookStart = (int) ((downY - paddingTop) / space);
                        if (bookStart > titles.length - 1 - 2) {
                            bookStart = titles.length - 1 - 2;
                        }
                        bookCount = 2;
                        break;
                }
                setBookRect(bookStart, bookCount);
                postInvalidate();
                break;
        }
        return false;
    }

    private boolean checkClick(float y) {
        for (RectF rectF : usedAreas) {
            if (y >= rectF.top && y <= rectF.bottom) {
                return false;
            }
        }
        //防止点击最下方边界外也绘制book区域
        int max = paddingTop + (lineNumber - 2) * space;
        return y <= max;
    }

    private void setBookRect(int start, int count) {
        if (bookChangeListener != null) {
            bookChangeListener.onBookCountChanged(count);
        }
        bookRect.set(bookRect.left, paddingTop + space * start
                , bookRect.right, paddingTop + space * (start + count));
    }

    public interface OverlappingStateChangeListener {
        void onOverlappingStateChanged(boolean isOverlapping);
    }

    public interface OnBookChangeListener {
        void onBookChange();

        void onBookCountChanged(int bookCount);
    }


}
