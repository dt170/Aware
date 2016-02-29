package com.dt.project.Place;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.dt.project.AsyncTasks.InfoOnPlaceAsyncTask;
import com.dt.project.CircleTransformPicasso.CircleTransform;
import com.dt.project.Helper;
import com.dt.project.MainActivity;
import com.dt.project.Phone.MapAndInfoPhoneActivity;
import com.dt.project.R;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class PlaceHolder extends RecyclerView.ViewHolder {
    private static final int UPDATE_PLACE = 5;
    private Activity activity;
    private TextView textViewPlaceName;
    private TextView textViewPlaceAddress;
    private TextView textViewPlaceDistance;
    private View itemView;
    private ImageView imageViewPlace;

    public PlaceHolder(Activity activity, View itemView) {
        super(itemView);
        this.activity = activity;
        this.itemView = itemView;
        //finding the views
        textViewPlaceName = (TextView) itemView.findViewById(R.id.placeName);
        textViewPlaceAddress = (TextView) itemView.findViewById(R.id.placeAddress);
        textViewPlaceDistance = (TextView) itemView.findViewById(R.id.placeDistance);
        imageViewPlace = (ImageView) itemView.findViewById(R.id.imageViewPlace);
    }

    public void bindPlace(final Place place) {
        //actions that had to do in order to use picasso in the right way and preventing images mixed between items
        //default action when there is no photo to show in the intent
        imageViewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity,R.string.no_image,Toast.LENGTH_LONG).show();
            }
        });
        //setting the default photo in case there is no photo of the place
        imageViewPlace.setImageDrawable(activity.getResources().getDrawable(R.drawable.map));
        // if there is photo use picasso to set the image
        if(place.getPlacePhoto()!=null) {
            Picasso.with(activity)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + place.getPlacePhoto() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII")
                    .error(R.drawable.map)
                    .placeholder(R.drawable.map)// show this pic before load
                    .transform(new CircleTransform())//make it round
                    .into(imageViewPlace);
            //show the image of the place in normal size when you click on the image
            showImageFavoritePlace(place);
        }
// Handles user press item in the recycler
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if device not a tablet open intent with result
                if (!Helper.isTablet(activity)) {
                    Intent intent = new Intent(activity, MapAndInfoPhoneActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("place", place);
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, UPDATE_PLACE);
                }
                //if the device is tablet execute an async task that brings more info on the press place (the implements in MainActivity)
                if (Helper.isTablet(activity)) {
                    Callbacks callbacks = (Callbacks) (activity);
                    //using call backs to mark the press location on the map
                    callbacks.showPressedLocation(place);
                    try {
                        URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place.getPlaceID() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
                        InfoOnPlaceAsyncTask infoOnPlaceAsyncTask = new InfoOnPlaceAsyncTask(((MainActivity) activity));
                        infoOnPlaceAsyncTask.execute(url);
                    } catch (Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //in case of long press there are 2 options share the place name and address or add to favorite (using Popup)
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.shareItem:
                                //Sharing the name and address of the chosen place
                                Helper.sharePlace(activity, place);
                                break;
                            case R.id.addToFavorite:
                                //Saving the place into the favorites
                                Helper.addToFavoriteDB(activity, place);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.long_click_menu);
                popupMenu.show();
                return true;
            }
        });

        String placeName = place.getPlaceName();
        String placeAddress = place.getPlaceAddress();

        String placeDistance;
        //checking the user chose (Show Km  or Show Miles )
        //and calculating the distance
        SharedPreferences sharedPreferences = activity.getSharedPreferences("preference", Context.MODE_PRIVATE);
        String units = sharedPreferences.getString("units", "km");
        double oneMileFromKM = 0.621371192;
        double distance = place.getPlaceDistance();
        if (units.equals("miles"))
            placeDistance = "" + Math.floor((distance * oneMileFromKM) * 100) / 100 + " miles";
        else
            placeDistance = ((int) (distance)) == 0 ? ("" + ((int) (distance * 1000)) + " meter") : ("" + (distance) + " km");

//setting the name ,address and distance after calculating
        textViewPlaceName.setText(placeName);
        textViewPlaceAddress.setText(placeAddress);
        textViewPlaceDistance.setText(placeDistance);

    }
//using call backs to transfer data and mark the chosen place (if its tablet)
    public interface Callbacks {
        void showPressedLocation(Place place);
    }
// show the image of the place in intent (only if the user press this) else he will see it in circle
    public void showImageFavoritePlace(final Place place){
        imageViewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + place.getPlacePhoto() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII";
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.addCategory(android.content.Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse(url), "image/*");
                activity.startActivity(intent);
            }
        });
    }
}
