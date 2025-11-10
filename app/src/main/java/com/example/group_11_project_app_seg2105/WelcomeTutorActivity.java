package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeTutorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_tutor);

        String tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null) {
            tutorEmail = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("email", "tutor@uottawa.ca");
        }

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        Button btnPending = findViewById(R.id.btnPendingRequests);
        btnPending.setOnClickListener(v ->
                startActivity(new Intent(this, TutorPendingRequestsActivity.class))
        );

        Button btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnManageAvailability.setOnClickListener(v -> {
            Intent i = new Intent(this, com.example.group_11_project_app_seg2105.tutor.TutorAvailabilityActivity.class);
            i.putExtra("email", tutorEmail);
            startActivity(i);
        });

        // ✅ NEW BUTTON — your Part 3
        Button btnPendingSessions = findViewById(R.id.btnPendingSessions);
        btnPendingSessions.setOnClickListener(v -> {
            Intent i = new Intent(this, com.example.group_11_project_app_seg2105.tutor.TutorPendingSessionsActivity.class);
            i.putExtra("email", tutorEmail);
            startActivity(i);
        });
    }
}
