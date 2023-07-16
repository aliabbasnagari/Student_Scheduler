package com.studentscheduler;

import static com.studentscheduler.DateUtils.cancelAlarm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {
    private final Context context;
    private final ArrayList<Assessment> assessments;

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
        int _aid = Integer.parseInt(assessment.getAID());

        holder.tvID.setText(assessment.getAID());
        holder.tvTitle.setText(assessment.getTitle());

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

        holder.btnDetails.setOnClickListener(v -> context.startActivity(new Intent(context, AssessmentDetailsActivity.class).putExtra("AID", assessment.getAID())));
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvID;
        private final TextView tvTitle;
        private final Button btnDelete, btnDetails;

        public AssessmentViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_id_a);
            tvTitle = itemView.findViewById(R.id.tv_title_a);
            btnDelete = itemView.findViewById(R.id.btn_delete_a);
            btnDetails = itemView.findViewById(R.id.btn_details_a);
        }
    }
}
