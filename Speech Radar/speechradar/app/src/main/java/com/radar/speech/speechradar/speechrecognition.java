package com.radar.speech.speechradar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radar.speech.speechradar.models.Classification;
import com.radar.speech.speechradar.models.Classifier;
import com.radar.speech.speechradar.models.MFCC;
import com.radar.speech.speechradar.models.TensorflowClassifier;
import com.radar.speech.speechradar.models.WavRecorder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class speechrecognition extends loginscreen {
    private static final int shape = 20;

    TextView maintitle;
    TextView ourtext;
    EditText userEmail;
    ImageView speechrecognitionmic;
    String oneWord;
    DatabaseReference myRef2;
    DatabaseReference child2;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speechrecognition);
        checkPermission();
        userEmail = (EditText) findViewById(R.id.emaillogin);
        maintitle = (TextView) findViewById(R.id.mainTitle);
        ourtext = (TextView) findViewById(R.id.speechtotext);
        speechrecognitionmic = (ImageView) findViewById(R.id.speechrec);
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
                String firstWord = arr[0];   //the
                speechrecognitionmic.setImageResource(R.drawable.redmic);
                ourtext.setText(firstWord);
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

                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        speechrecognitionmic.setImageResource(R.drawable.greenmic);
                        ourtext.setText("");
                        ourtext.setHint("Listening...");
                        break;
                }
                return false;
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





}



