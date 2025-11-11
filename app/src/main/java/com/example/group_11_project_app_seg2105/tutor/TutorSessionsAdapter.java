package com.example.group_11_project_app_seg2105.tutor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.SessionRequest;
import com.example.group_11_project_app_seg2105.data.StudentProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorSessionsAdapter extends RecyclerView.Adapter<TutorSessionsAdapter.ViewHolder> {

    public interface OnStudentClickListener {
        void onStudentInfoRequested(SessionRequest session);
    }

    private final List<TutorSessionPartitioner.SessionRow> data = new ArrayList<>();
    private final Map<String, StudentProfile> studentCache = new HashMap<>();
    private final OnStudentClickListener listener;

    public TutorSessionsAdapter(OnStudentClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<TutorSessionPartitioner.SessionRow> rows, Map<String, StudentProfile> profiles) {
        data.clear();
        studentCache.clear();
        if (rows != null) data.addAll(rows);
        if (profiles != null) studentCache.putAll(profiles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutor_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TutorSessionPartitioner.SessionRow row = data.get(position);
        SessionRequest session = row.session;

        StudentProfile profile = studentCache.get(session.studentEmail);
        String studentName = profile != null ? profile.getDisplayName() : session.studentEmail;
        holder.textStudent.setText(studentName);

        holder.textStatus.setText(session.status != null ? session.status : "UNKNOWN");

        String program = (profile != null && profile.program != null && !profile.program.isEmpty())
                ? profile.program : "Program not provided";
        holder.textCourse.setText(program);

        holder.textDate.setText(formatRange(session.date, session.start, session.end));

        holder.itemView.setAlpha(row.canOpenStudentInfo ? 1f : 0.6f);
        holder.itemView.setOnClickListener(row.canOpenStudentInfo && listener != null
                ? v -> listener.onStudentInfoRequested(session)
                : null);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String formatRange(String date, String start, String end) {
        if (date == null || start == null || end == null) return "";
        return date + "  " + start + " - " + end;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textStudent;
        final TextView textDate;
        final TextView textCourse;
        final TextView textStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudent = itemView.findViewById(R.id.textStudentName);
            textDate = itemView.findViewById(R.id.textSessionTime);
            textCourse = itemView.findViewById(R.id.textCourse);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}
