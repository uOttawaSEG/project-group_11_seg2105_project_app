package com.example.group_11_project_app_seg2105.core.validation;

import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Static utility for validating tutor availability slots. Validation rules
 * include checking that the date is not in the past, times are well formed,
 * start precedes end, times fall on 30-minute boundaries, and the duration
 * is a positive multiple of 30 minutes. Duplicate and overlapping slots are
 * rejected by comparing against existing slots for the same date.
 */
public final class AvailabilityValidator {

    private AvailabilityValidator() {
        // Prevent instantiation
    }

    public static List<String> validate(String date, String start, String end, List<AvailabilitySlot> existing) {
        List<String> errors = new ArrayList<>();

        LocalDate slotDate;
        try {
            slotDate = LocalDate.parse(date);
        } catch (Exception ex) {
            errors.add("Invalid date format. Use YYYY‑MM‑DD.");
            return errors;
        }

        LocalTime startTime;
        LocalTime endTime;
        try {
            startTime = LocalTime.parse(start);
            endTime = LocalTime.parse(end);
        } catch (Exception ex) {
            errors.add("Invalid time format. Use HH:mm (24‑hour).");
            return errors;
        }

        if (slotDate.isBefore(LocalDate.now())) {
            errors.add("Date cannot be in the past.");
        }

        if (!startTime.isBefore(endTime)) {
            errors.add("Start time must be before end time.");
        }

        if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
            errors.add("Start and end times must align with 30‑minute boundaries (e.g. 8:00, 8:30).");
        }

        int minutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        if (minutes < 30 || minutes % 30 != 0) {
            errors.add("Time slots must be in 30‑minute increments.");
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        for (AvailabilitySlot slot : existing) {
            if (!slot.date.equals(date)) {
                continue;
            }
            LocalTime existingStart = LocalTime.parse(slot.start);
            LocalTime existingEnd = LocalTime.parse(slot.end);
            boolean duplicate = startTime.equals(existingStart) && endTime.equals(existingEnd);
            if (duplicate) {
                errors.add("Duplicate slot exists for the same date and time.");
                return errors;
            }
            boolean overlaps = startTime.isBefore(existingEnd) && endTime.isAfter(existingStart);
            if (overlaps) {
                errors.add("Slot overlaps with an existing availability (" + slot.start + "‑" + slot.end + ").");
                return errors;
            }
        }
        return errors;
    }

    public static boolean isValid(String date, String start, String end, List<AvailabilitySlot> existing) {
        return validate(date, start, end, existing).isEmpty();
    }
}