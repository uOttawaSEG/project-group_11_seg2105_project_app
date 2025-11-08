package com.example.group_11_project_app_seg2105.tutor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.*;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;

import java.util.Calendar;

public class TutorAvailabilityActivity extends AppCompatActivity {

    private Button btnDate, btnStart, btnEnd, btnSave;
    private TextView errorText;
    private DatabaseHelper db;

    private String selectedDateIso = null; // yyyy-MM-dd
    private int startMin = -1;
    private int endMin = -1;

    // fixed tutor for testing
    private String tutorEmail = "tutor@uottawa.ca";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_availability);

        db = new DatabaseHelper(this);

        btnDate = findViewById(R.id.buttonPickDate);
        btnStart = findViewById(R.id.buttonPickStart);
        btnEnd = findViewById(R.id.buttonPickEnd);
        btnSave = findViewById(R.id.buttonSaveSlot);
        errorText = findViewById(R.id.errorText);

        btnDate.setOnClickListener(v -> pickDate());
        btnStart.setOnClickListener(v -> pickTime(true));
        btnEnd.setOnClickListener(v -> pickTime(false));
        btnSave.setOnClickListener(v -> saveSlot());

    }

    private void pickDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // month is 0-based
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year, month, dayOfMonth, 0, 0, 0);
                    chosen.set(Calendar.MILLISECOND, 0);

                    // Validate: not in the past
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    if (chosen.before(today)) {
                        showError("Date cannot be in the past.");
                        selectedDateIso = null;
                        btnDate.setText("Select date");
                    } else {
                        clearError();
                        selectedDateIso = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        btnDate.setText(selectedDateIso);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dlg.show();

    }

    private void pickTime(boolean isStart) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = 0; // default

        TimePickerDialog dlg = new TimePickerDialog(this,
                (view, h, m) -> {
                    // Enforce 30-minute intervals
                    if(m % 30 != 0) {
                        showError("Time must be a multiple of 30 minutes.");
                        return;
                    }
                    int totalMin = h * 60 + m;
                    String label = String.format("%02d:%02d", h, m);

                    if (isStart) {
                        startMin = totalMin;
                        btnStart.setText(label);
                    } else {
                        endMin = totalMin;
                        btnEnd.setText(label);
                    }
                    clearError();
                },
                hour, minute, true);
        dlg.show();


    }

    private void saveSlot() {
        clearError();

        if(selectedDateIso == null) {
            showError("Please select a date.");
            return;
        }
        if(startMin < 0 || endMin < 0) {
            showError("Please select a start and end time.");
            return;
        }
        if(endMin <= startMin) {
            showError("End time must be after start time.");
            return;
        }

        if(startMin % 30 != 0 || endMin % 30 != 0) {
            showError("Times must be in 30-minute intervals.");
            return;
        }

        long id = db.addTutorAvailabilitySlot(tutorEmail, selectedDateIso, startMin, endMin);
        if(id == -1) {
            showError("Failed to save. Please try again.");
        } else {
            Toast.makeText(this, "Successfully saved.", Toast.LENGTH_SHORT).show();
            // reset for another entry
            btnStart.setText("Select start");
            btnEnd.setText("Select end");
            startMin = -1;
            endMin = -1;
        }

    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(TextView.VISIBLE);
    }

    private void clearError() {
        errorText.setText("");
        errorText.setVisibility(TextView.GONE);


    }
}
