package com.example.group_11_project_app_seg2105.data;

public class StudentProfile {
    public final String email;
    public final String firstName;
    public final String lastName;
    public final String phone;
    public final String program;

    public StudentProfile(String email, String firstName, String lastName, String phone, String program) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.program = program;
    }

    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) builder.append(firstName);
        if (lastName != null && !lastName.isEmpty()) {
            if (builder.length() > 0) builder.append(' ');
            builder.append(lastName);
        }
        if (builder.length() == 0 && email != null) {
            builder.append(email);
        }
        return builder.toString();
    }
}
