package com.example.group_11_project_app_seg2105.tutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;
import java.util.ArrayList;
import java.util.List;

public class TutorPendingSessionsAdapter extends RecyclerView.Adapter<TutorPendingSessionsAdapter.ViewHolder> {

    private final Context context;
    private final DatabaseHelper db;
    private final String tutorEmail;
    private final Runnable refreshCallback;
    private List<SessionRequest> data = new ArrayList<>();

    public TutorPendingSessionsAdapter(Context context, DatabaseHelper db, String tutorEmail, Runnable refreshCallback) {
        this.context = context;
        this.db = db;
        this.tutorEmail = tutorEmail;
        this.refreshCallback = refreshCallback;
    }

    public void setData(List<SessionRequest> list) {
        data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_session_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        SessionRequest r = data.get(position);

        h.textStudent.setText(r.studentEmail);
        h.textDate.setText(r.date);
        h.textTime.setText(r.start + " - " + r.end);

        h.btnApprove.setOnClickListener(v -> {
            db.updateSessionRequestStatus(r.id, "APPROVED");
            refreshCallback.run();
        });

        h.btnReject.setOnClickListener(v -> {
            db.updateSessionRequestStatus(r.id, "REJECTED");
            refreshCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textStudent, textDate, textTime;
        Button btnApprove, btnReject;

        ViewHolder(View v) {
            super(v);
            textStudent = v.findViewById(R.id.textStudent);
            textDate = v.findViewById(R.id.textDate);
            textTime = v.findViewById(R.id.textTime);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}
