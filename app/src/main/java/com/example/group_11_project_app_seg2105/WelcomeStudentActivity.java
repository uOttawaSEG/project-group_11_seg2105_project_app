package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays welcome message for Students and allows logout.
 */
public class WelcomeStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_student);

        String studentEmail = getIntent().getStringExtra("email");
        if (studentEmail == null) {
            studentEmail = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("email", "student@uottawa.ca");
        }

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        Button btnSessions = findViewById(R.id.btnStudentSessions);
        if (btnSessions != null) {
            String finalStudentEmail = studentEmail;
            btnSessions.setOnClickListener(v -> {
                Intent i = new Intent(this, com.example.group_11_project_app_seg2105.student.StudentSessionsActivity.class);
                i.putExtra("email", finalStudentEmail);
                startActivity(i);
            });
        }
    }
}
