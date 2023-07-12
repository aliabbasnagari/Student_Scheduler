package com.studentscheduler;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class CourseDetailsActivity extends AppCompatActivity {
    private DatabaseManager DB;
    private String cid;
    private String chosenType;
    private RadioGroup rgViewSelect;
    private RadioButton rdAssessments;
    private RadioButton rdInstructors;
    private RadioButton rdNotes;
    private RecyclerView rvAIN;
    private FloatingActionButton fab;
    private Integer selectedRV = (R.id.rd_assessments);
    private TextView noItems;

    // Assessment views
    private LinearLayout llAddAssessment;
    private Button btnShowAssessment;
    private Button btnAddAssessment;
    private Button btnAddAssessmentCancel;
    private EditText etTitle;
    private TextView sDate, eDate;
    private RadioGroup rgType;
    private ArrayList<Assessment> assessments;
    private AssessmentAdapter assessmentAdapter;

    // Instructor views
    private LinearLayout llAddInstructor;
    private Button btnShowInstructor;
    private Button btnAddInstructor;
    private Button btnAddInstructorCancel;
    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private ArrayList<Instructor> instructors;
    private InstructorAdapter instructorAdapter;

    // Instructor views
    private LinearLayout llAddNote;
    private Button btnShowNote;
    private Button btnAddNote;
    private Button btnAddNoteCancel;
    private EditText etNote;
    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;
    private SwitchCompat swStartDate;
    private SwitchCompat swEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        cid = getIntent().getStringExtra("CID");

        rgViewSelect = findViewById(R.id.rg_group_view);
        rvAIN = findViewById(R.id.rv_ain);
        rdAssessments = findViewById(R.id.rd_assessments);
        rdInstructors = findViewById(R.id.rd_instructors);
        rdNotes = findViewById(R.id.rd_notes);
        fab = findViewById(R.id.fab_add_ain);
        noItems = findViewById(R.id.no_items);
        swStartDate = findViewById(R.id.sw_alert_start);
        swEndDate = findViewById(R.id.sw_alert_end);

        // Assessment
        btnShowAssessment = findViewById(R.id.btn_showform_assessment);
        btnAddAssessment = findViewById(R.id.btn_add_assessment);
        btnAddAssessmentCancel = findViewById(R.id.btn_cancel_assessment);
        etTitle = findViewById(R.id.et_title);
        sDate = findViewById(R.id.tv_start_date);
        eDate = findViewById(R.id.tv_end_date);
        rgType = findViewById(R.id.rg_group_type);
        llAddAssessment = findViewById(R.id.lyt_add_assessment);

        llAddAssessment.setVisibility(View.GONE);
        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(this, assessments);

        btnShowAssessment.setOnClickListener(v -> {
            llAddAssessment.setVisibility(View.VISIBLE);
        });
        btnAddAssessmentCancel.setOnClickListener(v -> llAddAssessment.setVisibility(View.GONE));

        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();

        btnAddAssessment.setOnClickListener(v -> {
            DB = new DatabaseManager(this);
            String _title = String.valueOf(etTitle.getText());
            String _sdate = String.valueOf(sDate.getText());
            String _edate = String.valueOf(eDate.getText());
            int _salert = swStartDate.isChecked() ? 1 : 0;
            int _ealert = swEndDate.isChecked() ? 1 : 0;
            if (DB.insertIntoAssessment(_title, _sdate, _edate, chosenType, _salert, _ealert)) {
                String _aid = DB.getLastInsertedRowID(DatabaseManager.TABLE_ASSESSMENTS);
                if (DB.insertIntoCourseAssessments(cid, _aid)) {
                    assessments.add(new Assessment(_aid, _title, _sdate, _edate, chosenType, _salert, _ealert));
                    assessmentAdapter.notifyItemInserted(assessments.size());
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    if (swStartDate.isChecked()) {
                        int nid = Integer.parseInt(_aid) + 40000;
                        intent.putExtra("title", "Assessment Started: " + _title);
                        intent.putExtra("nid", nid);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        DateUtils.scheduleAlarm(this, pendingIntent, sdi);
                    }
                    if (swEndDate.isChecked()) {
                        int nid = Integer.parseInt(_aid) + 50000;
                        intent.putExtra("title", "Assessment Ended: " + _title);
                        intent.putExtra("nid", nid);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        DateUtils.scheduleAlarm(this, pendingIntent, edi);
                    }
                    showToast("Assessment Added");
                } else {
                    DB.deleteAssessment(_aid);
                    showToast("Failed");
                }
            } else showToast("Failed");
            DB.close();
            showFAB(true, llAddAssessment);
            ExtraUtils.updateNoItemsMessage(assessments, noItems);
            swStartDate.setChecked(false);
            swEndDate.setChecked(false);
        });

        rgType.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            chosenType = String.valueOf(rb.getText());
        });

        // Instructor
        btnShowInstructor = findViewById(R.id.btn_showform_instructor);
        btnAddInstructor = findViewById(R.id.btn_add_instructor);
        btnAddInstructorCancel = findViewById(R.id.btn_cancel_instructor);
        etName = findViewById(R.id.et_iname);
        etPhone = findViewById(R.id.et_iphone);
        etEmail = findViewById(R.id.et_iemail);
        llAddInstructor = findViewById(R.id.lyt_add_instructor);
        llAddInstructor.setVisibility(View.GONE);
        instructors = new ArrayList<>();
        instructorAdapter = new InstructorAdapter(this, instructors);

        btnShowInstructor.setOnClickListener(v -> showFAB(false, llAddInstructor));
        btnAddInstructorCancel.setOnClickListener(v -> showFAB(true, llAddInstructor));

        btnAddInstructor.setOnClickListener(v -> {
            DB = new DatabaseManager(this);
            String _name = String.valueOf(etName.getText());
            String _phone = String.valueOf(etPhone.getText());
            String _email = String.valueOf(etEmail.getText());
            if (DB.insertIntoInstructor(_name, _phone, _email)) {
                String _iid = DB.getLastInsertedRowID(DatabaseManager.TABLE_INSTRUCTOR);
                if (DB.insertIntoCourseInstructors(cid, _iid)) {
                    instructors.add(new Instructor(_iid, _name, _phone, _email));
                    instructorAdapter.notifyItemInserted(instructors.size());
                    showToast("Success");
                } else {
                    DB.deleteInstructor(_iid);
                    showToast("Failed");
                }
            } else showToast("Failed");
            DB.close();
            showFAB(true, llAddInstructor);
            ExtraUtils.updateNoItemsMessage(instructors, noItems);
        });

        // Notes
        llAddNote = findViewById(R.id.lyt_add_note);
        btnShowNote = findViewById(R.id.btn_showform_notes);
        btnAddNote = findViewById(R.id.btn_add_note);
        btnAddNoteCancel = findViewById(R.id.btn_cancel_note);
        etNote = findViewById(R.id.et_note);

        notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, notes);

        btnShowNote.setOnClickListener(v -> showFAB(false, llAddNote));
        btnAddNoteCancel.setOnClickListener(v -> showFAB(true, llAddNote));

        btnAddNote.setOnClickListener(v -> {
            DB = new DatabaseManager(this);
            String _note = String.valueOf(etNote.getText());
            if (DB.insertIntoNote(_note)) {
                String _nid = DB.getLastInsertedRowID(DatabaseManager.TABLE_NOTES);
                if (DB.insertIntoCourseNotes(cid, _nid)) {
                    notes.add(new Note(_nid, _note));
                    noteAdapter.notifyItemInserted(notes.size());
                    showToast("Success");
                } else {
                    DB.deleteInstructor(_nid);
                    showToast("Failed");
                }
            } else showToast("Failed");
            DB.close();
            showFAB(true, llAddNote);
            ExtraUtils.updateNoItemsMessage(notes, noItems);
        });

        rvAIN.setHasFixedSize(true);
        rvAIN.setLayoutManager(new LinearLayoutManager(this));
        rvAIN.setAdapter(assessmentAdapter);

        rgViewSelect.setOnCheckedChangeListener((radioGroup, i) -> {
            selectedRV = i;
            rdAssessments.setChecked(false);
            rdInstructors.setChecked(false);
            rdNotes.setChecked(false);
            switch (i) {
                case (R.id.rd_assessments):
                    rvAIN.setAdapter(assessmentAdapter);
                    rdAssessments.setChecked(true);
                    ExtraUtils.updateNoItemsMessage(assessments, noItems);
                    break;
                case (R.id.rd_instructors):
                    rvAIN.setAdapter(instructorAdapter);
                    rdInstructors.setChecked(true);
                    ExtraUtils.updateNoItemsMessage(instructors, noItems);
                    break;
                case (R.id.rd_notes):
                    rvAIN.setAdapter(noteAdapter);
                    rdNotes.setChecked(true);
                    ExtraUtils.updateNoItemsMessage(notes, noItems);
                    break;
                default:
                    showToast("Error!");
                    break;
            }
        });

        fab.setOnClickListener(v -> {
            switch (selectedRV) {
                case (R.id.rd_assessments):
                    llAddAssessment.setVisibility(View.VISIBLE);
                    break;
                case (R.id.rd_instructors):
                    llAddInstructor.setVisibility(View.VISIBLE);
                    break;
                case (R.id.rd_notes):
                    llAddNote.setVisibility(View.VISIBLE);
                    break;
            }
            fab.setVisibility(View.GONE);
        });
        sDate.setOnClickListener(v -> DateUtils.chooseDate(this, sDate, sdi));
        eDate.setOnClickListener(v -> DateUtils.chooseDate(this, eDate, edi));
        loadAssessments();
        loadInstructors();
        loadNotes();
        ExtraUtils.updateNoItemsMessage(assessments, noItems);
        swStartDate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, sdi, swStartDate);
        });
        swEndDate.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, edi, swEndDate);
        });
    }

    public void loadAssessments() {
        DB = new DatabaseManager(this);
        String query = "Select * From " + DatabaseManager.TABLE_ASSESSMENTS;
        Cursor assessmentData = DB.runQuery(query);
        if (assessmentData != null) {
            if (assessmentData.moveToFirst()) {
                String _aid, _title, _sdate, _edate, _type;
                int _salert, _ealert;
                int idIndex = assessmentData.getColumnIndex("AID");
                int titleIndex = assessmentData.getColumnIndex("Title");
                int sdateIndex = assessmentData.getColumnIndex("StartDate");
                int edateIndex = assessmentData.getColumnIndex("EndDate");
                int typeIndex = assessmentData.getColumnIndex("Type");
                int salertIndex = assessmentData.getColumnIndex("StartAlert");
                int ealertIndex = assessmentData.getColumnIndex("EndAlert");

                do {
                    _aid = _title = _sdate = _edate = _type = "None";
                    _salert = _ealert = 0;
                    if (idIndex != -1)
                        _aid = assessmentData.getString(idIndex);
                    if (titleIndex != -1)
                        _title = assessmentData.getString(titleIndex);
                    if (sdateIndex != -1)
                        _sdate = assessmentData.getString(sdateIndex);
                    if (edateIndex != -1)
                        _edate = assessmentData.getString(edateIndex);
                    if (typeIndex != -1)
                        _type = assessmentData.getString(typeIndex);
                    if (salertIndex != -1)
                        _salert = assessmentData.getInt(salertIndex);
                    if (ealertIndex != -1)
                        _ealert = assessmentData.getInt(ealertIndex);
                    if (!_aid.equals("None")) {
                        assessments.add(new Assessment(_aid, _title, _sdate, _edate, _type, _salert, _ealert));
                        assessmentAdapter.notifyItemInserted(assessments.size());
                    }
                } while (assessmentData.moveToNext());
            }
            assessmentData.close();
        }
        DB.close();
    }

    public void loadInstructors() {
        DB = new DatabaseManager(this);
        String query = "Select * From " + DatabaseManager.TABLE_INSTRUCTOR;
        Cursor instructorData = DB.runQuery(query);
        if (instructorData != null) {
            if (instructorData.moveToFirst()) {
                String _iid;
                String _name;
                String _phone;
                String _email;
                int idIndex = instructorData.getColumnIndex("IID");
                int nameIndex = instructorData.getColumnIndex("FullName");
                int phoneIndex = instructorData.getColumnIndex("Phone");
                int emailIndex = instructorData.getColumnIndex("Email");
                do {
                    _iid = _name = _phone = _email = "None";
                    if (idIndex != -1)
                        _iid = instructorData.getString(idIndex);
                    if (nameIndex != -1)
                        _name = instructorData.getString(nameIndex);
                    if (phoneIndex != -1)
                        _phone = instructorData.getString(phoneIndex);
                    if (emailIndex != -1)
                        _email = instructorData.getString(emailIndex);
                    if (!_iid.equals("None")) {
                        instructors.add(new Instructor(_iid, _name, _phone, _email));
                        instructorAdapter.notifyItemInserted(instructors.size());
                    }
                } while (instructorData.moveToNext());
            }
            instructorData.close();
        }
        DB.close();
    }

    public void loadNotes() {
        DB = new DatabaseManager(this);
        String query = "Select * From " + DatabaseManager.TABLE_NOTES;
        Cursor noteData = DB.runQuery(query);
        if (noteData != null) {
            if (noteData.moveToFirst()) {
                String _nid;
                String _note;
                int idIndex = noteData.getColumnIndex("NID");
                int noteIndex = noteData.getColumnIndex("Note");
                do {
                    _nid = _note = "None";
                    if (idIndex != -1)
                        _nid = noteData.getString(idIndex);
                    if (noteIndex != -1)
                        _note = noteData.getString(noteIndex);
                    if (!_nid.equals("None")) {
                        notes.add(new Note(_nid, _note));
                        noteAdapter.notifyItemInserted(notes.size());
                    }
                } while (noteData.moveToNext());
            }
            noteData.close();
        }
        DB.close();
    }

    private void showFAB(boolean isVisible, LinearLayout ll) {
        if (isVisible) {
            fab.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
        }
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}