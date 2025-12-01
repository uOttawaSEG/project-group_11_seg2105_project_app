package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteOpenHelper implementation for the OTAMS application. This helper
 * ensures all tables defined in {@link DatabaseContract} are created and
 * provides simple database version upgrade logic. Default users and
 * profiles are seeded on first creation for convenience.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DB";

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.NAME, null, DatabaseContract.VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enforce foreign key constraints. Without this the ON DELETE CASCADE
        // clauses defined in our schema will be ignored.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create core tables
        db.execSQL(DatabaseContract.Users.CREATE);
        db.execSQL(DatabaseContract.StudentProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorCourses.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.INDEX_STATUS);
        db.execSQL(DatabaseContract.TutorAvailability.CREATE);
        db.execSQL(DatabaseContract.TutorAvailability.INDEX_TUTOR_DATE);
        db.execSQL(DatabaseContract.SessionRequests.CREATE);

        seedDefaults(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Ensure core tables exist when upgrading from very early versions. If you
        // are upgrading from a version that predates the existence of some
        // tables, create them. Since the CREATE statements use IF NOT EXISTS,
        // running them again is safe.
        db.execSQL(DatabaseContract.Users.CREATE);
        db.execSQL(DatabaseContract.StudentProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorCourses.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.INDEX_STATUS);
        db.execSQL(DatabaseContract.TutorAvailability.CREATE);
        db.execSQL(DatabaseContract.TutorAvailability.INDEX_TUTOR_DATE);
        db.execSQL(DatabaseContract.SessionRequests.CREATE);

        // Ensure the rating column exists on tutor_profiles. Versions prior to 10
        // did not include this column. We attempt to add it here; if it already
        // exists the ALTER statement will throw and we ignore the error.
        ensureColumn(db,
                DatabaseContract.TutorProfiles.TABLE,
                DatabaseContract.TutorProfiles.RATING,
                "REAL NOT NULL DEFAULT 0");
    }

    /**
     * Add a column to a table if it does not already exist. The type
     * definition should include any constraints (e.g. NOT NULL DEFAULT ...).
     */
    private void ensureColumn(SQLiteDatabase db, String table, String column, String typeDefinition) {
        try {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + typeDefinition);
        } catch (Exception ignored) {
            // Column already exists or table missing; ignore in upgrade context
        }
    }

    /**
     * Seed some default users and profiles so the application can be used
     * immediately after installation. In a production app these would not
     * typically be inserted automatically.
     */
    private void seedDefaults(SQLiteDatabase db) {
        insertUser(db, "admin@uottawa.ca", "admin123", "admin");
        insertUser(db, "student@uottawa.ca", "pass123", "student");
        insertUser(db, "tutor@uottawa.ca", "teach123", "tutor");

        // Create corresponding profiles. Only the tutor profile includes a
        // predefined rating to demonstrate the rating column. Students do not
        // require a profile for basic functionality.
        ContentValues tutorProfile = new ContentValues();
        tutorProfile.put(DatabaseContract.TutorProfiles.EMAIL, "tutor@uottawa.ca");
        tutorProfile.put(DatabaseContract.TutorProfiles.FIRST, "Jane");
        tutorProfile.put(DatabaseContract.TutorProfiles.LAST, "Tutor");
        tutorProfile.put(DatabaseContract.TutorProfiles.PHONE, "555‑1234");
        tutorProfile.put(DatabaseContract.TutorProfiles.DEGREE, "B.Sc.");
        tutorProfile.put(DatabaseContract.TutorProfiles.RATING, 4.5);
        db.insertWithOnConflict(
                DatabaseContract.TutorProfiles.TABLE,
                null,
                tutorProfile,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Insert a user into the users table. If a user already exists with the
     * same email this insert is ignored.
     */
    private void insertUser(SQLiteDatabase db, String email, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.EMAIL, email);
        values.put(DatabaseContract.Users.PASSWORD, password);
        values.put(DatabaseContract.Users.ROLE, role);
        db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
}