package com.example.group_11_project_app_seg2105.data;

import java.util.Objects;

public class RegistrationRequest {
    public final long id;
    public final String email;
    public final String firstName;
    public final String lastName;
    public final String role;
    public final RegistrationStatus status;
    public final String reason;
    public final long createdAt;
    public final String decidedBy;
    public final Long decidedAt;

    public RegistrationRequest(long id, String email, String firstName, String lastName, String role, RegistrationStatus status, String reason, long createdAt, String decidedBy, Long decidedAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt;
    }

    public RegistrationRequest withStatus(RegistrationStatus value, String decidedByValue, Long decidedAtValue, String reasonValue) {
        return new RegistrationRequest(id, email, firstName, lastName, role, value, reasonValue, createdAt, decidedByValue, decidedAtValue);
    }

    public String getFullName() {
        String name = "";
        if (firstName != null) {
            name += firstName;
        }
        if (lastName != null && !lastName.isEmpty()) {
            name += (name.isEmpty() ? "" : " ") + lastName;
        }
        return name.isEmpty() ? email : name;
    }
}
