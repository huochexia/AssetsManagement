package com.example.administrator.assetsmanagement.bean.Manager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.onIndexPressedListener;
import com.example.administrator.assetsmanagement.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class CharIndexBar extends View {

    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private List<String> mCharList;
    private Map<String, Integer> textColorMap = new HashMap<>();//用于存放每个索引的颜色
    private Paint mPaint;
    private int mCharHeight,mWidth,mHeight;

    private onIndexPressedListener mOnIndexPressedListener;
    private TextView mShowHintText;
    private LinearLayoutManager mLayoutManager;
    /**
     * 得到按下事件监听器
     * @return
     */
    public onIndexPressedListener getmOnIndexPressedListener() {
        return mOnIndexPressedListener;
    }

    /**
     * 设置按下事件监听器
     * @param mOnIndexPressedListener
     */
    public void setmOnIndexPressedListener(onIndexPressedListener mOnIndexPressedListener) {
        this.mOnIndexPressedListener = mOnIndexPressedListener;
    }
    public CharIndexBar(Context context) {
        this(context,null);
    }

    public CharIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CharIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics());
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CharIndexBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CharIndexBar_textSize:
                    textSize = typedArray.getDimensionPixelSize(attr, 15);

            }
        }
        typedArray.recycle();
        mCharList= Arrays.asList(INDEX_STRING);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        initEvent();
        initMap();
    }

    /**
     * 初始化Map
     */
    public void initMap() {
        for(int i=0;i<mCharList.size();i++) {
            textColorMap.put(mCharList.get(i), Color.GRAY);
        }
    }

    /**
     * 初始化事件
     */
    public void initEvent() {
        setmOnIndexPressedListener(new onIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (mShowHintText != null) {
                    mShowHintText.setVisibility(View.VISIBLE);
                    mShowHintText.setText(text);
                }
                if (mLayoutManager != null) {
//                    int position = getPosByTag(text);
//                    if (position != -1) {
//                        mLayoutManager.scrollToPositionWithOffset(position, 0);
//                    }
                }
                initMap();
                textColorMap.put(text, Color.BLUE);
                postInvalidate();
            }

            @Override
            public void onMotionEventEnd() {
                if (mShowHintText != null) {
                   Handler handler = new Handler();
                   Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mShowHintText.setVisibility(View.GONE);
                        }
                    };
                    handler.postDelayed(runnable, 3000);
                }
            }
        });
    }
    /**
     * 显示当前被按下的index的TextView
     *
     * @return
     */

    public CharIndexBar setShowHintText(TextView mShowHintText) {
        this.mShowHintText = mShowHintText;
        return this;
    }

    public CharIndexBar setmLayoutManager(LinearLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
        return this;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取出宽高的MeasureSpec  Mode 和Size
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidth = 0, measureHeight = 0;//最终测量出来的宽高

        //得到合适宽度：
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index;//每个要绘制的index内容
        for (int i = 0; i < mCharList.size(); i++) {
            index = mCharList.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
            measureWidth = Math.max(indexBounds.width(), measureWidth);//循环结束后，得到index的最大宽度
            measureHeight = Math.max(indexBounds.width(), measureHeight);//循环结束后，得到index的最大高度，然后*size
        }
        measureHeight *= mCharList.size();
        switch (wMode) {
            case MeasureSpec.EXACTLY:
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth, wSize);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        //得到合适的高度：
        switch (hMode) {
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight, hSize);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        setMeasuredDimension(measureWidth, measureHeight);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        int t = getPaddingTop();
        Rect BarBounds = new Rect();
        String index;//每个要绘制的index内容
        for (int i = 0; i < mCharList.size(); i++) {
            index = mCharList.get(i);
            mPaint.setColor(textColorMap.get(mCharList.get(i)));
            mPaint.getTextBounds(index, 0, index.length(), BarBounds);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int baseline = (int) ((mCharHeight - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(index, mWidth / 2 - BarBounds.width() / 2, t + mCharHeight * i + baseline, mPaint);
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCharHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mCharList.size();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                setBackgroundColor(mPressedBackground);//手指按下时背景变色
                //注意这里没有break，因为down时，也要计算落点 回调监听器
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                //通过计算判断落点在哪个区域：
                int pressI = (int) ((y - getPaddingTop()) / mCharHeight);
                //边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (pressI < 0) {
                    pressI = 0;
                } else if (pressI >= mCharList.size()) {
                    pressI = mCharList.size() - 1;
                }
                //回调监听器
                if (null != mOnIndexPressedListener) {
                    mOnIndexPressedListener.onIndexPressed(pressI, mCharList.get(pressI));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
//                setBackgroundResource(android.R.color.transparent);//手指抬起时背景恢复透明
                //回调监听器
                if ( mOnIndexPressedListener!= null) {
                    mOnIndexPressedListener.onMotionEventEnd();
                }
                break;
        }
        return true;
    }
}
