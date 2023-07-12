package com.studentscheduler;

import static com.studentscheduler.DateUtils.cancelAlarm;
import static com.studentscheduler.DateUtils.chooseTime;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {
    private final Context context;
    private ArrayList<Term> terms;

    public TermAdapter(Context context, ArrayList<Term> terms) {
        this.context = context;
        this.terms = terms;
    }

    @NonNull
    @Override
    public TermAdapter.TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.term_viewer, parent, false);
        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermAdapter.TermViewHolder holder, int position) {
        Term term = terms.get(position);
        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();
        int _tid = Integer.parseInt(term.getTID());

        holder.etTitle.setText(term.getTitle());
        holder.tvSDate.setText(term.getsDate());
        holder.tvEDate.setText(term.geteDate());
        holder.tvID.setText(term.getTID());

        holder.swStartAlert.setChecked(term.getsAlert() == 1);
        holder.swEndAlert.setChecked(term.geteAlert() == 1);

        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseActivity.class);
            intent.putExtra("TID", term.getTID());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            DatabaseManager DB = new DatabaseManager(context);
            if (ExtraUtils.canDeleteTerm(DB, term.getTID())) {
                if (DB.deleteTerm(term.getTID())) {
                    terms.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, terms.size());
                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                    cancelAlarm(context, _tid);
                    cancelAlarm(context, _tid + 10000);
                } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Term contains Courses!", Toast.LENGTH_SHORT).show();
            DB.close();
            ExtraUtils.updateNoItemsMessage(terms, ((TermsActivity) context).findViewById(R.id.no_items));
        });

        holder.tvSDate.setOnClickListener(v -> {
            if (holder.etTitle.isEnabled()) DateUtils.chooseDate(context, holder.tvSDate, sdi);
        });

        holder.tvEDate.setOnClickListener(v -> {
            if (holder.etTitle.isEnabled()) DateUtils.chooseDate(context, holder.tvEDate, edi);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (!holder.etTitle.isEnabled()) {
                holder.etTitle.setEnabled(true);
                holder.swStartAlert.setEnabled(true);
                holder.swEndAlert.setEnabled(true);
                holder.etTitle.requestFocus();
                holder.btnEdit.setText("Save");
            } else {
                holder.etTitle.setEnabled(false);
                holder.swStartAlert.setEnabled(false);
                holder.swEndAlert.setEnabled(false);
                holder.btnEdit.setText("Edit");
                DatabaseManager DB = new DatabaseManager(context);
                int salt = (holder.swStartAlert.isChecked()) ? 1 : 0;
                int ealt = (holder.swEndAlert.isChecked()) ? 1 : 0;
                ContentValues values = new ContentValues();
                values.put("Title", String.valueOf(holder.etTitle.getText()));
                values.put("StartDate", String.valueOf(holder.tvSDate.getText()));
                values.put("EndDate", String.valueOf(holder.tvEDate.getText()));
                values.put("StartAlert", salt);
                values.put("EndAlert", ealt);
                if (DB.updateData(term.getTID(), DatabaseManager.TABLE_TERM, values)) {
                    if (salt == 0)
                        DateUtils.cancelAlarm(context, _tid);
                    else
                        DateUtils.scheduleAlarm(context, "Term Started: " + term.getTitle(), _tid, sdi);
                    if (ealt == 0)
                        DateUtils.cancelAlarm(context, _tid + 10000);
                    else
                        DateUtils.scheduleAlarm(context, "Term Ended: " + term.getTitle(), _tid + 10000, edi);
                    Toast.makeText(context, "Success Update!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                DB.close();
            }
        });

        holder.swStartAlert.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(context, sdi, holder.swStartAlert);
        });
        holder.swEndAlert.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) chooseTime(context, edi, holder.swEndAlert);
        });
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvID;
        private final EditText etTitle;
        private final TextView tvSDate;
        private final TextView tvEDate;
        private final RelativeLayout termView;
        private final Button btnDelete, btnDetails, btnEdit;
        private final SwitchCompat swStartAlert, swEndAlert;

        public TermViewHolder(View itemView) {
            super(itemView);
            termView = itemView.findViewById(R.id.term_view);
            tvID = itemView.findViewById(R.id.tv_id);
            etTitle = itemView.findViewById(R.id.et_title);
            tvSDate = itemView.findViewById(R.id.tv_sdate);
            tvEDate = itemView.findViewById(R.id.tv_edate);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDetails = itemView.findViewById(R.id.btn_details);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            swStartAlert = itemView.findViewById(R.id.sw_alert_start);
            swEndAlert = itemView.findViewById(R.id.sw_alert_end);
        }
    }
}
