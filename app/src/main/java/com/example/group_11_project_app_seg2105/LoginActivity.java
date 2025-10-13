package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles login for all user roles.
 * Admin credentials are hardcoded:
 * admin@uottawa.ca / admin123
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

<<<<<<< Updated upstream
        loginButton.setOnClickListener(v -> handleLogin());
=======
        // Initialize SQLite and seed admin
        db = new DatabaseHelper(this);
        db.seedAdmin();

        loginButton.setOnClickListener(this::handleLogin);

        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
        String prefill = getIntent().getStringExtra("prefill_email");
        if(prefill != null) {
            emailField.setText(prefill);
        }
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }

    private void handleLogin() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hardcoded admin account
        if (email.equals("admin@uottawa.ca") && password.equals("admin123")) {
            startActivity(new Intent(this, WelcomeAdminActivity.class));
            finish();
            return;
        }

        // Tutor accounts contain "tutor" in the address
        if (email.contains("tutor") && email.endsWith("@uottawa.ca")) {
            startActivity(new Intent(this, WelcomeTutorActivity.class));
            finish();
            return;
        }

        // Student accounts contain "student" in the address
        if (email.contains("student") && email.endsWith("@uottawa.ca")) {
            startActivity(new Intent(this, WelcomeStudentActivity.class));
            finish();
            return;
        }

        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
    }
}
