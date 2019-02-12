package com.radar.speech.speechradar;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {
    Button resetpass;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetpass = (Button)findViewById(R.id.passwordreset);
        email = (EditText) findViewById(R.id.enteremail) ;
        send_password_reset_email();

    }

    public void send_password_reset_email() {
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email .getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(forgotPassword.this, "Reset password sent to " + email.getText().toString(),
                                            Toast.LENGTH_LONG).show();}
                               else {
                                    Toast.makeText(forgotPassword.this, "Email does not exist! please try another email",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }


        }
        );
    }
}