package com.example.group_11_project_app_seg2105.data;

public class SessionRequest {
    public final long id;
    public final String studentEmail;
    public final String tutorEmail;
    public final String date;
    public final String start;
    public final String end;
    public final String status; // PENDING, APPROVED, REJECTED

    public SessionRequest(long id, String studentEmail, String tutorEmail,
                          String date, String start, String end, String status) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
    }
}
