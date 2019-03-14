package com.radar.speech.speechradar;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {
    Button resetpass;
    EditText email;
    private long mLastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetpass = (Button)findViewById(R.id.passwordreset);
        email = (EditText) findViewById(R.id.enteremail) ;
        resetpass.startAnimation(AnimationUtils.loadAnimation(forgotPassword.this,android.R.anim.slide_in_left));
        email.startAnimation(AnimationUtils.loadAnimation(forgotPassword.this,android.R.anim.slide_in_left));

        send_password_reset_email();

    }

    public void send_password_reset_email() {
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(forgotPassword.this, "Reset password sent to " + email.getText().toString(),
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(forgotPassword.this, "Email does not exist! please try another email",
                                                Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }
                catch(Exception e){
                    Toast.makeText(forgotPassword.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }

            }


        }
        );
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
                startActivity(new Intent(forgotPassword.this, loginscreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}