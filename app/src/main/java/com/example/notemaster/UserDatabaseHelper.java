package com.example.notemaster;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SIGNATURE = "signature";
    private static final String COLUMN_PASSWORD = "password"; // 存储密码需要加密

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT"
                +")";
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(String username, String email, String password,String signature) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password); // 确保密码是加密的
        values.put(COLUMN_EMAIL, email);
        //values.put(COLUMN_SIGNATURE,signature);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        if (result != -1) {
            // 添加成功，result 是新创建的行的ID
            Log.e(TAG,"添加成功");
        } else {
            // 添加失败
            Log.e(TAG,"添加成功");
        }
    }

    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    public void updateUser(String username, String newPassword, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        values.put(COLUMN_EMAIL, newEmail);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    public void updateUserSignature(String username, String newSig) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SIGNATURE, newSig);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    public void updateUserPassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
    }

    public void updateUsername(String userId, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);

        // 执行更新操作，基于用户ID
        db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{userId}
        );

        db.close();
    }


    public int verifyUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (passwordIndex >= 0) {
                String storedPassword = cursor.getString(passwordIndex);
                if (storedPassword.equals(password)) {
                    cursor.close();
                    return 2; // Username and password are correct
                } else {
                    cursor.close();
                    return 1; // Username exists but password is incorrect
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return 0; // Username does not exist or other error occurred
    }

    public Cursor getUserDetailsByUserName(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_USERNAME, COLUMN_SIGNATURE};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {userName};
        Cursor cursor = db.query(
                TABLE_USERS,   // The table to query
                projection,             // The columns to return
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null                    // The sort order
        );
        return cursor;
    }

    public Cursor getUserID(String userName){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {userName};
        Cursor cursor = db.query(
                TABLE_USERS,   // The table to query
                projection,    // The columns to return (in this case, only COLUMN_ID)
                selection,     // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null,          // don't group the rows
                null,          // don't filter by row groups
                null           // The sort order
        );
        return cursor;
    }

}