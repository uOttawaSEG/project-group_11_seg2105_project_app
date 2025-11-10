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
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;
import com.example.group_11_project_app_seg2105.data.RegistrationStatus;
import com.example.group_11_project_app_seg2105.data.SQLiteRegistrationRequestRepository;

import java.util.ArrayList;
import java.util.List;

public class TutorPendingRequestsAdapter extends RecyclerView.Adapter<TutorPendingRequestsAdapter.ViewHolder> {

    private final Context context;
    private final SQLiteRegistrationRequestRepository repo;
    private final String tutorEmail;
    private final Runnable refreshCallback;
    private List<RegistrationRequest> data = new ArrayList<>();

    public TutorPendingRequestsAdapter(Context context,
                                       SQLiteRegistrationRequestRepository repo,
                                       String tutorEmail,
                                       Runnable refreshCallback) {
        this.context = context;
        this.repo = repo;
        this.tutorEmail = tutorEmail;
        this.refreshCallback = refreshCallback;
    }

    public void setData(List<RegistrationRequest> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TutorPendingRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorPendingRequestsAdapter.ViewHolder holder, int position) {
        RegistrationRequest item = data.get(position);

        String name = (item.firstName != null ? item.firstName : "") + " " +
                (item.lastName != null ? item.lastName : "");

        holder.textName.setText(name.trim());
        holder.textEmail.setText(item.email);
        holder.textDegree.setText(item.role != null ? item.role : "");

        holder.btnApprove.setOnClickListener(v -> {
            repo.updateStatus(item.id, RegistrationStatus.APPROVED, tutorEmail, null);
            refreshCallback.run();
        });

        holder.btnReject.setOnClickListener(v -> {
            repo.updateStatus(item.id, RegistrationStatus.REJECTED, tutorEmail, "Rejected by tutor");
            refreshCallback.run();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textEmail, textDegree;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textDegree = itemView.findViewById(R.id.textDegree);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
