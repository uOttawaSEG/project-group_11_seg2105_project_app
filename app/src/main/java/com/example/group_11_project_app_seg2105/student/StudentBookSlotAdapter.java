package com.example.group_11_project_app_seg2105.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;

import java.util.List;

public class StudentBookSlotAdapter extends RecyclerView.Adapter<StudentBookSlotAdapter.ViewHolder> {

    public interface OnBookClickListener {
        void onBook(AvailabilitySlot slot);
    }

    private final List<AvailabilitySlot> list;
    private final OnBookClickListener listener;

    public StudentBookSlotAdapter(List<AvailabilitySlot> list, OnBookClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void setData(List<AvailabilitySlot> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_slot, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        AvailabilitySlot s = list.get(position);
        holder.txtTime.setText(s.start + " - " + s.end);
        holder.txtDate.setText(s.date);
        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) listener.onBook(s);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtDate;
        Button btnBook;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
