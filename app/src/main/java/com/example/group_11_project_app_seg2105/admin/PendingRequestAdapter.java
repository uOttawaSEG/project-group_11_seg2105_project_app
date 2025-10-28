package com.example.group_11_project_app_seg2105.admin;

import android.app.AlertDialog;
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
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;

import java.util.ArrayList;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.ViewHolder> {

    private final ArrayList<RegistrationRequest> list;
    private final DatabaseHelper db;
    private final Context context;
    private final OnStatusChangeListener listener;

    // Callback interface for email notification
    public interface OnStatusChangeListener {
        void onStatusChange(String email, String newStatus);
    }

    // Updated constructor with listener
    public PendingRequestAdapter(ArrayList<RegistrationRequest> list, DatabaseHelper db, Context context, OnStatusChangeListener listener) {
        this.list = list;
        this.db = db;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistrationRequest r = list.get(position);

        holder.textName.setText(r.firstName + " " + r.lastName);
        holder.textEmail.setText(r.email);

        holder.btnApprove.setOnClickListener(v -> {
            db.updateRequestStatus(r.email, "APPROVED");
            showDialog("Approved " + r.email);
            listener.onStatusChange(r.email, "APPROVED");
            list.remove(position);
            notifyItemRemoved(position);
        });

        holder.btnReject.setOnClickListener(v -> {
            db.updateRequestStatus(r.email, "REJECTED");
            showDialog("Rejected " + r.email);
            listener.onStatusChange(r.email, "REJECTED");
            list.remove(position);
            notifyItemRemoved(position);
        });
    }

    private void showDialog(String msg) {
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textEmail;
        Button btnApprove, btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
