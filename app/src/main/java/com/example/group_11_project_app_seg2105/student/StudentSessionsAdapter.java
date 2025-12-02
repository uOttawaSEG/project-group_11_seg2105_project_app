package com.example.group_11_project_app_seg2105.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper.CancelResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentSessionsAdapter extends RecyclerView.Adapter<StudentSessionsAdapter.ViewHolder> {

    private final Context context;
    private final DatabaseHelper db;
    private final Runnable refreshCallback;
    private final List<SessionRequest> list;

    public StudentSessionsAdapter(Context context, DatabaseHelper db, Runnable refreshCallback) {
        this.context = context;
        this.db = db;
        this.refreshCallback = refreshCallback;
        this.list = new ArrayList<>();
    }

    public void setData(List<SessionRequest> data) {
        list.clear();
        list.addAll(data);
        Collections.sort(list, (a, b) -> b.date.compareTo(a.date)); // sort sessions by date
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_session, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder h,
            int position
    ) {
        SessionRequest r = list.get(position);

        h.txtDate.setText(r.date);
        h.txtTime.setText(r.start + " - " + r.end);
        h.txtTutor.setText("Tutor: " + r.tutorEmail);
        h.txtStatus.setText(r.status);

        // optional color-coding for status
        if ("APPROVED".equalsIgnoreCase(r.status)) {
            h.txtStatus.setTextColor(0xFF2E7D32); // green
        } else if ("PENDING".equalsIgnoreCase(r.status)) {
            h.txtStatus.setTextColor(0xFFF9A825); // amber
        } else {
            h.txtStatus.setTextColor(0xFFD32F2F); // red
        }

        h.btnCancel.setOnClickListener(v -> handleCancel(r, h.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private void handleCancel(SessionRequest r, int position) {
        CancelCheck check = evaluateCancelRule(r);
        if (!check.allowed) {
            Toast.makeText(context, check.reason, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("Cancel Session")
                .setMessage("Are you sure you want to cancel this session?")
                .setPositiveButton("Yes", (d, w) -> performCancel(r, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void performCancel(SessionRequest r, int position) {
        CancelResult result = db.cancelSession(r.id, r.studentEmail);
        if (!result.success) {
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        } else {
            notifyDataSetChanged();
        }
        refreshCallback.run();
        Toast.makeText(context, "Session cancelled", Toast.LENGTH_SHORT).show();
    }

    private CancelCheck evaluateCancelRule(SessionRequest r) {
        String status = r.status != null ? r.status.toUpperCase() : "";
        if ("CANCELLED".equals(status)) return new CancelCheck(false, "Already cancelled");
        if ("REJECTED".equals(status)) return new CancelCheck(false, "Cannot cancel this session");
        if ("PENDING".equals(status)) return new CancelCheck(true, "");
        if (!"APPROVED".equals(status)) return new CancelCheck(false, "Cannot cancel this session");

        long startMillis = toStartMillis(r);
        if (startMillis <= 0) return new CancelCheck(false, "Invalid session time");

        long millisUntilStart = startMillis - System.currentTimeMillis();
        if (millisUntilStart <= 0) return new CancelCheck(false, "Session already started");
        if (millisUntilStart <= 86_400_000L) {
            return new CancelCheck(false, "Cannot cancel within 24 hours of start");
        }
        return new CancelCheck(true, "");
    }

    private long toStartMillis(SessionRequest r) {
        if (r.date == null || r.start == null) return -1;
        try {
            LocalDate d = LocalDate.parse(r.date);
            LocalTime t = LocalTime.parse(r.start);
            LocalDateTime dt = LocalDateTime.of(d, t);
            return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            return -1;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtTime, txtTutor, txtStatus;
        Button btnCancel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTutor = itemView.findViewById(R.id.txtTutor);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }

    private static final class CancelCheck {
        final boolean allowed;
        final String reason;

        CancelCheck(boolean allowed, String reason) {
            this.allowed = allowed;
            this.reason = reason;
        }
    }
}
