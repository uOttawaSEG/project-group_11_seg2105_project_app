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

    private static final String DB_NAME = "otams.db";
    private static final int DB_VERSION = 7;

    // Tables
    private static final String T_USERS = "users";
    private static final String T_STUDENT_PROFILES = "student_profiles";
    private static final String T_TUTOR_PROFILES = "tutor_profiles";
    private static final String T_TUTOR_COURSES = "tutor_courses";
    private static final String T_REG_REQUESTS = "registration_requests";
    private static final String T_TUTOR_AVAIL = "tutor_availability";

    // Common Columns
    private static final String C_EMAIL = "email";
    private static final String C_PASSWORD = "password";
    private static final String C_ROLE = "role";

    // Student Columns
    private static final String C_FIRST = "first_name";
    private static final String C_LAST = "last_name";
    private static final String C_PHONE = "phone";
    private static final String C_PROGRAM = "program";

    // Tutor Columns
    private static final String C_DEGREE = "degree";
    private static final String C_COURSE = "course";

    // Registration Request Columns
    private static final String R_ID = "id";
    private static final String R_EMAIL = "email";
    private static final String R_CREATED_AT = "created_at";
    private static final String R_STATUS = "status";
    private static final String R_REASON = "reason";
    private static final String R_DECIDED_BY = "decided_by_admin";
    private static final String R_DECIDED_AT = "decided_at";

    // Tutor Availability Columns
    private static final String A_ID = "id";
    private static final String A_TUTOR_EMAIL = "tutor_email";
    private static final String A_DATE = "date"; // ISO yyyy-MM-dd

    private static final String A_START_MIN = "start_min"; // minutes from 00:00
    private static final String A_END_MIN = "end_min";




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


        seedDefaults(db);
        seedPart4Rejected(db);
        ensureAdminApproved(db);

        db.execSQL("CREATE TABLE " + T_STUDENT_PROFILES + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_FIRST + " TEXT, " +
                C_LAST + " TEXT, " +
                C_PHONE + " TEXT, " +
                C_PROGRAM + " TEXT, " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + T_TUTOR_PROFILES + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_FIRST + " TEXT NOT NULL, " +
                C_LAST + " TEXT NOT NULL, " +
                C_PHONE + " TEXT NOT NULL, " +
                C_DEGREE + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + T_TUTOR_COURSES + " (" +
                C_EMAIL + " TEXT NOT NULL, " +
                C_COURSE + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + C_EMAIL + ", " + C_COURSE + "), " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        db.execSQL("INSERT INTO " + T_USERS + " (" + C_EMAIL + ", " + C_PASSWORD + ", " + C_ROLE + ") VALUES " +
                "('admin@uottawa.ca','admin123','admin')," +
                "('student@uottawa.ca','pass123','student')," +
                "('tutor@uottawa.ca','teach123','tutor')");

        db.execSQL("INSERT INTO " + T_STUDENT_PROFILES + " (" + C_EMAIL + ", " + C_FIRST + ", " + C_LAST + ") VALUES " +
                "('student@uottawa.ca', 'John', 'Student')");

        db.execSQL("CREATE TABLE " + T_REG_REQUESTS + " (" +
                R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_EMAIL + " TEXT NOT NULL, " +
                R_CREATED_AT + " INTEGER NOT NULL, " +
                R_STATUS + " TEXT NOT NULL CHECK(" + R_STATUS + " IN ('PENDING', 'APPROVED', 'REJECTED')), " +
                R_REASON + " TEXT, " +
                R_DECIDED_BY + " TEXT, " +
                R_DECIDED_AT + " INTEGER, " +
                "FOREIGN KEY(" + R_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)"
        );
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reg_status ON " + T_REG_REQUESTS + "(" + R_STATUS +")");

        db.execSQL("CREATE TABLE " + T_TUTOR_AVAIL + " (" +
                A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                A_TUTOR_EMAIL + " TEXT NOT NULL, " +
                A_DATE + " TEXT NOT NULL, " +
                A_START_MIN + " INTEGER NOT NULL, " +
                A_END_MIN + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + A_TUTOR_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");
        //temp part 4 rejected users for testing
        seedPart4Rejected(db);


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

        ensureAdminApproved(db);


        if (oldV < 7) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_TUTOR_AVAIL + " (" +
                    A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    A_TUTOR_EMAIL + " TEXT NOT NULL, " +
                    A_DATE + " TEXT NOT NULL, " +
                    A_START_MIN + " INTEGER NOT NULL, " +
                    A_END_MIN + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + A_TUTOR_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");
        }

    }

    private void ensureColumn(SQLiteDatabase db, String table, String column) {
        try {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT");
        } catch (Exception ignored) {}
    }

    private void seedDefaults(SQLiteDatabase db) {
        insertUser(db, "admin@uottawa.ca", "admin123", "admin");
        insertUser(db, "student@uottawa.ca", "pass123", "student");
        insertUser(db, "tutor@uottawa.ca", "teach123", "tutor");

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

    public boolean createTutorWithProfile(String email, String password, String first, String last, String phone, String degree, Collection<String> courses) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.EMAIL, email);
            userValues.put(DatabaseContract.Users.PASSWORD, password);
            userValues.put(DatabaseContract.Users.ROLE, "tutor");
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
    // tutor insert method
    public long addTutorAvailabilitySlot(String tutorEmail, String dateIso, int startMin, int endMin) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(A_TUTOR_EMAIL, tutorEmail);
        cv.put(A_DATE, dateIso);
        cv.put(A_START_MIN, startMin);
        cv.put(A_END_MIN, endMin);

        return db.insert(T_TUTOR_AVAIL, null, cv);
    }



    // --- Student Profile Model ---
    public static class StudentProfile {
        public final String email;
        public final String firstName;
        public final String lastName;
        public final String phone;
        public final String program;

        public StudentProfile(String email, String firstName, String lastName,
                              String phone, String program) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.program = program;
        }
    }



    public List<RegistrationRequest> getRejectedRequests() {
        SQLiteDatabase db = getReadableDatabase();

        String sql =
                "SELECT r." + R_ID + ", " +
                        "r." + R_EMAIL + ", " +
                        "COALESCE(s." + C_FIRST + ", t." + C_FIRST + ") AS first_name, " +
                        "COALESCE(s." + C_LAST + ", t." + C_LAST + ") AS last_name, " +
                        "u." + C_ROLE + ", " +
                        "r." + R_STATUS + ", " +
                        "r." + R_REASON + " " +
                        "FROM " + T_REG_REQUESTS + " r " +
                        "LEFT JOIN " + T_USERS + " u ON r." + R_EMAIL + " = u." + C_EMAIL + " " +
                        "LEFT JOIN " + T_STUDENT_PROFILES + " s ON r." + R_EMAIL + " = s." + C_EMAIL + " " +
                        "LEFT JOIN " + T_TUTOR_PROFILES + " t ON r." + R_EMAIL + " = t." + C_EMAIL + " " +
                        "WHERE r." + R_STATUS + " = 'REJECTED' " +
                        "ORDER BY r." + R_CREATED_AT + " DESC";

        List<RegistrationRequest> out = new ArrayList<>();

        try(Cursor c = db.rawQuery(sql, null)) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String email = c.getString(1);
                String first = c.isNull(2) ? null : c.getString(2);
                String last = c.isNull(3) ? null : c.getString(3);
                String role = c.isNull(4) ? null : c.getString(4);
                String status = c.getString(5);
                String reason = c.isNull(6) ? null : c.getString(6);

                out.add(new RegistrationRequest(id, email, first, last, role, status, reason));
            }


        }
        return out;

    }

    public boolean reapproveRejected(long requestID, String adminEmail, @Nullable String note) {
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
}
