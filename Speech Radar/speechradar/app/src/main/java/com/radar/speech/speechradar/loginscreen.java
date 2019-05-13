package com.radar.speech.speechradar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class loginscreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button loginbutton;
    EditText userEmail;
    EditText userPass;
    TextView createAccountLink;
    TextView forgotpassword;
    TextView maintitle2;
    private long mLastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);

        loginbutton = (Button)findViewById(R.id.loginbtn);
        userEmail = (EditText) findViewById(R.id.emaillogin) ;
        userPass = (EditText) findViewById(R.id.passwordlogin);
        createAccountLink = (TextView) findViewById(R.id.createaccountlink);
        forgotpassword = (TextView) findViewById(R.id.forgotPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        maintitle2 = (TextView) findViewById(R.id.title) ;

        loginbutton.startAnimation(AnimationUtils.loadAnimation(loginscreen.this,android.R.anim.slide_in_left));
        forgotpassword.startAnimation(AnimationUtils.loadAnimation(loginscreen.this,android.R.anim.slide_in_left));
        createAccountLink.startAnimation(AnimationUtils.loadAnimation(loginscreen.this,android.R.anim.slide_in_left));
        userEmail.startAnimation(AnimationUtils.loadAnimation(loginscreen.this,android.R.anim.slide_in_left));
        userPass.startAnimation(AnimationUtils.loadAnimation(loginscreen.this,android.R.anim.slide_in_left));
        maintitle2.startAnimation(AnimationUtils.loadAnimation(loginscreen.this,android.R.anim.slide_in_left));


        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/chewy.ttf");

        maintitle2.setTypeface(custom_font);

        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(loginscreen.this, createaccount.class));
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(loginscreen.this, forgotPassword.class));
            }
        });

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("emailaddress","");
        String password = prefs.getString("pword","");

        userEmail.setText(username);
        userPass.setText(password);

        check();

    }

    public void check() {
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("emailaddress", userEmail.getText().toString());
                    editor.putString("pword", userPass.getText().toString());

                    editor.apply();
                    firebaseAuth.signInWithEmailAndPassword(userEmail.getText().toString().trim(),
                            userPass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                            if (SystemClock.elapsedRealtime() - mLastClickTime < 7000) {
                                                return;
                                            }
                                            mLastClickTime = SystemClock.elapsedRealtime();
                                            String email = userEmail.getText().toString();
                                            String email2 = userEmail.getText().toString();
                                            email = email.replace(".", "");
                                            email = email.replace(" ", "");

                                            Intent i = new Intent(loginscreen.this, speechrecognition.class);
                                            i.putExtra("email_var", email);
                                            i.putExtra("emailwithstop", email2);

                                            startActivity(i);

                                        } else {
                                            Toast.makeText(loginscreen.this, "Please verify your email address"
                                                    , Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(loginscreen.this, task.getException().getMessage()
                                                , Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                catch (Exception e ) {
                    Toast.makeText(loginscreen.this, e.toString()
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit the app?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}


