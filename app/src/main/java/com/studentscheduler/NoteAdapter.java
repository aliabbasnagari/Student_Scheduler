package com.studentscheduler;

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

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private final Context context;
    private ArrayList<Note> notes;

    public NoteAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_viewer, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.tvNote.setText(note.getNote());
        holder.tvID.setText(note.getNID());

        holder.btnDelete.setOnClickListener(v -> {
            DatabaseManager DB = new DatabaseManager(context);
            if (DB.deleteNote(note.getNID())) {
                notes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notes.size());
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            DB.close();
            ExtraUtils.updateNoItemsMessage(notes, ((CourseDetailsActivity) context).findViewById(R.id.no_items));
        });

        holder.btnShare.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Note " + note.getNID());
            emailIntent.putExtra(Intent.EXTRA_TEXT, note.getNote());
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView tvID;
        private TextView tvNote;
        private Button btnDelete;
        private Button btnShare;

        public NoteViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_id);
            tvNote = itemView.findViewById(R.id.tv_note);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnShare = itemView.findViewById(R.id.btn_share);
        }
    }
}
