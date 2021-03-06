package com.mrebollob.stepsview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leoye
 */

public class StepsViewIndicator extends View {

    private Paint paint = new Paint();
    private Paint doingPaint = new Paint();
    private Paint selectedPaint = new Paint();
    private Paint textPaint = new Paint();

    private float thumbSize = 100;
    private int mNumOfStep = 3;
    private float mLineHeight;
    private float mThumbRadius;
    private float mCircleRadius;
    private float mPadding;
    private int mProgressColor = Color.YELLOW;
    private int mdoProgressColor = Color.GREEN;
    private int mBarColor = Color.BLACK;

    private float mCenterY;
    private float mLeftX;
    private float mLeftY;
    private float mRightX;
    private float mRightY;
    private float mDelta;
    private List<Float> mThumbContainerXPosition = new ArrayList<>();
    private int mCompletedPosition;
    private OnDrawListener mDrawListener;

    public StepsViewIndicator(Context context) {
        this(context, null);
    }

    public StepsViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepsViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLineHeight = 0.2f * thumbSize;
        mThumbRadius = 0.4f * thumbSize;
        mCircleRadius = 0.7f * mThumbRadius;
        mPadding = 0.5f * thumbSize;
    }

    public void setStepSize(int size) {
        mNumOfStep = size;
        invalidate();
    }

    public void setThumbSize(float size) {
        thumbSize = size;
        init();
        invalidate();
    }

    public void setDrawListener(OnDrawListener drawListener) {
        mDrawListener = drawListener;
    }

    public List<Float> getThumbContainerXPosition() {
        return mThumbContainerXPosition;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterY = 0.5f * getHeight();
        mLeftX = mPadding;
        mLeftY = mCenterY - (mLineHeight / 2);
        mRightX = getWidth() - mPadding;
        mRightY = 0.5f * (getHeight() + mLineHeight);

        mDelta = (mRightX - mLeftX) / (mNumOfStep);

        mThumbContainerXPosition.add(mDelta / 2 + mLeftX);
        for (int i = 1; i < mNumOfStep; i++) {
            mThumbContainerXPosition.add(mThumbContainerXPosition.get(0) + (i * mDelta));
        }
        mDrawListener.onReady();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 200;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = Math.round(thumbSize);
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    public void setCompletedPosition(int position) {
        mCompletedPosition = position;
    }

    public void reset() {
        setCompletedPosition(0);
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
    }

    public void setBarColor(int barColor) {
        mBarColor = barColor;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawListener.onReady();
        // Draw rect bounds
        paint.setAntiAlias(true);
        paint.setColor(mBarColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        doingPaint.setAntiAlias(true);
        doingPaint.setColor(mdoProgressColor);
        doingPaint.setStyle(Paint.Style.STROKE);
        doingPaint.setStrokeWidth(2);

        selectedPaint.setAntiAlias(true);
        selectedPaint.setColor(mProgressColor);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setStrokeWidth(2);

        textPaint.setColor(Color.WHITE);
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        doingPaint.setStyle(Paint.Style.FILL);
        selectedPaint.setStyle(Paint.Style.FILL);
        boolean bbb = false;

        int aaa = mThumbContainerXPosition.size() - mCompletedPosition;
        if (aaa == 0 || aaa == mThumbContainerXPosition.size()) {
            bbb = true;
        }

        //灰色的线
        canvas.drawRoundRect(mRightX - mDelta * aaa - ((bbb) ? 0 : mCircleRadius * 2), mLeftY, mRightX, mRightY, mCircleRadius, mCircleRadius, paint);
        //红色的线
        canvas.drawRoundRect(mLeftX, mLeftY, mThumbContainerXPosition.get(0) - mDelta / 2 + mDelta * mCompletedPosition+150*3/(mThumbContainerXPosition.size()), mRightY, mCircleRadius, mCircleRadius, selectedPaint);


        // Draw rest of circle
        for (int i = 0; i < mThumbContainerXPosition.size(); i++) {
            final float pos = mThumbContainerXPosition.get(i);

            if(mCompletedPosition==i){
                canvas.drawCircle(pos, mCenterY, mCircleRadius,doingPaint);
            }else{
                canvas.drawCircle(pos, mCenterY, mCircleRadius,
                        (i < mCompletedPosition) ? selectedPaint : paint);
            }
        }
    }

    public interface OnDrawListener {
        void onReady();
    }
}