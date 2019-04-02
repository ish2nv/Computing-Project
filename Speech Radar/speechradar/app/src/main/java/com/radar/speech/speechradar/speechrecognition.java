package com.radar.speech.speechradar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class speechrecognition extends loginscreen {
    private static final int shape = 20;

    TextView maintitle;
    TextView ourtext;
    TextView taptospeak;
    TextView saving;
    ScrollView scrollView;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private long mLastClickTime;

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
    CountDownTimer mCountDownTimer2;
    CountDownTimer mCountDownTimer3;

    TextView subtitle;

    int i=0;
    int i2=0;


    private static final int SAMPLE_RATE = 16000;
    private static final int SAMPLE_DURATION_MS = 1000;
    private static final int RECORDING_LENGTH = (int) (SAMPLE_RATE * SAMPLE_DURATION_MS / 1000);
    private static final long AVERAGE_WINDOW_DURATION_MS = 500;
    private static final float DETECTION_THRESHOLD = 0.70f;
    private static final int SUPPRESSION_MS = 1500;
    private static final int MINIMUM_COUNT = 3;
    private static final long MINIMUM_TIME_BETWEEN_SAMPLES_MS = 30;
    private static final String LABEL_FILENAME = "file:///android_asset/conv_labels.txt";
    private static final String MODEL_FILENAME = "file:///android_asset/my_frozen_graph.pb";
    private static final String INPUT_DATA_NAME = "decoded_sample_data:0";
    private static final String SAMPLE_RATE_NAME = "decoded_sample_data:1";
    private static final String OUTPUT_SCORES_NAME = "labels_softmax";

    // UI elements.
    private static final int REQUEST_RECORD_AUDIO = 13;
    private final ReentrantLock recordingBufferLock = new ReentrantLock();
    short[] recordingBuffer = new short[RECORDING_LENGTH];
    int recordingOffset = 0;
    boolean shouldContinue = true;
    boolean shouldContinueRecognition = true;
    private Button quitButton;
    private ListView labelsListView;
    private Thread recordingThread;
    private Thread recognitionThread;
    private TensorFlowInferenceInterface inferenceInterface;
    private List<String> labels = new ArrayList<String>();
    private List<String> displayedLabels = new ArrayList<>();
    private RecognizeCommands recognizeCommands = null;





    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speechrecognition);
        userEmail = (EditText) findViewById(R.id.emaillogin);
        maintitle = (TextView) findViewById(R.id.paragraph);
        taptospeak = (TextView) findViewById(R.id.tapspeak);
        list = (ListView) findViewById(R.id.codewordlist);
        subtitle = (TextView) findViewById(R.id.codewordtxt);
        arrayList = new ArrayList<String>();
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
        list.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));
        subtitle.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));
        saveCodeWordinDB.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));
        saving.setVisibility(View.INVISIBLE);

        try {
            Bundle extras = getIntent().getExtras();
            String value = extras.getString("email_var");
            if(value.equals(null)) {
                throw new Exception();
            }
        }
        catch (Exception e) {
            Toast.makeText(speechrecognition.this, e.toString()
                    , Toast.LENGTH_LONG).show();
            Intent i = new Intent(speechrecognition.this, loginscreen.class);
            startActivity(i);
        }



        mCountDownTimer3 = new CountDownTimer(500, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                scrollView.fullScroll(View.FOCUS_UP);
                new FancyShowCaseView.Builder(speechrecognition.this).title("Scroll Down").focusOn(maintitle).build().show();
            }
        };
        mCountDownTimer3.start();


        list.setDivider(new ColorDrawable(0xFFFFFFFF));   //0xAARRGGBB
        list.setDividerHeight(1);
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, arrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                tv.setHeight(85);
                tv.setMinimumHeight(85);
                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);

                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                return view;
            }
        };

        list.setAdapter(adapter);
        speechrecognition.setListViewHeightBasedOnItems(list);

        arrayList.add("yes");
        arrayList.add("zero");
        arrayList.add("stop");
        arrayList.add("learn");
        arrayList.add("happy");
        arrayList.add("house");
        arrayList.add("sheila");
        arrayList.add("six");
        arrayList.add("three");
        arrayList.add("tree");
        arrayList.add("visual");
        arrayList.add("marvin");
        arrayList.add("up");
        arrayList.add("down");
        arrayList.add("left");
        arrayList.add("right");
        arrayList.add("backward");
        arrayList.add("forward");

        adapter.notifyDataSetChanged();
        speechrecognition.setListViewHeightBasedOnItems(list);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/chewy.ttf");

        maintitle.setTypeface(custom_font);
        subtitle.setTypeface(custom_font);
        taptospeak.setTypeface(custom_font);
        ourtext.setTypeface(custom_font);

        if(BackgroundService.counter > 0) {
            speechrecognitionmic.setEnabled(false);
            saveCodeWordinDB.setEnabled(false);
            Toast.makeText(speechrecognition.this, "You already have a code word! If you want to create another code word then terminate the app and try again"
                    , Toast.LENGTH_LONG).show();
        }
        Bundle extras = getIntent().getExtras();
        String value = extras.getString("email_var");
        System.out.println("value: " + value);

        System.out.println("email from login: " + value);
        myRef2 = FirebaseDatabase.getInstance().getReference();
        child2 = myRef2.child(value);

        child2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue().toString();
                s = s.replace("{","");
                s = s.replace("}","");

                s = s.replace(",","");
                System.out.println(s);
                int count = 1;
                for (char c : s.toCharArray()) {
                    if (c == ' ') {
                        count++;
                    }
                }
                System.out.println(count);
                String arr[] = s.split(" ", count);
                System.out.println("arr: " + Arrays.toString(arr));

                String firstWord = "";

                for(int i = 0; i < arr.length;i++) {
                    if(arr[i].contains("d_firstname")) {
                        firstWord = arr[i];
                    }
                }
                 System.out.println("firstWord: " + firstWord);
                firstWord = firstWord.replace("="," ");

                String arr2[] = firstWord.split(" ", 2);
                String secondWord = arr2[1];
                System.out.println("secondWord: " + secondWord);
                System.out.println("arr2: " + Arrays.toString(arr2));


                maintitle.setText("Hello " + secondWord);
                maintitle.startAnimation(AnimationUtils.loadAnimation(speechrecognition.this,android.R.anim.slide_in_left));





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        speechrecognitionmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taptospeak.getText().toString().equals("Tap to speak")) {
                    ourtext.setText("Listening....");
                    String actualFilename = LABEL_FILENAME.split("file:///android_asset/")[1];
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(getAssets().open(actualFilename)));
                        String line;
                        while ((line = br.readLine()) != null) {
                            labels.add(line);
                            if (line.charAt(0) != '_') {
                                displayedLabels.add(line.substring(0, 1).toUpperCase() + line.substring(1));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Problem reading label file!", e);
                    }


                    // Set up an object to smooth recognition results to increase accuracy.
                    recognizeCommands =
                            new RecognizeCommands(
                                    labels,
                                    AVERAGE_WINDOW_DURATION_MS,
                                    DETECTION_THRESHOLD,
                                    SUPPRESSION_MS,
                                    MINIMUM_COUNT,
                                    MINIMUM_TIME_BETWEEN_SAMPLES_MS);

                    // Load the TensorFlow model.
                    inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILENAME);
                    speechrecognitionmic.setImageResource(R.drawable.greenmic);
                    // Start the recording and recognition threads.
                    startRecording();
                    requestMicrophonePermission();
                    startRecognition();

                    mCountDownTimer2 = new CountDownTimer(3600, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            i2++;
                            System.out.println(i2);
                            System.out.println("millisUntilFinished: " + millisUntilFinished);
                            int a = (int) (millisUntilFinished/1000);
                            String g = String.valueOf(a) + " sec";
                            taptospeak.setText(g);
                            speechrecognitionmic.setEnabled(false);

                        }

                        @Override
                        public void onFinish() {
                            //Do what you want
                            i2++;
                            speechrecognitionmic.setEnabled(true);
                            taptospeak.setText("Tap to speak");
                            stopRecording();
                            stopRecognition();

                            speechrecognitionmic.setImageResource(R.drawable.redmic);
                            if(ourtext.getText().toString().equals("Listening....")) {
                                ourtext.setText("....");
                            }
                        }
                    };
                    mCountDownTimer2.start();
                }

            }

        });




        saveCodeWordinDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ourtext.getText().toString().contains("Code Word")) {

                    Toast.makeText(speechrecognition.this, "You have not registered a code word!"
                            , Toast.LENGTH_LONG).show();

                } else {
                    mProgressBar.setProgress(i);
                    mCountDownTimer = new CountDownTimer(4000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            i++;
                            mProgressBar.setVisibility(View.VISIBLE);
                            saving.setVisibility(View.VISIBLE);
                            mProgressBar.setProgress((int) i * 100 / (5000 / 1000));


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
                            i.putExtra("email_var2", firstWord);
                            i.putExtra("email_var4", value);
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2800) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            startActivity(i);
                        }
                    };
                    mCountDownTimer.start();

                }
            }
        });


    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(speechrecognition.this, loginscreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void requestMicrophonePermission() {
        ActivityCompat.requestPermissions(speechrecognition.this,
                new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
    }

    public synchronized void startRecording() {
        if (recordingThread != null) {
            return;
        }
        shouldContinue = true;
        recordingThread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                record();
                            }
                        });
        recordingThread.start();
    }

    public synchronized void stopRecording() {
        if (recordingThread == null) {
            return;
        }
        shouldContinue = false;
        recordingThread = null;
    }

    private void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // Estimate the buffer size we'll need for this device.
        int bufferSize =
                AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }
        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record =
                new AudioRecord(
                        MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            return;
        }

        record.startRecording();


        // Loop, gathering audio data and copying it to a round-robin buffer.
        while (shouldContinue) {
            int numberRead = record.read(audioBuffer, 0, audioBuffer.length);
            int maxLength = recordingBuffer.length;
            int newRecordingOffset = recordingOffset + numberRead;
            int secondCopyLength = Math.max(0, newRecordingOffset - maxLength);
            int firstCopyLength = numberRead - secondCopyLength;
            // We store off all the data for the recognition thread to access. The ML
            // thread will copy out of this buffer into its own, while holding the
            // lock, so this should be thread safe.
            recordingBufferLock.lock();
            try {
                System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, firstCopyLength);
                System.arraycopy(audioBuffer, firstCopyLength, recordingBuffer, 0, secondCopyLength);
                recordingOffset = newRecordingOffset % maxLength;
            } finally {
                recordingBufferLock.unlock();
            }
        }

        record.stop();
        record.release();
    }

    public synchronized void startRecognition() {
        if (recognitionThread != null) {
            return;
        }
        shouldContinueRecognition = true;
        recognitionThread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                recognize();
                            }
                        });
        recognitionThread.start();
    }

    public synchronized void stopRecognition() {
        if (recognitionThread == null) {
            return;
        }
        shouldContinueRecognition = false;
        recognitionThread = null;
    }

    private void recognize() {

        short[] inputBuffer = new short[RECORDING_LENGTH];
        float[] floatInputBuffer = new float[RECORDING_LENGTH];
        float[] outputScores = new float[labels.size()];
        String[] outputScoresNames = new String[]{OUTPUT_SCORES_NAME};
        int[] sampleRateList = new int[]{SAMPLE_RATE};

        // Loop, grabbing recorded data and running the recognition model on it.
        while (shouldContinueRecognition) {
            // The recording thread places data in this round-robin buffer, so lock to
            // make sure there's no writing happening and then copy it to our own
            // local version.
            recordingBufferLock.lock();
            try {
                int maxLength = recordingBuffer.length;
                int firstCopyLength = maxLength - recordingOffset;
                int secondCopyLength = recordingOffset;
                System.arraycopy(recordingBuffer, recordingOffset, inputBuffer, 0, firstCopyLength);
                System.arraycopy(recordingBuffer, 0, inputBuffer, firstCopyLength, secondCopyLength);
            } finally {
                recordingBufferLock.unlock();
            }

            // We need to feed in float values between -1.0f and 1.0f, so divide the
            // signed 16-bit inputs.
            for (int i = 0; i < RECORDING_LENGTH; ++i) {
                floatInputBuffer[i] = inputBuffer[i] / 32767.0f;
            }

            // Run the model.
            inferenceInterface.feed(SAMPLE_RATE_NAME, sampleRateList);
            inferenceInterface.feed(INPUT_DATA_NAME, floatInputBuffer, RECORDING_LENGTH, 1);
            inferenceInterface.run(outputScoresNames);
            inferenceInterface.fetch(OUTPUT_SCORES_NAME, outputScores);

            // Use the smoother to figure out if we've had a real recognition event.
            long currentTime = System.currentTimeMillis();
            final RecognizeCommands.RecognitionResult result = recognizeCommands.processLatestResults(outputScores, currentTime);

            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // If we do have a new command, highlight the right list entry.
                            if (!result.foundCommand.startsWith("_") && result.isNewCommand) {
                                int labelIndex = -1;
                                for (int i = 0; i < labels.size(); ++i) {
                                    if (labels.get(i).equals(result.foundCommand)) {
                                        labelIndex = i;
                                    }
                                }
                                ourtext.setText("Code Word: " + result.foundCommand);
                                firstWord = result.foundCommand;
                            }
                        }
                    });
            try {
                // We don't need to run too frequently, so snooze for a bit.
                Thread.sleep(MINIMUM_TIME_BETWEEN_SAMPLES_MS);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

    }

}