package com.example.group_11_project_app_seg2105.tutor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.PrefsUserStore;
import com.example.group_11_project_app_seg2105.data.SessionRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TutorAvailabilityActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String tutorEmail;
    private String selectedDate;
    private AvailabilityAdapter adapter;

    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_availability);

        db = new DatabaseHelper(this);

        tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null || tutorEmail.isEmpty()) {
            tutorEmail = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("email", "tutor@uottawa.ca");
        }

        PrefsUserStore prefs = new PrefsUserStore(this);

        Switch autoSwitch = findViewById(R.id.switch_auto_approve);
        autoSwitch.setChecked(prefs.getAutoApprove(tutorEmail));

        EditText editDate = findViewById(R.id.editDate);
        EditText editStart = findViewById(R.id.editStart);
        EditText editEnd = findViewById(R.id.editEnd);
        Button btnPickDate = findViewById(R.id.btnPickDate);
        Button btnAddSlot = findViewById(R.id.btnAddSlot);

        RecyclerView recycler = findViewById(R.id.recyclerSlots);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AvailabilityAdapter(
                db,
                tutorEmail,
                this::refresh,
                this::confirmDeleteSlot        // <-- NEW CALLBACK FOR SAFE DELETE
        );

        recycler.setAdapter(adapter);

        selectedDate = ymd.format(Calendar.getInstance().getTime());
        editDate.setText(selectedDate);

        // Pick date
        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (DatePicker view, int y, int m, int d) -> {
                        c.set(y, m, d);
                        selectedDate = ymd.format(c.getTime());
                        editDate.setText(selectedDate);
                        refresh();
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        // Add new slot
        btnAddSlot.setOnClickListener(v -> {
            boolean auto = autoSwitch.isChecked();
            prefs.setAutoApprove(tutorEmail, auto);

            String start = editStart.getText().toString().trim();
            String end = editEnd.getText().toString().trim();

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Enter both start and end times", Toast.LENGTH_SHORT).show();
                return;
            }

            AvailabilitySlot slot = new AvailabilitySlot(
                    0,
                    tutorEmail,
                    selectedDate,
                    start,
                    end,
                    auto
            );

            long result = db.insertAvailability(slot);

            if (result == -1) {
                Toast.makeText(this, "Slot already exists or invalid range", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Availability saved", Toast.LENGTH_SHORT).show();
                refresh();
            }
        });

        refresh();
    }

    // REFRESH THE LIST
    private void refresh() {
        if (tutorEmail == null || tutorEmail.isEmpty()) return;
        List<AvailabilitySlot> items = db.getAvailabilityForTutorOnDate(tutorEmail, selectedDate);
        adapter.setData(items);
    }

    // ================= PART 5: SAFE DELETE =================

    private void confirmDeleteSlot(int slotId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Availability")
                .setMessage("Are you sure you want to delete this availability slot?")
                .setPositiveButton("Delete", (d, w) -> attemptDeleteSlot(slotId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void attemptDeleteSlot(int slotId) {

        // 1. Get all session requests for the slot
        List<SessionRequest> requests = db.getRequestsBySlot(slotId);

        boolean hasBlockingSessions = false;

        for (SessionRequest req : requests) {
            String status = req.getStatus();
            if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("approved")) {
                hasBlockingSessions = true;
                break;
            }
        }

        // 2. Block deletion if there are booked sessions
        if (hasBlockingSessions) {

            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Slot")
                    .setMessage("This slot cannot be deleted because a student has already booked this session.")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        // 3. If no blocking bookings, delete normally
        boolean deleted = db.deleteAvailabilitySlot(slotId);

        if (deleted) {
            Toast.makeText(this, "Availability deleted", Toast.LENGTH_SHORT).show();
            refresh();
        } else {
            Toast.makeText(this, "Failed to delete slot", Toast.LENGTH_SHORT).show();
        }
    }
}
