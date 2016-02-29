package com.dt.project.Place;


import java.io.Serializable;
import java.lang.Override;
import java.lang.String;

//Place data members
public class Place implements Serializable {
    private long sqlID;
    private String placeName;
    private String placeAddress;
    private String placeID;
    private String PlaceReference;
    private String placePhoto;
    private String placeOpenHours;
    private String placeWebSite;
    private String placePhone;
    private double placeDistance;
    private double placeLatitude;
    private double placeLongitude;
    private float placeRating;

    public Place(long sqlID, String placeID, String placeName, String placeAddress, double placeLatitude, double placeLongitude, double placeDistance, String placeReference, String placeWebSite, String placeOpenHours, String placePhone, String placePhoto, float placeRating) {
        this(placeID, placeName, placeAddress, placeLatitude, placeLongitude, placeDistance, placeReference, placeWebSite, placeOpenHours, placePhone, placePhoto, placeRating);
        setSqlID(sqlID);

    }

    public Place(String placeID, String placeName, String placeAddress, double placeLatitude, double placeLongitude, double placeDistance, String placeReference, String placeWebSite, String placeOpenHours, String placePhone, String placePhoto, float placeRating) {
        setPlaceName(placeName);
        setPlaceAddress(placeAddress);
        setPlaceDistance(placeDistance);
        setPlaceID(placeID);
        setPlaceReference(placeReference);
        setPlacePhoto(placePhoto);
        setPlaceRating(placeRating);
        setPlaceLatitude(placeLatitude);
        setPlaceLongitude(placeLongitude);
        setPlaceWebSite(placeWebSite);
        setPlaceOpenHours(placeOpenHours);
        setPlacePhone(placePhone);
    }

    public String getPlaceOpenHours() {
        return placeOpenHours;
    }

    public void setPlaceOpenHours(String placeOpenHours) {
        this.placeOpenHours = placeOpenHours;
    }

    public String getPlacePhone() {
        return placePhone;
    }

    public void setPlacePhone(String placePhone) {
        this.placePhone = placePhone;
    }

    public String getPlaceWebSite() {
        return placeWebSite;
    }

    public void setPlaceWebSite(String placeWebSite) {
        this.placeWebSite = placeWebSite;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public long getSqlID() {
        return sqlID;
    }

    public void setSqlID(long sqlID) {
        this.sqlID = sqlID;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public double getPlaceDistance() {
        return placeDistance;
    }

    public void setPlaceDistance(double placeDistance) {
        this.placeDistance = placeDistance;
    }

    public String getPlaceReference() {
        return PlaceReference;
    }

    public void setPlaceReference(String placeReference) {
        PlaceReference = placeReference;
    }

    public String getPlacePhoto() {
        return placePhoto;
    }

    public void setPlacePhoto(String placePhoto) {
        this.placePhoto = placePhoto;
    }

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
    }

    @Override
    public String toString() {
        return "Name: " + placeName + " Address: " + placeAddress + " Distance " + placeDistance;
    }

}
