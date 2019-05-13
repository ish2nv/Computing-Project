package com.radar.speech.speechradar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

public class BackgroundService extends loginscreen {


    Bundle extras;
    public  String values;
    TextView txt;
    TextView txt2;
    ImageView img;
    public static String values2;
    public static int counter = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_service);
        txt = (TextView) findViewById(R.id.title2);
        txt2 = (TextView) findViewById(R.id.para);
        img = (ImageView) findViewById(R.id.myimg);
        extras = getIntent().getExtras();
        values = extras.getString("email_var2");
        values2 = values;
        startService(new Intent(BackgroundService.this, continuousService.class));
        counter++;
        txt.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this, android.R.anim.slide_in_left));
        txt2.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this, android.R.anim.slide_in_left));
        img.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this, android.R.anim.slide_in_left));

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/chewy.ttf");

        txt.setTypeface(custom_font);

        AutoStart();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void AutoStart() {
        for (Intent i : Constants.AUTO_START_INTENTS) {
            if (getPackageManager().resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                new MaterialDialog.Builder(this).title(R.string.enable_autostart)
                        .content(R.string.ask_permission)
                        .theme(Theme.LIGHT)
                        .positiveText(getString(R.string.allow))
                        .onPositive((dialog, which) -> {
                            try {
                                for (Intent i2 : Constants.AUTO_START_INTENTS)
                                    if (getPackageManager().resolveActivity(i2, PackageManager.MATCH_DEFAULT_ONLY)
                                            != null) {
                                        startActivity(i2);
                                        break;
                                    }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .show();
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {

    }

}
