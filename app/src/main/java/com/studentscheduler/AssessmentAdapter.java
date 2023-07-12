package com.studentscheduler;

import static com.studentscheduler.DateUtils.cancelAlarm;
import static com.studentscheduler.DateUtils.chooseTime;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {
    private final Context context;
    private ArrayList<Assessment> assessments;

    public AssessmentAdapter(Context context, ArrayList<Assessment> terms) {
        this.context = context;
        this.assessments = terms;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assessment_viewer, parent, false);
        return new AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();
        int _aid = Integer.parseInt(assessment.getAID());

        holder.etTitle.setText(assessment.getTitle());
        holder.tvSDate.setText(assessment.getsDate());
        holder.tvEDate.setText(assessment.geteDate());
        holder.tvType.setText(assessment.getType());
        holder.tvID.setText(assessment.getAID());

        holder.swStartAlert.setChecked(assessment.getsAlert() == 1);
        holder.swEndAlert.setChecked(assessment.geteAlert() == 1);

        holder.btnDelete.setOnClickListener(v -> {
            DatabaseManager DB = new DatabaseManager(context);
            if (DB.deleteAssessment(assessment.getAID())) {
                assessments.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, assessments.size());
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                cancelAlarm(context, _aid + 40000);
                cancelAlarm(context, _aid + 50000);
            } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            DB.close();
            ExtraUtils.updateNoItemsMessage(assessments, ((CourseDetailsActivity) context).findViewById(R.id.no_items));
        });

        holder.tvType.setOnClickListener(v -> {
            if (holder.etTitle.isEnabled()) {
                if (String.valueOf(holder.tvType.getText()).equals("Performance"))
                    holder.tvType.setText("Objective");
                else
                    holder.tvType.setText("Performance");
            }
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
                values.put("Type", String.valueOf(holder.tvType.getText()));
                values.put("StartAlert", salt);
                values.put("EndAlert", ealt);
                if (DB.updateData(assessment.getAID(), DatabaseManager.TABLE_ASSESSMENTS, values)) {
                    if (salt == 0)
                        DateUtils.cancelAlarm(context, _aid + 40000);
                    else
                        DateUtils.scheduleAlarm(context, "Assessment Started: " + assessment.getTitle(), _aid + 40000, sdi);
                    if (ealt == 0)
                        DateUtils.cancelAlarm(context, _aid + 50000);
                    else
                        DateUtils.scheduleAlarm(context, "Assessment Ended: " + assessment.getTitle(), _aid + 50000, edi);
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
        return assessments.size();
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvID;
        private final EditText etTitle;
        private final TextView tvSDate;
        private final TextView tvEDate;
        private final TextView tvType;
        private final RelativeLayout termView;
        private final Button btnDelete, btnEdit;
        private final SwitchCompat swStartAlert, swEndAlert;

        public AssessmentViewHolder(View itemView) {
            super(itemView);
            termView = itemView.findViewById(R.id.term_view);
            tvID = itemView.findViewById(R.id.tv_id);
            etTitle = itemView.findViewById(R.id.et_title);
            tvSDate = itemView.findViewById(R.id.tv_sdate);
            tvEDate = itemView.findViewById(R.id.tv_edate);
            tvType = itemView.findViewById(R.id.tv_type);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            swStartAlert = itemView.findViewById(R.id.sw_alert_start);
            swEndAlert = itemView.findViewById(R.id.sw_alert_end);
        }
    }
}
