package com.test.myprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import java.io.InputStream;

/**
 * Created by Administrator on 2018/4/19 0019.
 */

public class MyProgressBar extends ProgressBar {


    private String TAG = "MyProgressBar";
    private static final int DEFAULT_TEXT_SZIE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_TEXT_OFFSET = 10;
    private static final int DEFAULT_UNREACH_HEIGHT = 2;
    private static final int DEFAULT_UNREACH_COLOR = 0XF0F0F0;
    private static final int DEFAULT_REACH_HEIGHT = 3;
    private static final int DEFAULT_REACH_COLOR = 0X00FF00;


    private int mTextSize = sp2PX(DEFAULT_TEXT_SZIE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextOffest = dp2px(DEFAULT_TEXT_OFFSET);
    private int mTextHeight;
    private int mUnReachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);
    private int mUnReachColor = DEFAULT_UNREACH_COLOR;
    private int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
    private int mReachColor = DEFAULT_REACH_COLOR;
    private int mImageCar = R.mipmap.ic_launcher;
    private int mImageWeight;
    private int mImageHeight;


    private float mFileMax = 0f;
    private float mFileCurrent = 0f;
    private float mProgressCurrent = 0f;


    private Paint mReachPaint = new Paint();
    private Paint mUnReachPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private int mRealWidth;
    private boolean mIfDrawText = true;
    private Bitmap bmp;


    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getStyleAttrs(context, attrs);
    }

    private void getStyleAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyProgressBar);
        mTextSize = (int) ta.getDimension(R.styleable.MyProgressBar_progress_text_size, 0);
        mTextColor = ta.getColor(R.styleable.MyProgressBar_progress_text_color, mTextColor);
        mTextOffest = (int) ta.getDimension(R.styleable.MyProgressBar_progress_text_offest, mTextOffest);
        mUnReachColor = ta.getColor(R.styleable.MyProgressBar_progress_unreach_color, mUnReachColor);
        mUnReachHeight = (int) ta.getDimension(R.styleable.MyProgressBar_progress_unreach_height, mUnReachHeight);
        mReachColor = ta.getColor(R.styleable.MyProgressBar_progress_reach_color, mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.MyProgressBar_progress_reach_height, mReachHeight);
        mImageCar = ta.getResourceId(R.styleable.MyProgressBar_progress_image_car, mImageCar);
        ta.recycle();
        initPaint();
        resource2Bitmap();
    }

    private void resource2Bitmap() {
        Resources r = getResources();
        InputStream is = r.openRawResource(mImageCar);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        bmp = bmpDraw.getBitmap();
        mImageWeight = bmp.getWidth();
        mImageHeight = bmp.getHeight();
    }

    private void initPaint() {

        mReachPaint.setColor(mReachColor);
        mReachPaint.setStrokeWidth(mReachHeight);
        mUnReachPaint.setColor(mUnReachColor);
        mUnReachPaint.setStrokeWidth(mUnReachHeight);
        mTextPaint.setColor(mReachColor);
        mTextPaint.setTextSize(20);
        mTextPaint.setStrokeWidth(mReachHeight);
    }

    public void setFileCur(float FileCur) {
        //invalidate();
        mFileCurrent = FileCur;
    }

    public void setFileMax(float FileMax) {
        mFileMax = FileMax;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidthVal = MeasureSpec.getSize(widthMeasureSpec);
        int mHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mWidthVal, mHeight);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mTextHeight = ((int) mTextPaint.descent()) - ((int) mTextPaint.ascent());
        Log.e(TAG, "mTextHeight" + mTextHeight);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        mProgressCurrent = mFileCurrent / mFileMax * mRealWidth;
        canvas.save();
        //绘制已完成进度条
        canvas.drawLine(0, (mImageHeight - mReachHeight) / 2 + mTextHeight, mProgressCurrent, (mImageHeight - mReachHeight) / 2 + mTextHeight, mReachPaint);
        //绘制中心内容
        //绘制文本的时候，使用下面方法的时候，如果 y=0 就会看不到文本，因为绘制是以左下角开始的 必须给一个
        canvas.drawText((int)(mFileCurrent / mFileMax * 100) + "%", mProgressCurrent + 8, mTextHeight, mTextPaint);
        canvas.drawBitmap(bmp, mProgressCurrent + 4, mTextHeight, mReachPaint);
        //绘制未完成进度条
        canvas.drawLine(mProgressCurrent + mImageWeight + 8, (mImageHeight - mUnReachHeight) / 2 + mTextHeight, mRealWidth, (mImageHeight - mUnReachHeight) / 2 + mTextHeight, mUnReachPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w - getPaddingRight() - getPaddingLeft();
    }


    private int measureHeight(int heightMeasureSpec) {
        int heigtMode = MeasureSpec.getMode(heightMeasureSpec);
        int heigtVal = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        /**
         * 如果父控件传来的模式为精确 也就是固定DP
         * 否则就是无论为包裹还是充满 全是充满
         * 如果为精确值 控件高度就直接用值
         * 否则就获取控件的上下内边距+ 控件中高度最高的值
         */

        /**
         *  match_parent/固定DP_MeasureSpec.Mode=EXACTLY
         *  match_parent/固定DP_MeasureSpec.Size=屏幕像素尺寸值/固定DP
         *  if模式为精确值,就是固定DP或者充满父控件,
         *  else就是未指定,需要获取控件的本身的高度
         *      else模式等于控件最大值,就要从两者中取最小值 作为包裹了
         */
        if (heigtMode == MeasureSpec.EXACTLY) {
            result = heigtVal;
        } else {
            int imageHeight = mImageHeight + mTextHeight;
            result = getPaddingTop() + getBottom() + Math.max(Math.max(mReachHeight, mUnReachHeight), Math.abs(imageHeight));
            /**
             *  warp_content_MeasureSpec.Mode=AT_MOST
             *  warp_content_MeasureSpec.Size=屏幕像素尺寸值
             */
            if (heigtMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, heigtVal);
            }
        }
        return result;
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    private int sp2PX(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}
