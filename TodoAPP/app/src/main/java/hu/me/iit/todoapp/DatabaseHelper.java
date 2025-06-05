package hu.me.iit.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "todo.db";
    public static final int DATABASE_VERSION = 2; // Increment for schema changes
    public static final String TABLE_TODO = "todos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_COMPLETED = "completed";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TASK + " TEXT,"
                + COLUMN_COMPLETED + " INTEGER," // 0 for false, 1 for true
                + COLUMN_USER_EMAIL + " TEXT" + ")";

        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades here (if needed)
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    public long addTask(TodoItem task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task.task);
        values.put(COLUMN_COMPLETED, task.completed ? 1 : 0); // Store as 0 or 1
        values.put(COLUMN_USER_EMAIL, task.userEmail);

        long insertedId = db.insert(TABLE_TODO, null, values);
        db.close();
        return insertedId;
    }

    public List<TodoItem> getTasks(String userEmail) {
        List<TodoItem> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO, new String[]{COLUMN_ID, COLUMN_TASK, COLUMN_COMPLETED},
                COLUMN_USER_EMAIL + "=?", new String[]{userEmail}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK));
                int completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED));
                TodoItem todoItem = new TodoItem(task, completed == 1, userEmail);
                todoItem.id = id; // Set the ID
                tasks.add(todoItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public void updateTask(TodoItem task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task.task);
        values.put(COLUMN_COMPLETED, task.completed ? 1 : 0);

        db.update(TABLE_TODO, values, COLUMN_ID + "=?", new String[]{String.valueOf(task.id)});
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }


    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password); // In real app, HASH the password!

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Return true if insertion successful
    }

    public String getHashedPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD},
                COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);

        String hashedPassword = null;
        if (cursor != null && cursor.moveToFirst()) {
            hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            cursor.close();
        }
        db.close();
        return hashedPassword;
    }

}