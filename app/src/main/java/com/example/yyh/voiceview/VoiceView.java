package com.example.yyh.voiceview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yyh on 2016/3/30.
 */
public class VoiceView extends View {
    /**
     * 定义一些必要的属性
     * @param context
     */

    //第一圈的颜色
    private int mFirstColor;

    //第二圈的颜色
    private int mSecondColor;

    //圆的宽度

    private int mCircleWidth;

    //当前进度

    private int mCurrentCount = 3;

    //画笔

    private Paint mPaint;

    //中间的图片

    private Bitmap mImage;

    //每块的块间隙

    private int mSpliteSize;

    //块的个数

    private int mCount;

    private Rect mRect;





    public VoiceView(Context context) {
        this(context,null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



        //初始化，获得一些必要的属性设置

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.VoiceView,defStyleAttr,0);

        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.VoiceView_firstColor:
                    mFirstColor = typedArray.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.VoiceView_secondColor:
                    mSecondColor = typedArray.getColor(attr, Color.RED);
                    break;
                case R.styleable.VoiceView_bg:
                    mImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
                    break;
                case R.styleable.VoiceView_circleWidth:
                    mCircleWidth = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.VoiceView_dotCount:
                    mCount = typedArray.getInt(attr,20);
                    break;
                case R.styleable.VoiceView_splitSize:
                    mSpliteSize =typedArray.getInt(attr,20);
                    break;





            }



        }

        typedArray.recycle();
        mPaint = new Paint();
        mRect = new Rect();



    }


    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        mPaint.setAntiAlias(true); //   消除锯齿
        mPaint.setStrokeWidth(mCircleWidth); //设置圆环的宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 定义线段断点为圆头
        mPaint.setStyle(Paint.Style.STROKE);//设置为空心

        int centre = getWidth()/2; //圆心横坐标

        int radius = centre- mCircleWidth/2; // 半径

        //画圆块
        drawOval(canvas,centre,radius);

        /**
         * 计算内切正方形的位置
         */
        int relRadius = radius -mCircleWidth/2; //内圆半径

        /**
         * 内切正方形的距离顶部  mCircleWidth+relRadius*(1-根号2/2);
         *
         *
         */
       // mRect.top = (int) ((relRadius-Math.sqrt(2)*relRadius*1.0f/2)+mCircleWidth);
        //mRect.left = (int) (relRadius*(1-Math.sqrt(2)*1.0f/2)+mCircleWidth);

        mRect.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        mRect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;




        mRect.right = (int) (mRect.left+Math.sqrt(2)*relRadius);
        mRect.bottom = (int) (mRect.left+Math.sqrt(2)*relRadius);


        if (mImage.getWidth()<Math.sqrt(2)*relRadius){
            mRect.left = (int) (mRect.left+Math.sqrt(2)*relRadius*1.0f/2-mImage.getWidth()*1.0f/2);
            mRect.top = (int) (mRect.top+Math.sqrt(2)*relRadius*1.0f/2-mImage.getWidth()*1.0f/2);

            //这里写成了 mRect.right = mRect.right+mImage.getWidth();  错误，找了半天，才发现

            mRect.right = mRect.left+mImage.getWidth();
            mRect.bottom = mRect.top+mImage.getHeight();

        }

        //绘图

        canvas.drawBitmap(mImage,null,mRect,mPaint);






    }

    /**
     * 画出每个小块
     * @param canvas
     * @param centre
     * @param radius
     */
    private void drawOval(Canvas canvas, int centre, int radius) {

        /**
         * 每块所占的比例
         */
        float itemSize = (360*1.0f-mCount*mSpliteSize)/mCount;


        RectF oval = new RectF(centre-radius,centre-radius,centre+radius,centre+radius);//用于定义圆弧的大小和形状

        mPaint.setColor(mFirstColor);//圆环的颜色

        for (int i = 0; i < mCount; i++) {
            canvas.drawArc(oval,i*(itemSize+mSpliteSize),itemSize,false,mPaint);
        }
        mPaint.setColor(mSecondColor);//圆环的颜色

        for (int i = 0; i < mCurrentCount; i++) {
            canvas.drawArc(oval,i*(itemSize+mSpliteSize),itemSize,false,mPaint);
        }


    }

    /**
     * 增加了判断，当音量最大时，不在增加
     */
    public void up(){

        if (mCurrentCount<mCount){
            mCurrentCount++;
        }
        postInvalidate();

    }

    /**
     * 增加了判断，当音量为0时，不再减少。
     */
    public void down(){
        if (mCurrentCount>0){
        mCurrentCount--;

        }
        postInvalidate();

    }

    private int xDown,xUp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDown= (int) event.getY();


                break;
            case MotionEvent.ACTION_UP:
                xUp = (int) event.getY();
                if(xUp>xDown){
                    down();
                }else{
                    up();
                }

                break;

        }





        return true;


    }
}
