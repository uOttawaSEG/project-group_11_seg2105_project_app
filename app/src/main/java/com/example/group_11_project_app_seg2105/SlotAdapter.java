package com.example.group_11_project_app_seg2105;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;
import com.example.group_11_project_app_seg2105.data.DatabaseContract;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SQLiteSessionRequestRepository;

import java.util.List;

/**
 * RecyclerView adapter used to display a list of available tutor slots. Each item
 * shows the tutor's name, average rating, course code, date, time range and
 * auto-approval flag. Clicking an item will prompt the student to confirm
 * booking and, if confirmed, insert a session request and remove the slot
 * from the adapter's data set.
 */
public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {

    private final Context context;
    private final DatabaseHelper helper;
    private final SQLiteSessionRequestRepository sessionRepo;
    private final String studentEmail;
    private List<AvailabilitySlot> slots;
    private String courseCode;

    public SlotAdapter(Context context,
                       List<AvailabilitySlot> slots,
                       DatabaseHelper helper,
                       SQLiteSessionRequestRepository sessionRepo,
                       String studentEmail) {
        this.context = context;
        this.slots = slots;
        this.helper = helper;
        this.sessionRepo = sessionRepo;
        this.studentEmail = studentEmail;
        this.courseCode = "";
    }

    public void setCourseCode(String code) {
        this.courseCode = code;
    }

    public void updateData(List<AvailabilitySlot> newSlots) {
        this.slots.clear();
        this.slots.addAll(newSlots);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AvailabilitySlot slot = slots.get(position);
        String tutorName = getTutorName(slot.tutorEmail);
        double rating = getTutorRating(slot.tutorEmail);
        String ratingStr = String.format("%.1f", rating);
        holder.tutorNameRating.setText(tutorName + " (Rating: " + ratingStr + ")");
        String auto = slot.autoApprove ? "Yes" : "No";
        String details = "Course: " + courseCode +
                " | Date: " + slot.date +
                " | Time: " + slot.start + "-" + slot.end +
                " | Auto: " + auto;
        holder.slotDetails.setText(details);

        holder.itemView.setOnClickListener(v -> {
            // Confirmation dialog before booking
            new AlertDialog.Builder(context)
                    .setTitle("Book this session?")
                    .setMessage("Do you want to request/book this time slot?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String status = slot.autoApprove ? "APPROVED" : "PENDING";
                        // Insert session request
                        sessionRepo.create(
                                studentEmail,
                                slot.tutorEmail,
                                slot.id,
                                slot.date,
                                slot.start,
                                slot.end,
                                status,
                                null
                        );
                        // Remove from list and notify adapter
                        int index = holder.getAdapterPosition();
                        slots.remove(index);
                        notifyItemRemoved(index);
                        Toast.makeText(context, "Session requested.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    /**
     * Look up the full name of a tutor from the tutor_profiles table using
     * their email. If no profile exists, the email is returned as a fallback.
     */
    private String getTutorName(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String name = email;
        try (Cursor cursor = db.query(
                DatabaseContract.TutorProfiles.TABLE,
                new String[]{ DatabaseContract.TutorProfiles.FIRST, DatabaseContract.TutorProfiles.LAST },
                DatabaseContract.TutorProfiles.EMAIL + " = ?",
                new String[]{ email }, null, null, null
        )) {
            if (cursor.moveToFirst()) {
                String first = cursor.getString(0);
                String last = cursor.getString(1);
                name = first + " " + last;
            }
        }
        return name;
    }

    /**
     * Retrieve the average rating for a tutor from the tutor_profiles table. If
     * no rating is recorded, zero is returned.
     */
    private double getTutorRating(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        double rating = 0.0;
        try (Cursor cursor = db.query(
                DatabaseContract.TutorProfiles.TABLE,
                new String[]{ DatabaseContract.TutorProfiles.RATING },
                DatabaseContract.TutorProfiles.EMAIL + " = ?",
                new String[]{ email }, null, null, null
        )) {
            if (cursor.moveToFirst()) {
                rating = cursor.getDouble(0);
            }
        }
        return rating;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tutorNameRating;
        TextView slotDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorNameRating = itemView.findViewById(R.id.tutorNameRating);
            slotDetails = itemView.findViewById(R.id.slotDetails);
        }
    }
}