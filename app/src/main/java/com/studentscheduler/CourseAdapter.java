package com.studentscheduler;

import static com.studentscheduler.DateUtils.cancelAlarm;
import static com.studentscheduler.DateUtils.chooseTime;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final Context context;
    private final ArrayList<Course> courses;

    public CourseAdapter(Context context, ArrayList<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_viewer, parent, false);
        return new CourseViewHolder(view);
    }

    private static void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.cv_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                switch (itemId) {
                    case R.id.edit_details:
                        // Handle edit action
                        return true;
                    case R.id.set_alarm:
                        // Handle delete action
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }


    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        Calendar sdi = Calendar.getInstance();
        Calendar edi = Calendar.getInstance();
        int _cid = Integer.parseInt(course.getCID());

        holder.tvID.setText(course.getCID());
        holder.etTitle.setText(course.getTitle());
        holder.tvSDate.setText(course.getSdate());
        holder.tvEDate.setText(course.getEdate());
        holder.etStatus.setText(course.getStatus());

        holder.swStartAlert.setChecked(course.getsAlert() == 1);
        holder.swEndAlert.setChecked(course.geteAlert() == 1);

        holder.btnEdit.setOnClickListener(v -> {
            if (!holder.etTitle.isEnabled()) {
                holder.etTitle.setEnabled(true);
                holder.etStatus.setEnabled(true);
                holder.etTitle.requestFocus();
                holder.btnEdit.setText("Save");
            } else {
                holder.etTitle.setEnabled(false);
                holder.etStatus.setEnabled(false);
                holder.btnEdit.setText("Edit");
                int salt = (holder.swStartAlert.isChecked()) ? 1 : 0;
                int ealt = (holder.swEndAlert.isChecked()) ? 1 : 0;
                DatabaseManager DB = new DatabaseManager(context);
                ContentValues values = new ContentValues();
                values.put("Title", String.valueOf(holder.etTitle.getText()));
                values.put("StartDate", String.valueOf(holder.tvSDate.getText()));
                values.put("EndDate", String.valueOf(holder.tvEDate.getText()));
                values.put("CourseStatus", String.valueOf(holder.etStatus.getText()));
                if (DB.updateData(course.getCID(), DatabaseManager.TABLE_COURSE, values)) {
                    if (salt == 0)
                        DateUtils.cancelAlarm(context, _cid + 20000);
                    else
                        DateUtils.scheduleAlarm(context, "Course Started: " + course.getTitle(), _cid + 20000, sdi);
                    if (ealt == 0)
                        DateUtils.cancelAlarm(context, _cid + 30000);
                    else
                        DateUtils.scheduleAlarm(context, "Course Ended: " + course.getTitle(), _cid + 30000, edi);
                    Toast.makeText(context, "Success Update!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                DB.close();
            }
        });

        holder.tvSDate.setOnClickListener(v -> {
            if (holder.etTitle.isEnabled()) {
                DateUtils.chooseDate(context, holder.tvSDate, sdi);
            }
        });

        holder.tvEDate.setOnClickListener(v -> {
            if (holder.etTitle.isEnabled()) {
                DateUtils.chooseDate(context, holder.tvEDate, edi);
            }
        });

        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseDetailsActivity.class);
            intent.putExtra("CID", course.getCID());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            DatabaseManager DB = new DatabaseManager(context);
            if (DB.deleteCourse(course.getCID())) {
                courses.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, courses.size());
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                cancelAlarm(context, _cid + 20000);
                cancelAlarm(context, _cid + 30000);
            } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            DB.close();
            ExtraUtils.updateNoItemsMessage(courses, ((CourseActivity) context).findViewById(R.id.no_items));
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
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvID;
        private final EditText etTitle;
        private final TextView tvSDate;
        private final TextView tvEDate;
        private final EditText etStatus;
        private final RelativeLayout courseView;
        private final Button btnDelete, btnDetails, btnEdit;
        private final SwitchCompat swStartAlert, swEndAlert;

        public CourseViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_id);
            etTitle = itemView.findViewById(R.id.et_title);
            tvSDate = itemView.findViewById(R.id.tv_sdate);
            tvEDate = itemView.findViewById(R.id.tv_edate);
            etStatus = itemView.findViewById(R.id.et_status);
            courseView = itemView.findViewById(R.id.course_view);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDetails = itemView.findViewById(R.id.btn_details);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            swStartAlert = itemView.findViewById(R.id.sw_alert_start);
            swEndAlert = itemView.findViewById(R.id.sw_alert_end);
        }
    }
}
