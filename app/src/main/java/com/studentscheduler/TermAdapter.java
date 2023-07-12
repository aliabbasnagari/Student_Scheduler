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
        int _tid = Integer.parseInt(term.getTID());

        holder.tvID.setText(term.getTID());
        holder.tvTitle.setText(term.getTitle());

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
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvID, tvTitle;
        private final Button btnDelete, btnDetails;

        public TermViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_id);
            tvTitle = itemView.findViewById(R.id.tv_title);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
