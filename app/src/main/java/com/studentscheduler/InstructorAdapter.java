package com.studentscheduler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {
    private final Context context;
    private ArrayList<Instructor> instructors;

    public InstructorAdapter(Context context, ArrayList<Instructor> instructors) {
        this.context = context;
        this.instructors = instructors;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.instructor_viewer, parent, false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        Instructor instructor = instructors.get(position);

        holder.tvName.setText(instructor.getName());
        holder.tvPhone.setText(instructor.getPhone());
        holder.tvEmail.setText(instructor.getEmail());
        holder.tvID.setText(instructor.getIID());

        holder.btnDelete.setOnClickListener(v -> {
            DatabaseManager DB = new DatabaseManager(context);
            if (DB.deleteInstructor(instructor.getIID())) {
                instructors.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, instructors.size());
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            DB.close();
            ExtraUtils.updateNoItemsMessage(instructors, ((CourseDetailsActivity) context).findViewById(R.id.no_items));
        });
    }

    @Override
    public int getItemCount() {
        return instructors.size();
    }

    public static class InstructorViewHolder extends RecyclerView.ViewHolder {

        private TextView tvID;
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvEmail;
        private Button btnDelete;
        private Button btnDetails;

        public InstructorViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_id);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvEmail = itemView.findViewById(R.id.tv_email);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}