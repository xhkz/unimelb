package com.unimelb.breakout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class LevelActivity extends Activity implements View.OnClickListener {

    private int levels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        this.levels = getIntent().getIntExtra("levels", 0);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int resID = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? R.drawable.backw : R.drawable.backh;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(getResources().getDrawable(resID));
        } else {
            layout.setBackground(getResources().getDrawable(resID));
        }

        addButtons(layout);
        setContentView(layout);
    }

    private void addButtons(LinearLayout layout) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 20, 50, 0);

        for (int i = 0; i < this.levels; i++) {
            Button button = new Button(this);
            button.setText("Level " + (i + 1));
            button.setId(i);
            button.setOnClickListener(this);
            layout.addView(button, layoutParams);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LV", view.getId());
        startActivity(intent);
    }
}
