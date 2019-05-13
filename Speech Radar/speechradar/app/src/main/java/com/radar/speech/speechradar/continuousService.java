package com.radar.speech.speechradar;

import android.Manifest.permission;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.CountDownTimer;
import android.os.IBinder;
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

public class continuousService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    public static SpeechDelegate delegate;
    private Context mContext;
    CountDownTimer mCountDownTimer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            System.out.println(e);

        }
      Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            setRingtoneSoundLevel();
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
            setRingtoneSoundLevel();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
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
        mContext = getApplicationContext();
        String arr[] = result.split(" ", 2);
        String val = BackgroundService.values2;
        System.out.println("the codeWord is: " + val);
        ArrayList<String> a = new ArrayList<String>();
        Log.d("Result", arr[0] + "");
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone ringtone = RingtoneManager.getRingtone(mContext,uri);

        if (!TextUtils.isEmpty(arr[0])) {
            Toast.makeText(this, arr[0], Toast.LENGTH_SHORT).show();
        }
        if(a.size() < 2) {
            if (val.equals("Sheila")) {
                if (arr[0].equals("cheetah") || arr[0].equals("tschida")) {
                    a.add(arr[0]);
                    if (a.get(0).equals("cheetah") || arr[0].equals("tschida")) {
                        setRingtoneSoundLevel();

                        ringtone.play();
                        stopService(new Intent(getApplicationContext(), continuousService.class));

                        if (ringtone.isPlaying() == true) {
                            mCountDownTimer = new CountDownTimer(9000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {

                                    ringtone.stop();

                                }
                            };
                            mCountDownTimer.start();
                        }
                    }
                }
            }
            if (val.equals("backward")) {
                if (arr[0].equals("backwood")) {
                    a.add(arr[0]);

                    if (a.get(0).equals("backwood")) {
                        setRingtoneSoundLevel();
                        ringtone.play();
                        stopService(new Intent(getApplicationContext(),continuousService.class));
                        if (ringtone.isPlaying() == true) {
                            mCountDownTimer = new CountDownTimer(9000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {

                                    ringtone.stop();

                                }
                            };
                            mCountDownTimer.start();
                        }
                    }
                } }
            if (arr[0].equals(val)) {
                a.add(arr[0]);
            }
            if (a.get(0).equals(val)) {
                setRingtoneSoundLevel();
                ringtone.play();
                stopService(new Intent(getApplicationContext(),continuousService.class));
                if (ringtone.isPlaying() == true) {
                    mCountDownTimer = new CountDownTimer(9000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {

                            ringtone.stop();

                        }
                    };
                    mCountDownTimer.start();
                }
            } }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM,
                        true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Speech.getInstance().isListening()) {
            setRingtoneSoundLevel();
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) {
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {

                    } catch (GoogleVoiceTypingDisabledException exc) {
                    }
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                }
            });
            setRingtoneSoundLevel();
        }
    }


    private void setRingtoneSoundLevel() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            int maxvol = amanager.getStreamMaxVolume(AudioManager.STREAM_RING);
            float percentage = 0.9f;
            int setvolume = (int) (maxvol * percentage);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                try {
                    amanager.setStreamVolume(AudioManager.STREAM_RING, setvolume, 0);
                } catch (Exception e) {
                    NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        getApplicationContext().startActivity(intent);
                    }
                }
            } else {
                amanager.setStreamVolume(AudioManager.STREAM_RING, setvolume, 0);
            }
            }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), continuousService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }
}