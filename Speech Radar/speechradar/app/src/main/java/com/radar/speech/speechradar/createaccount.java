package com.radar.speech.speechradar;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class createaccount extends AppCompatActivity {
    Button createAccountbtn;
    EditText fname,lname,password,conf_password,email_address;
    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;

    private final static String salt="DGE$5SGr@3VsHYUMas2323E4d57vfBfFSTRU@!DSH(*%FDSdfg13sgfsg";

    private static final int PER_LOGIN = 1000;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);
        myRef = FirebaseDatabase.getInstance().getReference();

        createAccountbtn = (Button)findViewById(R.id.create_account_btn);
        fname = (EditText)findViewById(R.id.fname);
        lname = (EditText)findViewById(R.id.lname);
        password = (EditText)findViewById(R.id.password);
        conf_password = (EditText)findViewById(R.id.conf_password);
        email_address = (EditText)findViewById(R.id.email_address);
        firebaseAuth = FirebaseAuth.getInstance();

        fname.startAnimation(AnimationUtils.loadAnimation(createaccount.this,android.R.anim.slide_in_left));
        lname.startAnimation(AnimationUtils.loadAnimation(createaccount.this,android.R.anim.slide_in_left));
        password.startAnimation(AnimationUtils.loadAnimation(createaccount.this,android.R.anim.slide_in_left));
        conf_password.startAnimation(AnimationUtils.loadAnimation(createaccount.this,android.R.anim.slide_in_left));
        email_address.startAnimation(AnimationUtils.loadAnimation(createaccount.this,android.R.anim.slide_in_left));
        createAccountbtn.startAnimation(AnimationUtils.loadAnimation(createaccount.this,android.R.anim.slide_in_left));


        AddData();

        }
    public  void AddData() {
        createAccountbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String passwordstring = password.getText().toString();
                        final String confirmpasswordstring = conf_password.getText().toString();
                        final String firstname = fname.getText().toString();
                        final String lastname = lname.getText().toString();
                        final String emailaddress = email_address.getText().toString();
                        int size1 = firstname.length();
                        int size2 = lastname.length();

                        if(size1 > 1 && size2 > 1 && passwordLength(passwordstring) == true && confirmpasswordstring.equals(passwordstring) && !email_address.getText().toString().equals("")) {

                            firebaseAuth.createUserWithEmailAndPassword(email_address .getText().toString().trim(),
                                    password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {

                                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                boolean isInserted = false;

                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(createaccount.this, "Registered successfully. Please check your email for verification",
                                                                            Toast.LENGTH_LONG).show();
                                                                    String id = myRef.push().getKey();
                                                                    String hashpassword = md5Hash(passwordstring);
                                                                    String hashconfpassword = md5Hash(confirmpasswordstring);
                                                                    String emailaddress2 = emailaddress.replace(".", "");
                                                                    emailaddress2 = emailaddress2.replace(" ","");
                                                                    Account account = new Account(id,firstname,lastname,hashpassword,hashconfpassword,emailaddress);
                                                                    myRef.child(emailaddress2).setValue(account);



                                                                }else{
                                                                    Toast.makeText(createaccount.this,  task.getException().getMessage(),
                                                                            Toast.LENGTH_LONG).show();
                                                                }

                                                            }
                                                        });
                                            }

                                            else {
                                                Toast.makeText(createaccount.this, task.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(createaccount.this, "Error in above input fields. Please correct them",
                                    Toast.LENGTH_LONG).show();
                        }
                }}
        );
    }
    public static String md5Hash(String message) {
        String md5 = "";
        if(null == message)
            return null;

        message = message+salt;//adding a salt to the string before it gets hashed.
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");//Create MessageDigest object for MD5
            digest.update(message.getBytes(), 0, message.length());//Update input string in message digest
            md5 = new BigInteger(1, digest.digest()).toString(16);//Converts message digest value in base 16 (hex)

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static boolean passwordLength(String password) {
        /* Declare a boolean variable to hold the result of the method */
        boolean correct = true;

        /* Declare an int variable to hold the count of each digit */
        int digit = 0;

        if (password.length() < 8) {
            /* The password is less than 8 characters, return false */
            return false;
        }

        /* Declare a char variable to hold each element of the String */
        char element;

        /* Check if the password has 2 or more digits */
        for(int index = 0; index < password.length(); index++ ){

            /* Check each char in the String */
            element = password.charAt( index );

            /* Check if it is a digit or not */
            if( Character.isDigit(element) ){
                /* It is a digit, so increment digit */
                digit++;
            } // End if block

        } // End for loop

        /* Now check for the count of digits in the password */
        if( digit < 6 ){
            /* There are fewer than 2 digits in the password, return false */
            return false;
        }

        /* Use a regular expression (regex) to check for only letters and numbers */
        /* The regex will check for upper and lower case letters and digits */
        if( !password.matches("[a-zA-Z0-9]+") ){
            /* A non-alphanumeric character was found, return false */
            return false;
        }

        /* All checks at this point have passed, the password is valid */
        return correct;

    }

}
