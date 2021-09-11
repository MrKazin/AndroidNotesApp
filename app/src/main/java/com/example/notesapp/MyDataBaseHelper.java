package com.example.notesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDataBaseHelper  extends SQLiteOpenHelper {


    private Context context;
    private static final String DATABASE_NAME = "NotesCollection.db";
    private static final int DATABASE_VERSION = 1;

    //записка
    private static final String NOTE_TABLE_NAME = "Note";
    private static final String NOTE_COLUMN_ID = "_id";
    private static final String NOTE_COLUMN_HEADER = "header";
    private static final String NOTE_COLUMN_TEXT = "text";
    private static final String NOTE_COLUMN_IMAGE = "image";
    private static final String NOTE_COLUMN_DATE = "date";

    //тэги
    private static final String TAG_TABLE_NAME = "Tag";
    private static final String TAG_COLUMN_ID = "_id";
    private static final String TAG_COLUMN_VALUE = "value";
    private static final String TAG_COLUMN_COLOR = "color";

    //для хранения тэгов и записок
    private static final String CONNECTION_TABLE_NAME = "Note_Tag";
    private static final String CONNECTION_COLUMN_ID = "_id";
    private static final String CONNECTION_COLUMN_ID_NOTE = "_id_note";
    private static final String CONNECTION_COLUMN_ID_TAG = "_id_tag";

    MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE " + NOTE_TABLE_NAME +
                " (" + NOTE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTE_COLUMN_HEADER + " TEXT, " +
                NOTE_COLUMN_TEXT + " TEXT, " +
                NOTE_COLUMN_DATE + " TEXT, " +
                NOTE_COLUMN_IMAGE + " BLOB);");
        db.execSQL(String.valueOf(query));
        query.setLength(0);
        query.append("CREATE TABLE " + TAG_TABLE_NAME +
                " (" + TAG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TAG_COLUMN_VALUE + " TEXT," +
                TAG_COLUMN_COLOR + " INTEGER);");
        db.execSQL(String.valueOf(query));
        query.setLength(0);
        query.append("CREATE TABLE " + CONNECTION_TABLE_NAME +
                " (" + CONNECTION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CONNECTION_COLUMN_ID_NOTE + " INTEGER, " +
                CONNECTION_COLUMN_ID_TAG + " INTEGER);");
        db.execSQL(String.valueOf(query));
        query.setLength(0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONNECTION_TABLE_NAME);
        onCreate(db);
    }

    void addNote(String header, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NOTE_COLUMN_HEADER, header);
        cv.put(NOTE_COLUMN_TEXT, text);
        cv.putNull(NOTE_COLUMN_IMAGE);

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        cv.put(NOTE_COLUMN_DATE, formattedDate);
        long result = db.insert(NOTE_TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed To Add Note.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Added.", Toast.LENGTH_SHORT).show();
        }
    }

    void addNote(String header, String text, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NOTE_COLUMN_HEADER, header);
        cv.put(NOTE_COLUMN_TEXT, text);
        cv.put(NOTE_COLUMN_IMAGE, image);

        Date currentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);
        cv.put(NOTE_COLUMN_DATE, formattedDate);
        long result = db.insert(NOTE_TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed To Add Note.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Added With Image.", Toast.LENGTH_SHORT).show();
        }
    }

    void addTag(String value, Integer color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TAG_COLUMN_VALUE, value);
        cv.put(TAG_COLUMN_COLOR, color);
        long result = db.insert(TAG_TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed To Add Tag.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Added.", Toast.LENGTH_SHORT).show();
        }
    }

    void addTagToNote(String note_id, String tag_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CONNECTION_COLUMN_ID_NOTE,note_id);
        cv.put(CONNECTION_COLUMN_ID_TAG,tag_id);
        long result = db.insert(CONNECTION_TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed To Add Tags For Note.", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor getNodeWithId(String row_id){
        String query = "SELECT * FROM " + NOTE_TABLE_NAME +" WHERE " + NOTE_COLUMN_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{row_id});
        }
        return cursor;
    }

    Cursor getNote(String header, String text){
        String query = "SELECT * FROM " + NOTE_TABLE_NAME +" WHERE " + NOTE_COLUMN_HEADER + " = ? AND " + NOTE_COLUMN_TEXT +" = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{header,text});
        }
        return cursor;
    }

    Cursor readAllNotes() {
        String query = "SELECT * FROM " + NOTE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor readAllTags() {
        String query = "SELECT * FROM " + TAG_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor getTagsIdForNote(String row_id){
        String query = "SELECT * FROM " + CONNECTION_TABLE_NAME + " WHERE " + CONNECTION_COLUMN_ID_NOTE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{row_id});
        }
        return cursor;
    }

    Cursor getTag(String row_id){
        String query = "SELECT * FROM " + TAG_TABLE_NAME + " WHERE " + TAG_COLUMN_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{row_id});
        }
        return cursor;
    }

    void updateNote(String row_id, String header, String text, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NOTE_COLUMN_HEADER, header);
        cv.put(NOTE_COLUMN_TEXT, text);
        cv.put(NOTE_COLUMN_IMAGE,image);

        long result = db.update(NOTE_TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Update.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Updated.", Toast.LENGTH_SHORT).show();
        }
    }

    void updateNote(String row_id, String header, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NOTE_COLUMN_HEADER, header);
        cv.put(NOTE_COLUMN_TEXT, text);
        cv.putNull(NOTE_COLUMN_IMAGE);

        long result = db.update(NOTE_TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Update.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Updated.", Toast.LENGTH_SHORT).show();
        }
    }

    void updateTag(String row_id, String value, Integer color){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TAG_COLUMN_VALUE, value);
        cv.put(TAG_COLUMN_COLOR, color);

        long result = db.update(TAG_TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Update.", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, "Successfully Updated.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteNote(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(NOTE_TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteTag(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TAG_TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteTagInNote(String note_id, String tag_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(CONNECTION_TABLE_NAME, CONNECTION_COLUMN_ID_NOTE + "=? AND " + CONNECTION_COLUMN_ID_TAG + "=?", new String[]{note_id,tag_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }/* else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }*/
    }

    void deleteAllTagsWithNote(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(CONNECTION_TABLE_NAME, CONNECTION_COLUMN_ID_NOTE + "=?",new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }/* else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }*/
    }

    void deleteTagFromAllNotes(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(CONNECTION_TABLE_NAME,CONNECTION_COLUMN_ID_TAG + "=?",new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }/* else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }*/
    }
}
