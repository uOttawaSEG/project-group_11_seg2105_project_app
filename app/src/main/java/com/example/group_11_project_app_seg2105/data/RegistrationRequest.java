package com.example.group_11_project_app_seg2105.data;

public class RegistrationRequest {

    /**
     * Represents a registration request (pending, approved, or rejected)
     * stored in the registration_requests table.
     */
    public final long id;
    public final String email;
    public final String firstName;
    public final String lastName;
    public final String role;
    public final String status;
    public final String reason;

    public RegistrationRequest(long id, String email, String firstName, String lastName, String role, String status, String reason) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
        this.reason = reason;
    }

    public String getFullName() {
        String name = "";
        if (firstName != null) name += firstName;
        if (lastName != null && !lastName.isEmpty())
            name += (name.isEmpty() ? "" : " ") + lastName;
        return name.isEmpty() ? email : name;
    }

}
