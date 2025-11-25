package com.example.group_11_project_app_seg2105.data;

import java.util.List;

public interface AvailabilityRepository {

    // Create a new availability slot
    boolean create(String tutorEmail, String date, String start, String end);

    // Find slots for a tutor on a specific date
    List<AvailabilitySlot> findByTutorAndDate(String tutorEmail, String date);

    // Find all slots for a tutor
    List<AvailabilitySlot> findByTutor(String tutorEmail);

    // Basic delete (old behavior)
    boolean delete(AvailabilitySlot slot);

    // ============================
    // PART 5 — NEW SAFE DELETE API
    // ============================

    // Check if slot can be deleted (no pending/approved sessions)
    boolean canDelete(int slotId);

    // Delete by ID after passing the safety check
    boolean deleteById(int slotId);
}
