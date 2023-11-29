package com.manilov.requester;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "requester.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "request_values";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_SENSOR = "sensor";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_VALUE
                + " REAL, " + COLUMN_SENSOR + " TEXT" + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }

    public void insert(SQLiteDatabase db, double value, String sensor) {
        db.execSQL("INSERT OR IGNORE INTO " + TABLE + " (" + COLUMN_VALUE
                + ", " + COLUMN_SENSOR  + ") VALUES (" + value +", \"" + sensor + "\");");
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE + " WHERE _id IN (SELECT MIN(_id) FROM " + TABLE + ");");
    }
}

