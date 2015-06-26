package com.unimelb.breakout;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class HelpActivity extends Activity {

    private Animation leftIn;
    private Animation leftOut;
    private Animation rightIn;
    private Animation rightOut;

    private ViewFlipper viewFlipper;
    float x1, x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        leftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        leftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        rightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        rightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        viewFlipper = (ViewFlipper) findViewById(R.id.helpFlipper);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x1 = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = event.getX();
                if (x2 - x1 > 100) {
                    viewFlipper.setInAnimation(rightIn);
                    viewFlipper.setOutAnimation(rightOut);
                    viewFlipper.showPrevious();
                }
                if (x1 - x2 > 100) {
                    viewFlipper.setInAnimation(leftIn);
                    viewFlipper.setOutAnimation(leftOut);
                    viewFlipper.showNext();
                }
                break;
            }
        }
        return false;
    }
}
