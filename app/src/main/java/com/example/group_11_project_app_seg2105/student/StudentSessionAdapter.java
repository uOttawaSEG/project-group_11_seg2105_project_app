package com.example.group_11_project_app_seg2105.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.SessionRequest;

import java.util.List;

public class StudentSessionAdapter extends RecyclerView.Adapter<StudentSessionAdapter.VH>{

    private List<SessionRequest> data;

    public StudentSessionAdapter(List<SessionRequest> initial){
        this.data = initial;
    }

    public void submit(List<SessionRequest> list){
        this.data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_session, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SessionRequest r = data.get(position);

        String dateTime = r.date + "   " + r.start + "–" + r.end;
        h.textDateTime.setText(dateTime);
        h.textTutor.setText("Tutor: " + r.tutorEmail);

        String status = r.status == null ? "PENDING" : r.status.toUpperCase();
        h.textStatus.setText(status);

        int colorbg;
        int colorText = ContextCompat.getColor(h.itemView.getContext(), R.color.p4_button_text);
        switch (status) {
            case "APPROVED":
                colorbg = ContextCompat.getColor(h.itemView.getContext(), R.color.p4_accent);
                break;
            case "REJECTED":
                colorbg = ContextCompat.getColor(h.itemView.getContext(), android.R.color.holo_red_dark);
                break;
            default:
                colorbg = ContextCompat.getColor(h.itemView.getContext(), R.color.p4_divider);
                break;

        }
        h.textStatus.setBackgroundColor(colorbg);
        h.textStatus.setTextColor(colorText);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView textDateTime, textStatus, textTutor;

        VH(@NonNull View v) {
            super(v);
            textDateTime = v.findViewById(R.id.textDateTime);
            textStatus = v.findViewById(R.id.textStatus);
            textTutor = v.findViewById(R.id.textTutor);
        }
    }


}
