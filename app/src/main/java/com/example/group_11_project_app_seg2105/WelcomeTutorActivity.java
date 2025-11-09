package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays welcome message for Tutors and allows logout.
 */
public class WelcomeTutorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_tutor);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        Button btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnManageAvailability.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeTutorActivity.this,
                    com.example.group_11_project_app_seg2105.tutor.TutorAvailabilityActivity.class);
            startActivity(i);
        });
    }
}
