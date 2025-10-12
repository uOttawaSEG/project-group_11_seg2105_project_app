package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "otams.db";
    private static final int DB_VERSION = 1;

    private static final String T_USERS = "users";
    private static final String C_EMAIL = "email";
    private static final String C_PASSWORD = "password";
    private static final String C_ROLE = "role";

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USERS + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_PASSWORD + " TEXT, " +
                C_ROLE + " TEXT)");

        // Add test users (for quick testing)
        db.execSQL("INSERT INTO " + T_USERS +
                " (" + C_EMAIL + ", " + C_PASSWORD + ", " + C_ROLE + ") VALUES " +
                "('admin@uottawa.ca','admin123','admin')," +
                "('student@uottawa.ca','pass123','student')," +
                "('tutor@uottawa.ca','teach123','tutor');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }

    public void saveUser(String role, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(C_EMAIL, email);
        v.put(C_PASSWORD, password);
        v.put(C_ROLE, role);
        db.insertWithOnConflict(T_USERS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + T_USERS + " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean ok = c.moveToFirst();
        c.close();
        db.close();
        return ok;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + C_ROLE + " FROM " + T_USERS + " WHERE email=?",
                new String[]{email});
        String role = c.moveToFirst() ? c.getString(0) : null;
        c.close();
        db.close();
        return role;
    }

    public void seedAdmin() {
        if (getUserRole("admin@uottawa.ca") == null) {
            saveUser("admin", "admin@uottawa.ca", "admin123");
        }
    }
}
