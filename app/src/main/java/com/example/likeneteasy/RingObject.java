package com.example.likeneteasy;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Random;

//一个 圆环 需要一个画笔  一个 ValueAnimator
public class RingObject {
    private int RING_RADIUS = 0;

    private float CENTERY;
    private Paint paint = new Paint();
    private ValueAnimator valueAnimator;
    private View view;
    private Path pathParent;
    private Path pathTest;

    public Paint getPaint() {
        return paint;
    }

    public RingObject(int RING_RADIUS, float CENTERY, View view, Path pathParent, float CENTERX, int MAXRADIUSRING, int RADIUS_SMALLCIRCLE) {
        this.RING_RADIUS = RING_RADIUS;
        this.CENTERY = CENTERY;
        this.view = view;
        this.pathParent = pathParent;
        this.CENTERX = CENTERX;
        this.MAXRADIUSRING = MAXRADIUSRING;
        this.RADIUS_SMALLCIRCLE = RADIUS_SMALLCIRCLE;
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        pathTest = new Path();
        initAnimator();
    }

    private Canvas canvas;

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    private float CENTERX;

    public int getBili() {
        return bili;
    }

    private int bili = 0;
    private float alpha = 1.0f;
    private float currentAngle = 0;
    private int MAXRADIUSRING = 120;
    private int RADIUS_SMALLCIRCLE = 10;
    private int currentRingRandomAngle = 0;

    private int getCurrentRandomAngle() {

        return new Random().nextInt(359);

    }

    private void initAnimator() {
        valueAnimator = ValueAnimator.ofInt(0, 90);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        valueAnimator.setDuration(2000);
//        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                Log.e("5555555555555", "animatedValue=" + animatedValue);
                currentAngle = animatedValue;
                bili = (int) (MAXRADIUSRING * animatedValue / 90f);//
                alpha = 1 - animatedValue / 90f;//
                pathTest.reset();
                int mAlpha = (int) (255 * alpha);
                paint.setAlpha(mAlpha);
                pathTest.addCircle(CENTERX, CENTERY, RING_RADIUS + Math.min(bili, MAXRADIUSRING), Path.Direction.CCW);
                pathTest.addCircle((float) (CENTERX + (Math.min(RING_RADIUS + bili, RING_RADIUS + MAXRADIUSRING) * Math.cos((currentAngle + currentRingRandomAngle) * Math.PI / 180))), (float) (CENTERY - (Math.min(RING_RADIUS + bili, RING_RADIUS + MAXRADIUSRING) * Math.sin((currentAngle + currentRingRandomAngle) * Math.PI / 180))), RADIUS_SMALLCIRCLE, Path.Direction.CCW);//y 也要跟着变 否则不动
            }
        });
    }

    public void startAnimation() {
        if (valueAnimator != null) {
            boolean started = valueAnimator.isStarted();
            if (!started) {
                bili = 0;
                paint.setAlpha(255);
                currentAngle = 0;
                currentRingRandomAngle = getCurrentRandomAngle();
                valueAnimator.start();
            }
        }

    }

    public Path getPath() {

        return pathTest;
    }
    public void detch(){
        if (valueAnimator!=null){
            valueAnimator.cancel();
        }
    }
}
