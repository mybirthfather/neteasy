package com.example.likeneteasy;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import static android.view.animation.Animation.INFINITE;

public class PlayerView extends View {

    private int INNERRING_RADIUS = 0;//间距表示圆环的宽度
    private Paint innerRingPaint;
    private Paint innerBitmapPaint;
    private Bitmap bitmap;
    private int screenWidth;
    private int screenHeight;
    private ValueAnimator valueAnimator;
    private Matrix matrix;
    private Bitmap bitmapCopy;
    private Bitmap roundBitmap;
    private int CENTERX = 0;
    private int CENTERY = 0;
    private Paint mOutPaint;
    private int RING_RADIUS = 0;
    private Paint smallPaint;
    private long currentPlayTime;
    private ValueAnimator valueAnimatorMAXRADIUSRING;
    private RingObject ringObject;
    private RingObject ringObject1;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int DURATION = 5000;

    private void init(Context context) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        Log.e("1111111111111", "width=" + screenWidth);
        screenHeight = wm.getDefaultDisplay().getHeight();
        INNERRING_RADIUS = screenWidth / 2 - 100;//左右各留100的间距
        RING_RADIUS = INNERRING_RADIUS;
        innerRingPaint = new Paint();
        innerRingPaint.setColor(Color.CYAN);
        innerRingPaint.setStyle(Paint.Style.STROKE);
        innerRingPaint.setStrokeWidth(20);
        innerRingPaint.setAntiAlias(true);
        innerBitmapPaint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);

        innerBitmapPaint.setAntiAlias(true);
        valueAnimator = ValueAnimator.ofFloat(0f, 60f, 120f, 180f, 240f, 300f, 360f);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(INFINITE);
        matrix = new Matrix();
        bitmapCopy = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true).copy(Bitmap.Config.ARGB_8888, true);
        roundBitmap = toRoundBitmap(bitmapCopy);
        CENTERX = screenWidth / 2;
        CENTERY = screenHeight / 2;
        smallPaint = new Paint();
        smallPaint.setColor(Color.RED);
        smallPaint.setStyle(Paint.Style.STROKE);
        smallPaint.setAntiAlias(true);
//        matrix.reset();
//        matrix.postScale(0.5f, 0.5f);
//        matrix.preTranslate(-screenWidth/2, -screenHeight/2);
//        matrix.postTranslate(screenWidth/2, screenHeight/2);
//        matrix.setTranslate(screenWidth/2, screenHeight/2);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);//画原图的
        innerBitmapPaint.setShader(bitmapShader);
        mOutPaint = new Paint();
        mOutPaint.setColor(Color.CYAN);
        mOutPaint.setStyle(Paint.Style.STROKE);
        mOutPaint.setStrokeWidth(5);
        mOutPaint.setStrokeCap(Paint.Cap.ROUND);
        valueAnimatorMAXRADIUSRING = ValueAnimator.ofInt(0, 90);//zheli
        valueAnimatorMAXRADIUSRING.setInterpolator(new LinearInterpolator());
        valueAnimatorMAXRADIUSRING.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimatorMAXRADIUSRING.setRepeatMode(ValueAnimator.RESTART);
        IntEvaluator intEvaluator = new IntEvaluator() {
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return super.evaluate(fraction, startValue, endValue);
            }
        };
        valueAnimatorMAXRADIUSRING.setEvaluator(intEvaluator);
        valueAnimatorMAXRADIUSRING.setDuration(1200);
        ringObject = new RingObject(RING_RADIUS, CENTERY, this, path, CENTERX, MAXRADIUSRING, RADIUS_SMALLCIRCLE);
        ringObject1 = new RingObject(RING_RADIUS, CENTERY, this, path, CENTERX, MAXRADIUSRING, RADIUS_SMALLCIRCLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                flag+=1;
//                if (flag==1){
//                    ringObject1.startAnimation();
//                    flag=0;
//                }else {
//                    ringObject.startAnimation();
//                    flag=1;
//                }
                handler.postDelayed(this, 600);


            }
        }, 600);


    }

    private int flag = 0;
    // 用 透明度区分 透明度内 不断添加 因为超过0之后就又变1了
    //    旋转位图其实是通过创建一个矩阵，旋转该矩阵，用矩阵和原图创建一个新的原图副本（矩阵就是相框，副本就是相片）
    int bili = 0;
    private float alpha = 1.0f;
    private float currentAngle = 0;
    Path path = new Path();
    private int MAXRADIUSRING = 120;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean started = valueAnimator.isStarted();
        if (!started) {
            valueAnimator.start();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // 要么 先pre 在rotate 在post
                    // 要么 直接 setRotate 在post 移动位置
                    float animatedValue = (float) animation.getAnimatedValue();
                    currentPlayTime = animation.getCurrentPlayTime();
                    matrix.preTranslate(screenWidth / 2 - roundBitmap.getWidth() / 2, screenHeight / 2 - roundBitmap.getHeight() / 2);
                    matrix.setRotate(animatedValue, roundBitmap.getWidth() / 2, roundBitmap.getHeight() / 2);
                    matrix.postTranslate(screenWidth / 2 - roundBitmap.getWidth() / 2, screenHeight / 2 - roundBitmap.getHeight() / 2);
                    canvas.rotate(-animatedValue);//这个只负责旋转
                    invalidate();
                }
            });

        }
        canvas.drawCircle(screenWidth / 2, screenHeight / 2, INNERRING_RADIUS, innerRingPaint);
        canvas.drawBitmap(roundBitmap, matrix, innerBitmapPaint);
        drawCirclre(canvas);
    }

    private int RADIUS_SMALLCIRCLE = 10;

    private void drawCirclre(Canvas canvas) {
        ringObject.startAnimation();
        if (ringObject.getBili() == 60) {
            ringObject1.startAnimation();
        }
        Path ringObjectpath = ringObject.getPath();
        Path ringObjectpath1 = ringObject1.getPath();
        canvas.drawPath(ringObjectpath,ringObject.getPaint());
        canvas.drawPath(ringObjectpath1,ringObject1.getPaint());
        //        canvas.drawPath(path, smallPaint);
    }

    private final Handler handler = new Handler();


    public Bitmap toRoundBitmap(Bitmap bitmap) {
        //首先 把他搞成正方形 不然会指出来是矩形  坐标没法算 因为坐标根据的这个bitmap的宽高 如果他过于宽的话 那么绘制在外面了 因为我们 1/2的
        //宽度就在外面 还一个问题 宽高  还要和我们指定的圆的直径一样
        int finalWidth = 0;
        finalWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
// 这里可能需要调整一下图片的大小来让你的图片能在圆里面充分显示
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, INNERRING_RADIUS * 2, INNERRING_RADIUS * 2);//注意设置的宽高不能比图片实际宽高大否则抛异常
// 构建一个位图对象，画布绘制出来的图片将会绘制到此bitmap对象上
        Bitmap bm = Bitmap.createBitmap(INNERRING_RADIUS * 2, INNERRING_RADIUS * 2, Bitmap.Config.ARGB_8888);
// 构建一个画布,
        Canvas canvas = new Canvas(bm);//画布就那么大 这里画的circle什么的都是 基于给定的位图对象的宽高的
// 获得一个画笔对象，并设置为抗锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
// 获得一种渲染方式对象
// BitmapShader的作用是使用一张位图作为纹理来对某一区域进行填充。
// 可以想象成在一块区域内铺瓷砖，只是这里的瓷砖是一张张位图而已。
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
// 设置画笔的渲染方式
        paint.setShader(shader);
// 通过画布的画圆方法将渲染后的图片绘制出来
//        canvas.drawColor(Color.RED);
        //如果绘制一个颜色 会发现 这个这个绘制区域很大

        canvas.drawCircle(INNERRING_RADIUS, INNERRING_RADIUS, INNERRING_RADIUS, paint);//直径比本来的大 画出来就是矩形了
// 返回的就是一个圆形的bitmap对象
        return bm;
    }

}
