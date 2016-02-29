package com.dt.project.DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DAL extends SQLiteOpenHelper {

    // An object which can access the database:
    private SQLiteDatabase database;

    // Constructor:
    public DAL(Activity activity) {
        super(activity, DB.NAME, null, DB.VERSION);
    }

    // Will be called when the app is first running:
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB.Places.CREATION_STATEMENT); // Execute sql statement

        db.execSQL(DB.FavoritePlaces.CREATION_STATEMENT); // Execute sql statement
    }

    // Will be called when the app is being upgraded if our version will be different:
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DB.Places.DELETION_STATEMENT);
        db.execSQL(DB.Places.CREATION_STATEMENT);

        db.execSQL(DB.FavoritePlaces.DELETION_STATEMENT);
        db.execSQL(DB.FavoritePlaces.CREATION_STATEMENT);
    }

    // Opening the database for any action:
    public void open() {
        database = getWritableDatabase(); // This is the open method.
    }

    // Closing the database:
    public void close() {
        super.close(); // This is the close method.
    }

    // Add a new row to a table:
    public long insert(String tableName, ContentValues values) {
        return database.insert(tableName, null, values);
    }
    //delete
    public long delete(String tableName, String where) {
        return database.delete(tableName, where, null);
    }
    // We then can advanced the cursor to the next line
    public Cursor getTable(String tableName, String[] columns, String where, String orderBy) {
        return database.query(tableName, columns, where, null, null, null, orderBy);
    }
    // The update will return the number of rows affected.
    public long update(String tableName, ContentValues values, String where) {
        return (long) database.update(tableName, values, where, null);
    }
}
