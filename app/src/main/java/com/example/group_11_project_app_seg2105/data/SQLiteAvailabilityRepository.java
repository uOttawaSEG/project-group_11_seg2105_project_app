package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.group_11_project_app_seg2105.core.validation.AvailabilityValidator;

import java.util.ArrayList;
import java.util.List;

public class SQLiteAvailabilityRepository implements AvailabilityRepository {

    private static final String TABLE = DatabaseContract.TutorAvailability.TABLE;
    private final DatabaseHelper helper;

    public SQLiteAvailabilityRepository(DatabaseHelper helper) {
        this.helper = helper;

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                DatabaseContract.TutorAvailability.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.TutorAvailability.TUTOR_EMAIL + " TEXT NOT NULL, " +
                DatabaseContract.TutorAvailability.DATE + " TEXT NOT NULL, " +
                DatabaseContract.TutorAvailability.START + " TEXT NOT NULL, " +
                DatabaseContract.TutorAvailability.END + " TEXT NOT NULL, " +
                DatabaseContract.TutorAvailability.AUTO_APPROVE + " INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(" + DatabaseContract.TutorAvailability.TUTOR_EMAIL + ") REFERENCES " +
                DatabaseContract.Users.TABLE + "(" + DatabaseContract.Users.EMAIL + ") ON DELETE CASCADE)"
        );

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_tutor_availability_unique ON " +
                TABLE + " (" +
                DatabaseContract.TutorAvailability.TUTOR_EMAIL + ", " +
                DatabaseContract.TutorAvailability.DATE + ", " +
                DatabaseContract.TutorAvailability.START + ", " +
                DatabaseContract.TutorAvailability.END + ")"
        );
    }

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public boolean create(String tutorEmail, String date, String start, String end) {

        List<AvailabilitySlot> existing = findByTutorAndDate(tutorEmail, date);

        if (!AvailabilityValidator.isValid(date, start, end, existing)) {
            return false;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TutorAvailability.TUTOR_EMAIL, tutorEmail);
        values.put(DatabaseContract.TutorAvailability.DATE, date);
        values.put(DatabaseContract.TutorAvailability.START, start);
        values.put(DatabaseContract.TutorAvailability.END, end);
        values.put(DatabaseContract.TutorAvailability.AUTO_APPROVE, 0);

        long result = db.insert(TABLE, null, values);
        return result != -1;
    }

    // ============================================================
    // FIND METHODS
    // ============================================================

    @Override
    public List<AvailabilitySlot> findByTutorAndDate(String tutorEmail, String date) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<AvailabilitySlot> slots = new ArrayList<>();

        try (Cursor cursor = db.query(
                TABLE,
                new String[]{
                        DatabaseContract.TutorAvailability.ID,
                        DatabaseContract.TutorAvailability.TUTOR_EMAIL,
                        DatabaseContract.TutorAvailability.DATE,
                        DatabaseContract.TutorAvailability.START,
                        DatabaseContract.TutorAvailability.END,
                        DatabaseContract.TutorAvailability.AUTO_APPROVE
                },
                DatabaseContract.TutorAvailability.TUTOR_EMAIL + " = ? AND " +
                        DatabaseContract.TutorAvailability.DATE + " = ?",
                new String[]{tutorEmail, date},
                null, null,
                DatabaseContract.TutorAvailability.START + " ASC"
        )) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String email = cursor.getString(1);
                String d = cursor.getString(2);
                String start = cursor.getString(3);
                String end = cursor.getString(4);
                boolean autoApprove = cursor.getInt(5) != 0;

                slots.add(new AvailabilitySlot(id, email, d, start, end, autoApprove));
            }
        }

        return slots;
    }

    @Override
    public List<AvailabilitySlot> findByTutor(String tutorEmail) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<AvailabilitySlot> slots = new ArrayList<>();

        try (Cursor cursor = db.query(
                TABLE,
                new String[]{
                        DatabaseContract.TutorAvailability.ID,
                        DatabaseContract.TutorAvailability.TUTOR_EMAIL,
                        DatabaseContract.TutorAvailability.DATE,
                        DatabaseContract.TutorAvailability.START,
                        DatabaseContract.TutorAvailability.END,
                        DatabaseContract.TutorAvailability.AUTO_APPROVE
                },
                DatabaseContract.TutorAvailability.TUTOR_EMAIL + " = ?",
                new String[]{tutorEmail},
                null, null,
                DatabaseContract.TutorAvailability.DATE + " ASC, " +
                        DatabaseContract.TutorAvailability.START + " ASC"
        )) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String email = cursor.getString(1);
                String d = cursor.getString(2);
                String start = cursor.getString(3);
                String end = cursor.getString(4);
                boolean autoApprove = cursor.getInt(5) != 0;

                slots.add(new AvailabilitySlot(id, email, d, start, end, autoApprove));
            }
        }

        return slots;
    }

    // ============================================================
    // OLD DELETE (still required, but not used directly anymore)
    // ============================================================

    @Override
    public boolean delete(AvailabilitySlot slot) {
        SQLiteDatabase db = helper.getWritableDatabase();

        int rows = db.delete(
                TABLE,
                DatabaseContract.TutorAvailability.ID + " = ?",
                new String[]{String.valueOf(slot.id)}
        );

        return rows > 0;
    }

    // ============================================================
    // PART 5 — SAFE DELETE SUPPORT
    // ============================================================

    @Override
    public boolean canDelete(int slotId) {
        // Reuse existing logic in DatabaseHelper that matches by tutor/date/time
        List<SessionRequest> requests = helper.getRequestsBySlot(slotId);

        for (SessionRequest req : requests) {
            String status = req.status;   // or req.status() if it's a record
            if ("PENDING".equalsIgnoreCase(status) ||
                    "APPROVED".equalsIgnoreCase(status)) {
                return false;  // block delete
            }
        }

        return true; // safe to delete
    }



    @Override
    public boolean deleteById(int slotId) {
        if (!canDelete(slotId)) return false;

        SQLiteDatabase db = helper.getWritableDatabase();

        int rows = db.delete(
                TABLE,
                DatabaseContract.TutorAvailability.ID + " = ?",
                new String[]{String.valueOf(slotId)}
        );

        return rows > 0;
    }
}
