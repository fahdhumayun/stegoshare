// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fahd on 12/15/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "stegoshareDB";
    private static final int DB_VER = 1;

    // First Table - Words Table
    public static final String DB_WORDS_TABLE = "wordsTable";
    public static final String COLUMN_WORDS_ID = "ID";
    public static final String COLUMN_WORDS = "WORDS";

    // Second Table - Shares Table
    public static final String DB_SHARES_TABLE = "sharesTable";
    public static final String COLUMN_SHARES_ID = "ID";
    public static final String COLUMN_SHARES = "SHARES";

    // Third Table -

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


}
