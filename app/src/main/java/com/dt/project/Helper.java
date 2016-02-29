package com.dt.project;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dt.project.DataBase.FavoritePlaceLogic;
import com.dt.project.DataBase.PlaceLogic;
import com.dt.project.FavoritePlace.FavoritePlace;
import com.dt.project.Place.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Helper {
//Calculating the air distance between user location and place location
    public static double calculationByDistance(LatLng startPoint, LatLng endPoint) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = startPoint.latitude;
        double lat2 = endPoint.latitude;
        double lon1 = startPoint.longitude;
        double lon2 = endPoint.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c;
    }

//taking the result string from the async task and retrieve all the data on the specific place
    public static ArrayList<Place> placeInformation(String result, LatLng startPoint,Activity activity) {
        ArrayList<Place> places = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JO = jsonArray.getJSONObject(i);
                String id = JO.getString("place_id");
                //  URL icon = new URL(JO.getString("icon"));
                String name = JO.getString("name");
                String vicinity = JO.getString("vicinity");
                String reference = JO.getString("reference");
                float rating;
                try {
                    rating = (float) JO.getDouble("rating");
                } catch (Exception ex) {
                    rating = 0;
                }

                double latitude = JO.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double longitude = JO.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                String photo;
                try {
                    JSONArray jsonArrayPhoto = JO.getJSONArray("photos");
                    JSONObject JoPhotos = jsonArrayPhoto.getJSONObject(0);
                    photo = JoPhotos.getString("photo_reference");
                } catch (Exception e) {
                    photo = null;
                }

                LatLng endPoint = new LatLng(latitude, longitude);
                double distance = calculationByDistance(startPoint, endPoint);
                //calculating 3 digits in order to show km
                double placeDistanceInKm = (int) (distance * 1000) / 1000.0;
                // putting null because those items don't have value in this search
                Place place = new Place(id, name, vicinity, latitude, longitude, placeDistanceInKm, reference, null, null, null, photo, rating);
                places.add(place);
            }
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return places;
    }
// checking if there is network connection
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
// taking the result of the async task and retrieve the information (lists of predictions )
    public static ArrayList<String> autoComplete(String result) {
        ArrayList<String> words = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("predictions");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JO = jsonArray.getJSONObject(i);
                String description = JO.getString("description");
                words.add(description);
            }
        } catch (Exception ex) {

        }
        // remove duplication words in  the list
        Set<String> removeDuplication = new HashSet<>();
        removeDuplication.addAll(words);
        words.clear();
        //Adding the array list back with out duplication
        words.addAll(removeDuplication);
        return words;
    }
// taking the result of the async task and retrieve the information of the place from a text search
    public static void PlaceInformationByText(String result, PlaceLogic placeLogic, LatLng startPoint) {
        //clear the data base from information
        placeLogic.open();
        placeLogic.deleteAll();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JO = jsonArray.getJSONObject(i);
                String id = JO.getString("place_id");
                //  URL icon = new URL(JO.getString("icon"));
                String name = JO.getString("name");
                String address = JO.getString("formatted_address");
                String reference = JO.getString("reference");
                float rating;
                try {
                    rating = (float) JO.getDouble("rating");
                } catch (Exception ex) {
                    rating = 0;
                }

                double latitude = JO.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double longitude = JO.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                String photo;
                try {
                    JSONArray jsonArrayPhoto = JO.getJSONArray("photos");
                    JSONObject JoPhotoes = jsonArrayPhoto.getJSONObject(0);
                    photo = JoPhotoes.getString("photo_reference");
                } catch (Exception e) {
                    photo = null;
                }

                LatLng endPoint = new LatLng(latitude, longitude);
                double distance = calculationByDistance(startPoint, endPoint);
                //calculating 3 digits in order to show km
                double placeDistanceInKm = (int) (distance * 1000) / 1000.0;

                Place place = new Place(id, name, address, latitude, longitude, placeDistanceInKm, reference, null, null, null, photo, rating);
                placeLogic.addPlace(place);
            }
        } catch (Exception ex) {

        }
        placeLogic.close();
    }
//sharing the place name and address
    public static void sharePlace(Activity activity, Place place) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + place.getPlaceName() + "\n" + "Address: " + place.getPlaceAddress());
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

// checking if the device of the user is tablet
    public static boolean isTablet(Activity activity) {
        return ((activity.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);
        //Check if landscape

    }
//make a call if there is a phone number
    public static void sharePhoneNumber(Activity activity, Place place) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + place.getPlacePhone()));

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(intent);
    }
// adding a place to the favorite place DB
    public static void addToFavoriteDB(Activity activity, Place place) {
        FavoritePlaceLogic favoritePlaceLogic = new FavoritePlaceLogic(activity);
        favoritePlaceLogic.open();
        FavoritePlace favoritePlace = new FavoritePlace(place.getSqlID(), place.getPlaceID(), place.getPlaceName(), place.getPlaceAddress(), place.getPlaceLatitude(), place.getPlaceLongitude(), place.getPlaceDistance(), place.getPlaceReference(), place.getPlaceWebSite(), place.getPlaceOpenHours(), place.getPlacePhone(), place.getPlacePhoto(), place.getPlaceRating());
        favoritePlaceLogic.addFavoritePlace(favoritePlace);
        favoritePlaceLogic.close();
    }
// snack bar notification that let the user option to turn on wifi
    public static void snackBarWifiSetting(final Activity activity){
        Snackbar.make(activity.findViewById(android.R.id.content), R.string.no_connection, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.try_wifi, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).show();
    }

}

