package com.studentscheduler;

import static com.studentscheduler.DateUtils.cancelAllAlarms;
import static com.studentscheduler.DateUtils.chooseDate;
import static com.studentscheduler.DateUtils.chooseTime;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class TermsActivity extends AppCompatActivity {
    private LinearLayout llAddTerm;
    private ArrayList<Term> terms;
    private TermAdapter termAdapter;
    private RecyclerView rvTerms;
    private DatabaseManager DB;
    private EditText etTitle;
    private TextView sDate, eDate, noItems;
    private Button btnAddTerm;
    private Button btnCancel;
    private ImageView fab;
    private SwitchCompat swStartDate;
    private SwitchCompat swEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        rvTerms = findViewById(R.id.rv_terms);
        etTitle = findViewById(R.id.et_title);
        sDate = findViewById(R.id.tv_start_date);
        eDate = findViewById(R.id.tv_end_date);
        noItems = findViewById(R.id.no_items);
        btnAddTerm = findViewById(R.id.btn_add_term);
        btnCancel = findViewById(R.id.btn_cancel);
        llAddTerm = findViewById(R.id.lyt_add_term);
        fab = findViewById(R.id.fab_add_term);
        swStartDate = findViewById(R.id.sw_alert_start);
        swEndDate = findViewById(R.id.sw_alert_end);

        llAddTerm.setVisibility(View.GONE);

        terms = new ArrayList<>();
        termAdapter = new TermAdapter(this, terms);

        rvTerms.setHasFixedSize(true);
        rvTerms.setLayoutManager(new LinearLayoutManager(this));
        rvTerms.setAdapter(termAdapter);

        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();

        btnAddTerm.setOnClickListener(v -> {
            DB = new DatabaseManager(this);
            String _title = String.valueOf(etTitle.getText());
            String _sdate = String.valueOf(sDate.getText());
            String _edate = String.valueOf(eDate.getText());
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
            if (DB.insertIntoTerm(_title, _sdate, _edate, _salert, _ealert)) {
                String _tid = DB.getLastInsertedRowID(DatabaseManager.TABLE_TERM);
                terms.add(new Term(_tid, _title, _sdate, _edate, _salert, _ealert));
                termAdapter.notifyItemInserted(terms.size());
                Intent intent = new Intent(this, AlarmReceiver.class);
                if (swStartDate.isChecked()) {
                    int nid = Integer.parseInt(_tid);
                    intent.putExtra("title", "Term Started: " + _title);
                    intent.putExtra("nid", nid);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    DateUtils.scheduleAlarm(this, pendingIntent, sdi);
                }
                if (swEndDate.isChecked()) {
                    int nid = Integer.parseInt(_tid) + 10000;
                    intent.putExtra("title", "Term Ended: " + _title);
                    intent.putExtra("nid", nid);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    DateUtils.scheduleAlarm(this, pendingIntent, edi);
                }
                Toast.makeText(this, "Term Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            }
            DB.close();
            ExtraUtils.updateNoItemsMessage(terms, noItems);
            llAddTerm.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            swStartDate.setChecked(false);
            swEndDate.setChecked(false);
        });

        btnCancel.setOnClickListener(v -> {
            llAddTerm.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        });

        fab.setOnClickListener(v -> {
            if (llAddTerm.getVisibility() == View.GONE) {
                fab.setVisibility(View.GONE);
                llAddTerm.setVisibility(View.VISIBLE);
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
        DatabaseManager.loadTerms(this, terms);
        termAdapter.notifyItemRangeInserted(0, terms.size());
        ExtraUtils.updateNoItemsMessage(terms, noItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_database) {
            DB = new DatabaseManager(this);
            DB.Clear();
            termAdapter.notifyItemRangeRemoved(0, terms.size());
            terms.clear();
            ExtraUtils.updateNoItemsMessage(terms, noItems);
            cancelAllAlarms(this);
        }
        return super.onOptionsItemSelected(item);
    }
}