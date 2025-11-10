package com.example.group_11_project_app_seg2105.core.validation;

import com.example.group_11_project_app_seg2105.data.AvailabilitySlot;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class AvailabilityValidatorTest {

    @Test
    public void testRejectsPastDates() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<AvailabilitySlot> existing = new ArrayList<>();
        boolean valid = AvailabilityValidator.isValid(
                yesterday.toString(), "10:00", "10:30", existing);
        assertFalse("Slots on past dates should be invalid", valid);
    }

    @Test
    public void testRejectsNonThirtyMinuteAlignment() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailabilitySlot> existing = new ArrayList<>();
        boolean valid1 = AvailabilityValidator.isValid(
                tomorrow.toString(), "10:10", "10:40", existing);
        assertFalse("Start times not aligned to 30-minute increments should be invalid", valid1);
        boolean valid2 = AvailabilityValidator.isValid(
                tomorrow.toString(), "10:00", "10:45", existing);
        assertFalse("End times not aligned to 30-minute increments should be invalid", valid2);
    }

    @Test
    public void testRejectsDurationNotDivisibleByThirty() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailabilitySlot> existing = new ArrayList<>();
        boolean valid = AvailabilityValidator.isValid(
                tomorrow.toString(), "10:00", "10:45", existing);
        assertFalse("Durations not divisible by 30 minutes should be invalid", valid);
    }

    @Test
    public void testDetectsDuplicate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailabilitySlot> existing = new ArrayList<>();
        existing.add(new AvailabilitySlot(0L, "tutor@uottawa.ca", tomorrow.toString(), "09:00", "09:30", false));
        boolean valid = AvailabilityValidator.isValid(
                tomorrow.toString(), "09:00", "09:30", existing);
        assertFalse("Duplicate slots should be invalid", valid);
    }

    @Test
    public void testDetectsOverlap() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailabilitySlot> existing = new ArrayList<>();
        existing.add(new AvailabilitySlot(0L, "tutor@uottawa.ca", tomorrow.toString(), "10:00", "10:30", false));
        boolean valid = AvailabilityValidator.isValid(
                tomorrow.toString(), "10:15", "10:45", existing);
        assertFalse("Overlapping slots should be invalid", valid);
    }

    @Test
    public void testAllowsAdjacentSlots() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<AvailabilitySlot> existing = new ArrayList<>();
        existing.add(new AvailabilitySlot(0L, "tutor@uottawa.ca", tomorrow.toString(), "10:00", "10:30", false));
        boolean valid = AvailabilityValidator.isValid(
                tomorrow.toString(), "10:30", "11:00", existing);
        assertTrue("Adjacent non-overlapping slots should be valid", valid);
    }
}