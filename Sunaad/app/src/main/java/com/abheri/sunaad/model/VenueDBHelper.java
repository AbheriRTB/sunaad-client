package com.abheri.sunaad.model;

/**
 * Created by prasanna.ramaswamy on 22/08/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class VenueDBHelper {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public VenueDBHelper(Context context) {
        dbHelper = new DBHelper(context);
    }

    public VenueDBHelper(Context context, SQLiteDatabase db) {
        dbHelper = new DBHelper(context);
        database = db;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Venue createVenue(int id, String name, String desc, String ispublished,
                                 String address1, String address2,
                                 String city, String state, String country, String pincode,
                                 String mapcoords, String image) {
        ContentValues values = new ContentValues();
        values.put(SQLStrings.COLUMN_VENUE_ID, id);
        values.put(SQLStrings.COLUMN_VENUE_NAME, name);
        values.put(SQLStrings.COLUMN_VENUE_DESCRIPTION, desc);
        values.put(SQLStrings.COLUMN_VENUE_IS_PUBLISHED, ispublished);
        values.put(SQLStrings.COLUMN_VENUE_ADDRESS1, address1);
        values.put(SQLStrings.COLUMN_VENUE_ADDRESS2, address2);
        values.put(SQLStrings.COLUMN_VENUE_CITY,city);
        values.put(SQLStrings.COLUMN_VENUE_STATE, state);
        values.put(SQLStrings.COLUMN_VENUE_COUNTRY, country);
        values.put(SQLStrings.COLUMN_VENUE_PINCODE, pincode);
        values.put(SQLStrings.COLUMN_VENUE_MAPCOORDS, mapcoords);
        values.put(SQLStrings.COLUMN_VENUE_IMAGE, image);

        long insertId = database.insert(SQLStrings.TABLE_VENUE, null,
                values);
        Cursor cursor = database.query(SQLStrings.TABLE_VENUE,
                SQLStrings.venueAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        Venue newVenue = cursorToVenue(cursor);
        cursor.close();
        return newVenue;
    }

    public Venue createVenue(int id, String name, String art_type, String desc, String ispublished) {
        ContentValues values = new ContentValues();
        values.put(SQLStrings.COLUMN_VENUE_ID, id);
        values.put(SQLStrings.COLUMN_VENUE_NAME, name);
        values.put(SQLStrings.COLUMN_VENUE_DESCRIPTION, desc);
        values.put(SQLStrings.COLUMN_VENUE_IS_PUBLISHED, ispublished);
        long insertId = database.insert(SQLStrings.TABLE_VENUE, null,
                values);
        Cursor cursor = database.query(SQLStrings.TABLE_VENUE,
                SQLStrings.venueAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        Venue newVenue = cursorToVenue(cursor);
        cursor.close();
        return newVenue;
    }

    public void deleteVenue(Venue Venue) {
        /*long id = Venue.getMinutes();
        System.out.println("Topic deleted with id: " + minutes);
        database.delete(DBHelper.TABLE_ARTISTE, DBHelper.COLUMN_ALARM_BEFORE
                + " = " + id, null); */
        deleteAllVenue();
    }

    public void deleteAllVenue() {
        List<Venue> allrows = getAllVenue();
        if(allrows != null && allrows.size() > 0) {
            int nrows = database.delete(SQLStrings.TABLE_VENUE, "1", null);
            System.out.println(nrows + " Venues deleted");
        }
    }

    public List<Venue> getAllVenue() {
        List<Venue> venue = new ArrayList<Venue>();

        Cursor cursor = database.query(SQLStrings.TABLE_VENUE,
                SQLStrings.venueAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Venue venue1 = cursorToVenue(cursor);
            venue.add(venue1);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return venue;
    }

    private Venue cursorToVenue(Cursor cursor) {
        Venue Venue = new Venue();
        Venue.setId(cursor.getInt(0));
        Venue.setVenue_name(cursor.getString(1));
        Venue.setIs_published(cursor.getString(2));

        return Venue;
    }
}