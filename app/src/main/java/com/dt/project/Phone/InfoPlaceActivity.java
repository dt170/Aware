package com.dt.project.Phone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dt.project.AsyncTasks.DownloadImageAsyncTask;
import com.dt.project.AsyncTasks.InfoOnPlaceAsyncTask;
import com.dt.project.DataBase.FavoritePlaceLogic;
import com.dt.project.DataBase.PlaceLogic;
import com.dt.project.FavoritePlace.FavoritePlace;
import com.dt.project.Helper;
import com.dt.project.Place.Place;
import com.dt.project.R;
import com.dt.project.Receiver.PhoneReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class InfoPlaceActivity extends Activity implements InfoOnPlaceAsyncTask.Callbacks, DownloadImageAsyncTask.Callbacks {
    private ProgressDialog progressDialog;
    private PlaceLogic placeLogic;
    private ImageView imageViewHeader;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placePhone;
    private TextView placeOpenHours;
    private TextView placeWeb;
    private TextView placeRating;
    private Place place;
    private CardView cardViewAddress;
    private CardView cardViewWeb;
    private CardView cardViewPhone;
    private CardView cardViewOpenHours;
    private CardView cardViewRating;
    private CardView cardViewExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //finding the views
        placeName = (TextView) findViewById(R.id.textViewName);
        placeAddress = (TextView) findViewById(R.id.textViewAddress);
        placePhone = (TextView) findViewById(R.id.textViewPhone);
        placeOpenHours = (TextView) findViewById(R.id.textViewOpenHours);
        placeWeb = (TextView) findViewById(R.id.textViewWebSite);
        placeRating = (TextView) findViewById(R.id.textViewRating);
        imageViewHeader = (ImageView) findViewById(R.id.imageViewPhoto);
        cardViewWeb = (CardView) findViewById(R.id.cardViewWeb);
        cardViewAddress = (CardView) findViewById(R.id.cardViewAddress);
        cardViewPhone = (CardView) findViewById(R.id.cardViewPhone);
        cardViewOpenHours = (CardView) findViewById(R.id.cardViewOpenHours);
        cardViewRating = (CardView) findViewById(R.id.cardViewRating);
        cardViewExit = (CardView) findViewById(R.id.cardViewExit);
        //open DB
        placeLogic = new PlaceLogic(this);
        placeLogic.open();
        //getting the place
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        place = (Place) bundle.getSerializable("place");
        //setting the place name if not null
        assert place != null;
        placeName.setText(place.getPlaceName());
        //retrieving more info on the current place
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place.getPlaceID() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
            InfoOnPlaceAsyncTask infoOnPlaceAsyncTask = new InfoOnPlaceAsyncTask(this);
            infoOnPlaceAsyncTask.execute(url);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //setting the place image
        try {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + place.getPlacePhoto() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII";
            DownloadImageAsyncTask infoOnPlaceAsyncTask = new DownloadImageAsyncTask(this);
            infoOnPlaceAsyncTask.execute(url);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //before starting the async tasks
    @Override
    public void onAboutToStart() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Download");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    //after finish that async task take the result info and put in the user (checking and taking information that found )
    @Override
    public void onSuccessInfoOnPlace(String result) {
        progressDialog.dismiss();
        String openHours = null;
        String phone_number = null;
        String webSite = null;
        JSONObject jsonObject = null;

// checking what values we got
        try {
            jsonObject = new JSONObject(result).getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                phone_number = jsonObject.getString("formatted_phone_number");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                webSite = jsonObject.getString("website");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONObject("opening_hours").getJSONArray("weekday_text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonArray != null)
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (i == 0) {
                        try {
                            openHours = jsonArray.getString(i) + "\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        openHours += jsonArray.getString(i) + "\n";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
        //checking if there are values that not null if it not null set visibility VISIBLE else GONE
        if (place.getPlaceAddress() != null) {
            cardViewAddress.setVisibility(View.VISIBLE);
            placeAddress.setText(place.getPlaceAddress());
        }
        //checking if there are values that not null if it not null set visibility VISIBLE else GONE
        if (phone_number != null) {
            place.setPlacePhone(phone_number);
            cardViewPhone.setVisibility(View.VISIBLE);
            placePhone.setText(place.getPlacePhone());
        }
        //checking if there are values that not null if it not null set visibility VISIBLE else GONE
        if (openHours != null) {
            place.setPlaceOpenHours(openHours);
            cardViewOpenHours.setVisibility(View.VISIBLE);
            placeOpenHours.setText(place.getPlaceOpenHours());
        }
        //checking if there are values that not null if it not null set visibility VISIBLE else GONE
        if (webSite != null) {
            place.setPlaceWebSite(webSite);
            cardViewWeb.setVisibility(View.VISIBLE);
        }
        //checking if there are values that not null if it not null set visibility VISIBLE else GONE
        if (place.getPlaceRating() != 0) {
            cardViewRating.setVisibility(View.VISIBLE);
            placeRating.setText(String.format("%s", place.getPlaceRating()));
        }

        //Setting the website intent in order to let the user open the internet page
        placeWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("" + place.getPlaceWebSite()));
                startActivity(browse);
            }
        });
        //if user press the phone number that trigger an intent to make a phone call
        placePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activate the intent
                Helper.sharePhoneNumber(InfoPlaceActivity.this, place);
            }
        });
        //exit the current screen and go back to map
        cardViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Updating place information in the DB website,open hours, phone etc,
        placeLogic.updatePlace(place);
// checking if we have this place in favorite place and update that place
        FavoritePlaceLogic favoritePlaceLogic = new FavoritePlaceLogic(this);
        favoritePlaceLogic.open();
        ArrayList<FavoritePlace> favoritePlaces = favoritePlaceLogic.getAllPlaces();
        for (int i = 0; i < favoritePlaces.size(); i++) {
            // checking if they have the same id if do set result ok to trigger favorite place to refresh adapter
            if (favoritePlaces.get(i).getPlaceID().equals(place.getPlaceID())) {
                FavoritePlace favoritePlace = new FavoritePlace(favoritePlaces.get(i).getSqlID(), place.getPlaceID(), place.getPlaceName(), place.getPlaceAddress(), place.getPlaceLatitude(), place.getPlaceLongitude(), place.getPlaceDistance(), place.getPlaceReference(), place.getPlaceWebSite(), place.getPlaceOpenHours(), place.getPlacePhone(), place.getPlacePhoto(), place.getPlaceRating());
                favoritePlaceLogic.updateFavoritePlace(favoritePlace);
                setResult(RESULT_OK);
            }
        }
        favoritePlaceLogic.close();
    }

    @Override
    public void onError(String errorMessage) {
        progressDialog.dismiss();
    }

    //if there is image setting it .
    @Override
    public void onSuccess(Bitmap result) {
        imageViewHeader.setVisibility(View.VISIBLE);
        imageViewHeader.setImageBitmap(result);
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    }
}

