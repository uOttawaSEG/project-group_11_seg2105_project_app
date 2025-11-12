package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeTutorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_tutor);

        String email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            email = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("email", "tutor@uottawa.ca");
        }

        final String tutorEmail = email; // must be final for lambdas

        if (tutorEmail == null || tutorEmail.isEmpty()) {
            Toast.makeText(this, "Tutor email missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        Button btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnManageAvailability.setOnClickListener(v -> {
            Intent i = new Intent(this, com.example.group_11_project_app_seg2105.tutor.TutorAvailabilityActivity.class);
            i.putExtra("email", tutorEmail);
            startActivity(i);
        });

        Button btnPendingSessions = findViewById(R.id.btnPendingSessions);
        btnPendingSessions.setOnClickListener(v -> {
            Intent i = new Intent(this, com.example.group_11_project_app_seg2105.tutor.TutorPendingSessionsActivity.class);
            i.putExtra("email", tutorEmail);
            startActivity(i);
        });

        Button btnTutorSessions = findViewById(R.id.btnTutorSessions);
        btnTutorSessions.setOnClickListener(v -> {
            Intent i = new Intent(this, com.example.group_11_project_app_seg2105.tutor.TutorSessionsActivity.class);
            i.putExtra("email", tutorEmail);
            startActivity(i);
        });
    }
}
