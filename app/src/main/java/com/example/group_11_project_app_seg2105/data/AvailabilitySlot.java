package com.example.group_11_project_app_seg2105.data;

public class AvailabilitySlot {
    public long id;
    public String tutorEmail;
    public String date;   // YYYY-MM-DD
    public String start;  // HH:mm
    public String end;    // HH:mm
    public boolean autoApprove;

    public AvailabilitySlot(long id,
                            String tutorEmail,
                            String date,
                            String start,
                            String end,
                            boolean autoApprove) {

        this.id = id;
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.start = start;
        this.end = end;
        this.autoApprove = autoApprove;
    }
}
