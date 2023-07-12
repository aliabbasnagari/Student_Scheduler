package com.studentscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class InstructorDetailsActivity extends AppCompatActivity {

    private TextView tvId;
    private Button btnEdit;
    private EditText etName, etPhone, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tvId = findViewById(R.id.tv_id);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        btnEdit = findViewById(R.id.btn_edit);

        String iid = getIntent().getStringExtra("IID");
        Instructor instructor = DatabaseManager.getInstructor(this, iid);
        if (instructor != null) {
            tvId.setText(instructor.getIID());
            etName.setText(instructor.getName());
            etPhone.setText(instructor.getPhone());
            etEmail.setText(instructor.getEmail());
        }

        btnEdit.setOnClickListener(v -> {
            if (!etName.isEnabled()) {
                etName.setEnabled(true);
                etPhone.setEnabled(true);
                etEmail.setEnabled(true);
                etName.requestFocus();
                btnEdit.setText("Save");
            } else {
                etName.setEnabled(false);
                etPhone.setEnabled(false);
                etEmail.setEnabled(false);
                btnEdit.setText("Edit");
                DatabaseManager DB = new DatabaseManager(this);
                ContentValues values = new ContentValues();
                values.put("FullName", String.valueOf(etName.getText()));
                values.put("Email", String.valueOf(etEmail.getText()));
                values.put("Phone", String.valueOf(etPhone.getText()));
                if (instructor != null && DB.updateData(instructor.getIID(), DatabaseManager.TABLE_INSTRUCTOR, values)) {
                    Toast.makeText(this, "Success Update!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                DB.close();
            }
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