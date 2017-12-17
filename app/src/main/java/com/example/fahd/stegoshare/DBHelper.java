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

    private static final String DB_NAME = "stegoshareDB_nathan_test";
    private static final int DB_VER = 1;

    // First Table - Words Table
    public static final String DB_WORDS_TABLE = "wordsTable";
    public static final String COLUMN_WORDS_ID = "ID";
    public static final String COLUMN_WORDS = "WORDS";

    // Second Table - Shares Table
    public static final String DB_SHARES_TABLE = "sharesTable";
    public static final String COLUMN_SHARES_ID = "ID";
    public static final String COLUMN_SHARES = "SHARES";

    public static final String COLUMN_DATE_CREATED = "date";

    // Third Table - Date table
    public static final String DB_LAST_USED_TABLE = "dateTable";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query_1 = String.format(
                "create table " + DB_WORDS_TABLE + " (" +
                        COLUMN_WORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_WORDS + " TEXT)"
        );

        sqLiteDatabase.execSQL(query_1);

        String query_2 = String.format(
                "create table " + DB_SHARES_TABLE + " (" +
                        COLUMN_SHARES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_SHARES + " TEXT)"
        );

        sqLiteDatabase.execSQL(query_2);

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
    public void addShare(String share){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(COLUMN_SHARES, share);

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

    /*END DATABASE METHODS FOR TABLE DB_SHARE_TABLE */

}
