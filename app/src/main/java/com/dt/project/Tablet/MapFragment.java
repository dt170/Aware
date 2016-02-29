package com.dt.project.Tablet;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dt.project.AsyncTasks.InfoOnPlaceAsyncTask;
import com.dt.project.DataBase.PlaceLogic;
import com.dt.project.Helper;
import com.dt.project.MainActivity;
import com.dt.project.Phone.MapAndInfoPhoneActivity;
import com.dt.project.Place.Place;
import com.dt.project.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private PlaceLogic placeLogic;
    private ArrayList<Place> places = new ArrayList<>();
    private Place place;
    private LatLng currentItem;
    private HashMap<String, String> markersId = new HashMap<>();
    private double latitude;
    private double longitude;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Bundle bundle = getArguments();
        //if the device not a tablet
        if (!Helper.isTablet(getActivity()))
            place = (Place) bundle.getSerializable("place");
        //if the device a tablet
        if (Helper.isTablet(getActivity())) {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
        }

        // Object for updating our UI:
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap));
        // Send the object for updating the UI:
        supportMapFragment.getMapAsync(this);
        placeLogic = new PlaceLogic(getActivity());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //Calling add markers function
        addMarkers();
    }

    // open the data base and take all the places in order to add there location to the map as markers
    public void addMarkers() {
        if (googleMap == null) {
            return;
        }
//open DB and clear array list
        placeLogic.open();
        places.clear();
        places.addAll(placeLogic.getAllPlaces());
        //Adding 1 by 1 and if the location the user pressed is true the mark of his location will be marked as red
        for (int i = 0; i < places.size(); i++) {
            Place placeToMark = places.get(i);
            LatLng location = new LatLng(placeToMark.getPlaceLatitude(), placeToMark.getPlaceLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .snippet(placeToMark.getPlaceAddress())
                    .title(placeToMark.getPlaceName());
            Marker marker;
            //checking the place id to find match to the user press
            if (place != null && places.get(i).getPlaceID().equals(place.getPlaceID())) {
                //taking the item location (user chose) and mark him red
                currentItem = location;
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .alpha(1f);
                marker = googleMap.addMarker(markerOptions);
                //open the window of the place
                marker.showInfoWindow();
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .alpha(0.6f);
                marker = googleMap.addMarker(markerOptions);
            }
//if no place pressed (happens in tablet) use the user location
            if (place == null) {
                currentItem = new LatLng(latitude, longitude);
            }
            //setting onclick to all of the markers
            googleMap.setOnMarkerClickListener(this);
            //putting the place id in each marker
            markersId.put(marker.getId(), placeToMark.getPlaceID());
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentItem, 12);
        //a must have check
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        try {
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {

        }

    }
// when user press on the markers
    @Override
    public boolean onMarkerClick(Marker marker) {
        String id = markersId.get(marker.getId());
        //checking the place id and the marker id that we entered before in order to find a match
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getPlaceID().equals(id))
                place = places.get(i);
            //trigger a function that update the user chosen marker and bring info on the new place
            if (getActivity() instanceof MapAndInfoPhoneActivity)
                ((MapAndInfoPhoneActivity) getActivity()).setPlace(place);
        }
        // if the devise is tablet use async task to get plae information such as phone web ,etc (implement on main activity)
        if (Helper.isTablet(getActivity())) {
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place.getPlaceID() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
                InfoOnPlaceAsyncTask infoOnPlaceAsyncTask = new InfoOnPlaceAsyncTask(((MainActivity) getActivity()));
                infoOnPlaceAsyncTask.execute(url);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    //Happens only when the AsyncTask success the take the result and take the info and checks what info of the place we got and if we don't have its visibility stays Gone
    public void markerOnClickInformation(String result) {
        TabletPlaceInfo tabletPlaceInfo = new TabletPlaceInfo(getActivity(), result, place);
        tabletPlaceInfo.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        tabletPlaceInfo.show();
    }
//setting the new place that the user chose and add all markers from 0 in order to mark the place in red
    public void showNewPlace(Place place) {
        this.place = place;
        googleMap.clear();
        addMarkers();
    }
}
