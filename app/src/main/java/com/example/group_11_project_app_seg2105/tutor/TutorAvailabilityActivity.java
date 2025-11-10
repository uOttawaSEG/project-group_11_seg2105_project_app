package com.example.group_11_project_app_seg2105.tutor;

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

        // Get the correct tutor email (passed from WelcomeTutorActivity)
        tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null) {
            tutorEmail = getSharedPreferences("auth", MODE_PRIVATE).getString("email", "tutor@uottawa.ca");
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
        adapter = new AvailabilityAdapter(db, tutorEmail, this::refresh);
        recycler.setAdapter(adapter);

        selectedDate = ymd.format(Calendar.getInstance().getTime());
        editDate.setText(selectedDate);

        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this, (DatePicker view, int y, int m, int d) -> {
                c.set(y, m, d);
                selectedDate = ymd.format(c.getTime());
                editDate.setText(selectedDate);
                refresh();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnAddSlot.setOnClickListener(v -> {

            // Save auto-approve state when tutor interacts
            boolean auto = autoSwitch.isChecked();
            prefs.setAutoApprove(tutorEmail, auto);

            String start = editStart.getText().toString().trim();
            String end = editEnd.getText().toString().trim();
            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Enter start and end", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = db.insertAvailability(new AvailabilitySlot(0, tutorEmail, selectedDate, start, end, false));
            if (result <= 0) Toast.makeText(this, "Invalid or overlapping slot", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                refresh();
            }
        });

        refresh();
    }

    private void refresh() {
        List<AvailabilitySlot> items = db.getAvailabilityForTutorOnDate(tutorEmail, selectedDate);
        adapter.setData(items);
    }
}
