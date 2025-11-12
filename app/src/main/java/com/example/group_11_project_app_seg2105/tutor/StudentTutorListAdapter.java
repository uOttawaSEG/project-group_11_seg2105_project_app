package com.example.group_11_project_app_seg2105.tutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.User;

import java.util.ArrayList;
import java.util.List;

public class StudentTutorListAdapter extends RecyclerView.Adapter<StudentTutorListAdapter.VH> {
    private final LayoutInflater inflater;
    private final List<User> data;

    public StudentTutorListAdapter(@NonNull Context ctx, List<User> data) {
        this.inflater = LayoutInflater.from(ctx);
        this.data = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_simple_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        User u = data.get(pos);
        String first = u.firstName == null ? "" : u.firstName;
        String last  = u.lastName  == null ? "" : u.lastName;
        String full  = (first + " " + last).trim();

        h.name.setText(full.isEmpty() ? "(No name)" : full);
        h.email.setText(u.email == null ? "" : u.email);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        // Stable id from email to help RecyclerView
        String key = data.get(position).email;
        return key == null ? position : key.hashCode();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView email;

        VH(@NonNull View v) {
            super(v);
            name  = v.findViewById(R.id.textName);
            email = v.findViewById(R.id.textEmail);
        }
    }
}
