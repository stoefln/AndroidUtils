package com.receiptpalapp.android.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.receiptpalapp.android.R;

/**
 * Created by stephan on 1/23/15.
 */
public class AnimationTools {

    public interface OnAnimationFinishedListener {
        void onAnimationFinished(View view);
    }

    public static void slideUp(final View view, float percentage, int duration) {
        final float newMargin = (float) view.getHeight() * percentage;
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        Animation animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                params.topMargin = -(int) (newMargin * interpolatedTime);
                params.bottomMargin = (int) (newMargin * interpolatedTime);
                view.setLayoutParams(params);
            }
        };
        animation.setDuration(duration);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    public static void resetSlide(View view) {
        slideUp(view, 0, 0);
    }

    public static void slideUpMore(final View view, float percentage) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        final int currentTopMargin = params.topMargin;
        final int currentBottomMargin = params.bottomMargin;

        final float newMargin = (float) view.getHeight() * percentage;
        Animation animation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                params.topMargin = currentTopMargin - (int) (newMargin * interpolatedTime);
                params.bottomMargin = currentBottomMargin + (int) (newMargin * interpolatedTime);
                view.setLayoutParams(params);
            }
        };
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.startAnimation(animation);

    }

    public static void animateCollapse(final View view, final OnAnimationFinishedListener listener) {

        final int originalWidth = view.getWidth();
        final int originalHeight = view.getHeight();
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();

        Animation a1 = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //params.leftMargin = (int)(newLeftMargin * interpolatedTime);

                params.height = (int) ((float) originalHeight * (1f-interpolatedTime));
                view.setLayoutParams(params);
            }
        };
        int duration = 300;
        a1.setDuration(duration); // in ms

        Animation a2 = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //params.leftMargin = (int)(newLeftMargin * interpolatedTime);
                params.width = (int) ((float) originalWidth * (1f-interpolatedTime));

                view.setLayoutParams(params);
            }
        };
        a2.setDuration(duration); // in ms
        //a2.setStartOffset(duration);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(a1);
        animationSet.addAnimation(a2);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onAnimationFinished(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //animationSet.setDuration(5000);
        view.startAnimation(animationSet);
    }

    public static void animateFullScreen(Activity activity, final RelativeLayout container, final View view, final OnAnimationFinishedListener listener) {

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int topOffset = dm.heightPixels - container.getMeasuredHeight();


        final int originalWidth = view.getWidth();
        final int originalHeight = view.getHeight();
        int finalWidth = container.getWidth();
        int finalHeight = container.getHeight();
        final int dWidth = finalWidth - originalWidth;
        final int dHeight = finalHeight - originalHeight;

        int[] pos = new int[2];
        view.getLocationOnScreen(pos);

        ViewGroup parent = (ViewGroup) view.getParent();

        if (parent != null) {
            // detach the child from parent or you get an exception if you try
            // to add it to another one
            parent.removeView(view);
        }


        final int locationX = pos[0];
        final int locationY = pos[1] - topOffset;

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(originalWidth, originalHeight);
        params.leftMargin = locationX;
        params.topMargin = locationY;


        final RelativeLayout backgroundView = new RelativeLayout(container.getContext());
        backgroundView.setBackgroundColor(view.getContext().getResources().getColor(R.color.punch_blank));
        RelativeLayout.LayoutParams innerParams = new RelativeLayout.LayoutParams(originalWidth, originalHeight);
        innerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        innerParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        backgroundView.addView(view, innerParams);
        container.addView(backgroundView, params);

        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //params.leftMargin = (int)(newLeftMargin * interpolatedTime);
                params.width = (int) (originalWidth + (float) dWidth * interpolatedTime);
                params.height = (int) (originalHeight + (float) dHeight * interpolatedTime);
                params.leftMargin = (int) ((float) locationX * (1f - interpolatedTime));
                params.topMargin = (int) ((float) locationY * (1f - interpolatedTime));
                backgroundView.setLayoutParams(params);
            }
        };
        a.setDuration(500); // in ms
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onAnimationFinished(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(a);
    }
}
