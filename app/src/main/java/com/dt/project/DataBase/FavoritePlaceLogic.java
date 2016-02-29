package com.dt.project.DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import com.dt.project.FavoritePlace.FavoritePlace;

import java.util.ArrayList;


public class FavoritePlaceLogic extends BaseLogic{

    public FavoritePlaceLogic(Activity activity) {
        super(activity);
    }
    // adding the values of a favoritePlace to the DB
    public long addFavoritePlace(FavoritePlace favoritePlace) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DB.FavoritePlaces.PLACE_ID, favoritePlace.getPlaceID());
        contentValues.put(DB.FavoritePlaces.PLACE_NAME, favoritePlace.getPlaceName());
        contentValues.put(DB.FavoritePlaces.PLACE_VICINITY, favoritePlace.getPlaceAddress());
        contentValues.put(DB.FavoritePlaces.PLACE_LATITUDE, favoritePlace.getPlaceLatitude());
        contentValues.put(DB.FavoritePlaces.PLACE_LONGITUDE, favoritePlace.getPlaceLongitude());
        contentValues.put(DB.FavoritePlaces.PLACE_DISTANCE, favoritePlace.getPlaceDistance());
        contentValues.put(DB.FavoritePlaces.PLACE_REFERENCE, favoritePlace.getPlaceReference());
        contentValues.put(DB.FavoritePlaces.PLACE_WEBSITE, favoritePlace.getPlaceWebSite());
        contentValues.put(DB.FavoritePlaces.PLACE_OPEN_HOURS, favoritePlace.getPlaceOpenHours());
        contentValues.put(DB.FavoritePlaces.PLACE_PHONE, favoritePlace.getPlacePhone());
        contentValues.put(DB.FavoritePlaces.PLACE_PHOTO, favoritePlace.getPlacePhoto());
        contentValues.put(DB.FavoritePlaces.PLACE_RATING, favoritePlace.getPlaceRating());

        long createdId = dal.insert(DB.FavoritePlaces.TABLE_NAME, contentValues);
        return createdId;
    }
//geting all the favorite places from the DB
    public ArrayList<FavoritePlace> getFavoritePlaces(String where, String orderBy) {

        ArrayList<FavoritePlace> places = new ArrayList<>();

        Cursor cursor = dal.getTable(DB.FavoritePlaces.TABLE_NAME, DB.FavoritePlaces.ALL_COLUMNS,where,orderBy);

        while (cursor.moveToNext()) {

            int idIndex = cursor.getColumnIndex(DB.FavoritePlaces.SQL_ID);
            long sqlID = cursor.getLong(idIndex);
            String placeId = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_ID));
            String placeName = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_NAME));
            String placeVicinity = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_VICINITY));
            double placeLatitude = cursor.getDouble(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_LATITUDE));
            double placeLongitude = cursor.getDouble(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_LONGITUDE));
            double placeDistance = cursor.getDouble(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_DISTANCE));
            String placeReference = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_REFERENCE));
            String placeWebSite = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_WEBSITE));
            String placeOpenHours = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_OPEN_HOURS));
            String placePhone = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_PHONE));
            String placePhoto = cursor.getString(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_PHOTO));
            float placeRating = cursor.getFloat(cursor.getColumnIndex(DB.FavoritePlaces.PLACE_RATING));

            FavoritePlace favoritePlace = new FavoritePlace(sqlID,placeId,placeName,placeVicinity,placeLatitude,placeLongitude,placeDistance,placeReference,placeWebSite,placeOpenHours,placePhone,placePhoto,placeRating);
            places.add(favoritePlace);

        }
        cursor.close();
        return places;
    }
//delete all items from DB
    public long deleteAllFavorites() {

        long affectedRows = dal.delete(DB.FavoritePlaces.TABLE_NAME,null);

        return affectedRows;
    }
// delete one item from DB
    public long deleteOneItemFavorite(long sql_id) {
        String where =DB.FavoritePlaces.SQL_ID+" = "+sql_id;

        long affectedRows = dal.delete(DB.FavoritePlaces.TABLE_NAME,where);

        return affectedRows;
    }
    //updating favorite place
public long updateFavoritePlace(FavoritePlace favoritePlace){
    ContentValues contentValues = new ContentValues();
    contentValues.put(DB.FavoritePlaces.PLACE_ID, favoritePlace.getPlaceID());
    contentValues.put(DB.FavoritePlaces.PLACE_NAME, favoritePlace.getPlaceName());
    contentValues.put(DB.FavoritePlaces.PLACE_VICINITY, favoritePlace.getPlaceAddress());
    contentValues.put(DB.FavoritePlaces.PLACE_LATITUDE, favoritePlace.getPlaceLatitude());
    contentValues.put(DB.FavoritePlaces.PLACE_LONGITUDE, favoritePlace.getPlaceLongitude());
    contentValues.put(DB.FavoritePlaces.PLACE_DISTANCE, favoritePlace.getPlaceDistance());
    contentValues.put(DB.FavoritePlaces.PLACE_REFERENCE, favoritePlace.getPlaceReference());
    contentValues.put(DB.FavoritePlaces.PLACE_WEBSITE, favoritePlace.getPlaceWebSite());
    contentValues.put(DB.FavoritePlaces.PLACE_OPEN_HOURS, favoritePlace.getPlaceOpenHours());
    contentValues.put(DB.FavoritePlaces.PLACE_PHONE, favoritePlace.getPlacePhone());
    contentValues.put(DB.FavoritePlaces.PLACE_PHOTO, favoritePlace.getPlacePhoto());
    contentValues.put(DB.FavoritePlaces.PLACE_RATING, favoritePlace.getPlaceRating());

    String where = DB.FavoritePlaces.SQL_ID + "=" + favoritePlace.getSqlID();

    long affectedRows = dal.update(DB.FavoritePlaces.TABLE_NAME, contentValues, where);

    return affectedRows;

}

//bring all the favorite places by the distance
    public ArrayList<FavoritePlace> getAllPlaces(){
        return getFavoritePlaces(null, DB.FavoritePlaces.PLACE_DISTANCE);
    }
}
