package com.example.elawn_android.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.elawn_android.Service.Coordinate;
import com.example.elawn_android.Service.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = Config.DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        //CREATE THE TABLES

        String CREATE_PATH_TABLE = "CREATE TABLE " + Config.TABLE_PATH_LIST + " ("
                + Config.COLUMN_PATH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_PATH_NAME + " TEXT NOT NULL, "
                + Config.COLUMN_PATH_ADDRESS + " TEXT NOT NULL"
                + ")";

        Log.d(TAG, "Table created with this query: " + CREATE_PATH_TABLE);

        sqLiteDatabase.execSQL(CREATE_PATH_TABLE);

        Log.d(TAG, "path table created");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // ALTER THE DESIGN FOR AN UPDATE

    }

    public long insertPath(Path path) {
        long id = -1;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_PATH_NAME, path.getPathName());
        contentValues.put(Config.COLUMN_PATH_ADDRESS, path.getPathAddress());
        /*
        contentValues.put(Config.COLUMN_PATH_V1, path.getPathAddress());
        contentValues.put(Config.COLUMN_PATH_V2, path.getPathAddress());
        contentValues.put(Config.COLUMN_PATH_V3, path.getPathAddress());
        contentValues.put(Config.COLUMN_PATH_V4, path.getPathAddress());
        contentValues.put(Config.COLUMN_PATH_VCHARGE, path.getPathAddress());

         */


        try {
            id = db.insertOrThrow(Config.TABLE_PATH_LIST, null, contentValues);
        } catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }

        return id;
    }

    public List<Path> getAllPaths() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(Config.TABLE_PATH_LIST, null, null, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<Path> pathList = new ArrayList<>();

                    do {
                        //getting information from cursor

                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_PATH_ID));
                        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PATH_NAME));
                        String address = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PATH_ADDRESS));

                        //adding this path to pathList
                        pathList.add(0,new Path(id,name, address));

                    } while (cursor.moveToNext());

                    //returns an arrayList of all paths in the database
                    return pathList;
                }

            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return Collections.emptyList();
    }

    public void deletePathByID(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Config.TABLE_PATH_LIST, Config.COLUMN_PATH_ID + " =?", new String[]{ID});

    }

    public int getRecentPathId() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(Config.TABLE_PATH_LIST, null, null, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToLast()) {
                    int id;
                    id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_PATH_ID));

                    //returns an arrayList of all paths in the database
                    return id;
                }

            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return 0;
    }

    public ArrayList<String> getAllPathNames() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(Config.TABLE_PATH_LIST, null, null, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    ArrayList<String> pathNameList = new ArrayList<>();

                    do {
                        //getting information from cursor

                        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PATH_NAME));

                        //adding this path to pathList
                        pathNameList.add(0,name);

                    } while (cursor.moveToNext());

                    //returns an arrayList of all paths in the database
                    return pathNameList;
                }

            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return null;
    }

    public int getPathIdFromName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.query(Config.TABLE_PATH_LIST, new String[] {Config.COLUMN_PATH_ID,Config.COLUMN_PATH_NAME,Config.COLUMN_PATH_ADDRESS},
                    Config.COLUMN_PATH_NAME+ "=?",new String[]{String.valueOf(name)},null,null,null,null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id;
                    id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_PATH_ID));
                    //returns an arrayList of all paths in the database
                    return id;
                }

            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return 0;
    }


}



