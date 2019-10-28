package com.map4d.SQLiteDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.map4d.model.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "LOCATION_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LOCATION = "LOCATION";
    private static final String ID_COLUMN = "id";
    private static final String STATUS_COLUMN = "Status";
    private static final String LATITUDE_COLUMN = "Latitude";
    private static final String LONGITUDE_COLUMN = "Longitude";
    private static final String CREATE_LOCATION_TABLE_SQL = "CREATE TABLE " + TABLE_LOCATION + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            STATUS_COLUMN + " TEXT ," +
            LATITUDE_COLUMN + " DOUBLE," +
            LONGITUDE_COLUMN + " DOUBLE" +
            ")";
    private static DatabaseHelper sInstance;
    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e(TAG, "DatabaseHelper: ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate: ");
        db.execSQL(CREATE_LOCATION_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "onUpgrade: ");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }

    public boolean insertLocation(Data data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_COLUMN, data.getStatus());
        values.put(LATITUDE_COLUMN, data.getLatitude());
        values.put(LONGITUDE_COLUMN, data.getLongitude());
        long rowId = db.insert(TABLE_LOCATION, null, values);
        db.close();
        if (rowId != -1)
            return true;
        return false;
    }

    public List<Data> getData(String status) {
        SQLiteDatabase db = getReadableDatabase();
        Data data = null;
        Cursor cursor = db.query(TABLE_LOCATION, new String[]{ID_COLUMN, STATUS_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN}, STATUS_COLUMN + " = ?",
                new String[]{status}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            data = new Data(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),cursor.getDouble(3));
            cursor.close();
        }
        db.close();
        return Collections.singletonList(data);
    }

    public List<Data> getAllLocation() {
        SQLiteDatabase db = getReadableDatabase();
        List<Data> words = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_LOCATION;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                words.add(new Data(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),cursor.getDouble(3)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return words;
    }
    public int getTotalLocation() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_LOCATION;
        Cursor cursor = db.rawQuery(sql, null);
        int totalRows = cursor.getCount();
        cursor.close();
        return totalRows;
    }

    public int updateLocation(Data data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_COLUMN, data.getStatus());
        values.put(String.valueOf(LATITUDE_COLUMN), data.getLatitude());
        values.put(String.valueOf(LONGITUDE_COLUMN), data.getLongitude());
        int rowEffect = db.update(TABLE_LOCATION, values, ID_COLUMN + " = ?", new String[]{String.valueOf(data.getId())});
        db.close();
        return rowEffect;
    }

    public int deleteLocation(Data data) {
        SQLiteDatabase db = getReadableDatabase();
        int rowEffect = db.delete(TABLE_LOCATION, ID_COLUMN + " = ?", new String[]{String.valueOf(data.getId())});
        db.close();
        return rowEffect;
    }
}
