package com.dt.project.FavoritePlace;

import com.dt.project.Place.Place;

//favorite place
public class FavoritePlace extends Place {

    public FavoritePlace(long sqlID, String placeID, String placeName, String placeAddress, double placeLatitude, double placeLongitude, double placeDistance, String placeReference, String placeWebSite, String placeOpenHours, String placePhone, String placePhoto, float placeRating) {
        super(sqlID, placeID, placeName, placeAddress, placeLatitude, placeLongitude, placeDistance, placeReference, placeWebSite, placeOpenHours, placePhone, placePhoto, placeRating);

    }

}
