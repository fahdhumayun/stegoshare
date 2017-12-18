// By Fahd Humayun and Nathan Morgenstern

package com.example.fahd.stegoshare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Fahd on 12/15/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "stegoshareDB_test_v2";
    private static final int DB_VER = 1;


    // First Table - Words Table
    public static final String DB_WORDS_TABLE           = "wordsTable";
    public static final String COLUMN_WORDS_PRIMARY_KEY = "id";
    public static final String COLUMN_WORDS             = "WORDS";

    // Second Table - Shares Table
    public static final String DB_SHARES_TABLE           = "sharesTable";
    public static final String COLUMN_SHARES_PRIMARY_KEY = "id";
    public static final String COLUMN_DATE_ID            = "date_id";
    public static final String COLUMN_SHARES             = "SHARES";


    // Third Table - Date table
    public static final String DB_LAST_USED_TABLE      = "dateTable";
    public static final String COLUMN_DATE_CREATED     = "date";
    public static final String COLUMN_HASH_OF_LIST     = "list_hash";
    public static final String COLUMN_PASS_HASH        = "pass_hash";
    public static final String COLUMN_DATE_PRIMARY_KEY = "id";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query_1 = String.format(
                "create table " + DB_WORDS_TABLE + " (" +
                        COLUMN_WORDS_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_WORDS + " TEXT)"
        );

        sqLiteDatabase.execSQL(query_1);

        String query_2 = String.format(
                "create table " + DB_SHARES_TABLE + " (" +
                        COLUMN_SHARES_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_DATE_ID   + " INTEGER," +
                        COLUMN_SHARES + " TEXT)"
        );

        sqLiteDatabase.execSQL(query_2);

        String query_3 = String.format(
                "create table " + DB_LAST_USED_TABLE + " (" +
                        COLUMN_DATE_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_HASH_OF_LIST + " TEXT," +
                        COLUMN_DATE_CREATED + " TEXT," +
                        COLUMN_PASS_HASH    + " TEXT)"
        );

        sqLiteDatabase.execSQL(query_3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query_1 = String.format("DROP TABLE IF EXISTS " + DB_WORDS_TABLE);
        sqLiteDatabase.execSQL(query_1);

        String query_2 = String.format("DROP TABLE IF EXISTS " + DB_SHARES_TABLE);
        sqLiteDatabase.execSQL(query_2);

        onCreate(sqLiteDatabase);
    }

    /* BEGIN DATABASE METHODS FOR TABLE DB_SHARE_TABLE */

    //Stores shamirs secret share in the format: share + share number
    public void addShare(String share, int dateKey){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(COLUMN_SHARES, share);
        values.put(COLUMN_DATE_ID, dateKey);

        db.insert(DB_SHARES_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        // 4. close
        db.close();
    }

    public ArrayList<SecretShare> getSecretShares(){

        ArrayList<SecretShare> shareList = new ArrayList<SecretShare>();
        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DB_SHARES_TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                shareList.add(new SecretShare(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return shareList;

    }

    //FORMAT: seed list hash + prime number + secret share + share number + n + m
    public ArrayList<String> getSecretSharesStringList(){
        ArrayList<String> shareList = new ArrayList<String>();
        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //select * from dateTable where id = (select max(id) from dateTable);
        String selectQuery = "SELECT  * FROM " + DB_SHARES_TABLE + " WHERE date_id = (SELECT MAX(date_id) FROM sharesTable)";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            int key = cursor.getInt(1);
            System.out.println("key: " + key);
            String hash = getListHash(key);
            Log.v("SecretShareList", "hash: " + hash);
            do {
                Log.v("SecretShareList",hash + ","  + cursor.getString(2));
                shareList.add(hash + ","  +  cursor.getString(2));
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return shareList;
    }

    /*END DATABASE METHODS FOR TABLE DB_SHARE_TABLE */


    /* BEGIN DATABASE METHODS FOR DB_DATE_TABLE */

    public void addShareInfo(String share, String hash, String pass){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE_CREATED, share);
        values.put(COLUMN_HASH_OF_LIST, hash);
        values.put(COLUMN_PASS_HASH, pass);

        db.insert(DB_LAST_USED_TABLE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        // 4. close
        db.close();
    }

    public String getDate(){

        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_DATE_CREATED + " FROM " + DB_LAST_USED_TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        String date = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                date = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return date;

    }

    public int getDatePrimaryKey(String d){

        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        String strQuery = "SELECT " + COLUMN_DATE_PRIMARY_KEY + " FROM " + DB_LAST_USED_TABLE + " WHERE date=?";
        Cursor cursor = db.rawQuery(strQuery, new String[] {d},null);

        int key = -1;
        if (cursor != null)
            cursor.moveToFirst();
        key = cursor.getInt(0);

        db.close();
        cursor.close();

        return key;
    }

    public String getListHash(int key){
        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        String strQuery = "SELECT " + COLUMN_HASH_OF_LIST + " FROM " + DB_LAST_USED_TABLE + " WHERE id=?";
        Cursor cursor = db.rawQuery(strQuery, new String[] {Integer.toString(key)},null);

        String hash = "";
        if (cursor != null)
            cursor.moveToFirst();
       hash = cursor.getString(0);

        db.close();
        cursor.close();

        return hash;
    }

    public Boolean hasPassword(){
        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //+ DB_SHARES_TABLE + " WHERE date_id = (SELECT MAX(date_id) FROM sharesTable)";
        //String strQuery = "SELECT " + COLUMN_PASS_HASH + " FROM " + DB_LAST_USED_TABLE + " WHERE id=?";
        String strQuery   = "SELECT " + COLUMN_PASS_HASH + " FROM " + DB_LAST_USED_TABLE + " WHERE id = (SELECT MAX(id) FROM dateTable)";

        Cursor cursor = db.rawQuery(strQuery,null);

        String pass_hash = "";
        if (cursor != null)
            cursor.moveToFirst();
        pass_hash = cursor.getString(0);

        db.close();
        cursor.close();

        if(pass_hash.equals("NULL"))
            return false;
        else
            return true;

    }

    public String  getPassword(){
        //Get a readable reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //+ DB_SHARES_TABLE + " WHERE date_id = (SELECT MAX(date_id) FROM sharesTable)";
        //String strQuery = "SELECT " + COLUMN_PASS_HASH + " FROM " + DB_LAST_USED_TABLE + " WHERE id=?";
        String strQuery   = "SELECT " + COLUMN_PASS_HASH + " FROM " + DB_LAST_USED_TABLE + " WHERE id = (SELECT MAX(id) FROM dateTable)";

        Cursor cursor = db.rawQuery(strQuery,null);

        String pass_hash = "";
        if (cursor != null)
            cursor.moveToFirst();
        pass_hash = cursor.getString(0);

        db.close();
        cursor.close();

        return pass_hash;
    }

    /*END DATABASE METHODS FOR DB_DATE_TABLE */

}
