package com.example.group_11_project_app_seg2105.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {
    public final String email;
    public final String password;
    public final String role;
    public final String firstName;
    public final String lastName;
    public final String phone;
    public final String program;
    public final String degree;
    public final List<String> courses;
    public final boolean autoApprove; // <-- NEW FIELD

    // Updated constructor with autoApprove
    public User(String email, String password, String role, String firstName, String lastName, String phone, String program, String degree, List<String> courses, boolean autoApprove) {
        this.email = Objects.requireNonNull(email);
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.program = program;
        this.degree = degree;
        this.courses = courses == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(courses));
        this.autoApprove = autoApprove;
    }

    // Constructor compatibility for older code (default autoApprove = false)
    public User(String email, String password, String role, String firstName, String lastName, String phone, String program, String degree, List<String> courses) {
        this(email, password, role, firstName, lastName, phone, program, degree, courses, false);
    }

    // Existing "with" methods updated to preserve autoApprove

    public User withPassword(String value) {
        return new User(email, value, role, firstName, lastName, phone, program, degree, courses, autoApprove);
    }

    public User withRole(String value) {
        return new User(email, password, value, firstName, lastName, phone, program, degree, courses, autoApprove);
    }

    // NEW "builder" method to modify autoApprove safely
    public User withAutoApprove(boolean value) {
        return new User(email, password, role, firstName, lastName, phone, program, degree, courses, value);
    }
}
