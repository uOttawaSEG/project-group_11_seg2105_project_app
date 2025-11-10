package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.NAME, null, DatabaseContract.VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.Users.CREATE);
        db.execSQL(DatabaseContract.StudentProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorCourses.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.INDEX_STATUS);
        db.execSQL(DatabaseContract.TutorAvailability.CREATE);
        db.execSQL(DatabaseContract.TutorAvailability.INDEX_TUTOR_DATE);

        seedDefaults(db);
        seedPart4Rejected(db);
        ensureAdminApproved(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) db.execSQL(DatabaseContract.StudentProfiles.CREATE);
        if (oldVersion < 3) db.execSQL(DatabaseContract.TutorProfiles.CREATE);
        if (oldVersion < 4) {
            db.execSQL(DatabaseContract.RegistrationRequests.CREATE);
            db.execSQL(DatabaseContract.RegistrationRequests.INDEX_STATUS);
            seedPart4Rejected(db);
        }
        if (oldVersion < 5) {
            ensureColumn(db, DatabaseContract.StudentProfiles.TABLE, DatabaseContract.StudentProfiles.FIRST);
            ensureColumn(db, DatabaseContract.StudentProfiles.TABLE, DatabaseContract.StudentProfiles.LAST);
            ensureColumn(db, DatabaseContract.TutorProfiles.TABLE, DatabaseContract.TutorProfiles.FIRST);
            ensureColumn(db, DatabaseContract.TutorProfiles.TABLE, DatabaseContract.TutorProfiles.LAST);
        }
        if (oldVersion < 7) {
            db.execSQL(DatabaseContract.TutorAvailability.CREATE);
            db.execSQL(DatabaseContract.TutorAvailability.INDEX_TUTOR_DATE);
        }
        ensureAdminApproved(db);
    }

    private void ensureColumn(SQLiteDatabase db, String table, String column) {
        try {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT");
        } catch (Exception ignored) {}
    }

    private void seedDefaults(SQLiteDatabase db) {
        insertUser(db, "admin@uottawa.ca", "admin123", "admin");
        insertUser(db, "student@uottawa.ca", "pass123", "student");
        insertUser(db, "tutor@uottawa.ca", "teach123", "com/example/group_11_project_app_seg2105/tutor");

        ContentValues studentProfile = new ContentValues();
        studentProfile.put(DatabaseContract.StudentProfiles.EMAIL, "student@uottawa.ca");
        studentProfile.put(DatabaseContract.StudentProfiles.FIRST, "John");
        studentProfile.put(DatabaseContract.StudentProfiles.LAST, "Student");
        db.insertWithOnConflict(
                DatabaseContract.StudentProfiles.TABLE, null, studentProfile,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        ensureAdminApproved(db);
    }

    private void insertUser(SQLiteDatabase db, String email, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.EMAIL, email);
        values.put(DatabaseContract.Users.PASSWORD, password);
        values.put(DatabaseContract.Users.ROLE, role);
        db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    // === FIXED: Always seed visible rejected/pending data ===
    private void seedPart4Rejected(SQLiteDatabase db) {
        Log.d(TAG, "Seeding sample rejected and pending requests...");
        long now = System.currentTimeMillis();

        insertReg(db, "student@uottawa.ca", RegistrationStatus.REJECTED, now - 86_400_000L, "Rejected student reason");
        insertReg(db, "tutor@uottawa.ca", RegistrationStatus.REJECTED, now - 43_200_000L, "Rejected tutor reason");
        insertReg(db, "pending_tutor@uottawa.ca", RegistrationStatus.PENDING, now, null);
        insertReg(db, "pending_student@uottawa.ca", RegistrationStatus.PENDING, now, null);
    }

    private void insertReg(SQLiteDatabase db, String email, RegistrationStatus status, long createdAt, String reason) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RegistrationRequests.EMAIL, email);
        values.put(DatabaseContract.RegistrationRequests.CREATED_AT, createdAt);
        values.put(DatabaseContract.RegistrationRequests.STATUS, status.name());
        values.put(DatabaseContract.RegistrationRequests.REASON, reason);
        db.insert(DatabaseContract.RegistrationRequests.TABLE, null, values);
    }

    private void ensureAdminApproved(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.RegistrationRequests.STATUS, RegistrationStatus.APPROVED.name());
        cv.put(DatabaseContract.RegistrationRequests.REASON, (String) null);
        cv.put(DatabaseContract.RegistrationRequests.DECIDED_BY, "system");
        cv.put(DatabaseContract.RegistrationRequests.DECIDED_AT, System.currentTimeMillis());

        int rows = db.update(
                DatabaseContract.RegistrationRequests.TABLE,
                cv,
                DatabaseContract.RegistrationRequests.EMAIL + "=?",
                new String[]{"admin@uottawa.ca"}
        );
        if (rows == 0) {
            cv.put(DatabaseContract.RegistrationRequests.EMAIL, "admin@uottawa.ca");
            cv.put(DatabaseContract.RegistrationRequests.CREATED_AT, System.currentTimeMillis());
            db.insertWithOnConflict(
                    DatabaseContract.RegistrationRequests.TABLE,
                    null,
                    cv,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
        }
        Log.d(TAG, "ensureAdminApproved applied, rowsUpdated=" + rows);
    }

    public void seedAdmin() {
        if (getUserRole("admin@uottawa.ca") == null) {
            saveUser("admin", "admin@uottawa.ca", "admin123");
        }
        ensureAdminApproved(getWritableDatabase());
    }

    public void saveUser(String role, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.EMAIL, email);
        values.put(DatabaseContract.Users.PASSWORD, password);
        values.put(DatabaseContract.Users.ROLE, role);
        db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, "Inserted user: " + email + " | Role: " + role);
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + DatabaseContract.Users.TABLE +
                        " WHERE " + DatabaseContract.Users.EMAIL + "=? AND " +
                        DatabaseContract.Users.PASSWORD + "=?",
                new String[]{email, password});
        try {
            boolean ok = cursor.moveToFirst();
            if (!ok) return false;

            RegistrationStatus status = getRegistrationStatus(email);
            if (status == RegistrationStatus.PENDING || status == RegistrationStatus.REJECTED) {
                Log.d(TAG, "Blocked login for " + email + " (status=" + status + ")");
                return false;
            }
            return true;
        } finally {
            cursor.close();
        }
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + DatabaseContract.Users.ROLE +
                        " FROM " + DatabaseContract.Users.TABLE +
                        " WHERE " + DatabaseContract.Users.EMAIL + "=?",
                new String[]{email});
        try {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        } finally {
            cursor.close();
        }
    }

    public void saveStudentProfile(String email, String first, String last, String phone, String program) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StudentProfiles.EMAIL, email);
        values.put(DatabaseContract.StudentProfiles.FIRST, first);
        values.put(DatabaseContract.StudentProfiles.LAST, last);
        values.put(DatabaseContract.StudentProfiles.PHONE, phone);
        values.put(DatabaseContract.StudentProfiles.PROGRAM, program);
        db.insertWithOnConflict(DatabaseContract.StudentProfiles.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean createStudentWithProfile(String email, String password, String first, String last, String phone, String program) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.EMAIL, email);
            userValues.put(DatabaseContract.Users.PASSWORD, password);
            userValues.put(DatabaseContract.Users.ROLE, "student");
            long userResult = db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, userValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (userResult == -1) return false;

            ContentValues profileValues = new ContentValues();
            profileValues.put(DatabaseContract.StudentProfiles.EMAIL, email);
            profileValues.put(DatabaseContract.StudentProfiles.FIRST, first);
            profileValues.put(DatabaseContract.StudentProfiles.LAST, last);
            profileValues.put(DatabaseContract.StudentProfiles.PHONE, phone);
            profileValues.put(DatabaseContract.StudentProfiles.PROGRAM, program);
            db.insertWithOnConflict(DatabaseContract.StudentProfiles.TABLE, null, profileValues, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }
    public List<SessionRequest> getPendingSessionRequests(String tutorEmail) {
        ArrayList<SessionRequest> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, student_email, tutor_email, date, start, end, status FROM session_requests " +
                        "WHERE tutor_email=? AND status='PENDING' ORDER BY date, start",
                new String[]{tutorEmail});
        while (c.moveToNext()) {
            out.add(new SessionRequest(
                    c.getLong(0), c.getString(1), c.getString(2),
                    c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
        }
        c.close();
        return out;
    }

    public void updateSessionRequestStatus(long id, String status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        getWritableDatabase().update("session_requests", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public boolean createTutorWithProfile(String email, String password, String first, String last, String phone, String degree, Collection<String> courses) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.EMAIL, email);
            userValues.put(DatabaseContract.Users.PASSWORD, password);
            userValues.put(DatabaseContract.Users.ROLE, "com/example/group_11_project_app_seg2105/tutor");
            long userResult = db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, userValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (userResult == -1) return false;

            ContentValues profileValues = new ContentValues();
            profileValues.put(DatabaseContract.TutorProfiles.EMAIL, email);
            profileValues.put(DatabaseContract.TutorProfiles.FIRST, first);
            profileValues.put(DatabaseContract.TutorProfiles.LAST, last);
            profileValues.put(DatabaseContract.TutorProfiles.PHONE, phone);
            profileValues.put(DatabaseContract.TutorProfiles.DEGREE, degree);
            db.insertWithOnConflict(DatabaseContract.TutorProfiles.TABLE, null, profileValues, SQLiteDatabase.CONFLICT_REPLACE);

            if (courses != null) {
                for (String course : courses) {
                    ContentValues courseValues = new ContentValues();
                    courseValues.put(DatabaseContract.TutorCourses.EMAIL, email);
                    courseValues.put(DatabaseContract.TutorCourses.COURSE, course);
                    db.insertWithOnConflict(DatabaseContract.TutorCourses.TABLE, null, courseValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public boolean insertRegistrationRequest(String email, String role, String first, String last, String password, String phone, @Nullable String extra) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DatabaseContract.RegistrationRequests.TABLE +
                        " WHERE " + DatabaseContract.RegistrationRequests.EMAIL + "=?",
                new String[]{email});
        try {
            if (c.moveToFirst()) return false;
        } finally {
            c.close();
        }

        ContentValues v = new ContentValues();
        v.put(DatabaseContract.RegistrationRequests.EMAIL, email);
        v.put(DatabaseContract.RegistrationRequests.STATUS, RegistrationStatus.PENDING.name());
        v.put(DatabaseContract.RegistrationRequests.CREATED_AT, System.currentTimeMillis());
        v.put(DatabaseContract.RegistrationRequests.REASON, (String) null);
        db.insert(DatabaseContract.RegistrationRequests.TABLE, null, v);
        return true;
    }

    // === Deliverable 2: Extended registration with full profile info ===
    public boolean insertFullRegistrationRequest(String firstName, String lastName, String email,
                                                 String password, String role, String phone, String program) {
        SQLiteDatabase db = this.getWritableDatabase();

        try (Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DatabaseContract.RegistrationRequests.TABLE +
                        " WHERE " + DatabaseContract.RegistrationRequests.EMAIL + "=?",
                new String[]{email})) {
            if (c.moveToFirst()) return false;
        }

        ContentValues v = new ContentValues();
        v.put(DatabaseContract.RegistrationRequests.EMAIL, email);
        v.put(DatabaseContract.RegistrationRequests.STATUS, RegistrationStatus.PENDING.name());
        v.put(DatabaseContract.RegistrationRequests.CREATED_AT, System.currentTimeMillis());
        v.put(DatabaseContract.RegistrationRequests.REASON, (String) null);

        long result = db.insert(DatabaseContract.RegistrationRequests.TABLE, null, v);
        Log.d(TAG, "Inserted registration request for " + email + " result=" + result);
        return result != -1;
    }


    public RegistrationStatus getRegistrationStatus(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + DatabaseContract.RegistrationRequests.STATUS +
                        " FROM " + DatabaseContract.RegistrationRequests.TABLE +
                        " WHERE " + DatabaseContract.RegistrationRequests.EMAIL + "=?",
                new String[]{email});
        try {
            if (c.moveToFirst()) return RegistrationStatus.valueOf(c.getString(0));
            return null;
        } finally {
            c.close();
        }
    }

    public ArrayList<RegistrationRequest> getPendingRequests() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RegistrationRequest> out = new ArrayList<>();

        String sql =
                "SELECT r." + DatabaseContract.RegistrationRequests.ID + ", " +
                        "r." + DatabaseContract.RegistrationRequests.EMAIL + ", " +
                        "COALESCE(s." + DatabaseContract.StudentProfiles.FIRST + ", t." + DatabaseContract.TutorProfiles.FIRST + ") AS first_name, " +
                        "COALESCE(s." + DatabaseContract.StudentProfiles.LAST + ", t." + DatabaseContract.TutorProfiles.LAST + ") AS last_name, " +
                        "u." + DatabaseContract.Users.ROLE + ", " +
                        "r." + DatabaseContract.RegistrationRequests.STATUS + ", " +
                        "r." + DatabaseContract.RegistrationRequests.REASON + " " +
                        "FROM " + DatabaseContract.RegistrationRequests.TABLE + " r " +
                        "LEFT JOIN " + DatabaseContract.Users.TABLE + " u ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = u." + DatabaseContract.Users.EMAIL + " " +
                        "LEFT JOIN " + DatabaseContract.StudentProfiles.TABLE + " s ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = s." + DatabaseContract.StudentProfiles.EMAIL + " " +
                        "LEFT JOIN " + DatabaseContract.TutorProfiles.TABLE + " t ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = t." + DatabaseContract.TutorProfiles.EMAIL + " " +
                        "WHERE r." + DatabaseContract.RegistrationRequests.STATUS + " = 'PENDING' " +
                        "ORDER BY r." + DatabaseContract.RegistrationRequests.CREATED_AT + " DESC";

        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String email = c.getString(1);
                String first = c.isNull(2) ? "" : c.getString(2);
                String last = c.isNull(3) ? "" : c.getString(3);
                String role = c.isNull(4) ? "" : c.getString(4);
                String status = c.getString(5);
                String reason = c.isNull(6) ? "" : c.getString(6);

                out.add(new RegistrationRequest(
                        id, email, first, last, role,
                        RegistrationStatus.valueOf(status),
                        reason, 0L, null, null
                ));
            }
        }
        return out;
    }

    public void updateRequestStatus(String email, String newStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.RegistrationRequests.STATUS, newStatus);
        cv.put(DatabaseContract.RegistrationRequests.DECIDED_AT, System.currentTimeMillis());
        db.update(
                DatabaseContract.RegistrationRequests.TABLE,
                cv,
                DatabaseContract.RegistrationRequests.EMAIL + "=?",
                new String[]{email}
        );
        Log.d(TAG, "Updated request status for " + email + " → " + newStatus);
    }

    public List<RegistrationRequest> getRejectedRequests() {
        SQLiteDatabase db = getReadableDatabase();
        List<RegistrationRequest> out = new ArrayList<>();

        String sql =
                "SELECT r." + DatabaseContract.RegistrationRequests.ID + ", " +
                        "r." + DatabaseContract.RegistrationRequests.EMAIL + ", " +
                        "COALESCE(s." + DatabaseContract.StudentProfiles.FIRST + ", t." + DatabaseContract.TutorProfiles.FIRST + ") AS first_name, " +
                        "COALESCE(s." + DatabaseContract.StudentProfiles.LAST + ", t." + DatabaseContract.TutorProfiles.LAST + ") AS last_name, " +
                        "u." + DatabaseContract.Users.ROLE + ", " +
                        "r." + DatabaseContract.RegistrationRequests.STATUS + ", " +
                        "r." + DatabaseContract.RegistrationRequests.REASON + ", " +
                        "r." + DatabaseContract.RegistrationRequests.CREATED_AT + " " +
                        "FROM " + DatabaseContract.RegistrationRequests.TABLE + " r " +
                        "LEFT JOIN " + DatabaseContract.Users.TABLE + " u ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = u." + DatabaseContract.Users.EMAIL + " " +
                        "LEFT JOIN " + DatabaseContract.StudentProfiles.TABLE + " s ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = s." + DatabaseContract.StudentProfiles.EMAIL + " " +
                        "LEFT JOIN " + DatabaseContract.TutorProfiles.TABLE + " t ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = t." + DatabaseContract.TutorProfiles.EMAIL + " " +
                        "WHERE r." + DatabaseContract.RegistrationRequests.STATUS + " = 'REJECTED' " +
                        "ORDER BY r." + DatabaseContract.RegistrationRequests.CREATED_AT + " DESC";

        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String email = c.getString(1);
                String first = c.isNull(2) ? "" : c.getString(2);
                String last = c.isNull(3) ? "" : c.getString(3);
                String role = c.isNull(4) ? "" : c.getString(4);
                String status = c.getString(5);
                String reason = c.isNull(6) ? "" : c.getString(6);
                long createdAt = c.getLong(7);

                out.add(new RegistrationRequest(
                        id, email, first, last, role,
                        RegistrationStatus.valueOf(status),
                        reason, createdAt,
                        null, null
                ));
            }
        }

        Log.d(TAG, "Fetched " + out.size() + " rejected requests");
        return out;
    }

    public boolean reapproveRejected(long requestId, String adminEmail, @Nullable String note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RegistrationRequests.STATUS, RegistrationStatus.APPROVED.name());
        values.put(DatabaseContract.RegistrationRequests.DECIDED_BY, adminEmail);
        values.put(DatabaseContract.RegistrationRequests.DECIDED_AT, System.currentTimeMillis());
        if (note != null) values.put(DatabaseContract.RegistrationRequests.REASON, note);

        int rows = db.update(
                DatabaseContract.RegistrationRequests.TABLE,
                values,
                DatabaseContract.RegistrationRequests.ID + "=? AND " +
                        DatabaseContract.RegistrationRequests.STATUS + "=?",
                new String[]{String.valueOf(requestId), RegistrationStatus.REJECTED.name()}
        );

        Log.d(TAG, "Reapproved rejected request ID=" + requestId + " Rows=" + rows);
        return rows == 1;
    }
    public long insertAvailability(AvailabilitySlot s) {
        if (!isValidTimeStep(s.start) || !isValidTimeStep(s.end)) return -1;
        if (isOverlapping(s.tutorEmail, s.date, s.start, s.end)) return -1;

        ContentValues v = new ContentValues();
        v.put(DatabaseContract.TutorAvailability.TUTOR_EMAIL, s.tutorEmail);
        v.put(DatabaseContract.TutorAvailability.DATE, s.date);
        v.put(DatabaseContract.TutorAvailability.START, s.start);
        v.put(DatabaseContract.TutorAvailability.END, s.end);
        v.put(DatabaseContract.TutorAvailability.AUTO_APPROVE, s.autoApprove ? 1 : 0);

        return getWritableDatabase().insert(DatabaseContract.TutorAvailability.TABLE, null, v);
    }

    public boolean deleteAvailability(long id, String tutorEmail) {
        int rows = getWritableDatabase().delete(
                DatabaseContract.TutorAvailability.TABLE,
                DatabaseContract.TutorAvailability.ID + "=? AND " +
                        DatabaseContract.TutorAvailability.TUTOR_EMAIL + "=?",
                new String[]{String.valueOf(id), tutorEmail});
        return rows == 1;
    }

    public List<AvailabilitySlot> getAvailabilityForTutorOnDate(String tutorEmail, String date) {
        ArrayList<AvailabilitySlot> out = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, tutor_email, date, start_time, end_time, auto_approve " +
                        "FROM " + DatabaseContract.TutorAvailability.TABLE +
                        " WHERE tutor_email=? AND date=? ORDER BY start_time",
                new String[]{tutorEmail, date});

        try {
            while (c.moveToNext()) {
                out.add(new AvailabilitySlot(
                        c.getLong(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getInt(5) == 1
                ));
            }
        } finally {
            c.close();
        }

        return out;
    }

    private boolean isValidTimeStep(String time) {
        // time format HH:mm — minutes must be 00 or 30
        int mm = Integer.parseInt(time.substring(3,5));
        return (mm == 0 || mm == 30);
    }

    private boolean isOverlapping(String email, String date, String start, String end) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT 1 FROM " + DatabaseContract.TutorAvailability.TABLE +
                        " WHERE tutor_email=? AND date=? AND start_time < ? AND end_time > ? LIMIT 1",
                new String[]{email, date, end, start});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }
}
