package com.example.group_11_project_app_seg2105.admin;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;

import java.util.List;

public class RejectedRequestsAdapter extends RecyclerView.Adapter<RejectedRequestsAdapter.VH> {

    public interface OnReapprove {
        void onClick(RegistrationRequest req, int position);
    }

    private List<RegistrationRequest> data;
    private final OnReapprove listener;

    public RejectedRequestsAdapter(List<RegistrationRequest> initial, OnReapprove listener) {
        this.data = initial;
        this.listener = listener;

    }
    public void submit(List<RegistrationRequest> list) {
        data = list;
        notifyDataSetChanged();

    }

    public void removeAt(int pos) {
        if (pos < 0 || pos >= data.size()) {
            return;
        }
        data.remove(pos);
        notifyItemRemoved(pos);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rejected_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {

        RegistrationRequest r = data.get(position);

        String displayName = r.getFullName();
        if(r.role!= null && !r.role.isEmpty()) {
            displayName += " (" + r.role + ")";
        }

        h.textName.setText(displayName);
        h.textEmail.setText(r.email);

        h.textReason.setText(
                (r.reason == null || r.reason.isEmpty())
                        ? "Reason: -"
                        : "Reason: " + r.reason
        );

        h.buttonReapprove.setOnClickListener(v ->
                listener.onClick(r, h.getBindingAdapterPosition()));

    }

    @Override public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textName, textEmail, textReason;
        Button buttonReapprove;

        VH(@NonNull View v) {
            super(v);
            textName = v.findViewById(R.id.textName);
            textEmail = v.findViewById(R.id.textEmail);
            textReason = v.findViewById(R.id.textReason);
            buttonReapprove = v.findViewById(R.id.buttonReapprove);

        }

    }

}
