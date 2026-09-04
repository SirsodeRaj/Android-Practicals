package com.example.studentrecords;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "students";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_ROLL = "roll_number";
    private static final String COL_COURSE = "course";
    private static final String COL_MARKS = "marks";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_ROLL + " TEXT NOT NULL, " +
                COL_COURSE + " TEXT NOT NULL, " +
                COL_MARKS + " REAL NOT NULL)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public boolean insertStudent(
            String name,
            String rollNumber,
            String course,
            double marks) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_ROLL, rollNumber);
        values.put(COL_COURSE, course);
        values.put(COL_MARKS, marks);

        long result = db.insert(
                TABLE_NAME,
                null,
                values
        );

        return result != -1;
    }

    public Cursor getAllStudents() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " ORDER BY " + COL_ID + " DESC",
                null
        );
    }

    public int getStudentCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_NAME,
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public boolean deleteStudent(int studentId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(studentId)}
        );

        return result > 0;
    }
}