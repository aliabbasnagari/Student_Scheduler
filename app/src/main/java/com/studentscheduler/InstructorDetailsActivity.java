package com.studentscheduler;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class InstructorDetailsActivity extends AppCompatActivity {

    private Button btnEdit;
    private EditText etName, etPhone, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView tvId = findViewById(R.id.tv_id);
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
                btnEdit.setText(getString(R.string.save));
            } else {
                etName.setEnabled(false);
                etPhone.setEnabled(false);
                etEmail.setEnabled(false);
                btnEdit.setText(getString(R.string.edit));
                DatabaseManager DB = new DatabaseManager(this);
                ContentValues values = new ContentValues();
                values.put("FullName", String.valueOf(etName.getText()));
                values.put("Email", String.valueOf(etEmail.getText()));
                values.put("Phone", String.valueOf(etPhone.getText()));
                if (instructor != null && DB.updateData(instructor.getIID(), DatabaseManager.TABLE_INSTRUCTOR, values)) {
                    Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, getString(R.string.failure), Toast.LENGTH_SHORT).show();
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