package com.example.group_11_project_app_seg2105.student;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StudentBookSessionActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String studentEmail;
    private String selectedTutorEmail;
    private String selectedDate;

    private Spinner spinnerTutors;
    private EditText editDate;
    private RecyclerView recyclerSlots;

    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private final List<User> tutorList = new ArrayList<>();
    private StudentBookSlotAdapter slotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_book_session);

        db = new DatabaseHelper(this);

        // current logged-in student
        studentEmail = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("email", "student@uottawa.ca");

        spinnerTutors = findViewById(R.id.spinnerTutors);
        editDate = findViewById(R.id.editDate);
        Button btnPickDate = findViewById(R.id.btnPickDate);
        recyclerSlots = findViewById(R.id.recyclerSlots);

        recyclerSlots.setLayoutManager(new LinearLayoutManager(this));
        slotAdapter = new StudentBookSlotAdapter(new ArrayList<>(), slot -> bookSlot(slot));
        recyclerSlots.setAdapter(slotAdapter);

        // default date = today
        selectedDate = ymd.format(Calendar.getInstance().getTime());
        editDate.setText(selectedDate);

        loadTutors();

        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (DatePicker view, int y, int m, int d) -> {
                        c.set(y, m, d);
                        selectedDate = ymd.format(c.getTime());
                        editDate.setText(selectedDate);
                        refreshSlots();
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void loadTutors() {
        tutorList.clear();
        tutorList.addAll(db.getUsersByRole("tutor"));

        if (tutorList.isEmpty()) {
            Toast.makeText(this, "No tutors available", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> labels = new ArrayList<>();
        for (User u : tutorList) {
            String name = (u.firstName != null ? u.firstName : "") + " " +
                    (u.lastName != null ? u.lastName : "");
            name = name.trim();
            if (name.isEmpty()) name = u.email;
            labels.add(name + " (" + u.email + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTutors.setAdapter(adapter);

        // default to first tutor
        selectedTutorEmail = tutorList.get(0).email;

        spinnerTutors.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       android.view.View view, int position, long id) {
                selectedTutorEmail = tutorList.get(position).email;
                refreshSlots();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ignore
            }
        });

        refreshSlots();
    }

    private void refreshSlots() {
        if (selectedTutorEmail == null || selectedTutorEmail.isEmpty() ||
                selectedDate == null || selectedDate.isEmpty()) {
            return;
        }

        List<AvailabilitySlot> slots =
                db.getAvailabilityForTutorOnDate(selectedTutorEmail, selectedDate);
        slotAdapter.setData(slots);
    }

    private void bookSlot(AvailabilitySlot slot) {
        if (studentEmail == null || studentEmail.isEmpty()) {
            Toast.makeText(this, "No logged-in student", Toast.LENGTH_SHORT).show();
            return;
        }

        // Auto-approve logic: if tutor marked slot as autoApprove
        String status = slot.autoApprove ? "APPROVED" : "PENDING";

        long result = db.insertSessionRequest(
                studentEmail,
                slot.tutorEmail,
                slot.date,
                slot.start,
                slot.end,
                status
        );

        if (result == -1) {
            Toast.makeText(this, "Failed to book session", Toast.LENGTH_SHORT).show();
        } else {
            if (slot.autoApprove) {
                Toast.makeText(this, "Session booked (auto-approved)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Session request sent (pending approval)", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
