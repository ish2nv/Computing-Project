package com.radar.speech.speechradar;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

public class BackgroundService extends loginscreen {

    private Button btStartService;

    Bundle extras;
    public  String values;
    TextView txt;
    TextView txt2;
    ImageView img;
    public static String values2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_service);
        btStartService = (Button) findViewById(R.id.backgroundService);
        txt = (TextView) findViewById(R.id.txt_content) ;
        txt2 = (TextView) findViewById(R.id.txt_content2) ;
        img = (ImageView) findViewById(R.id.screenshot1);
        extras = getIntent().getExtras();
        values = extras.getString("email_var2");
        values2 = values;

        txt.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this,android.R.anim.slide_in_left));
        txt2.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this,android.R.anim.slide_in_left));
        img.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this,android.R.anim.slide_in_left));
        btStartService.startAnimation(AnimationUtils.loadAnimation(BackgroundService.this,android.R.anim.slide_in_left));

        //Some devices will not allow background service to work, So we have to enable autoStart for the app.
        //As per now we are not having any way to check autoStart is enable or not,so better to give this in LoginArea,
        //so user will not get this popup again and again until he logout
        enableAutoStart();

        if (checkServiceRunning()) {
            btStartService.setText(getString(R.string.stop_service));
        }

        btStartService.setOnClickListener(v -> {

            if (btStartService.getText().toString().equalsIgnoreCase(getString(R.string.start_service))) {
                startService(new Intent(BackgroundService.this, MyService.class));
                btStartService.setText(getString(R.string.stop_service));
            } else {
                stopService(new Intent(BackgroundService.this, MyService.class));
                btStartService.setText(getString(R.string.start_service));
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.speechrecognition_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(BackgroundService.this, loginscreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
