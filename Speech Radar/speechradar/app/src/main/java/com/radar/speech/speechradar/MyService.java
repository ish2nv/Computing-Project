package com.radar.speech.speechradar;

import android.Manifest.permission;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MyService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    public static SpeechDelegate delegate;
    private MediaPlayer player;
    private AudioManager mAudioManager;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            System.setProperty("rx.unsafe-disable", "True");
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d("Result", partial+"");
        }
    }

    @Override
    public void onSpeechResult(String result) {

      String val = BackgroundService.values2;
      System.out.println("the codeWord is: " + val);
        ArrayList<String> a = new ArrayList<String>();
        Log.d("Result", result+"");
        if (!TextUtils.isEmpty(result)) {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        }
        if(result.equals(val)) {
            a.add(result);
        }
        if(a.get(0).equals(val)) {
            muteBeepSoundOfRecorders();
            player = MediaPlayer.create(this,
                    Settings.System.DEFAULT_RINGTONE_URI);
            player.setLooping(true);
            player.start();

        }

    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Function to remove the beep sound of voice recognizer.
     */
    private void muteBeepSoundOfRecorders() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float percent = 0.9f;
            int seventyVolume = (int) (maxVolume*percent);
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Restarting the service if it is removed.
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), MyService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }
}