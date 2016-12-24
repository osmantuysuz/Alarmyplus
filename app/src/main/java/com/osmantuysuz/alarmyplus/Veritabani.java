package com.osmantuysuz.alarmyplus;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Veritabani extends SQLiteOpenHelper {

    private static final String VERITABANI_ISMI="veritabanim";
    private static final String TABLE_NAME = "tablo_ismi";
    private static final int VERITABANI_VERSION=1;

    private static final String COLUMN_NAME_ALARM_NAME ="alarm_ismi" ;
    private static final String _ID ="_id" ;
    private static final String COLUMN_NAME_ALARM_TIME_HOUR ="saat" ;
    private static final String COLUMN_NAME_ALARM_TIME_MINUTE = "dakika";
    private static final String COLUMN_NAME_ALARM_REPEAT_DAYS ="gun" ;
    private static final String COLUMN_NAME_ALARM_REPEAT_WEEKLY = "tekrar";
    private static final String COLUMN_NAME_ALARM_TONE = "ton";
    private static final String COLUMN_NAME_ALARM_ENABLED = "aktif";



    private static final String SQL_CREATE_ALARM = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME_ALARM_NAME + " TEXT," +
            COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
            COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
            COLUMN_NAME_ALARM_REPEAT_DAYS + " TEXT," +
            COLUMN_NAME_ALARM_REPEAT_WEEKLY + " BOOLEAN," +
            COLUMN_NAME_ALARM_TONE + " TEXT," +
            COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" +
            " )";
    private static final String SQL_DELETE_ALARM =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public Veritabani(Context context) {
        super(context, VERITABANI_ISMI, null, VERITABANI_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }

    public long createAlarm(AlarmModel alarmModel) {
        ContentValues values = populateContent(alarmModel);
        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public long updateAlarm(AlarmModel alarmModel) {
        ContentValues values = populateContent(alarmModel);
        return getWritableDatabase().update(TABLE_NAME, values, _ID + " = ?", new String[] { String.valueOf(alarmModel.id) });
    }

    public AlarmModel getAlarm(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + TABLE_NAME + " WHERE " + _ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }

        return null;
    }

    public List<AlarmModel> getAlarms() {

        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<AlarmModel> alarmList = new ArrayList<AlarmModel>();

        while (c.moveToNext()) {
            alarmList.add(populateModel(c));
        }

        if (!alarmList.isEmpty()) {
            return alarmList;
        }

        return null;
    }

    public int deleteAlarm(long id) {
        return getWritableDatabase().delete(TABLE_NAME, _ID + " = ?", new String[] { String.valueOf(id) });
    }



    private ContentValues populateContent(AlarmModel model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ALARM_NAME, model.name);
        values.put(COLUMN_NAME_ALARM_TIME_HOUR, model.timeHour);
        values.put(COLUMN_NAME_ALARM_TIME_MINUTE, model.timeMinute);
        values.put(COLUMN_NAME_ALARM_REPEAT_WEEKLY, model.haftalikTekrar);
        values.put(COLUMN_NAME_ALARM_TONE, model.alarmTone != null ? model.alarmTone.toString() : "");
        values.put(COLUMN_NAME_ALARM_ENABLED, model.isEnabled);

        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
            repeatingDays += model.getRepeatingDay(i) + ",";
        }
        values.put(COLUMN_NAME_ALARM_REPEAT_DAYS, repeatingDays);

        return values;
    }

    private AlarmModel populateModel(Cursor c) {
        AlarmModel model = new AlarmModel();
        model.id = c.getLong(c.getColumnIndex(_ID));
        model.name = c.getString(c.getColumnIndex(COLUMN_NAME_ALARM_NAME));
        model.timeHour = c.getInt(c.getColumnIndex(COLUMN_NAME_ALARM_TIME_HOUR));
        model.timeMinute = c.getInt(c.getColumnIndex(COLUMN_NAME_ALARM_TIME_MINUTE));
        model.haftalikTekrar = c.getInt(c.getColumnIndex(COLUMN_NAME_ALARM_REPEAT_WEEKLY)) == 0 ? false : true;
        model.alarmTone = c.getString(c.getColumnIndex(COLUMN_NAME_ALARM_TONE)) != "" ? Uri.parse(c.getString(c.getColumnIndex(COLUMN_NAME_ALARM_TONE))) : null;
        model.isEnabled = c.getInt(c.getColumnIndex(COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true;

        String[] repeatingDays = c.getString(c.getColumnIndex(COLUMN_NAME_ALARM_REPEAT_DAYS)).split(",");
        for (int i = 0; i < repeatingDays.length; ++i) {
            model.setRepeatingDay(i, repeatingDays[i].equals("false") ? false : true);
        }

        return model;
    }
}
