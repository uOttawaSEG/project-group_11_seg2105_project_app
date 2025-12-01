package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Repository for managing session requests in the database.
 * A session request records a student's request to book a tutor's availability slot.
 * Each request references a specific slot via its ID and includes the tutor and student
 * identifiers, date and time range, booking status and optional rating.
 */
public class SQLiteSessionRequestRepository {

    private final DatabaseHelper helper;

    public SQLiteSessionRequestRepository(DatabaseHelper helper) {
        this.helper = helper;
        // Ensure the table exists. This is defensive; DatabaseHelper.onCreate should
        // already create the table but calling CREATE TABLE IF NOT EXISTS here makes
        // the repository resilient to older database versions.
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(DatabaseContract.SessionRequests.CREATE);
    }

    /**
     * Create a session request. The status should be one of "PENDING", "APPROVED" or
     * "REJECTED". Rating can be null when the session has not yet been rated.
     */
    public void create(String studentEmail, String tutorEmail, long slotId, String date,
                       String start, String end, String status, Double rating) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SessionRequests.STUDENT_EMAIL, studentEmail);
        values.put(DatabaseContract.SessionRequests.TUTOR_EMAIL, tutorEmail);
        values.put(DatabaseContract.SessionRequests.SLOT_ID, slotId);
        values.put(DatabaseContract.SessionRequests.DATE, date);
        values.put(DatabaseContract.SessionRequests.START, start);
        values.put(DatabaseContract.SessionRequests.END, end);
        values.put(DatabaseContract.SessionRequests.STATUS, status);
        if (rating != null) {
            values.put(DatabaseContract.SessionRequests.RATING, rating);
        }
        db.insert(DatabaseContract.SessionRequests.TABLE, null, values);
    }

    /**
     * Determine if a time slot is already booked. A slot is considered booked
     * if there is a session request record with matching tutor email, date,
     * start and end time with status either PENDING or APPROVED.
     */
    public boolean isSlotBooked(String tutorEmail, String date, String start, String end) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseContract.SessionRequests.TABLE,
                new String[]{ DatabaseContract.SessionRequests.ID },
                DatabaseContract.SessionRequests.TUTOR_EMAIL + " = ? AND " +
                        DatabaseContract.SessionRequests.DATE + " = ? AND " +
                        DatabaseContract.SessionRequests.START + " = ? AND " +
                        DatabaseContract.SessionRequests.END + " = ? AND (" +
                        DatabaseContract.SessionRequests.STATUS + " = 'PENDING' OR " +
                        DatabaseContract.SessionRequests.STATUS + " = 'APPROVED')",
                new String[]{ tutorEmail, date, start, end }, null, null, null
        )) {
            return cursor.moveToFirst();
        }
    }

    /**
     * Check if a student has an overlapping booking or pending request at the given
     * date and time. This prevents students from booking overlapping sessions.
     */
    public boolean hasStudentOverlap(String studentEmail, String date, String start, String end) {
        SQLiteDatabase db = helper.getReadableDatabase();
        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        try (Cursor cursor = db.query(
                DatabaseContract.SessionRequests.TABLE,
                new String[]{ DatabaseContract.SessionRequests.START, DatabaseContract.SessionRequests.END },
                DatabaseContract.SessionRequests.STUDENT_EMAIL + " = ? AND " +
                        DatabaseContract.SessionRequests.DATE + " = ? AND (" +
                        DatabaseContract.SessionRequests.STATUS + " = 'PENDING' OR " +
                        DatabaseContract.SessionRequests.STATUS + " = 'APPROVED')",
                new String[]{ studentEmail, date }, null, null, null
        )) {
            while (cursor.moveToNext()) {
                String existingStartStr = cursor.getString(0);
                String existingEndStr = cursor.getString(1);
                LocalTime existingStart = LocalTime.parse(existingStartStr);
                LocalTime existingEnd = LocalTime.parse(existingEndStr);
                boolean overlaps = startTime.isBefore(existingEnd) && endTime.isAfter(existingStart);
                if (overlaps) {
                    return true;
                }
            }
        }
        return false;
    }
}