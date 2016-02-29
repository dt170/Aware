package com.dt.project.Place;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dt.project.Place.Place;
import com.dt.project.Place.PlaceHolder;
import com.dt.project.R;

import java.util.ArrayList;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceHolder> {
    private Activity activity;
    private ArrayList<Place> places;

    public PlaceAdapter(Activity activity, ArrayList<Place> places) {
        this.activity = activity;
        this.places = places;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View linearLayout = layoutInflater.inflate(R.layout.place_item, null);

        return new PlaceHolder(activity, linearLayout);
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        Place place = places.get(position);
        holder.bindPlace(place);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

}
