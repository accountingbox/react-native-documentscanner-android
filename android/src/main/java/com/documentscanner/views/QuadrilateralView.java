package com.documentscanner.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Thymo on 27/07/16.
 */


import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;

public class QuadrilateralView extends View {

    private Paint paint;
    private AnimatablePoint[] corners;
    private boolean hidden = false;
    AnimatorSet animSet;

    private class AnimatablePoint {
        /**
         * Point holds two integer coordinates
         */
        public int x;
        public int y;

        public AnimatablePoint() {
        }

        public AnimatablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public AnimatablePoint(Point src) {
            this.x = src.x;
            this.y = src.y;
        }

        /**
         * Set the point's x and y coordinates
         */
        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setX(int x) {
            this.x = x;
         //   Log.d("TAG", "setX: "+ x);

        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public QuadrilateralView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL);
    }

    private AnimatablePoint[] convertOpenCVPoints(org.opencv.core.Point[] points) {
        AnimatablePoint[] convertedPoints = new AnimatablePoint[points.length];
        int i=0;
        for (org.opencv.core.Point point : points) {
            convertedPoints[i] = new AnimatablePoint((int)point.x, (int)point.y);
            i++;
        }
        return convertedPoints;
    }


    public boolean isHidden() {
       return this.hidden;
    }

    public void hide() {
        this.hidden = true;
    }

    public void show() {
        this.hidden = false;
    }

    public void setCorners(org.opencv.core.Point[] corners){
        setCorners(convertOpenCVPoints(corners));
    }

    public void setCorners(AnimatablePoint[] corners){
        this.corners = corners;
     //   Log.d("TAG", "setCorners");
    }

    public void setCornersWithAnimation(org.opencv.core.Point[] corners){
        setCornersWithAnimation(convertOpenCVPoints(corners));
    }

    public void setCornersWithAnimation(AnimatablePoint[] corners){

       // Log.d("TAG", "setCornersWithAnimation 111");

        if(this.corners==null || this.corners.length!=4) {
            setCorners(corners);
            return;
        }
     //   Log.d("TAG", "setCornersWithAnimation 2222");

        ArrayList<ObjectAnimator> arrayListObjectAnimators = new ArrayList<ObjectAnimator>(); //ArrayList of ObjectAnimators
        for(int i = 0; i < corners.length; i++ ){
            ObjectAnimator animY = ObjectAnimator.ofInt(this.corners[i], "y", this.corners[i].y, corners[i].y);
            arrayListObjectAnimators.add(animY);
            ObjectAnimator animX = ObjectAnimator.ofInt(this.corners[i], "x", this.corners[i].x, corners[i].x);
            arrayListObjectAnimators.add(animX);
        }

        ObjectAnimator[] objectAnimators = arrayListObjectAnimators.toArray(new ObjectAnimator[arrayListObjectAnimators.size()]);

      /*  if(animSet!=null && animSet.isRunning()) {
            animSet.cancel();
        }*/
        animSet = new AnimatorSet();
        animSet.playTogether(objectAnimators);
        animSet.setDuration(200);
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animSet.start();

      //  this.corners = corners;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!hidden) {
            drawPoly(canvas, Color.argb(255 / 2, 19, 193, 129), this.corners);
        }
        //Log.d("TAG", "onDraw canvas");

        invalidate();
    }

    /**
     * Draw polygon
     *
     * @param canvas The canvas to draw on
     * @param color  Integer representing a fill color (see http://developer.android.com/reference/android/graphics/Color.html)
     * @param points Polygon corner points
     */
    private void drawPoly(Canvas canvas, int color, AnimatablePoint[] points) {
        // line at minimum...
//        Log.d("TAG", "drawPoly points");

        if (points==null || points.length < 2) {
            return;
        }
//        Log.d("TAG", "On drawing points: ");

        // paint
        Paint polyPaint = new Paint();
        polyPaint.setColor(color);
        polyPaint.setStyle(Paint.Style.FILL);

        // path
        Path polyPath = new Path();
        polyPath.moveTo((float)points[0].x, (float)points[0].y);
        int i, len;
        len = points.length;
        for (i = 0; i < len; i++) {
            polyPath.lineTo((float)points[i].x, (float)points[i].y);
        }
        polyPath.lineTo((float)points[0].x, (float)points[0].y);

        // draw
        canvas.drawPath(polyPath, polyPaint);
    }
}