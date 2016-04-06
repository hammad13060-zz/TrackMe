package com.iiitd.hammad13060.trackme.dbHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iiitd.hammad13060.trackme.entities.Journey;

import java.net.ContentHandler;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hammad on 8/3/16.
 */
public class JourneyDBHandler extends SQLiteOpenHelper {

    private static final String TAG = JourneyDBHandler.class.getName();

    private Context context = null;

    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "journey.db";
    private static final String TABLE_JOURNEY = "journeys";

    //columns

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_JOURNEY_TOPIC = "journey_topic";
    private static final String COLUMN_FROM = "_from";
    private static final String COLUMN_SRC_LAT = "src_lat";
    private static final String COLUMN_SRC_LONG = "src_long";
    private static final String COLUMN_DST_LAT = "dst_lat";
    private static final String COLUMN_DST_LONG = "dst_long";
    private static final String COLUMN_CURRENT_LAT = "current_lat";
    private static final String COLUMN_CURRENT_LONG = "current_long";



    public JourneyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_JOURNEY_TABLE = "CREATE TABLE " + TABLE_JOURNEY + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_JOURNEY_TOPIC + " TEXT, " +
                COLUMN_FROM + " TEXT, " +
                COLUMN_SRC_LAT + " Double, " +
                COLUMN_SRC_LONG + " Double, " +
                COLUMN_DST_LAT + " Double, " +
                COLUMN_DST_LONG + " Double, " +
                COLUMN_CURRENT_LAT + " Double, " +
                COLUMN_CURRENT_LONG + " Double " + ");";

        db.execSQL(CREATE_JOURNEY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNEY + ";");
        onCreate(db);
    }

    public void insertJourney(Journey journey) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_JOURNEY_TOPIC, journey.get_journey_topic());
        values.put(COLUMN_FROM, journey.get_from());
        values.put(COLUMN_SRC_LAT, journey.get_src_lat());
        values.put(COLUMN_SRC_LONG, journey.get_src_long());
        values.put(COLUMN_DST_LAT, journey.get_dst_lat());
        values.put(COLUMN_DST_LONG, journey.get_dst_long());
        values.put(COLUMN_CURRENT_LAT, journey.get_current_lat());
        values.put(COLUMN_CURRENT_LONG, journey.get_current_long());

        try {
            db.insert(TABLE_JOURNEY, null, values);
            Log.d(TAG, "insertion of journey succeeded");
        } catch(SQLiteException e) {
            Log.d(TAG, "insertion of journey failed");
            e.printStackTrace();
        }

        db.close();
    }

    public List<Journey> getAllJourneys(){
        String query = "SELECT * FROM " + TABLE_JOURNEY + ";";
        List<Journey> journeyList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while(!c.isAfterLast()) {
            Journey journey = cursorToJourney(c);
            journeyList.add(journey);
            c.moveToNext();
        }

        c.close();
        db.close();

        return journeyList;
    }

    public Journey deleteJourney(String journey_topic) {

        //finding the record for return
        String findQuery = "SELECT * FROM " + TABLE_JOURNEY + " WHERE " + COLUMN_JOURNEY_TOPIC + "=\'" + journey_topic +"\';";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(findQuery, null);

        if (c.isAfterLast())
            return null;

        c.moveToFirst();
        Journey journey = cursorToJourney(c);


        //deleting record
        String deleteQuery = "DELETE FROM " + TABLE_JOURNEY + " WHERE " + COLUMN_JOURNEY_TOPIC + "=\'" + journey_topic + "\';";
        db.execSQL(deleteQuery);
        db.close();
        c.close();


        return journey;
    }

    public boolean updateCurrentLocation(Journey journey) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_LAT, journey.get_current_lat());
        values.put(COLUMN_CURRENT_LONG, journey.get_current_long());

        db.update(TABLE_JOURNEY, values, COLUMN_JOURNEY_TOPIC + " = ?", new String[] {journey.get_journey_topic()});

        return true;
    }


    private Journey cursorToJourney(Cursor c) {
        return new Journey(
                c.getString(c.getColumnIndex(COLUMN_JOURNEY_TOPIC)),
                c.getString(c.getColumnIndex(COLUMN_FROM)),
                c.getDouble(c.getColumnIndex(COLUMN_SRC_LAT)),
                c.getDouble(c.getColumnIndex(COLUMN_SRC_LONG)),
                c.getDouble(c.getColumnIndex(COLUMN_DST_LAT)),
                c.getDouble(c.getColumnIndex(COLUMN_DST_LONG)),
                c.getDouble(c.getColumnIndex(COLUMN_CURRENT_LAT)),
                c.getDouble(c.getColumnIndex(COLUMN_CURRENT_LONG))
        );
    }
}
