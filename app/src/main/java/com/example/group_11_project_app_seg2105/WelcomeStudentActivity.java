package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.student.StudentSessionActivity;

/**
 * Displays welcome message for Students and allows logout.
 */
public class WelcomeStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_student);

        // Retrieve the student's email from the Intent or SharedPreferences
        String studentEmail = getIntent().getStringExtra("email");
        if (studentEmail == null) {
            studentEmail = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("email", "student@uottawa.ca");
        }

        // Initialize the buttons
        Button btnMySessions = findViewById(R.id.btnMySessions);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        Button btnSessions = findViewById(R.id.btnStudentSessions);

        // Set up the 'My Sessions' button to navigate to the StudentSessionActivity
        btnMySessions.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentSessionActivity.class));
        });

        // Set up the 'Logout' button to navigate back to the LoginActivity
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Set up the 'Student Sessions' button to navigate to StudentSessionsActivity and pass the student's email
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
