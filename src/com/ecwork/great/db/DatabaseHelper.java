package com.ecwork.great.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * User: ecsark
 * Date: 2/8/14
 * Time: 9:43 PM
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {

        if (instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());

        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sql : DatabaseContract.SQL_SETUP) {
            System.err.println(sql);
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
        for (String sql : DatabaseContract.SQL_TEARDOWN) {
            db.execSQL(sql);
        }
        onCreate(db);
    }
}