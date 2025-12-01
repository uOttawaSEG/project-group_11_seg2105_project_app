package com.example.group_11_project_app_seg2105;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;
import com.example.group_11_project_app_seg2105.data.DatabaseContract;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SQLiteAvailabilityRepository;
import com.example.group_11_project_app_seg2105.data.SQLiteSessionRequestRepository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity allowing students to search for available tutor sessions by course code.
 * Students enter a course code, tap the search button, and see a list of all
 * unbooked availability slots for tutors who teach that course. Selecting a slot
 * requests the session and removes it from the list. A simple toast notifies
 * the user of the booking action.
 */
public class SearchActivity extends AppCompatActivity {

    private DatabaseHelper helper;
    private SQLiteAvailabilityRepository availRepo;
    private SQLiteSessionRequestRepository sessionRepo;
    private RecyclerView recyclerView;
    private SlotAdapter adapter;
    private String studentEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        helper = new DatabaseHelper(this);
        availRepo = new SQLiteAvailabilityRepository(helper);
        sessionRepo = new SQLiteSessionRequestRepository(helper);

        // In a real app the current user's email would come from a logged-in session.
        // For demonstration we default to the seeded student account.
        studentEmail = "student@uottawa.ca";

        EditText input = findViewById(R.id.searchInput);
        Button searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.slotsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SlotAdapter(this, new ArrayList<>(), helper, sessionRepo, studentEmail);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String course = input.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(course)) {
                Toast.makeText(SearchActivity.this, "Enter a course code", Toast.LENGTH_SHORT).show();
                return;
            }
            List<AvailabilitySlot> results = findAvailableSlotsByCourse(course);
            adapter.setCourseCode(course);
            adapter.updateData(results);
            if (results.isEmpty()) {
                Toast.makeText(SearchActivity.this, "No available slots for " + course, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Query the database for all available slots for tutors who teach the given course.
     * A slot is considered available if it is not currently booked (pending or approved)
     * and if the student does not already have a conflicting request on the same date.
     */
    private List<AvailabilitySlot> findAvailableSlotsByCourse(String course) {
        List<AvailabilitySlot> results = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        // Query tutor_courses to find tutors teaching this course
        try (Cursor cursor = db.query(
                DatabaseContract.TutorCourses.TABLE,
                new String[]{ DatabaseContract.TutorCourses.EMAIL },
                DatabaseContract.TutorCourses.COURSE + " = ?",
                new String[]{ course },
                null, null, null
        )) {
            while (cursor.moveToNext()) {
                String tutorEmail = cursor.getString(0);
                // Fetch all availability slots for this tutor
                List<AvailabilitySlot> slots = availRepo.findByTutor(tutorEmail);
                for (AvailabilitySlot slot : slots) {
                    // Exclude slots that are already booked or overlap with student's existing requests
                    boolean booked = sessionRepo.isSlotBooked(slot.tutorEmail, slot.date, slot.start, slot.end);
                    boolean overlap = sessionRepo.hasStudentOverlap(studentEmail, slot.date, slot.start, slot.end);
                    if (!booked && !overlap) {
                        results.add(slot);
                    }
                }
            }
        }
        // Sort results by date then start time using natural ordering (already sorted per tutor)
        // Additional sorting could be applied here if necessary.
        return results;
    }
}