package com.dt.project.FavoritePlace;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dt.project.AsyncTasks.InfoOnPlaceAsyncTask;
import com.dt.project.CircleTransformPicasso.CircleTransform;
import com.dt.project.DataBase.FavoritePlaceLogic;
import com.dt.project.Helper;
import com.dt.project.MainActivity;
import com.dt.project.Phone.MapAndInfoPhoneActivity;
import com.dt.project.R;
import com.squareup.picasso.Picasso;

import java.net.URL;


public class FavoriteHolder extends RecyclerView.ViewHolder {
    private Activity activity;
    private TextView textViewFavoriteName;
    private TextView textViewFavoriteAddress;
    private ImageView imageViewFavoritePlace;
    private ImageView imageViewFavoriteDrive;
    private ImageView imageViewFavoriteWebPage;
    private ImageView imageViewFavoritePhone;
    private ImageView imageViewFavoriteShare;
    private ImageView imageViewFavoriteDelete;
    private View itemView;
    private final static int UPDATE_INFORMATION = 3;
    private FavoritePlaceLogic favoritePlaceLogic;
    private Callbacks callbacks;


    public FavoriteHolder(Activity activity, View itemView, Callbacks callbacks) {
        super(itemView);
        this.activity = activity;
        this.itemView = itemView;
        //finding the views
        textViewFavoriteName = (TextView) itemView.findViewById(R.id.textViewFavoriteName);
        textViewFavoriteAddress = (TextView) itemView.findViewById(R.id.textViewFavoriteAddress);
        imageViewFavoritePlace = (ImageView) itemView.findViewById(R.id.imageViewFavoritePlace);
        imageViewFavoriteDrive = (ImageView) itemView.findViewById(R.id.imageViewFravoriteDrive);
        imageViewFavoriteWebPage = (ImageView) itemView.findViewById(R.id.imageViewFavoriteWebPage);
        imageViewFavoritePhone = (ImageView) itemView.findViewById(R.id.imageViewFavoritePhone);
        imageViewFavoriteShare = (ImageView) itemView.findViewById(R.id.imageViewFaviteShare);
        imageViewFavoriteDelete = (ImageView) itemView.findViewById(R.id.imageViewFavoriteDelete);

        this.callbacks = callbacks;
    }

    public void bindPlace(final FavoritePlace favoritePlace) {

        String placeName = favoritePlace.getPlaceName();
        String placeAddress = favoritePlace.getPlaceAddress();
        //setting name address and other information from the DB info
        textViewFavoriteName.setText(placeName);
        textViewFavoriteAddress.setText(placeAddress);
        // Handles user press item in the recycler
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if device not a tablet open intent with result
                if (!Helper.isTablet(activity)) {
                    Intent intent = new Intent(activity, MapAndInfoPhoneActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("place", favoritePlace);
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, UPDATE_INFORMATION);
                }
                //if the device is tablet execute an async task that brings more info on the press place (the implements in MainActivity)
                if (Helper.isTablet(activity)) {

                    try {
                        URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + favoritePlace.getPlaceID() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII");
                        InfoOnPlaceAsyncTask infoOnPlaceAsyncTask = new InfoOnPlaceAsyncTask(((MainActivity) activity));
                        infoOnPlaceAsyncTask.execute(url);
                    } catch (Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //default action when there is no photo to show in the intent
        imageViewFavoritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, R.string.no_image, Toast.LENGTH_LONG).show();
            }
        });
        //setting the default photo in case there is no photo of the place
        imageViewFavoritePlace.setImageDrawable(activity.getResources().getDrawable(R.drawable.map));
        // if there is photo use picasso to set the image
        if (favoritePlace.getPlacePhoto() != null) {
            Picasso.with(activity)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + favoritePlace.getPlacePhoto() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII")
                    .error(R.drawable.map)//setting that image if error happens
                    .placeholder(R.drawable.map)//show this pic before load
                    .transform(new CircleTransform())//make it round
                    .into(imageViewFavoritePlace);
            //show the image of the place in normal size when you click on the image(intent)
            showImageFavoritePlace(favoritePlace);
        }
        //Deleting item when user press trash
        deleteOneFavoriteItem(favoritePlace);
        //navigation to the favorite place
        driveFavoritePlace(favoritePlace);
        //if the favorite place have this data put visibility VISIBLE else it stays GONE
        if (favoritePlace.getPlacePhone() != null) {
            imageViewFavoritePhone.setVisibility(View.VISIBLE);
            //calling the favorite place
            callFavoritePlace(favoritePlace);
        }
        //if the favorite place have this data put visibility VISIBLE else it stays GONE
        if (favoritePlace.getPlaceWebSite() != null) {
            imageViewFavoriteWebPage.setVisibility(View.VISIBLE);
            //open a web page with the website of the favorite place
            webSiteFavoritePlace(favoritePlace);
        }
        //Share the name and address of the favorite place
        imageViewFavoriteShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //using helper to trigger an intent
                Helper.sharePlace(activity, favoritePlace);
            }
        });
    }

    //Callbacks in order to transfer data and refresh the adapter
    public interface Callbacks {
        void refreshAdapter(int position);
    }

    //delete one item from favorite and notify the adapter that one item removed
    public void deleteOneFavoriteItem(final FavoritePlace favoritePlace) {
        imageViewFavoriteDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritePlaceLogic = new FavoritePlaceLogic(activity);
                favoritePlaceLogic.open();
                favoritePlaceLogic.deleteOneItemFavorite(favoritePlace.getSqlID());
                callbacks.refreshAdapter(getAdapterPosition());
                favoritePlaceLogic.close();
            }
        });
    }

    //using intent to make a phone call if user press this
    public void callFavoritePlace(final FavoritePlace favoritePlace) {
        imageViewFavoritePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //using helper to trigger an intent
                Helper.sharePhoneNumber(activity, favoritePlace);
            }
        });
    }

    //using intent to open browser with the place website
    public void webSiteFavoritePlace(final FavoritePlace favoritePlace) {
        imageViewFavoriteWebPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("" + favoritePlace.getPlaceWebSite()));
                activity.startActivity(browse);
            }
        });
    }

    //sharing the geo location of the place and can navigate
    public void driveFavoritePlace(final FavoritePlace favoritePlace) {
        imageViewFavoriteDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + favoritePlace.getPlaceLatitude() + "," + favoritePlace.getPlaceLongitude()));
                activity.startActivity(intent);
            }
        });
    }

    // show the image by using intent
    public void showImageFavoritePlace(final FavoritePlace favoritePlace) {
        imageViewFavoritePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + favoritePlace.getPlacePhoto() + "&key=AIzaSyBSg3o7puUY7kYHvIOg-VxKWX6c1cjjSII";
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.addCategory(android.content.Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse(url), "image/*");
                activity.startActivity(intent);
            }
        });
    }

}
