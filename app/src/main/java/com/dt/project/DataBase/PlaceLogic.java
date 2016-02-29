package com.dt.project.DataBase;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import com.dt.project.Place.Place;

import java.util.ArrayList;

public class PlaceLogic extends BaseLogic {

    public PlaceLogic(Activity activity) {
        super(activity);
    }

    // adding the values of a place to the DB
    public long addPlace(Place place) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DB.Places.PLACE_ID, place.getPlaceID());
        contentValues.put(DB.Places.PLACE_NAME, place.getPlaceName());
        contentValues.put(DB.Places.PLACE_VICINITY, place.getPlaceAddress());
        contentValues.put(DB.Places.PLACE_LATITUDE, place.getPlaceLatitude());
        contentValues.put(DB.Places.PLACE_LONGITUDE, place.getPlaceLongitude());
        contentValues.put(DB.Places.PLACE_DISTANCE, place.getPlaceDistance());
        contentValues.put(DB.Places.PLACE_REFERENCE, place.getPlaceReference());
        contentValues.put(DB.Places.PLACE_WEBSITE, place.getPlaceWebSite());
        contentValues.put(DB.Places.PLACE_OPEN_HOURS, place.getPlaceOpenHours());
        contentValues.put(DB.Places.PLACE_PHONE, place.getPlacePhone());
        contentValues.put(DB.Places.PLACE_PHOTO, place.getPlacePhoto());
        contentValues.put(DB.Places.PLACE_RATING, place.getPlaceRating());

        long createdId = dal.insert(DB.Places.TABLE_NAME, contentValues);
        return createdId;
    }
//bring all places from the DB
    public ArrayList<Place> getPlaces(String where,String orderBy) {

        ArrayList<Place> places = new ArrayList<>();

        Cursor cursor = dal.getTable(DB.Places.TABLE_NAME, DB.Places.ALL_COLUMNS,where,orderBy);

        while (cursor.moveToNext()) {

            int idIndex = cursor.getColumnIndex(DB.Places.SQL_ID);
            long sqlID = cursor.getLong(idIndex);
            String placeId = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_ID));
            String placeName = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_NAME));
            String placeVicinity = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_VICINITY));
            double placeLatitude = cursor.getDouble(cursor.getColumnIndex(DB.Places.PLACE_LATITUDE));
            double placeLongitude = cursor.getDouble(cursor.getColumnIndex(DB.Places.PLACE_LONGITUDE));
            double placeDistance = cursor.getDouble(cursor.getColumnIndex(DB.Places.PLACE_DISTANCE));
            String placeReference = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_REFERENCE));
            String placeWebSite = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_WEBSITE));
            String placeOpenHours = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_OPEN_HOURS));
            String placePhone = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_PHONE));
            String placePhoto = cursor.getString(cursor.getColumnIndex(DB.Places.PLACE_PHOTO));
            float placeRating = cursor.getFloat(cursor.getColumnIndex(DB.Places.PLACE_RATING));

            Place place = new Place(sqlID,placeId,placeName,placeVicinity,placeLatitude,placeLongitude,placeDistance,placeReference,placeWebSite,placeOpenHours,placePhone,placePhoto,placeRating);
            places.add(place);
        }
        cursor.close();
        return places;
    }
// delete all places
    public long deleteAll() {

        long affectedRows = dal.delete(DB.Places.TABLE_NAME,null);

        return affectedRows;
    }
    //updating the place information
public long updatePlace(Place place){
    ContentValues contentValues = new ContentValues();
    contentValues.put(DB.Places.PLACE_ID, place.getPlaceID());
    contentValues.put(DB.Places.PLACE_NAME, place.getPlaceName());
    contentValues.put(DB.Places.PLACE_VICINITY, place.getPlaceAddress());
    contentValues.put(DB.Places.PLACE_LATITUDE, place.getPlaceLatitude());
    contentValues.put(DB.Places.PLACE_LONGITUDE, place.getPlaceLongitude());
    contentValues.put(DB.Places.PLACE_DISTANCE, place.getPlaceDistance());
    contentValues.put(DB.Places.PLACE_REFERENCE, place.getPlaceReference());
    contentValues.put(DB.Places.PLACE_WEBSITE, place.getPlaceWebSite());
    contentValues.put(DB.Places.PLACE_OPEN_HOURS, place.getPlaceOpenHours());
    contentValues.put(DB.Places.PLACE_PHONE, place.getPlacePhone());
    contentValues.put(DB.Places.PLACE_PHOTO, place.getPlacePhoto());
    contentValues.put(DB.Places.PLACE_RATING, place.getPlaceRating());

    String where = DB.Places.SQL_ID + "=" + place.getSqlID();

    long affectedRows = dal.update(DB.Places.TABLE_NAME, contentValues, where);

    return affectedRows;
}
//brings all the places by distance
    public ArrayList<Place> getAllPlaces(){
        return getPlaces(null,DB.Places.PLACE_DISTANCE);
    }

}
