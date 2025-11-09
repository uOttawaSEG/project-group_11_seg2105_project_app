package com.example.group_11_project_app_seg2105.tutor;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.ViewHolder> {

    private final List<AvailabilitySlot> list = new ArrayList<>();
    private final DatabaseHelper db;
    private final String tutorEmail;
    private final Runnable onChange; // callback to refresh activity

    public AvailabilityAdapter(DatabaseHelper db, String tutorEmail, Runnable onChange) {
        this.db = db;
        this.tutorEmail = tutorEmail;
        this.onChange = onChange;
    }

    public void setData(List<AvailabilitySlot> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_availability_slot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AvailabilitySlot s = list.get(position);
        h.txtTime.setText(s.start + " - " + s.end);
        h.txtDate.setText(s.date);

        h.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Slot")
                    .setMessage("Delete " + s.start + " - " + s.end + " on " + s.date + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        boolean ok = db.deleteAvailability(s.id, tutorEmail);
                        if (ok) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            onChange.run(); // activity refresh
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtDate;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
