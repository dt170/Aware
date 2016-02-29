package com.dt.project.FavoritePlace;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dt.project.R;
import java.util.ArrayList;


public class FavoritePlaceAdapter extends RecyclerView.Adapter<FavoriteHolder> implements FavoriteHolder.Callbacks {
    private Activity activity;
    private ArrayList<FavoritePlace> places;


    public FavoritePlaceAdapter(Activity activity, ArrayList<FavoritePlace> places) {
        this.activity = activity;
        this.places = places;
    }

    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View linearLayout = layoutInflater.inflate(R.layout.favorite_item, null);

        return new FavoriteHolder(activity, linearLayout, this);
    }

    @Override
    public void onBindViewHolder(FavoriteHolder holder, int position) {
        FavoritePlace favoritePlace = places.get(position);

        holder.bindPlace(favoritePlace);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    //this function being called when the user try to delete one item from the list
    @Override
    public void refreshAdapter(int position) {
        try {
            places.remove(position);
            notifyItemRemoved(position);
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
