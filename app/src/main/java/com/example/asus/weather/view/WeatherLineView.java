package com.example.asus.weather.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.asus.weather.R;

/**
 * 自定义View实现天气折线
 * Created by ASUS on 2018/5/29.
 */

public class WeatherLineView extends View {

    /**
     * 默认最小宽度50dp
     */
    private static final int defaultMinWidth = 300;

    /**
     * 默认最小高度80dp
     */
    private static final int defaultMinHeight = 200;

    /**
     * 字体最小默认35dp
     */
    private int mTextSize = 35;

    /**
     * 文字颜色
     */
    private int mTextColor = Color.BLACK;

    /**
     * 线的宽度
     */
    private int mLineWidth = 5;

    /**
     * 圆点的宽度
     */
    private int mDotRadius = 10;

    /**
     * 文字和点的间距
     */
    private int mTextDotDistance = 15;

    /**
     * 画文字的画笔
     */
    private TextPaint mTextPaint;

    /**
     * 画点的画笔
     */
    private Paint mDotPaint;

    /**
     * 画线的画笔
     */
    private Paint mLinePaint;

    /**
     * 3天最低温度的数据
     */
    private int mLowestData;

    /**
     * 3天最高温度的数据
     */
    private int mHighestData;
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;

    /**
     * 分别代表最左边的，中间的，右边的三个当天最低温度值
     */
    private int mLowData[];

    private int mHighData[];

    public WeatherLineView(Context context) {
        super(context);
    }

    public WeatherLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeatherLineView);
        int typeCount = a.getIndexCount();
        /**
         * 1、获得我们所定义的自定义样式属性 ，并设置默认值
         */
        for(int i = 0; i < typeCount; i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.WeatherLineView_textSize:
                    // 字体最小默认16dp，TypeValue可以把sp转化为px
                    mTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.WeatherLineView_dotRadius:
                    //圆点的宽度,默认5sp
                    mDotRadius = a.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.WeatherLineView_lineWidth:
                    //线的宽度, 默认1sp
                    mLineWidth = a.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.WeatherLineView_textColor:
                    //文字颜色, 默认为黑色
                    mTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.WeatherLineView_textDotDistance:
                    //文字和点的间距, 默认5sp
                    mTextDotDistance = a.getDimensionPixelOffset(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }
        a.recycle();

        /**
         * 2、设置画笔
         */
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStyle(Paint.Style.STROKE);
        mDotPaint.setColor(mTextColor);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mTextColor);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLineWidth);

        mBound = new Rect();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else {
            mTextPaint.setTextSize(mTextSize);
            String tempLow = Integer.toString(mLowData[1]);
            mTextPaint.getTextBounds(tempLow, 0, tempLow.length(), mBound);
            width = getPaddingLeft() + getPaddingRight() + mBound.width() + defaultMinWidth;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else {
            mTextPaint.setTextSize(mTextSize);
            String tempLow = Integer.toString(mLowData[1]);
            mTextPaint.getTextBounds(tempLow, 0, tempLow.length(), mBound);
            height = getPaddingTop() + getPaddingBottom() + mBound.height() * 2 + mTextDotDistance * 2 + defaultMinHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mHighData == null || mHighestData == 0 || mLowestData == 0 || mLowData == null) {
            return;
        }

        canvas.drawColor(Color.WHITE);

        /* 最低温度的折线 */
        String tempLow = Integer.toString(mLowData[1]);
        mTextPaint.getTextBounds(tempLow, 0, tempLow.length(), mBound);
        int tempHeight = mBound.height();//温度的高度
        int tempWidth = mBound.width();
        //画温度文字
        canvas.drawText(tempLow + "℃", (getWidth() / 2) - (tempWidth / 2),  getHeight() - cacHeight(mLowData[1]), mTextPaint);
        //画圆点
        canvas.drawCircle(getWidth() / 2, getHeight() - mTextDotDistance - tempHeight - cacHeight(mLowData[1]), mDotRadius, mDotPaint);
        //最低温度的左边
        if(mLowData[0] != 0){
            float x1 = 0;
            float y1 = getHeight() - (cacHeight(mLowData[0]) + tempHeight + mTextDotDistance);
            float x2 = getWidth() / 2 - mDotRadius / 2 - 4;
            float y2 = getHeight() - (cacHeight(mLowData[1]) + tempHeight + mTextDotDistance);
           canvas.drawLine(x1, y1, x2, y2, mLinePaint);
        }
        //最低温度的右边
        if(mLowData[2] != 0){
            float x1 = getWidth() / 2 + mDotRadius / 2 + 4;
            float y1 = getHeight() - (cacHeight(mLowData[1]) + tempHeight + mTextDotDistance);
            float x2 = getWidth();
            float y2 = getHeight() - (cacHeight(mLowData[2]) + tempHeight + mTextDotDistance);
            canvas.drawLine(x1, y1, x2, y2, mLinePaint);
        }

        /* 最高温度的折线 */
        String tempHigh = Integer.toString(mHighestData);
        //画温度文字
        canvas.drawText(tempHigh + "℃", (getWidth() / 2) - (tempWidth / 2), getHeight() - (mTextDotDistance + tempHeight + cacHeight(mHighData[1])), mTextPaint);
        //画圆点
        canvas.drawCircle(getWidth() / 2, getHeight() - (tempHeight + cacHeight(mHighData[1])), mDotRadius, mDotPaint);
        //最高温度的左边
        if(mHighData[0] != 0){
            float x1 = 0;
            float y1 = getHeight() - (cacHeight(mHighData[0]) + tempHeight);
            float x2 = getWidth() / 2 - mDotRadius / 2 - 4;
            float y2 = getHeight() - (cacHeight(mHighData[1]) + tempHeight);
            canvas.drawLine(x1, y1, x2, y2, mLinePaint);
        }
        //最高温度的右边
        if(mHighData[2] != 0){
            float x1 = getWidth() / 2 + mDotRadius / 2 + 4;
            float y1 = getHeight() - (cacHeight(mHighData[1]) + tempHeight);
            float x2 = getWidth();
            float y2 = getHeight() - (cacHeight(mHighData[2]) + tempHeight);
            canvas.drawLine(x1, y1, x2, y2, mLinePaint);
        }
    }

    /**
     * 设置当天的三个低温度数据，中间的数据就是当天的最低温度数据，
     * 第一个数据是当天和前天的数据加起来的平均数，
     * 第二个数据是当天和明天的数据加起来的平均数
     *
     * @param low  最低温度
     * @param high 最高温度
     */
    public void setLowHighData(int low[], int high[]) {
        mLowData = low;
        mHighData = high;
        invalidate();
    }

    /**
     * 设置3天里面的最低和最高的温度数据
     *
     * @param low  最低温度
     * @param high 最高温度
     */
    public void setLowHighestData(int low, int high) {
        mLowestData = low;
        mHighestData = high;
        invalidate();
    }

    /**
     * 计算当前温度相对于最高与最低温之间的温差的View的高度
     * @param tem
     * @return
     */
    private int cacHeight(int tem) {
        // 最低，最高温度之差
        int tempDistance = mHighestData - mLowestData;
        // view的最高和最低之差，需要减去文字高度和文字与圆点之间的空隙
        String temp = Integer.toString(mLowData[1]);
        mTextPaint.getTextBounds(temp, 0, temp.length(), mBound);
        int tempHeight = mBound.height();//温度的高度
        int viewDistance = getHeight() - 2 * tempHeight - 2 * mTextDotDistance;
        // 今天的温度和最低温度之间的差别
        int currentTempDistance = tem - mLowestData;
        return currentTempDistance * viewDistance / tempDistance;
    }

}
