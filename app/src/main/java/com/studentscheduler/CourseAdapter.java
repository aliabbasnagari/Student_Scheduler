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

import org.w3c.dom.Text;

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
        View view = LayoutInflater.from(context).inflate(R.layout.term_viewer, parent, false);
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
        int _cid = Integer.parseInt(course.getCID());

        holder.tvID.setText(course.getCID());
        holder.tvTitle.setText(course.getTitle());

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
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvID;
        private final TextView tvTitle;
        private final Button btnDelete, btnDetails;

        public CourseViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_id);
            tvTitle = itemView.findViewById(R.id.tv_title);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
