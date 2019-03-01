package com.radar.speech.speechradar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class speechrecognition extends loginscreen {
    private static final int shape = 20;

    TextView maintitle;
    TextView ourtext;
    TextView taptospeak;
    TextView saving;
    ScrollView scrollView;

    public static String firstWord;
    EditText userEmail;
    ImageView speechrecognitionmic;
    String oneWord;
    DatabaseReference myRef2;
    DatabaseReference child2;
     Button saveCodeWordinDB;
     AudioManager audioManager;
    FirebaseDatabase  database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = database.getReference();
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speechrecognition);
        checkPermission();
        userEmail = (EditText) findViewById(R.id.emaillogin);
        maintitle = (TextView) findViewById(R.id.mainTitle);
        taptospeak = (TextView) findViewById(R.id.tapspeak);
        ourtext = (TextView) findViewById(R.id.speechtotext);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        saveCodeWordinDB = (Button) findViewById(R.id.saveCodeWord);
        scrollView = (ScrollView) findViewById(R.id.scrolling);
        speechrecognitionmic = (ImageView) findViewById(R.id.speechrecognitionmic);
        saving = (TextView) findViewById(R.id.save);
        speechrecognitionmic.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));
        ourtext.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));
        taptospeak.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));
        saveCodeWordinDB.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));

        saving.setVisibility(View.INVISIBLE);


        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        Bundle extras = getIntent().getExtras();
        String value = extras.getString("email_var");
        System.out.println("value: " + value);

        System.out.println("email from login: " + value);
        myRef2 = FirebaseDatabase.getInstance().getReference();
        child2 = myRef2.child(value);

        child2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("hello world");
                String s = dataSnapshot.getValue().toString();
                s = s.replace("{","");
                s = s.replace(",","");
                String arr[] = s.split(" ", 2);

                String firstWord = arr[0];
                firstWord = firstWord.replace("="," ");
                String arr2[] = firstWord.split(" ", 2);
                String secondWord = arr2[1];

                System.out.println(secondWord);
                maintitle.setText("Hello " + secondWord);
                maintitle.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                speechrecognitionmic.setImageResource(R.drawable.redmic);
                taptospeak.setVisibility(View.VISIBLE);
                ourtext.setHint("");

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                      oneWord = matches.get(0);
                      String arr[] = oneWord.split(" ");
                      firstWord = arr[0];   //the
                      speechrecognitionmic.setImageResource(R.drawable.redmic);
                      taptospeak.setVisibility(View.VISIBLE);
                      ourtext.setText("Code word: " + firstWord);    //extract this variable into the MyService class

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        speechrecognitionmic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        speechrecognitionmic.setImageResource(R.drawable.redmic);
                        taptospeak.setVisibility(view.VISIBLE);

                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        speechrecognitionmic.setImageResource(R.drawable.greenmic);
                        taptospeak.setVisibility(view.INVISIBLE);
                        ourtext.setText("");
                        ourtext.setHint("Listening...");
                        break;
                }
                return false;
            }
        });


            saveCodeWordinDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressBar.setProgress(i);
                    mCountDownTimer=new CountDownTimer(4000,1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            i++;
                            mProgressBar.setVisibility(View.VISIBLE);
                            saving.setVisibility(View.VISIBLE);
                            mProgressBar.setProgress((int)i*100/(5000/1000));



                            scrollView.post(new Runnable() {
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }

                        @Override
                        public void onFinish() {
                            //Do what you want
                            i++;
                            mProgressBar.setProgress(100);
                            Bundle extras = getIntent().getExtras();
                            String value = extras.getString("email_var");
                            mDatabaseRef.child(value).child("codeWord").setValue(firstWord);
                            Intent i = new Intent(speechrecognition.this, BackgroundService.class);
                            i.putExtra("email_var2",firstWord);
                            startActivity(i);
                        }
                    };
                    mCountDownTimer.start();

                }
            });


        }



    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
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
                startActivity(new Intent(speechrecognition.this, loginscreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}



