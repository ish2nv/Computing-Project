package com.radar.speech.speechradar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class createaccount extends AppCompatActivity {
    Button createAccountbtn;
    EditText fname,lname,password,conf_password,email_address;
    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;
    private long mLastClickTime;
    DBHelper myDb;
    public static final String TABLE_NAME = "recovery_account_table";




    private final static String salt="DGE$5SGr@3VsHYUMas2323E4d57vfBfFSTRU@!DSH(*%FDSdfg13sgfsg";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);
        myRef = FirebaseDatabase.getInstance().getReference();

        createAccountbtn = (Button) findViewById(R.id.create_account_btn);
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        password = (EditText) findViewById(R.id.password);
        conf_password = (EditText) findViewById(R.id.conf_password);
        email_address = (EditText) findViewById(R.id.email_address);

        myDb = new DBHelper(this);

        firebaseAuth = FirebaseAuth.getInstance();

        fname.startAnimation(AnimationUtils.loadAnimation(createaccount.this, android.R.anim.slide_in_left));
        lname.startAnimation(AnimationUtils.loadAnimation(createaccount.this, android.R.anim.slide_in_left));
        password.startAnimation(AnimationUtils.loadAnimation(createaccount.this, android.R.anim.slide_in_left));
        conf_password.startAnimation(AnimationUtils.loadAnimation(createaccount.this, android.R.anim.slide_in_left));
        email_address.startAnimation(AnimationUtils.loadAnimation(createaccount.this, android.R.anim.slide_in_left));
        createAccountbtn.startAnimation(AnimationUtils.loadAnimation(createaccount.this, android.R.anim.slide_in_left));

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
                        if(size1 >1) {
                            fname.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }
                        if(size2>1) {
                            lname.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }
                        if(passwordRules(passwordstring) == true) {
                            password.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }
                        if(confirmpasswordstring.equals(passwordstring)) {
                            conf_password.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }
                        if(isValidEmailAddress(emailaddress) == true) {
                            email_address.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }

                        if(size1 > 1 && size2 > 1 && passwordRules(passwordstring) == true && confirmpasswordstring.equals(passwordstring) && !email_address.getText().toString().equals("") && isValidEmailAddress(emailaddress) == true) {


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
                                                                    String hashpassword = Hash(passwordstring);
                                                                    String hashconfpassword = Hash(confirmpasswordstring);
                                                                    String emailaddress2 = emailaddress.replace(".", "");
                                                                    emailaddress2 = emailaddress2.replace(" ","");
                                                                    Account account = new Account(id,firstname,lastname,hashpassword,hashconfpassword,emailaddress);
                                                                    myRef.child(emailaddress2).setValue(account);
                                                                    myDb.insert_to_DB(fname.getText().toString(), lname.getText().toString(), hashpassword,hashconfpassword,email_address.getText().toString(),"yes" );



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
                            if(email_address.getText().toString().equals("")) {
                                email_address.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                            }
                            if(size1 <=1) {
                                fname.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                            }
                            if(size2<=1) {
                                lname.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                            }
                            if(passwordRules(passwordstring) == false) {
                                password.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

                            }
                            if(!confirmpasswordstring.equals(passwordstring)) {
                                conf_password.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

                            }
                            if(isValidEmailAddress(emailaddress) == false) {
                                email_address.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

                            }
                             Toast.makeText(createaccount.this, "Error in above input fields. Please correct them. Make sure password is min 8 characters long containing 5 digits",
                                    Toast.LENGTH_LONG).show();
                        }
                }}
        );
    }

    public boolean isValidEmailAddress(String emailaddress) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(emailaddress);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        catch (Exception e) {
            Toast.makeText(createaccount.this, e.toString(),
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static String Hash(String message) {
        String md5 = "";
        if(null == message)
            return null;

        message = message+salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(message.getBytes(), 0, message.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static boolean passwordRules(String password) {
        boolean c = true;

        int number = 0;

        if (password.length() < 8) {
            return false;
        }

        char elem;

        for(int i = 0; i < password.length(); i++ ){

            elem = password.charAt( i );

            if( Character.isDigit(elem) ){
                number++;
            }

        }

        if( number < 5 ){
            return false;
        }

        if( !password.matches("[a-zA-Z0-9]+") ){
            return false;
        }

        return c;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.speechrecognition_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(createaccount.this, loginscreen.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
