package com.example.todolistapp;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tasks.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE tasks(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT," +
            "description TEXT," +
            "priority TEXT," +
            "duedate TEXT," +
            "status INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    /** Insert a new task. Returns the new row id. */
    public long addTask(String title, String description, String priority, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("priority", priority);
        cv.put("duedate", date);
        cv.put("status", 0);
        return db.insert("tasks", null, cv);
    }

    /** Fetch all tasks ordered by id descending (newest first). */
    public Cursor getTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks ORDER BY id DESC", null);
    }

    /** Fetch tasks filtered by status: 0 = active, 1 = completed. */
    public Cursor getTasksByStatus(int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM tasks WHERE status=? ORDER BY id DESC",
            new String[]{String.valueOf(status)}
        );
    }

    /** Delete a task by id. */
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id=?", new String[]{String.valueOf(id)});
    }

    /** Toggle completion status. */
    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("tasks", cv, "id=?", new String[]{String.valueOf(id)});
    }

    /** Update task title, description, priority and duedate. */
    public void updateTask(int id, String title, String description, String priority, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("priority", priority);
        cv.put("duedate", date);
        db.update("tasks", cv, "id=?", new String[]{String.valueOf(id)});
    }

    /** Count active (incomplete) tasks. */
    public int countActiveTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE status=0", null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }
}