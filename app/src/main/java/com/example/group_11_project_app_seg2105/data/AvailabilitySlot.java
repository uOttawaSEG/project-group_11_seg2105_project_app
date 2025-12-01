package com.example.group_11_project_app_seg2105.data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Immutable value object representing a single availability slot for a tutor.
 * Each slot records the owning tutor's email, the date and start/end times
 * along with an auto-approval flag indicating whether booking requests are
 * automatically approved or require tutor action. Slots are identified
 * internally by a generated numeric ID.
 */
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