package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.core.validation.InputValidator;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.RegistrationStatus;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        db = new DatabaseHelper(this);
        db.seedAdmin();

        TextView registerLink = findViewById(R.id.registerLink);
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
                finish();
            });
        }

        String prefill = getIntent().getStringExtra("prefill_email");
        if (prefill != null) emailField.setText(prefill);

        loginButton.setOnClickListener(this::handleLogin);
    }

    private void handleLogin(View v) {
        String email = safeText(emailField);
        String password = safeText(passwordField);

        if (!InputValidator.isValidEmail(email) || !InputValidator.isValidPassword(password)) {
            Toast.makeText(this, "Invalid email or password format", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ CHECK REGISTRATION STATUS BEFORE VALIDATING LOGIN
        RegistrationStatus status = db.getRegistrationStatus(email);

        if (status == RegistrationStatus.PENDING) {
            Toast.makeText(this, "Your registration is pending admin approval.", Toast.LENGTH_LONG).show();
            return;
        }

        if (status == RegistrationStatus.REJECTED) {
            Toast.makeText(this, "Your registration was rejected. Contact admin.", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Now validate password
        if (!db.validateLogin(email, password)) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = db.getUserRole(email);
        if (role == null) {
            Toast.makeText(this, "No role found for account", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent next;
        switch (role.toLowerCase()) {
            case "admin":
                next = new Intent(this, WelcomeAdminActivity.class);
                break;

            case "tutor": // ✅ FIXED TUTOR ROLE
                next = new Intent(this, WelcomeTutorActivity.class);
                next.putExtra("email", email);
                break;

            case "student":
                next = new Intent(this, WelcomeStudentActivity.class);
                next.putExtra("email", email);
                break;

            default:
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(next);
        finish();
    }

    private static String safeText(EditText field) {
        return field == null ? "" : field.getText().toString().trim();
    }
}
