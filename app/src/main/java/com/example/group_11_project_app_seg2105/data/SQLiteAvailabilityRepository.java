package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.group_11_project_app_seg2105.core.validation.AvailabilityValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite-backed implementation of {@link AvailabilityRepository}. This class
 * encapsulates all persistence logic related to tutor availability slots and
 * ensures that slots are created only if they pass validation and do not
 * conflict with existing entries. It also prevents deletion of slots that
 * have pending or approved session requests.
 */
public class SQLiteAvailabilityRepository implements AvailabilityRepository {

    private static final String TABLE = DatabaseContract.TutorAvailability.TABLE;
    private final DatabaseHelper helper;

    public SQLiteAvailabilityRepository(DatabaseHelper helper) {
        this.helper = helper;
        // Ensure table and index exist. While DatabaseHelper.onCreate and
        // onUpgrade should create these, calling them here is defensive and
        // allows the repository to operate even if the application is upgraded
        // without bumping the database version.
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(DatabaseContract.TutorAvailability.CREATE);
        db.execSQL(DatabaseContract.TutorAvailability.INDEX_TUTOR_DATE);
    }

    @Override
    public boolean create(String tutorEmail, String date, String start, String end) {
        // Validate new slot against existing slots for the tutor on that date
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
                new String[]{ tutorEmail, date },
                null, null,
                DatabaseContract.TutorAvailability.START + " ASC"
        )) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String email = cursor.getString(1);
                String d = cursor.getString(2);
                String s = cursor.getString(3);
                String e = cursor.getString(4);
                boolean auto = cursor.getInt(5) != 0;
                slots.add(new AvailabilitySlot(id, email, d, s, e, auto));
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
                new String[]{ tutorEmail },
                null, null,
                DatabaseContract.TutorAvailability.DATE + " ASC, " +
                        DatabaseContract.TutorAvailability.START + " ASC"
        )) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String email = cursor.getString(1);
                String d = cursor.getString(2);
                String s = cursor.getString(3);
                String e = cursor.getString(4);
                boolean auto = cursor.getInt(5) != 0;
                slots.add(new AvailabilitySlot(id, email, d, s, e, auto));
            }
        }
        return slots;
    }

    @Override
    public boolean delete(AvailabilitySlot slot) {
        return deleteById((int) slot.id);
    }

    @Override
    public boolean canDelete(int slotId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseContract.SessionRequests.TABLE,
                new String[]{ DatabaseContract.SessionRequests.STATUS },
                DatabaseContract.SessionRequests.SLOT_ID + " = ?",
                new String[]{ String.valueOf(slotId) },
                null, null, null
        )) {
            while (cursor.moveToNext()) {
                String status = cursor.getString(0);
                if ("PENDING".equalsIgnoreCase(status) ||
                        "APPROVED".equalsIgnoreCase(status)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean deleteById(int slotId) {
        if (!canDelete(slotId)) {
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(
                TABLE,
                DatabaseContract.TutorAvailability.ID + " = ?",
                new String[]{ String.valueOf(slotId) }
        );
        return rows > 0;
    }
}