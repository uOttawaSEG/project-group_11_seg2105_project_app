package com.example.group_11_project_app_seg2105.tutor;

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
    private final Runnable onChange;           // refresh callback
    private final DeleteCallback deleteCallback;  // NEW CALLBACK for safe delete

    public interface DeleteCallback {
        void onRequestDelete(int slotId);
    }

    public AvailabilityAdapter(
            DatabaseHelper db,
            String tutorEmail,
            Runnable onChange,
            DeleteCallback deleteCallback
    ) {
        this.db = db;
        this.tutorEmail = tutorEmail;
        this.onChange = onChange;
        this.deleteCallback = deleteCallback;
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

        // ========================
        // PART 5 — HANDLE DELETE THROUGH ACTIVITY
        // ========================
        h.btnDelete.setOnClickListener(v -> {
            if (deleteCallback != null) {
                deleteCallback.onRequestDelete(s.id);  // send slotId to activity
            }
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
