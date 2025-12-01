package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Welcome screen for students. Displays a greeting and provides options to
 * search for tutoring sessions or log out. This activity replaces the
 * placeholder provided in the original project template.
 */
public class WelcomeStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_student);

        Button bookBtn = findViewById(R.id.bookBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        // Navigate to the search screen when the user wants to book a session
        bookBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        // Return to the login screen when logging out
        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}