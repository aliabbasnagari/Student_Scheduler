package com.studentscheduler;

import static com.studentscheduler.DateUtils.chooseDate;
import static com.studentscheduler.DateUtils.chooseTime;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CourseActivity extends AppCompatActivity {
    private DatabaseManager DB;
    private ArrayList<Course> courses;
    private CourseAdapter courseAdapter;
    private FloatingActionButton fab;
    private EditText etTitle, etStatus, etNote;
    private TextView sDate, eDate, noItems;
    private Button btnAddCourse;
    private Button btnCancel;
    private RecyclerView rvCourses;
    private LinearLayout llAddCourse;
    private SwitchCompat swStartDate;
    private SwitchCompat swEndDate;
    private String tid = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        etTitle = findViewById(R.id.et_title);
        etStatus = findViewById(R.id.et_status);
        etNote = findViewById(R.id.et_note);
        sDate = findViewById(R.id.tv_start_date);
        eDate = findViewById(R.id.tv_end_date);
        noItems = findViewById(R.id.no_items);
        btnAddCourse = findViewById(R.id.btn_add_course);
        btnCancel = findViewById(R.id.btn_cancel);
        rvCourses = findViewById(R.id.rv_courses);
        llAddCourse = findViewById(R.id.lyt_add_course);
        fab = findViewById(R.id.fab_add_course);
        swStartDate = findViewById(R.id.sw_alert_start);
        swEndDate = findViewById(R.id.sw_alert_end);

        llAddCourse.setVisibility(View.GONE);

        courses = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courses);
        rvCourses.setHasFixedSize(true);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(courseAdapter);
        tid = getIntent().getStringExtra("TID");

        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();

        btnAddCourse.setOnClickListener(v -> {
            DB = new DatabaseManager(this);
            String _title = String.valueOf(etTitle.getText());
            String _sdate = String.valueOf(sDate.getText());
            String _edate = String.valueOf(eDate.getText());
            String _status = String.valueOf(etStatus.getText());
            int _salert = swStartDate.isChecked() ? 1 : 0;
            int _ealert = swEndDate.isChecked() ? 1 : 0;
            if (ExtraUtils.fieldIsNotValid(etTitle, 1, "Invalid Title!")) {
                return;
            } else if (_sdate.equals("Start Date")) {
                sDate.setError("Choose Date");
                return;
            } else if (_edate.equals("End Date")) {
                eDate.setError("Choose Date");
                return;
            }
            if (DB.insertIntoCourse(_title, _sdate, _edate, _status, _salert, _ealert)) {
                String _cid = DB.getLastInsertedRowID(DatabaseManager.TABLE_COURSE);
                if (DB.insertIntoTermCourse(tid, _cid)) {
                    courses.add(new Course(_cid, _title, _sdate, _edate, _status, _salert, _ealert));
                    courseAdapter.notifyItemInserted(courses.size());
                    DB.insertIntoNote(String.valueOf(etNote.getText()));
                    String _nid = DB.getLastInsertedRowID(DatabaseManager.TABLE_NOTES);
                    DB.insertIntoCourseNotes(_cid, _nid);
                    ExtraUtils.updateNoItemsMessage(courses, noItems);
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    if (swStartDate.isChecked()) {
                        int nid = Integer.parseInt(_cid) + 20000;
                        intent.putExtra("title", "Course Started: " + _title);
                        intent.putExtra("nid", nid);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        DateUtils.scheduleAlarm(this, pendingIntent, sdi);
                    }
                    if (swEndDate.isChecked()) {
                        int nid = Integer.parseInt(_cid) + 30000;
                        intent.putExtra("title", "Course Ended: " + _title);
                        intent.putExtra("nid", nid);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        DateUtils.scheduleAlarm(this, pendingIntent, edi);
                    }
                    Toast.makeText(this, "Course Added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            }
            DB.close();
            llAddCourse.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            swStartDate.setChecked(false);
            swEndDate.setChecked(false);
        });

        btnCancel.setOnClickListener(v -> {
            llAddCourse.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        });

        fab.setOnClickListener(v -> {
            if (llAddCourse.getVisibility() == View.GONE) {
                fab.setVisibility(View.GONE);
                llAddCourse.setVisibility(View.VISIBLE);
            }
        });

        sDate.setOnClickListener(v -> chooseDate(this, sDate, sdi));
        eDate.setOnClickListener(v -> chooseDate(this, eDate, edi));
        swStartDate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, sdi, swStartDate);
        });

        swEndDate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, edi, swEndDate);
        });
        loadCourses();
    }

    public void loadCourses() {
        DB = new DatabaseManager(this);
        String query = "Select C.* from COURSE C " +
                "       Join TERM_COURSES TC on TC.CID = C.CID " +
                "       Where TC.TID = '" + tid + "';";
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
                    courseAdapter.notifyItemInserted(courses.size());
                }
            } while (termData.moveToNext());
        }
        if (termData != null)
            termData.close();
        DB.close();
        ExtraUtils.updateNoItemsMessage(courses, noItems);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}