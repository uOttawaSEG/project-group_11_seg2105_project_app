package com.example.group_11_project_app_seg2105.data;

import java.time.LocalDate;
import java.time.LocalTime;

public final class AvailabilitySlot {
    public final long id;
    public final String tutorEmail;
    public final String date;
    public final String start;
    public final String end;
    public final boolean autoApprove;

    public AvailabilitySlot(long id, String tutorEmail, String date, String start, String end, boolean autoApprove) {
        this.id = id;
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.start = start;
        this.end = end;
        this.autoApprove = autoApprove;
    }

    public LocalDate getDate() {
        return LocalDate.parse(date);
    }

    public LocalTime getStart() {
        return LocalTime.parse(start);
    }

    public LocalTime getEnd() {
        return LocalTime.parse(end);
    }
}