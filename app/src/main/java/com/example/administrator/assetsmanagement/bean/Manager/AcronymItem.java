package com.example.administrator.assetsmanagement.bean.Manager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class AcronymItem extends RecyclerView.ItemDecoration {
    private List<Person> mManagers;
    private Paint mPaint;
    private Rect mBounds;

    private int mTitleHeight;
    private static int COLOR_TITLE_BG = Color.parseColor("#bcf2ed");
    private static int COLOR_TITLE_FONT = Color.parseColor("#7C9FFF");
    private static int mTitleFontSize;

    public AcronymItem(Context context,List<Person> managers) {
        super();
        mManagers = managers;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                20, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (position > -1) {
            if (position == 0) {
                outRect.set(0, mTitleHeight, 0, 0);
            } else {
                boolean isSame =mManagers.get(position).getAcronym().equals(mManagers.get(position - 1).getAcronym());
                if ( mManagers.get(position).getAcronym()!=null && !isSame) {
                    outRect.set(0, mTitleHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (position > -1) {
                if (position == 0) {//等于0肯定要有title的
                    drawAcronymArea(c, left, right, child, params, position);
                } else {
                    if (mManagers.get(position).getAcronym() != null) {
                        boolean isSame = mManagers.get(position).getAcronym().equals(mManagers.get(position - 1).getAcronym());
                        if (!isSame) {
                            drawAcronymArea(c,left,right,child,params,position);
                        }
                    }
                }
            }
        }
    }
    private void drawAcronymArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(mManagers.get(position).getAcronym(), 0, mManagers.get(position).getAcronym().length(), mBounds);
        c.drawText(mManagers.get(position).getAcronym(), child.getPaddingLeft()+100, child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int position = ((LinearLayoutManager)(parent.getLayoutManager())).findFirstVisibleItemPosition();
        String acronym = mManagers.get(position).getAcronym();
        View child = parent.findViewHolderForLayoutPosition(position).itemView;;
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(acronym, 0, acronym.length(), mBounds);
        c.drawText(acronym, child.getPaddingLeft()+100,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
    }
}
