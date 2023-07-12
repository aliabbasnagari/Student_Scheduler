package com.studentscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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

    public static Term getTerm(Context context, String _tid) {
        Term term = null;
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select * From " + DatabaseManager.TABLE_TERM + "\n" +
                "       Where TID = " + _tid;
        Cursor termData = DB.runQuery(query);
        if (termData != null) {
            if (termData.moveToFirst()) {
                String name;
                String sdate;
                String edate;
                int salert;
                int ealert;
                int titleIndex = termData.getColumnIndex("Title");
                int sdateIndex = termData.getColumnIndex("StartDate");
                int edateIndex = termData.getColumnIndex("EndDate");
                int salertIndex = termData.getColumnIndex("StartAlert");
                int ealertIndex = termData.getColumnIndex("EndAlert");
                name = "None";
                sdate = "None";
                edate = "None";
                salert = 0;
                ealert = 0;
                if (titleIndex != -1)
                    name = termData.getString(titleIndex);
                if (sdateIndex != -1)
                    sdate = termData.getString(sdateIndex);
                if (edateIndex != -1)
                    edate = termData.getString(edateIndex);
                if (salertIndex != -1)
                    salert = termData.getInt(salertIndex);
                if (ealertIndex != -1)
                    ealert = termData.getInt(ealertIndex);
                if (!name.equals("None")) {
                    term = new Term(_tid, name, sdate, edate, salert, ealert);
                }
            }
            termData.close();
        }
        DB.close();
        return term;
    }

    public static void loadTerms(Context context, ArrayList<Term> terms) {
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select * From " + DatabaseManager.TABLE_TERM;
        Cursor termData = DB.runQuery(query);
        if (termData != null && termData.moveToFirst()) {
            String tid;
            String name;
            String sdate;
            String edate;
            int salert;
            int ealert;
            int idIndex = termData.getColumnIndex("TID");
            int titleIndex = termData.getColumnIndex("Title");
            int sdateIndex = termData.getColumnIndex("StartDate");
            int edateIndex = termData.getColumnIndex("EndDate");
            int salertIndex = termData.getColumnIndex("StartAlert");
            int ealertIndex = termData.getColumnIndex("EndAlert");
            do {
                tid = "None";
                name = "None";
                sdate = "None";
                edate = "None";
                salert = 0;
                ealert = 0;
                if (idIndex != -1)
                    tid = termData.getString(idIndex);
                if (titleIndex != -1)
                    name = termData.getString(titleIndex);
                if (sdateIndex != -1)
                    sdate = termData.getString(sdateIndex);
                if (edateIndex != -1)
                    edate = termData.getString(edateIndex);
                if (salertIndex != -1)
                    salert = termData.getInt(salertIndex);
                if (ealertIndex != -1)
                    ealert = termData.getInt(ealertIndex);
                if (!name.equals("None")) {
                    terms.add(new Term(tid, name, sdate, edate, salert, ealert));
                }
            } while (termData.moveToNext());
        }
        if (termData != null)
            termData.close();
        DB.close();
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

    public static Course getCourse(Context context, String _cid) {
        Course course = null;
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select * From " + TABLE_COURSE + "\n" +
                "       Where CID = " + _cid;
        Cursor termData = DB.runQuery(query);
        if (termData != null) {
            if (termData.moveToFirst()) {
                String cid, name, sdate, edate, status;
                int salert, ealert;
                int idIndex = termData.getColumnIndex("CID");
                int titleIndex = termData.getColumnIndex("Title");
                int sdateIndex = termData.getColumnIndex("StartDate");
                int edateIndex = termData.getColumnIndex("EndDate");
                int statusIndex = termData.getColumnIndex("CourseStatus");
                int salertIndex = termData.getColumnIndex("StartAlert");
                int ealertIndex = termData.getColumnIndex("EndAlert");

                cid = name = sdate = edate = status = "None";
                salert = ealert = 0;
                if (idIndex != -1)
                    cid = termData.getString(idIndex);
                if (titleIndex != -1)
                    name = termData.getString(titleIndex);
                if (sdateIndex != -1)
                    sdate = termData.getString(sdateIndex);
                if (edateIndex != -1)
                    edate = termData.getString(edateIndex);
                if (statusIndex != -1)
                    status = termData.getString(statusIndex);
                if (salertIndex != -1)
                    salert = termData.getInt(salertIndex);
                if (ealertIndex != -1)
                    ealert = termData.getInt(ealertIndex);
                if (!cid.equals("None")) {
                    course = new Course(cid, name, sdate, edate, status, salert, ealert);
                }
            }
            termData.close();
        }
        DB.close();
        return course;
    }

    public static void loadCourses(Context context, ArrayList<Course> courses, String tid) {
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select C.* from COURSE C ;";
        if (tid != null) {
            query = "Select C.* from COURSE C " +
                    "       Join TERM_COURSES TC on TC.CID = C.CID " +
                    "       Where TC.TID = '" + tid + "';";
        }

        Cursor termData = DB.runQuery(query);
        if (termData != null && termData.moveToFirst()) {
            String cid, name, sdate, edate, status;
            int salert, ealert;
            int idIndex = termData.getColumnIndex("CID");
            int titleIndex = termData.getColumnIndex("Title");
            int sdateIndex = termData.getColumnIndex("StartDate");
            int edateIndex = termData.getColumnIndex("EndDate");
            int statusIndex = termData.getColumnIndex("CourseStatus");
            int salertIndex = termData.getColumnIndex("StartAlert");
            int ealertIndex = termData.getColumnIndex("EndAlert");
            do {
                cid = name = sdate = edate = status = "None";
                salert = ealert = 0;
                if (idIndex != -1)
                    cid = termData.getString(idIndex);
                if (titleIndex != -1)
                    name = termData.getString(titleIndex);
                if (sdateIndex != -1)
                    sdate = termData.getString(sdateIndex);
                if (edateIndex != -1)
                    edate = termData.getString(edateIndex);
                if (statusIndex != -1)
                    status = termData.getString(statusIndex);
                if (salertIndex != -1)
                    salert = termData.getInt(salertIndex);
                if (ealertIndex != -1)
                    ealert = termData.getInt(ealertIndex);
                if (!cid.equals("None")) {
                    courses.add(new Course(cid, name, sdate, edate, status, salert, ealert));
                }
            } while (termData.moveToNext());
        }
        if (termData != null)
            termData.close();
        DB.close();
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

    public static Assessment getAssessment(Context context, String _aid) {
        Assessment assessment = null;
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select * From " + TABLE_ASSESSMENTS + "\n" +
                "       Where AID = " + _aid;
        Cursor termData = DB.runQuery(query);
        if (termData != null) {
            if (termData.moveToFirst()) {
                String title, sdate, edate, type;
                int salert, ealert;
                int titleIndex = termData.getColumnIndex("Title");
                int sdateIndex = termData.getColumnIndex("StartDate");
                int edateIndex = termData.getColumnIndex("EndDate");
                int statusIndex = termData.getColumnIndex("Type");
                int salertIndex = termData.getColumnIndex("StartAlert");
                int ealertIndex = termData.getColumnIndex("EndAlert");

                title = sdate = edate = type = "None";
                salert = ealert = 0;
                if (titleIndex != -1)
                    title = termData.getString(titleIndex);
                if (sdateIndex != -1)
                    sdate = termData.getString(sdateIndex);
                if (edateIndex != -1)
                    edate = termData.getString(edateIndex);
                if (statusIndex != -1)
                    type = termData.getString(statusIndex);
                if (salertIndex != -1)
                    salert = termData.getInt(salertIndex);
                if (ealertIndex != -1)
                    ealert = termData.getInt(ealertIndex);
                if (!title.equals("None")) {
                    assessment = new Assessment(_aid, title, sdate, edate, type, salert, ealert);
                }
            }
            termData.close();
        }
        DB.close();
        return assessment;
    }

    public static int getAssessmentCount(Context context, String cid) {
        int count = 0;
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select Count(*) as Counter From " + TABLE_COURSE_ASSESSMENTS + " AS TC\n" +
                "       Where TC.CID = " + cid;
        Cursor termData = DB.runQuery(query);
        if (termData != null) {
            if (termData.moveToFirst()) {
                count = termData.getInt(0);
            }
            termData.close();
        }
        DB.close();
        return count;
    }

    public static int getTableCount(Context context, String tableName) {
        int count = 0;
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select Count(*) AS COUNT From " + tableName + ";";
        Cursor termData = DB.runQuery(query);
        if (termData != null) {
            if (termData.moveToFirst()) {
                count = termData.getInt(0);
            }
            termData.close();
        }
        DB.close();
        return count;
    }

    public boolean deleteAssessment(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (deleteCourseAssessments(id))
            return (db.delete(TABLE_ASSESSMENTS, "AID = ?", new String[]{id}) != -1);
        else return false;
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

    public static Instructor getInstructor(Context context, String _iid) {
        Instructor instructor = null;
        DatabaseManager DB = new DatabaseManager(context);
        String query = "Select * From " + TABLE_INSTRUCTOR + "\n" +
                "       Where IID = " + _iid;
        Cursor termData = DB.runQuery(query);
        if (termData != null) {
            if (termData.moveToFirst()) {
                String name = "None", phone = "None", email = "None";
                int nameIndex = termData.getColumnIndex("FullName");
                int phoneIndex = termData.getColumnIndex("Phone");
                int emailIndex = termData.getColumnIndex("Email");

                if (nameIndex != -1) name = termData.getString(nameIndex);
                if (phoneIndex != -1) phone = termData.getString(phoneIndex);
                if (emailIndex != -1) email = termData.getString(emailIndex);
                if (!name.equals("None")) instructor = new Instructor(_iid, name, phone, email);
            }
            termData.close();
        }
        DB.close();
        return instructor;
    }

    public boolean deleteInstructor(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (deleteCourseInstructors(id))
            return (db.delete(TABLE_INSTRUCTOR, "IID = ?", new String[]{id}) != -1);
        else return false;
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
        if (deleteCourseNotes(id))
            return (db.delete(TABLE_NOTES, "NID = ?", new String[]{id}) != -1);
        else return false;
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
