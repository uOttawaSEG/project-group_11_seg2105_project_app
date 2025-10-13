package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.core.validation.InputValidator;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity{

    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField, phoneNumberField, programOfStudyField;
    private Button registerButton;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);


        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        programOfStudyField = findViewById(R.id.programOfStudyField);
        registerButton = findViewById(R.id.registerButton);

        // Initialize SQLite and seed admin
        db = new DatabaseHelper(this);
        db.seedAdmin();

        registerButton.setOnClickListener(this::handleRegister);

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void handleRegister(View v) {

        clearAllFieldErrors();

        String firstName = safeText(firstNameField);
        String lastName = safeText(lastNameField);
        String email = safeText(emailField);
        String password = safeText(passwordField);
        String confirmPassword = safeText(confirmPasswordField);
        String phone = safeText(phoneNumberField);
        String program = safeText(programOfStudyField);

        // Validate inputs
        List<String> errors = InputValidator.validateStudent(firstName, lastName, email, password, confirmPassword, phone, program);

        if(!errors.isEmpty()) {

            applyFieldErrors(firstNameField, errors, "First name is required");
            applyFieldErrors(lastNameField, errors, "Last name is required");
            applyFieldErrors(emailField, errors, "Invalid email");
            applyFieldErrors(passwordField, errors, "Password must be at least 6 characters");
            applyFieldErrors(confirmPasswordField, errors, "Passwords do not match");
            applyFieldErrors(phoneNumberField, errors, "Phone mist be digits only");
            applyFieldErrors(programOfStudyField, errors, "Program of study is required");

            Toast.makeText(this, bulletJoin(errors), Toast.LENGTH_LONG ).show();
            return;
        }

        if(db.getUserRole(email) != null) {
            setError(emailField, "An account with this email already exists");
            Toast.makeText(this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            db.saveUser("student", email, password);
            Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("prefill_email", email);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
        }


    }

    private static String safeText(EditText text) {
        return text == null ? "" : text.getText().toString().trim();
    }

    private static String bulletJoin(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for(String s: dedupe(lines)) {
            if(sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(". ").append(s);
        }
        return sb.toString();
    }

    // Remove duplicate messages
    private static List<String> dedupe(List<String> list) {
        return new ArrayList<>(new java.util.LinkedHashSet<>(list));
    }


    private void setError(EditText field, String message) {
        if (field == null) {
            return;
        }
        if (field.getParent() instanceof TextInputLayout) {
            ((TextInputLayout) field.getParent()).setError(message);
        } else {
            field.setError(message);
        }
    }

    private void clearError(EditText field) {
        if (field == null) return;
        if(field.getParent() instanceof TextInputLayout) {
            ((TextInputLayout) field.getParent()).setError(null);
        } else {
            field.setError(null);
        }
    }

    private void clearAllFieldErrors() {
        clearError(firstNameField);
        clearError(lastNameField);
        clearError(emailField);
        clearError(passwordField);
        clearError(confirmPasswordField);
        clearError(phoneNumberField);
        clearError(programOfStudyField);
    }

    private void applyFieldErrors(EditText field, List<String> errors, String matchesMessage) {
        for (String e : errors) {
            if(e.equalsIgnoreCase(matchesMessage)) {
                setError(field, e);
                return;
            }
        }
    }


}
