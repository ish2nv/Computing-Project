package com.radar.speech.speechradar;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.toptas.fancyshowcase.FancyShowCaseView;

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
        img = (ImageView) findViewById(R.id.screenshot1);
        extras = getIntent().getExtras();
        values = extras.getString("email_var2");
        values2 = values;
        startService(new Intent(BackgroundService.this, MyService.class));
        counter++;
        txt.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this, android.R.anim.slide_in_left));
        txt2.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this, android.R.anim.slide_in_left));
        img.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this, android.R.anim.slide_in_left));

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/chewy.ttf");

        txt.setTypeface(custom_font);
        //Some devices will not allow background service to work, So we have to enable autoStart for the app.
        //As per now we are not having any way to check autoStart is enable or not,so better to give this in LoginArea,
        //so user will not get this popup again and again until he logout
        enableAutoStart();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void enableAutoStart() {
        for (Intent intent : Constants.AUTO_START_INTENTS) {
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                new MaterialDialog.Builder(this).title(R.string.enable_autostart)
                        .content(R.string.ask_permission)
                        .theme(Theme.LIGHT)
                        .positiveText(getString(R.string.allow))
                        .onPositive((dialog, which) -> {
                            try {
                                for (Intent intent1 : Constants.AUTO_START_INTENTS)
                                    if (getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY)
                                            != null) {
                                        startActivity(intent1);
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

    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (getString(R.string.my_service_name).equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {

    }

}
