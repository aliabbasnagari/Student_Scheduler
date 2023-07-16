package com.studentscheduler;

import static com.studentscheduler.DateUtils.chooseDate;
import static com.studentscheduler.DateUtils.chooseTime;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
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
    private EditText etTitleT, etTitleC, etStatus, etNote;
    private TextView tvStartDateC;
    private TextView tvEndDateC;
    private TextView tvStartDateT;
    private TextView tvEndDateT;
    private TextView tvNoItems;
    private Button btnEditT;
    private LinearLayout llAddCourse;
    private SwitchCompat swStartAlertT, swEndAlertT, swStartAlertC, swEndAlertC;
    private String tid = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView tvIdT = findViewById(R.id.tv_id_t);
        Button btnAddCourse = findViewById(R.id.btn_add_course);
        Button btnCancel = findViewById(R.id.btn_cancel);
        RecyclerView rvCourses = findViewById(R.id.rv_courses);
        etTitleT = findViewById(R.id.et_title_t);
        etTitleC = findViewById(R.id.et_title_c);
        etStatus = findViewById(R.id.et_status);
        etNote = findViewById(R.id.et_note);
        tvStartDateC = findViewById(R.id.tv_start_date_c);
        tvEndDateC = findViewById(R.id.tv_end_date_c);
        tvStartDateT = findViewById(R.id.tv_start_date_t);
        tvEndDateT = findViewById(R.id.tv_end_date_t);
        tvNoItems = findViewById(R.id.no_items);
        llAddCourse = findViewById(R.id.lyt_add_course);
        fab = findViewById(R.id.fab_add_course);
        swStartAlertT = findViewById(R.id.sw_alert_start_t);
        swEndAlertT = findViewById(R.id.sw_alert_end_t);
        swStartAlertC = findViewById(R.id.sw_alert_start_c);
        swEndAlertC = findViewById(R.id.sw_alert_end_c);
        btnEditT = findViewById(R.id.btn_edit_t);

        llAddCourse.setVisibility(View.GONE);

        courses = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courses);
        rvCourses.setHasFixedSize(true);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(courseAdapter);
        tid = getIntent().getStringExtra("TID");

        Term term = DatabaseManager.getTerm(this, tid);
        if (term != null) {
            tvIdT.setText(term.getTID());
            etTitleT.setText(term.getTitle());
            tvStartDateT.setText(term.getsDate());
            tvEndDateT.setText(term.geteDate());
            swStartAlertT.setChecked(term.getsAlert() == 1);
            swEndAlertT.setChecked(term.geteAlert() == 1);
        }

        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();

        btnAddCourse.setOnClickListener(v -> {
            DB = new DatabaseManager(this);
            String _title = String.valueOf(etTitleC.getText());
            String _sdate = String.valueOf(tvStartDateC.getText());
            String _edate = String.valueOf(tvEndDateC.getText());
            String _status = String.valueOf(etStatus.getText());
            int _salert = swStartAlertC.isChecked() ? 1 : 0;
            int _ealert = swEndAlertC.isChecked() ? 1 : 0;
            if (ExtraUtils.fieldIsNotValid(etTitleC, 1, "Invalid Title!")) {
                return;
            } else if (_sdate.equals("Start Date")) {
                tvStartDateC.setError("Choose Date");
                return;
            } else if (_edate.equals("End Date")) {
                tvEndDateC.setError("Choose Date");
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
                    ExtraUtils.updateNoItemsMessage(courses, tvNoItems);
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    if (swStartAlertC.isChecked()) {
                        int nid = Integer.parseInt(_cid) + 20000;
                        intent.putExtra("title", "Course Started: " + _title);
                        intent.putExtra("nid", nid);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        DateUtils.scheduleAlarm(this, pendingIntent, sdi);
                    }
                    if (swEndAlertC.isChecked()) {
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
            swStartAlertC.setChecked(false);
            swEndAlertC.setChecked(false);
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

        tvStartDateT.setOnClickListener(v -> {
            if (etTitleT.isEnabled()) DateUtils.chooseDate(this, tvStartDateT, sdi);
        });
        tvEndDateT.setOnClickListener(v -> {
            if (etTitleT.isEnabled()) DateUtils.chooseDate(this, tvEndDateT, edi);
        });

        btnEditT.setOnClickListener(v -> {
            if (!etTitleT.isEnabled()) {
                etTitleT.setEnabled(true);
                swStartAlertT.setEnabled(true);
                swEndAlertT.setEnabled(true);
                etTitleT.requestFocus();
                btnEditT.setText(getString(R.string.save));
            } else {
                etTitleT.setEnabled(false);
                swStartAlertT.setEnabled(false);
                swEndAlertT.setEnabled(false);
                btnEditT.setText(getString(R.string.edit));
                DatabaseManager DB = new DatabaseManager(this);
                int salt = (swStartAlertT.isChecked()) ? 1 : 0;
                int ealt = (swEndAlertT.isChecked()) ? 1 : 0;
                ContentValues values = new ContentValues();
                values.put("Title", String.valueOf(etTitleT.getText()));
                values.put("StartDate", String.valueOf(tvStartDateT.getText()));
                values.put("EndDate", String.valueOf(tvEndDateT.getText()));
                values.put("StartAlert", salt);
                values.put("EndAlert", ealt);
                if (term != null && DB.updateData(term.getTID(), DatabaseManager.TABLE_TERM, values)) {
                    int _tid = Integer.parseInt(tid);
                    if (salt == 0)
                        DateUtils.cancelAlarm(this, _tid);
                    else
                        DateUtils.scheduleAlarm(this, "Term Started: " + term.getTitle(), _tid, sdi);
                    if (ealt == 0)
                        DateUtils.cancelAlarm(this, _tid + 10000);
                    else
                        DateUtils.scheduleAlarm(this, "Term Ended: " + term.getTitle(), _tid + 10000, edi);
                    Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, getString(R.string.failure), Toast.LENGTH_SHORT).show();
                DB.close();
            }
        });
        swStartAlertT.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, sdi, swStartAlertT);
        });
        swEndAlertT.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, edi, swEndAlertT);
        });

        tvStartDateC.setOnClickListener(v -> chooseDate(this, tvStartDateC, sdi));
        tvEndDateC.setOnClickListener(v -> chooseDate(this, tvEndDateC, edi));
        swStartAlertC.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, sdi, swStartAlertC);
        });
        swEndAlertC.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, edi, swEndAlertC);
        });
        DatabaseManager.loadCourses(this, courses, tid);
        courseAdapter.notifyItemRangeInserted(0, courses.size());
        ExtraUtils.updateNoItemsMessage(courses, tvNoItems);
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