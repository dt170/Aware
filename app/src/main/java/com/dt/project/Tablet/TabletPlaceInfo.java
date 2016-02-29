package com.dt.project.Tablet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dt.project.Helper;
import com.dt.project.Place.Place;
import com.dt.project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//only in tablet
public class TabletPlaceInfo extends Dialog {
    private LinearLayout linearLayoutPhone;
    private LinearLayout linearLayoutFavorite;
    private LinearLayout linearLayoutOpenHours;
    private LinearLayout linearLayoutShare;
    private LinearLayout linearLayoutWeb;
    private Place place;
    private String result;
    private Activity activity;
    private Button buttonCancel;
    private TextView textViewInformationName;

    //taking the place and result string of the info async task
    public TabletPlaceInfo(Activity activity, String result, Place place) {
        super(activity);
        this.activity = activity;
        this.result = result;
        this.place = place;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_place_info);
//finding the views
        linearLayoutPhone = (LinearLayout) findViewById(R.id.linearLayoutPhone);
        linearLayoutFavorite = (LinearLayout) findViewById(R.id.linearLayoutFavorite);
        linearLayoutOpenHours = (LinearLayout) findViewById(R.id.linearLayoutOpenHours);
        linearLayoutShare = (LinearLayout) findViewById(R.id.linearLayoutShare);
        linearLayoutWeb = (LinearLayout) findViewById(R.id.linearLayoutWeb);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        textViewInformationName = (TextView) findViewById(R.id.textViewInformationName);
        TextView textViewDialogAddFavorite = (TextView) findViewById(R.id.textViewDialogAddFavorite);
        TextView textViewDialogOpenHours = (TextView) findViewById(R.id.textViewDialogOpenHours);
        TextView textViewDialogPhone = (TextView) findViewById(R.id.textViewDialogPhone);
//using the result string to find more information of the place and show it to the user
        String openHours = null;
        String phone_number = null;
        String webSite = null;
        JSONObject jsonObject = null;


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
        textViewInformationName.setText(place.getPlaceName());
        //checking those values if it's null visibility stays GONE else its VISIBLE
        if (phone_number != null) {
            place.setPlacePhone(phone_number);
            linearLayoutPhone.setVisibility(View.VISIBLE);
            textViewDialogPhone.setText(place.getPlacePhone());
        }
        //checking those values if it's null visibility stays GONE else its VISIBLE
        if (openHours != null) {
            place.setPlaceOpenHours(openHours);
            linearLayoutOpenHours.setVisibility(View.VISIBLE);
            textViewDialogOpenHours.setText(place.getPlaceOpenHours());
        }
        //checking those values if it's null visibility stays GONE else its VISIBLE
        if (webSite != null) {
            place.setPlaceWebSite(webSite);
            linearLayoutWeb.setVisibility(View.VISIBLE);
        }
        //checking those values if it's null visibility stays GONE else its VISIBLE
        if (place.getPlaceRating() != 0) {
            linearLayoutFavorite.setVisibility(View.VISIBLE);
            textViewDialogAddFavorite.setText(String.format("%s", place.getPlaceRating()));
        }
        //Setting the website intent in order to let the user open the internet page
        linearLayoutWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("" + place.getPlaceWebSite()));
                activity.startActivity(browse);
            }
        });

        //Share the name and address of the place
        linearLayoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.sharePlace(activity, place);
            }
        });

        //Share the number of the place
        linearLayoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.sharePhoneNumber(activity, place);
            }
        });
        //closing the dialog
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}


