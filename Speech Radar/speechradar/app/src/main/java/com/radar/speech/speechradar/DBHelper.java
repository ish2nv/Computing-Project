package com.radar.speech.speechradar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "Accounts.db";
    public static final String TABLE = "recovery_account_table";
    public static final String _1 = "ID";
    public static final String _2 = "FIRSTNAME";
    public static final String _3 = "LASTNAME";
    public static final String _4 = "PASSWORD";
    public static final String _5 = "CONFIRM_PASSWORD";
    public static final String _6 = "EMAIL_ADDRESS";
    public static final String _7 = "CODEWORD";


    public DBHelper(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,FIRSTNAME TEXT,LASTNAME TEXT,PASSWORD TEXT,CONFIRM_PASSWORD TEXT, EMAIL_ADDRESS TEXT, CODEWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }

    public boolean insert_to_DB(String firstname,String lastname,String password,String confirm_password,String email, String codeword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(_2,firstname);
        contentValues.put(_3,lastname);
        contentValues.put(_4,password);
        contentValues.put(_5,confirm_password);
        contentValues.put(_6,email);
        contentValues.put(_7,codeword);


        long result = db.insert(TABLE,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor AllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE,null);
        return res;
    }

    public boolean updateDB(String id,String firstname,String lastname,String password,String confirm_password,String email,String codeword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(_1,id);
        contentValues.put(_2,firstname);
        contentValues.put(_3,lastname);
        contentValues.put(_4,password);
        contentValues.put(_5,confirm_password);
        contentValues.put(_6,email);
        contentValues.put(_7,codeword);

        db.update(TABLE, contentValues, "ID = ?",new String[] { id });
        return true;
    }

}