package com.example.group_11_project_app_seg2105.data;

public class TutorAvailabilitySlot {
    public final long id;
    public final String tutorEmail;
    public final String date; // yyyy-MM-dd
    public final int startMin;
    public final int endMin;

    public TutorAvailabilitySlot(long id, String tutorEmail, String date, int startMin, int endMin) {
        this.id = id;
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.startMin = startMin;
        this.endMin = endMin;

    }
}
