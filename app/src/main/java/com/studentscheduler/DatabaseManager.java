package com.studentscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "StudentScheduler";
    public static final String TABLE_TERM = "TERM";
    public static final String TABLE_COURSE = "COURSE";
    public static final String TABLE_TERM_COURSES = "TERM_COURSES";
    public static final String TABLE_INSTRUCTOR = "INSTRUCTOR";
    public static final String TABLE_COURSE_INSTRUCTORS = "COURSE_INSTRUCTORS";
    public static final String TABLE_ASSESSMENTS = "ASSESSMENTS";
    public static final String TABLE_COURSE_ASSESSMENTS = "COURSE_ASSESSMENTS";
    public static final String TABLE_NOTES = "NOTES";
    public static final String TABLE_COURSE_NOTES = "COURSE_NOTES";

    Context context;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TERM + " (\n" +
                "    TID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    Title VARCHAR(100),\n" +
                "    StartDate DATE,\n" +
                "    EndDate DATE,\n" +
                "    StartAlert INTEGER,\n" +
                "    EndAlert INTEGER\n" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_COURSE + " (\n" +
                "    CID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    Title VARCHAR(100),\n" +
                "    StartDate DATE,\n" +
                "    EndDate DATE,\n" +
                "    CourseStatus VARCHAR(100),\n" +
                "    StartAlert INTEGER,\n" +
                "    EndAlert INTEGER\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_TERM_COURSES + " (\n" +
                "    TID INTEGER,\n" +
                "    CID INTEGER,\n" +
                "    PRIMARY KEY (TID, CID),\n" +
                "    FOREIGN KEY (TID) REFERENCES Term(TID),\n" +
                "    FOREIGN KEY (CID) REFERENCES " + TABLE_COURSE + "(CID)\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_INSTRUCTOR + " (\n" +
                "    IID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    FullName VARCHAR(100),\n" +
                "    Phone VARCHAR(100),\n" +
                "    Email VARCHAR(100)\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_COURSE_INSTRUCTORS + " (\n" +
                "    CID INTEGER,\n" +
                "    IID INTEGER,\n" +
                "    PRIMARY KEY (CID, IID),\n" +
                "    FOREIGN KEY (IID) REFERENCES " + TABLE_INSTRUCTOR + "(IID),\n" +
                "    FOREIGN KEY (CID) REFERENCES " + TABLE_COURSE + "(CID)\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_ASSESSMENTS + " (\n" +
                "    AID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    Title VARCHAR(100),\n" +
                "    Type VARCHAR(100),\n" +
                "    StartDate DATE,\n" +
                "    EndDate DATE,\n" +
                "    StartAlert INTEGER,\n" +
                "    EndAlert INTEGER\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_COURSE_ASSESSMENTS + " (\n" +
                "    CID INTEGER,\n" +
                "    AID INTEGER,\n" +
                "    PRIMARY KEY (CID, AID),\n" +
                "    FOREIGN KEY (AID) REFERENCES " + TABLE_ASSESSMENTS + "(AID),\n" +
                "    FOREIGN KEY (CID) REFERENCES " + TABLE_COURSE + "(CID)\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_NOTES + " (\n" +
                "    NID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    Note VARCHAR(250)\n" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_COURSE_NOTES + " (\n" +
                "    CID INTEGER,\n" +
                "    NID INTEGER,\n" +
                "    PRIMARY KEY (CID, NID),\n" +
                "    FOREIGN KEY (NID) REFERENCES " + TABLE_NOTES + "(NID),\n" +
                "    FOREIGN KEY (CID) REFERENCES " + TABLE_COURSE + "(CID)\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_INSTRUCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_ASSESSMENTS);
        onCreate(db);
    }

    // Term
    public boolean insertIntoTerm(String title, String sdate, String edate, int salert, int ealert) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("StartDate", sdate);
        values.put("EndDate", edate);
        values.put("StartAlert", salert);
        values.put("EndAlert", ealert);
        return (db.insert(TABLE_TERM, null, values) != -1);
    }

    public boolean deleteTerm(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_TERM, "TID = ?", new String[]{id}) != -1);
    }

    // Course
    public boolean insertIntoCourse(String title, String sdate, String edate, String status, int salert, int ealert) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("StartDate", sdate);
        values.put("EndDate", edate);
        values.put("CourseStatus", status);
        values.put("StartAlert", salert);
        values.put("EndAlert", ealert);
        return (db.insert(TABLE_COURSE, null, values) != -1);
    }

    public boolean deleteCourse(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (deleteTermCourses(id))
            return (db.delete(TABLE_COURSE, "CID = ?", new String[]{id}) != -1);
        else return false;
    }

    // Term Course
    public boolean deleteTermCourses(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_TERM_COURSES, "CID = ?", new String[]{id}) != -1);
    }

    public boolean insertIntoTermCourse(String tid, String cid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TID", tid);
        values.put("CID", cid);
        return (db.insert(TABLE_TERM_COURSES, null, values) != -1);
    }

    // Assessment
    public boolean insertIntoAssessment(String title, String sdate, String edate, String type, int salert, int ealert) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", title);
        values.put("StartDate", sdate);
        values.put("EndDate", edate);
        values.put("Type", type);
        values.put("StartAlert", salert);
        values.put("EndAlert", ealert);
        return (db.insert(TABLE_ASSESSMENTS, null, values) != -1);
    }

    public boolean deleteAssessment(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_ASSESSMENTS, "AID = ?", new String[]{id}) != -1);
    }

    // Course Assessment
    public boolean deleteCourseAssessments(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_COURSE_ASSESSMENTS, "AID = ?", new String[]{id}) != -1);
    }

    public boolean insertIntoCourseAssessments(String cid, String aid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CID", cid);
        values.put("AID", aid);
        return (db.insert(TABLE_COURSE_ASSESSMENTS, null, values) != -1);
    }

    // Instructor
    public boolean insertIntoInstructor(String name, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FullName", name);
        values.put("Phone", phone);
        values.put("Email", email);
        return (db.insert(TABLE_INSTRUCTOR, null, values) != -1);
    }

    public boolean deleteInstructor(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_INSTRUCTOR, "IID = ?", new String[]{id}) != -1);
    }

    // Course Instructor
    public boolean deleteCourseInstructors(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_COURSE_INSTRUCTORS, "IID = ?", new String[]{id}) != -1);
    }

    public boolean insertIntoCourseInstructors(String cid, String iid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CID", cid);
        values.put("IID", iid);
        return (db.insert(TABLE_COURSE_INSTRUCTORS, null, values) != -1);
    }

    // Note
    public boolean insertIntoNote(String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Note", note);
        return (db.insert(TABLE_NOTES, null, values) != -1);
    }

    public boolean deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_NOTES, "NID = ?", new String[]{id}) != -1);
    }

    // Course Note
    public boolean deleteCourseNotes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_COURSE_NOTES, "NID = ?", new String[]{id}) != -1);
    }

    public boolean insertIntoCourseNotes(String cid, String nid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CID", cid);
        values.put("NID", nid);
        return (db.insert(TABLE_COURSE_NOTES, null, values) != -1);
    }

    public boolean updateData(String _id, String table_name, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        String key = getKey(table_name);
        return (db.update(table_name, values, key + " = ?", new String[]{_id}) != -1);
    }

    public boolean getAlertStatus(String tableName, String alert_pos, String id) {
        int value = -1;
        String key = getKey(tableName);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + alert_pos + " FROM " + tableName + " Where " + key + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                value = cursor.getInt(0);
            }
            cursor.close();
        }
        return value == 1;
    }

    public boolean updateAlertStatus(String tableName, String alert_pos, String id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String key = getKey(tableName);
        ContentValues values = new ContentValues();
        values.put(alert_pos, status);
        return (db.update(tableName, values, key + " = ?", new String[]{id}) != -1);
    }

    public String getLastInsertedRowID(String tableName) {
        String id = null;
        String key = getKey(tableName);
        if (key != null) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT MAX(" + key + ") AS " + key + " FROM " + tableName;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(key);
                if (idIndex != -1)
                    id = cursor.getString(idIndex);
            }
            if (cursor != null)
                cursor.close();
        }
        return id;
    }

    public String getKey(String tableName) {
        switch (tableName) {
            case TABLE_TERM:
                return "TID";
            case TABLE_COURSE:
                return "CID";
            case TABLE_INSTRUCTOR:
                return "IID";
            case TABLE_ASSESSMENTS:
                return "AID";
            default:
                return null;
        }
    }

    public Cursor runQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public void Clear() {
        this.close();
        context.deleteDatabase(DATABASE_NAME);
        /*
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERM, null, null);
        db.delete(TABLE_COURSE, null, null);
        db.delete(TABLE_TERM_COURSES, null, null);
        db.delete(TABLE_INSTRUCTOR, null, null);
        db.delete(TABLE_COURSE_INSTRUCTORS, null, null);
        db.delete(TABLE_ASSESSMENTS, null, null);
        db.delete(TABLE_COURSE_ASSESSMENTS, null, null);*/
    }
}
