package com.dt.project.DataBase;


public class DB {
    public static final String NAME = "PlacesDatabase.db"; // Database file name.
    public static final int VERSION = 1; // Places Database version.

    // Inner Class Places
    public static class Places {

        public static final String TABLE_NAME = "Places"; // Table name.
        public static final String SQL_ID = "sqlID"; // id column name (this is the primary key)
        public static final String PLACE_ID = "placeId"; // id of the place column
        public static final String PLACE_NAME = "placeName"; // name of the place column
        public static final String PLACE_VICINITY = "placeVicinity"; // vicinity of the place column
        public static final String PLACE_LATITUDE = "placeLatitude"; // latitude of the place column
        public static final String PLACE_LONGITUDE = "placeLongitude"; // longitude of the place column
        public static final String PLACE_DISTANCE = "placeDistance"; // distance of the place column
        public static final String PLACE_REFERENCE = "placeReference"; // reference of the place column
        public static final String PLACE_WEBSITE = "placeWebSite"; // website of the place column
        public static final String PLACE_OPEN_HOURS = "placeOpenHours"; // open hours of the place column
        public static final String PLACE_PHONE = "placePhone"; // phone of the place column
        public static final String PLACE_PHOTO = "placePhoto"; // photo of the place column
        public static final String PLACE_RATING = "placeRating"; // rating of the place column


        public static final String[] ALL_COLUMNS = new String[]{SQL_ID, PLACE_ID, PLACE_NAME, PLACE_VICINITY,PLACE_LATITUDE,PLACE_LONGITUDE, PLACE_DISTANCE, PLACE_REFERENCE,PLACE_WEBSITE,PLACE_OPEN_HOURS,PLACE_PHONE, PLACE_PHOTO, PLACE_RATING};

        // the creation table is a string.
        public static final String CREATION_STATEMENT = "CREATE TABLE " + TABLE_NAME +
                " ( " + SQL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLACE_ID + " TEXT, " +
                PLACE_NAME + " TEXT, "
                + PLACE_VICINITY + " TEXT, "
                + PLACE_LATITUDE + " NUMERIC, "
                + PLACE_LONGITUDE + " NUMERIC, "
                + PLACE_DISTANCE + " NUMERIC, "
                + PLACE_REFERENCE + " TEXT, "
                + PLACE_WEBSITE + " TEXT, "
                + PLACE_OPEN_HOURS + " TEXT, "
                + PLACE_PHONE + " TEXT, "
                + PLACE_PHOTO + " TEXT, "
                + PLACE_RATING + " NUMERIC ) ";

        public static final String DELETION_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // Inner Class FavoritePlaces
    public static class FavoritePlaces {

        public static final String TABLE_NAME = "FavoritePlaces"; // Table name.
        public static final String SQL_ID = "sqlID"; // id column name (this is the primary key)
        public static final String PLACE_ID = "placeId"; // id of the place column
        public static final String PLACE_NAME = "placeName"; // name of the place column
        public static final String PLACE_VICINITY = "placeVicinity"; // vicinity of the place column
        public static final String PLACE_LATITUDE = "placeLatitude"; // latitude of the place column
        public static final String PLACE_LONGITUDE = "placeLongitude"; // longitude of the place column
        public static final String PLACE_DISTANCE = "placeDistance"; // distance of the place column
        public static final String PLACE_REFERENCE = "placeReference"; // reference of the place column
        public static final String PLACE_WEBSITE = "placeWebSite"; // website of the place column
        public static final String PLACE_OPEN_HOURS = "placeOpenHours"; // open hours of the place column
        public static final String PLACE_PHONE = "placePhone"; // phone of the place column
        public static final String PLACE_PHOTO = "placePhoto"; // photo of the place column
        public static final String PLACE_RATING = "placeRating"; // rating of the place column


        public static final String[] ALL_COLUMNS = new String[]{SQL_ID, PLACE_ID, PLACE_NAME, PLACE_VICINITY,PLACE_LATITUDE,PLACE_LONGITUDE, PLACE_DISTANCE, PLACE_REFERENCE,PLACE_WEBSITE,PLACE_OPEN_HOURS,PLACE_PHONE, PLACE_PHOTO,PLACE_RATING};

        // the creation table is a string.
        public static final String CREATION_STATEMENT = "CREATE TABLE " + TABLE_NAME +
                " ( " + SQL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLACE_ID + " TEXT, " +
                PLACE_NAME + " TEXT, "
                + PLACE_VICINITY + " TEXT, "
                + PLACE_LATITUDE + " NUMERIC, "
                + PLACE_LONGITUDE + " NUMERIC, "
                + PLACE_DISTANCE + " NUMERIC, "
                + PLACE_REFERENCE + " TEXT, "
                + PLACE_WEBSITE + " TEXT, "
                + PLACE_OPEN_HOURS + " TEXT, "
                + PLACE_PHONE + " TEXT, "
                + PLACE_PHOTO + " TEXT, "
                + PLACE_RATING + " NUMERIC ) ";

        public static final String DELETION_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
