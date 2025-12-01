package com.example.group_11_project_app_seg2105.data;

import java.util.List;

/**
 * Abstraction over persistence for tutor availability slots. Implementations
 * encapsulate the underlying storage technology (e.g. SQLite) and provide
 * common operations such as creating, querying and deleting slots. Safe
 * deletion is supported via {@link #canDelete} and {@link #deleteById}.
 */
public interface AvailabilityRepository {

    /**
     * Attempt to create a new availability slot. Returns {@code true} if
     * the slot is inserted successfully. The implementation must enforce
     * validation rules (date not in past, 30-minute increments, no overlap)
     * and reject invalid slots by returning {@code false}.
     */
    boolean create(String tutorEmail, String date, String start, String end);

    /**
     * Find all slots for a given tutor on a specific date. Slots should be
     * sorted by start time ascending.
     */
    List<AvailabilitySlot> findByTutorAndDate(String tutorEmail, String date);

    /**
     * Find all availability slots for a given tutor. Results should be
     * ordered by date then start time ascending.
     */
    List<AvailabilitySlot> findByTutor(String tutorEmail);

    /**
     * Delete a slot by value. This method is retained for backward
     * compatibility but should delegate to {@link #deleteById} in new code.
     */
    boolean delete(AvailabilitySlot slot);

    /**
     * Check if a slot can be safely deleted. A slot cannot be deleted if
     * there are any pending or approved session requests referencing it.
     */
    boolean canDelete(int slotId);

    /**
     * Delete a slot by its identifier. Implementations should call
     * {@link #canDelete} before performing the deletion and return
     * {@code false} if the safety check fails.
     */
    boolean deleteById(int slotId);
}