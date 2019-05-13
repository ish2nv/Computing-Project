package com.radar.speech.speechradar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;


public class SplashScreen extends AppCompatActivity {
    CountDownTimer mCountDownTimer;
    ProgressBar mProgressBar;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mProgressBar=(ProgressBar)findViewById(R.id.loading);

        Log.d("undetected_word", "no word detected! Please try again");


        mCountDownTimer=new CountDownTimer(4000,400) {
            @Override
            public void onTick(long millisUntilFinished) {
                i = i + 10;
                mProgressBar.setProgress(i);
            }

            @Override
            public void onFinish() {
                 Intent i = new Intent(SplashScreen.this, loginscreen.class);
                    startActivity(i);


            }
        };
        mCountDownTimer.start();

    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

}
