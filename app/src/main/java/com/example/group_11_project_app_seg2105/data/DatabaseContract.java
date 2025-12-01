package com.example.group_11_project_app_seg2105.data;

/**
 * Defines the database schema used by the OTAMS application. The database
 * consists of tables for users, profiles, tutor courses, registration requests,
 * availability slots and session requests. Adding new columns or tables
 * requires bumping the {@link #VERSION} value so that {@link DatabaseHelper}
 * performs the appropriate upgrade.
 */
public final class DatabaseContract {

    /**
     * Bump this when you modify the schema. Upgrading from an older
     * version will trigger creation of missing tables/columns via
     * {@link DatabaseHelper#onUpgrade}.
     */
    public static final int VERSION = 10;
    public static final String NAME = "otams.db";

    private DatabaseContract() {}

    /**
     * Core user record used for authentication and role differentiation.
     */
    public static final class Users {
        public static final String TABLE = "users";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String ROLE = "role";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        EMAIL + " TEXT PRIMARY KEY, " +
                        PASSWORD + " TEXT NOT NULL, " +
                        ROLE + " TEXT NOT NULL" +
                        ")";
    }

    /**
     * Extended information for student accounts. Linked to {@link Users} by
     * foreign key on email.
     */
    public static final class StudentProfiles {
        public static final String TABLE = "student_profiles";
        public static final String EMAIL = "email";
        public static final String FIRST = "first_name";
        public static final String LAST = "last_name";
        public static final String PHONE = "phone";
        public static final String PROGRAM = "program";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        EMAIL + " TEXT PRIMARY KEY, " +
                        FIRST + " TEXT, " +
                        LAST + " TEXT, " +
                        PHONE + " TEXT, " +
                        PROGRAM + " TEXT, " +
                        "FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE" +
                        ")";
    }

    /**
     * Extended information for tutor accounts. Includes an average rating
     * column that is updated as students rate completed sessions. Linked
     * to {@link Users} by foreign key on email.
     */
    public static final class TutorProfiles {
        public static final String TABLE = "tutor_profiles";
        public static final String EMAIL = "email";
        public static final String FIRST = "first_name";
        public static final String LAST = "last_name";
        public static final String PHONE = "phone";
        public static final String DEGREE = "degree";
        public static final String RATING = "rating";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        EMAIL + " TEXT PRIMARY KEY, " +
                        FIRST + " TEXT NOT NULL, " +
                        LAST + " TEXT NOT NULL, " +
                        PHONE + " TEXT NOT NULL, " +
                        DEGREE + " TEXT NOT NULL, " +
                        RATING + " REAL NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE" +
                        ")";
    }

    /**
     * Maps tutors to the courses they can teach. Composite primary key
     * enforces uniqueness of (email, course) combinations.
     */
    public static final class TutorCourses {
        public static final String TABLE = "tutor_courses";
        public static final String EMAIL = "email";
        public static final String COURSE = "course";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        EMAIL + " TEXT NOT NULL, " +
                        COURSE + " TEXT NOT NULL, " +
                        "PRIMARY KEY (" + EMAIL + ", " + COURSE + "), " +
                        "FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE" +
                        ")";
    }

    /**
     * Registration requests allow students and tutors to request approval
     * to join the system. Requests can be pending, approved or rejected.
     */
    public static final class RegistrationRequests {
        public static final String TABLE = "registration_requests";
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String CREATED_AT = "created_at";
        public static final String STATUS = "status";
        public static final String REASON = "reason";
        public static final String DECIDED_BY = "decided_by_admin";
        public static final String DECIDED_AT = "decided_at";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EMAIL + " TEXT NOT NULL, " +
                        CREATED_AT + " INTEGER NOT NULL, " +
                        STATUS + " TEXT NOT NULL CHECK(" + STATUS + " IN ('PENDING','APPROVED','REJECTED')), " +
                        REASON + " TEXT, " +
                        DECIDED_BY + " TEXT, " +
                        DECIDED_AT + " INTEGER, " +
                        "FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE" +
                        ")";
        public static final String INDEX_STATUS =
                "CREATE INDEX IF NOT EXISTS idx_reg_status ON " + TABLE + "(" + STATUS + ")";
    }

    /**
     * Table capturing tutor availability slots. Each row represents a time
     * interval during which a tutor is available to hold a session. Slots
     * are uniquely identified by the combination of tutor, date, start and end.
     */
    public static final class TutorAvailability {
        public static final String TABLE = "tutor_availability";
        public static final String ID = "id";
        public static final String TUTOR_EMAIL = "tutor_email";
        public static final String DATE = "date";
        public static final String START = "start_time";
        public static final String END = "end_time";
        public static final String AUTO_APPROVE = "auto_approve";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TUTOR_EMAIL + " TEXT NOT NULL, " +
                        DATE + " TEXT NOT NULL, " +
                        START + " TEXT NOT NULL, " +
                        END + " TEXT NOT NULL, " +
                        AUTO_APPROVE + " INTEGER NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY(" + TUTOR_EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE" +
                        ")";
        public static final String INDEX_TUTOR_DATE =
                "CREATE INDEX IF NOT EXISTS idx_tutor_availability_tutor_date ON " +
                        TABLE + "(" + TUTOR_EMAIL + ", " + DATE + ")";
    }

    /**
     * A session request represents a student's intent to book a specific availability slot.
     * Each request references the slot via its ID and records the tutor, date/time,
     * status and optional rating once the session is completed.
     */
    public static final class SessionRequests {
        public static final String TABLE = "session_requests";
        public static final String ID = "id";
        public static final String STUDENT_EMAIL = "student_email";
        public static final String TUTOR_EMAIL = "tutor_email";
        public static final String DATE = "date";
        public static final String START = "start_time";
        public static final String END = "end_time";
        public static final String SLOT_ID = "slot_id";
        public static final String STATUS = "status";
        public static final String RATING = "rating";
        public static final String CREATE =
                "CREATE TABLE " + TABLE + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        STUDENT_EMAIL + " TEXT NOT NULL, " +
                        TUTOR_EMAIL + " TEXT NOT NULL, " +
                        DATE + " TEXT NOT NULL, " +
                        START + " TEXT NOT NULL, " +
                        END + " TEXT NOT NULL, " +
                        SLOT_ID + " INTEGER NOT NULL, " +
                        STATUS + " TEXT NOT NULL, " +
                        RATING + " REAL, " +
                        "FOREIGN KEY(" + SLOT_ID + ") REFERENCES " + TutorAvailability.TABLE + "(" + TutorAvailability.ID + ") ON DELETE CASCADE" +
                        ")";
    }
}