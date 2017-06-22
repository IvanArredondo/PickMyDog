package com.walnutapps.pickmydog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class SignUpActivity extends AppCompatActivity {

    ToggleButton phoneToggleButton;
    ToggleButton emailToggleButton;
    Boolean phoneChecked = true;

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final View phoneOptionView = (View)findViewById(R.id.phoneOptionView);
        final View emailOptionView = (View)findViewById(R.id.emailOptionView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        float sideMarginPixels = width * (float)0.05;
        float buttonWidthPixels = width * (float)0.45;

        Log.i("sidePixels: ", Float.toString(sideMarginPixels));

        float sideMarginDp = convertPixelsToDp(sideMarginPixels, getApplicationContext());
        float butonWidthDp = convertPixelsToDp(buttonWidthPixels, getApplicationContext());

        Log.i("sideDp: ", Float.toString(sideMarginDp));


        Log.i("width: ", Integer.toString(width));
        Log.i("height: ", Integer.toString(height));

        phoneToggleButton = (ToggleButton) findViewById(R.id.phoneToggleButton);
        emailToggleButton = (ToggleButton)findViewById(R.id.emailToggleButton);


//        phoneToggleButton.setLayoutParams(new ConstraintLayout.LayoutParams((int)butonWidthDp, 50));
//        emailToggleButton.setLayoutParams(new ConstraintLayout.LayoutParams((int)butonWidthDp, 50));
        phoneToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneChecked = true;

                    phoneOptionView.setVisibility(View.VISIBLE);

                   emailOptionView.setVisibility(View.INVISIBLE);

            }
        });

        emailToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneChecked = false;

                emailOptionView.setVisibility(View.VISIBLE);

                phoneOptionView.setVisibility(View.INVISIBLE);
            }
        });


    }
}
