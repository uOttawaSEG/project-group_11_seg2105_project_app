package com.example.group_11_project_app_seg2105.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.SessionRequest;

import java.util.Collections;
import java.util.List;

public class StudentSessionsAdapter extends RecyclerView.Adapter<StudentSessionsAdapter.ViewHolder> {

    private final List<SessionRequest> list;

    public StudentSessionsAdapter(List<SessionRequest> list) {
        this.list = list;
    }

    public void setData(List<SessionRequest> data) {
        list.clear();
        list.addAll(data);
        Collections.sort(list, (a, b) -> b.date.compareTo(a.date));
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

        // optional color-coding
        if ("APPROVED".equalsIgnoreCase(r.status)) {
            h.txtStatus.setTextColor(0xFF2E7D32); // green
        } else if ("PENDING".equalsIgnoreCase(r.status)) {
            h.txtStatus.setTextColor(0xFFF9A825); // amber
        } else {
            h.txtStatus.setTextColor(0xFFD32F2F); // red
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtTime, txtTutor, txtStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTutor = itemView.findViewById(R.id.txtTutor);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
