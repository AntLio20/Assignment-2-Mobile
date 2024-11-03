package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    // SQLDatabase info
    private static final String DATABASE_NAME = "locations_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "locations";

    // 4 database columns: id, address, latitude, longitude
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating the 4 database columns and setting their expected values
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL)";
        db.execSQL(CREATE_TABLE);
    }

    // Function that defines what to do when upgrading to the newest database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Function to add a location to the database
    public long addLoc(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

        // Insert info into the database, retrieving the result (result = -1 if failed)
        long result = db.insert(TABLE_NAME, null, values);
        db.close(); // Close database connection
        return result;
    }

    public boolean locationExists(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to check if an entry exists with a matching address or latitude and longitude
        String query = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_ADDRESS + " = ? " +
                "OR (" + COLUMN_LATITUDE + " = ? AND " + COLUMN_LONGITUDE + " = ?)";
        Cursor cursor = db.rawQuery(query, new String[] {
                address,
                String.valueOf(latitude),
                String.valueOf(longitude)
        });

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        // Return 1 for yes, 0 for no
        return exists;
    }

    // Check if already in DB only used in .txt insertion where the latitude and longitude arent provided
    public boolean locationNameExists(String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_ADDRESS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { address });

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    // Function to delete a location from the database
    public boolean delLoc(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Perform the delete operation and capture the number of rows affected
        int rowsAffected = db.delete(TABLE_NAME, COLUMN_ADDRESS + " = ?", new String[]{address});
        db.close();

        // Return 1 for success, 0 for failure
        return rowsAffected > 0;
    }

    // Function to update a particular database entry
    public boolean updateLoc(String originalAddress, String newAddress, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, newAddress);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

        // Use the original address to find the entry num then update it with the new address
        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ADDRESS + " = ?", new String[]{originalAddress});
        db.close();
        return rowsAffected > 0; // Return 1 for success if a row was updated
    }

    // Function to retrieve all entries in the database
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ADDRESS, COLUMN_LATITUDE, COLUMN_LONGITUDE}, null, null, null, null, null);

        // Iterate over each object in the database
        if (cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                locations.add(new Location(address, latitude, longitude));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return locations;
    }
}
