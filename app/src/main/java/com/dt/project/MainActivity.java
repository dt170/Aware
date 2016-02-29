package com.dt.project;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.dt.project.AsyncTasks.AutoCompleteAsyncTask;
import com.dt.project.AsyncTasks.InfoOnPlaceAsyncTask;
import com.dt.project.AsyncTasks.SearchByTextAsyncTask;
import com.dt.project.AsyncTasks.SearchNearYouAsyncTask;
import com.dt.project.DataBase.FavoritePlaceLogic;
import com.dt.project.DataBase.PlaceLogic;
import com.dt.project.Place.Place;
import com.dt.project.Place.PlaceHolder;
import com.dt.project.Receiver.PhoneReceiver;
import com.dt.project.Tablet.MapFragment;
import com.dt.project.ViewPager.ViewPagerAdapter;
import com.dt.project.ViewPager.ZoomOutPageTransformer;
import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import layout.FavoriteFragment;
import layout.ResultFragment;
import layout.SearchFragment;

public class MainActivity extends ActionBarActivity implements SearchNearYouAsyncTask.Callbacks, LocationListener, SearchFragment.Callbacks, AutoCompleteAsyncTask.Callbacks, SearchByTextAsyncTask.Callbacks, InfoOnPlaceAsyncTask.Callbacks, PlaceHolder.Callbacks {

    //Data members
    private ViewPager viewPagerHolder;
    private LocationManager locationManager;
    private ProgressDialog progressDialog;
    private double latitude = 0;
    private double longitude = 0;
    private final static int FAVORITE_FRAGMENT = 0;
    private final static int RESULT_FRAGMENT = 2;
    private final static int SEARCH_FRAGMENT = 1;
    private static final int UPDATE_PLACE = 5;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int MIN_AND_A_HALF = 90000;
    private static final int TEN_METERS = 10;
    private PlaceLogic placeLogic;
    private ResultFragment resultFragment;
    private FavoriteFragment favoriteFragment;
    private SearchFragment searchFragment;
    private SearchView searchView;
    private ListView autoCompleteListView;
    private ArrayList<String> words;
    private int FIRST_TIME_SEARCH = 0;
    private static MapFragment mapFragment;
    private ViewPagerAdapter viewPagerAdapter;
    private Location currentBestLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //open DB
        placeLogic = new PlaceLogic(this);

        if (savedInstanceState != null) {
            FIRST_TIME_SEARCH = savedInstanceState.getInt("FIRST_TIME_SEARCH");
            //only happens if the device is tablet

            longitude = savedInstanceState.getDouble("longitude");
            latitude = savedInstanceState.getDouble("latitude");
            if (Helper.isTablet(this)) {
                //opens the map with the user location
                if (mapFragment == null)
                    mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                mapFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentMap, mapFragment)
                        .commit();
            }
        }

        //finding views
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        viewPagerHolder = (ViewPager) findViewById(R.id.viewPagerHolder);

        TabLayout tabLayoutHeaders = (TabLayout) findViewById(R.id.tabLayoutHeaders);
        searchView = (SearchView) findViewById(R.id.searchView);
        autoCompleteListView = (ListView) findViewById(R.id.autoCompleteListView);
        //Setting view holder in order to let the user navigate between fragments and tabs
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager, this);

        //finding the result fragment and favorite fragment (making them static) not loosing data while rotate
        resultFragment = (ResultFragment) viewPagerAdapter.getItem(RESULT_FRAGMENT);
        favoriteFragment = (FavoriteFragment) viewPagerAdapter.getItem(FAVORITE_FRAGMENT);
        searchFragment = (SearchFragment) viewPagerAdapter.getItem(SEARCH_FRAGMENT);


        viewPagerHolder.setAdapter(viewPagerAdapter);
        tabLayoutHeaders.setupWithViewPager(viewPagerHolder);

        // Setting the default tab to start in ResultFragment = tab "result"
        viewPagerHolder.setCurrentItem(RESULT_FRAGMENT);
        //setting animation when the user move from page result to search to favorite etc,
        viewPagerHolder.setPageTransformer(true, new ZoomOutPageTransformer());
        //open DB
        placeLogic.open();
        //checks if gps enable and if no , notify user with dialog
        checkIfGpsEnabled();
        // find Current Location of the user
        getUserLocation();
        //Calling this function will put search view on click (that contain autocomplete and search)
        SearchViewOnClick();

    }

    // inflate the custom menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        placeLogic.close();
        super.onDestroy();
    }

    // triggered when the user rotate the screen and save the values (latitude,longitude,FIRST_TIME_SEARCH)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // handel the setting user have 3 options exit,change km to mile ,delete all favorite
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences("preference", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()) {
            //handel the user chose with alert dialog
            case R.id.distancePreference:
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).create();
                dialog.setTitle(R.string.choose_unit);
                dialog.setButton(Dialog.BUTTON_NEUTRAL, "Km", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("units", "km");
                        editor.apply();
                    }
                });
                dialog.setButton(Dialog.BUTTON_POSITIVE, "Miles", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("units", "miles");
                        editor.apply();
                    }
                });
                dialog.setCancelable(true);
                dialog.show();
                break;
            //delete all favorite
            case R.id.deleteAll:
                FavoritePlaceLogic favoritePlaceLogic = new FavoritePlaceLogic(this);
                favoritePlaceLogic.open();
                favoritePlaceLogic.deleteAllFavorites();
                favoritePlaceLogic.close();
                favoriteFragment.refreshAdapter();
                break;
            //exit the app
            case R.id.exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  placeLogic.close();
        //turn of the receiver (that check connection)
        try {
            ComponentName component = new ComponentName(this, PhoneReceiver.class);
            getPackageManager()
                    .setComponentEnabledSetting(component,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
        } catch (IllegalArgumentException e) {
            Log.d("receiver", e.toString());
        }
        // After we have the location of the user , remove the request for updating the user location
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Start PhoneReceiver and Read phone state
        ComponentName component = new ComponentName(this, PhoneReceiver.class);
        getPackageManager()
                .setComponentEnabledSetting(component,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

        //finding user location and update if need to
        getUserLocation();
    }

    //before tha async task starts open progress dialog
    @Override
    public void onAboutToStart() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }

    //on success of the info on place async task (work on tablet) sent the result string to trigger an intent
    // white the place information
    @Override
    public void onSuccessInfoOnPlace(String result) {
        progressDialog.dismiss();
        mapFragment.markerOnClickInformation(result);
    }

    //on success of search by text async task that use helper to retrieve the information from the result string
    @Override
    public void onSuccessSearchByTextAsyncTask(String result) {
        progressDialog.dismiss();
        //opens the DB access
        placeLogic.open();
        placeLogic.deleteAll();
        //in order to Calculate distance in km from the user location to the place we need the startPoint = user location
        LatLng startPoint = new LatLng(latitude, longitude);
        // Taking the AsyncTask result string and use his information in order to create a place and add it to the Data Base
        Helper.PlaceInformationByText(result, placeLogic, startPoint);
        //refresh the result fragment adapter
        resultFragment.refreshAdapter();
        //moving the uset to the result tab
        viewPagerHolder.setCurrentItem(RESULT_FRAGMENT);
    }

    // on success words async task taking the result and put in auto complete then create array list of string
    //if the user press it puts the word in the search view and execute a search
    @Override
    public void onSuccessWords(String result) {
        //Create an Array list of strings with the Helper function
        words = Helper.autoComplete(result);
        //only if there are items make the list visible
        if (words.size() > 0) {
            autoCompleteListView.setVisibility(View.VISIBLE);
        } else {
            autoCompleteListView.setVisibility(View.GONE);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, words);
        autoCompleteListView.setAdapter(adapter);
        // if the user press on a prediction word it activate an async task and search the place by the pressed prediction
        autoCompleteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //getting the String of the list from the position that the user pressed and turn it into a UTF-8 encode string for internet search
                    String text = URLEncoder.encode(words.get(position), "UTF-8");
                    SearchByTextAsyncTask searchByTextAsyncTask = new SearchByTextAsyncTask(MainActivity.this);
                    URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + text + "&location=" + latitude + "," + longitude + "&radius=50000&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
                    searchByTextAsyncTask.execute(url);
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                }
                //Clear the text search and close the search view
                searchView.setIconified(true);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
            }
        });
    }

    //on success async task of Search near you
    public void onSuccess(String result) {
        progressDialog.dismiss();
        ArrayList<Place> places = new ArrayList<>();
        //Calculating distance in km from the user location to the place
        LatLng startPoint = new LatLng(latitude, longitude);
        // Taking the AsyncTask result string and use his information in order to create a place and add it to the Data Base
        places.addAll(Helper.placeInformation(result, startPoint, this));
        if (places.size() == 0) {
            Toast.makeText(this, "Places not found , please try again", Toast.LENGTH_LONG).show();
            viewPagerHolder.setCurrentItem(SEARCH_FRAGMENT);
        }
        //Delete DB and insert the new results
        try {
            placeLogic.deleteAll();
        } catch (Exception e) {
            placeLogic.open();
        } finally {
            for (int i = 0; i < places.size(); i++) {
                placeLogic.addPlace(places.get(i));
            }
        }
        //refresh the result adapter
        resultFragment.takeFromDataBaseAndRefeshList();
        //setting the user in the result tab
        viewPagerHolder.setCurrentItem(RESULT_FRAGMENT);
        //In the first time opens the map in tablet
        if (Helper.isTablet(this)) {
            mapFragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);
            mapFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentMap, mapFragment)
                    .commit();
        }
    }

    //Showing the error that happened in the asyncTask
    @Override
    public void onError(String errorMessage) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }


    //getting the user location
    //--------------------------------------------------------------------------------
    private void getUserLocation() {
        // Activate getUserLocation function in order to bring the latitude and longitude of the user location.
        String providerNetwork = null;
        String providerGps = null;
        // Create the desired provider:
        try {
            providerNetwork = LocationManager.NETWORK_PROVIDER;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
            providerGps = LocationManager.GPS_PROVIDER;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // I had to add this code in order to get the latitude and longitude
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //use network
        locationManager.requestLocationUpdates(providerNetwork, MIN_AND_A_HALF, TEN_METERS, this);
        //use gps
        locationManager.requestLocationUpdates(providerGps, TWO_MINUTES, TEN_METERS, this);
        // in order to bring the best location
    }

    //wakes in order to get the user location & can update his location if need to.
    @Override
    public void onLocationChanged(Location location) {
        //using the best location we got from the user
        if (isBetterLocation(location, currentBestLocation)) {
            currentBestLocation = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        //a flag that allow the search by location run only on time after the program started
        if (FIRST_TIME_SEARCH == 0) {
            searchNearYou(null);
            FIRST_TIME_SEARCH = 1;
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// as we talked no need for toast here
    }

    @Override
    public void onProviderEnabled(String provider) {
        // as we talked no need for toast here

    }

    @Override
    public void onProviderDisabled(String provider) {
        // as we talked no need for toast here

    }
    //-------------------------------------------------------------------------------------

    //save those value when the screen rotate
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("FIRST_TIME_SEARCH", FIRST_TIME_SEARCH);
        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);
        super.onSaveInstanceState(outState);
    }

    //Handles the onClick of current location to get the list of places near you
    public void currentLocation_onClick(View view) {
        //Calling the function to activate async task only by location(not with types search) this why we send null (null=no types)
        searchNearYou(null);
    }

    // A function to call the async task of searching a place by location and types
    public void searchNearYou(String searchType) {

        try {
            //Searching in the device language
            String language = Locale.getDefault().getLanguage();

            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=5000&language=" + language + "&types=" + searchType + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
            //Checking if there is an internet connection
            if (Helper.isNetworkAvailable(this)) {
                SearchNearYouAsyncTask searchNearYouAsyncTask = new SearchNearYouAsyncTask(this);
                searchNearYouAsyncTask.execute(url);
            } else {
                //opens a snack bar that says no connection and give the user option to turn on wifi settings
                Helper.snackBarWifiSetting(this);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    //Searching by Images and types (Callbacks of the user press) returns string and activate asyncTask search by the user chose.
    @Override
    public void popularSearchType(String searchType) {
        //Calling the search near you function that starts async task , and get the string to search by it .
        searchNearYou(searchType);
    }

    //Handle all the search view situations autocomplete and text search query
    public void SearchViewOnClick() {
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerHolder.setCurrentItem(SEARCH_FRAGMENT);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    //getting the String and turn it into a UTF-8 encode string for internet search
                    String text = URLEncoder.encode(query, "UTF-8");
                    URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + text + "&location=" + latitude + "," + longitude + "&radius=50000&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
                    //Checking if there is an internet connection
                    if (Helper.isNetworkAvailable(MainActivity.this)) {
                        SearchByTextAsyncTask searchByTextAsyncTask = new SearchByTextAsyncTask(MainActivity.this);
                        searchByTextAsyncTask.execute(url);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                }
                //Clear the text search and close the search view
                searchView.setIconified(true);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    URL url = new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + newText.replace(" ", "+") + "&location=32.044,34.926&radius=10000&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
                    if (Helper.isNetworkAvailable(MainActivity.this)) {
                        AutoCompleteAsyncTask autoCompleteAsyncTask = new AutoCompleteAsyncTask(MainActivity.this);
                        autoCompleteAsyncTask.execute(url);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_LONG).show();
                    }
                } catch (MalformedURLException e) {
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    //setting the new place that the user chose and add all markers from 0 in order to mark the place in red
    @Override
    public void showPressedLocation(Place place) {
        mapFragment.showNewPlace(place);
    }

    //checking if there is gps on if no i use dialog to offer the use to unable the GPS
    public void checkIfGpsEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user with dialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.Gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.Open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == UPDATE_PLACE) {
            //UPDATE the place add website and phone if have
            favoriteFragment.refreshAdapter();
        }
    }

    //checking the best location of the user
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


}

