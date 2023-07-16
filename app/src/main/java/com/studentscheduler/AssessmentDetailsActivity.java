package com.studentscheduler;

import static com.studentscheduler.DateUtils.chooseTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

public class AssessmentDetailsActivity extends AppCompatActivity {

    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvType;
    private Button btnEdit;
    private EditText etTitle;
    private SwitchCompat swStartAlert, swEndAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView tvId = findViewById(R.id.tv_id);
        etTitle = findViewById(R.id.et_title);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        tvType = findViewById(R.id.tv_type);
        swStartAlert = findViewById(R.id.sw_alert_start);
        swEndAlert = findViewById(R.id.sw_alert_end);
        btnEdit = findViewById(R.id.btn_edit);

        String aid = getIntent().getStringExtra("AID");
        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();

        Assessment assessment = DatabaseManager.getAssessment(this, aid);
        if (assessment != null) {
            tvId.setText(assessment.getAID());
            etTitle.setText(assessment.getTitle());
            tvStartDate.setText(assessment.getsDate());
            tvEndDate.setText(assessment.geteDate());
            tvType.setText(assessment.getType());
            swStartAlert.setChecked(assessment.getsAlert() == 1);
            swEndAlert.setChecked(assessment.geteAlert() == 1);
        }

        tvType.setOnClickListener(v -> {
            String[] assessmentTypes = getResources().getStringArray(R.array.assessment_type);
            if (etTitle.isEnabled()) {
                if (String.valueOf(tvType.getText()).equals("Performance"))
                    tvType.setText(assessmentTypes[0]);
                else
                    tvType.setText(assessmentTypes[1]);
            }
        });

        tvStartDate.setOnClickListener(v -> {
            if (etTitle.isEnabled()) DateUtils.chooseDate(this, tvStartDate, sdi);
        });

        tvEndDate.setOnClickListener(v -> {
            if (etTitle.isEnabled()) DateUtils.chooseDate(this, tvEndDate, edi);
        });

        btnEdit.setOnClickListener(v -> {
            if (!etTitle.isEnabled()) {
                etTitle.setEnabled(true);
                swStartAlert.setEnabled(true);
                swEndAlert.setEnabled(true);
                etTitle.requestFocus();
                btnEdit.setText(getString(R.string.save));
            } else {
                etTitle.setEnabled(false);
                swStartAlert.setEnabled(false);
                swEndAlert.setEnabled(false);
                btnEdit.setText(getString(R.string.edit));
                DatabaseManager DB = new DatabaseManager(this);
                int salt = (swStartAlert.isChecked()) ? 1 : 0;
                int ealt = (swEndAlert.isChecked()) ? 1 : 0;
                ContentValues values = new ContentValues();
                values.put("Title", String.valueOf(etTitle.getText()));
                values.put("StartDate", String.valueOf(tvStartDate.getText()));
                values.put("EndDate", String.valueOf(tvEndDate.getText()));
                values.put("Type", String.valueOf(tvType.getText()));
                values.put("StartAlert", salt);
                values.put("EndAlert", ealt);
                if (assessment != null && DB.updateData(assessment.getAID(), DatabaseManager.TABLE_ASSESSMENTS, values)) {
                    int _aid = Integer.parseInt(aid);
                    if (salt == 0)
                        DateUtils.cancelAlarm(this, _aid + 40000);
                    else
                        DateUtils.scheduleAlarm(this, "Assessment Started: " + assessment.getTitle(), _aid + 40000, sdi);
                    if (ealt == 0)
                        DateUtils.cancelAlarm(this, _aid + 50000);
                    else
                        DateUtils.scheduleAlarm(this, "Assessment Ended: " + assessment.getTitle(), _aid + 50000, edi);
                    Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, getString(R.string.failure), Toast.LENGTH_SHORT).show();
                DB.close();
            }
        });

        swStartAlert.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, sdi, swStartAlert);
        });
        swEndAlert.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(this, edi, swEndAlert);
        });
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