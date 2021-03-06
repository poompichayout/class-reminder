/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.classreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReminderDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "ReminderDatabase";

    // Table name
    private static final String TABLE_REMINDERS = "ReminderTable";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME_START = "time_start";
    private static final String KEY_TIME_END = "time_end";
    private static final String KEY_COLOR = "color";
    private static final String KEY_APPLICATION_TITLE = "app_title";
    private static final String KEY_CLASS_DESCRIPTION = "class_description";
    private static final String KEY_INSTRUCTOR_NAME = "instructor_name";
    private static final String KEY_ACTIVE = "active";

    // Date Column Delimiter
    private static final String DATE_DELIMITER = ", ";

    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME_START + " INTEGER,"
                + KEY_TIME_END + " INTEGER,"
                + KEY_COLOR + " TEXT,"
                + KEY_APPLICATION_TITLE + " TEXT,"
                + KEY_CLASS_DESCRIPTION + " TEXT,"
                + KEY_INSTRUCTOR_NAME + " TEXT,"
                + KEY_ACTIVE + " BOOLEAN" + ")";
        db.execSQL(CREATE_REMINDERS_TABLE);
        System.out.println("CREATED DATABASE");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        // Create tables again
        onCreate(db);
    }

    // Adding new Reminder
    public int addReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE , reminder.getTitle());
        values.put(KEY_DATE , TextUtils.join(DATE_DELIMITER, reminder.getDate()));
        values.put(KEY_TIME_START , reminder.getTimeStart());
        values.put(KEY_TIME_END , reminder.getTimeEnd());
        values.put(KEY_COLOR, reminder.getColor());
        values.put(KEY_APPLICATION_TITLE, reminder.getApplicationTitle());
        values.put(KEY_CLASS_DESCRIPTION, reminder.getClassDescription());
        values.put(KEY_INSTRUCTOR_NAME, reminder.getInstructorName());
        values.put(KEY_ACTIVE, reminder.getActive());

        // Inserting Row
        long ID = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        System.out.println("Database id: " + ID);
        return (int) ID;
    }

    // Getting single Reminder
    public Reminder getReminder(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDERS, new String[]
                        {
                                KEY_ID,
                                KEY_TITLE,
                                KEY_DATE,
                                KEY_TIME_START,
                                KEY_TIME_END,
                                KEY_COLOR,
                                KEY_APPLICATION_TITLE,
                                KEY_CLASS_DESCRIPTION,
                                KEY_INSTRUCTOR_NAME,
                                KEY_ACTIVE
                        }, KEY_ID + "=?",

                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2).split(DATE_DELIMITER), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                cursor.getString(9));

        return reminder;
    }

    // Getting all Reminders
    public List<Reminder> getAllReminders(){
        List<Reminder> reminderList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Reminder reminder = new Reminder();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDate(cursor.getString(2).split(DATE_DELIMITER));
                reminder.setTimeStart(cursor.getString(3));
                reminder.setTimeEnd(cursor.getString(4));
                reminder.setColor(cursor.getString(5));
                reminder.setApplicationTitle(cursor.getString(6));
                reminder.setClassDescription(cursor.getString(7));
                reminder.setInstructorName(cursor.getString(8));
                reminder.setActive(cursor.getString(9));

                // Adding Reminders to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }

    // Getting Reminders Count
    public int getRemindersCount(){
        String countQuery = "SELECT * FROM " + TABLE_REMINDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // Updating single Reminder
    public int updateReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE , reminder.getTitle());
        values.put(KEY_DATE , TextUtils.join(DATE_DELIMITER, reminder.getDate()));
        values.put(KEY_TIME_START , reminder.getTimeStart());
        values.put(KEY_TIME_END , reminder.getTimeEnd());
        values.put(KEY_COLOR, reminder.getColor());
        values.put(KEY_APPLICATION_TITLE, reminder.getApplicationTitle());
        values.put(KEY_CLASS_DESCRIPTION, reminder.getClassDescription());
        values.put(KEY_INSTRUCTOR_NAME, reminder.getInstructorName());
        values.put(KEY_ACTIVE, reminder.getActive());

        // Updating row
        return db.update(TABLE_REMINDERS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getID())});
    }

    // Deleting single Reminder
    public void deleteReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getID())});
        db.close();
    }

    // Get reminder on specific date
    public List<Reminder> getAllRemindersOnDate(Calendar calendar) {
        List<Reminder> reminderList = new ArrayList<>();

        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String weekdays[] = dfs.getWeekdays();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String nameOfDay = weekdays[day];
        System.out.println(nameOfDay);

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS
                + " WHERE " + KEY_DATE + " LIKE \'%" + nameOfDay + "%\';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Reminder reminder = new Reminder();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDate(cursor.getString(2).split(DATE_DELIMITER));
                reminder.setTimeStart(cursor.getString(3));
                reminder.setTimeEnd(cursor.getString(4));
                reminder.setColor(cursor.getString(5));
                reminder.setApplicationTitle(cursor.getString(6));
                reminder.setClassDescription(cursor.getString(7));
                reminder.setInstructorName(cursor.getString(8));
                reminder.setActive(cursor.getString(9));

                // Adding Reminders to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        return reminderList;
    }
}
