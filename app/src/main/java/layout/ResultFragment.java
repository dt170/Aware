package layout;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dt.project.DataBase.PlaceLogic;
import com.dt.project.Helper;
import com.dt.project.Place.Place;
import com.dt.project.Place.PlaceAdapter;
import com.dt.project.R;
import java.util.ArrayList;

public class ResultFragment extends Fragment{

    private  int SEARCH_ON_START = 0;
    private PlaceLogic placeLogic;
    private PlaceAdapter adapter;
    private RecyclerView recyclerViewPlaces;
    private ArrayList<Place> places = new ArrayList<>();

    public ResultFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View linearLayoutRoot = inflater.inflate(R.layout.fragment_result, container, false);
        //finding the recycler view components
        recyclerViewPlaces = (RecyclerView) linearLayoutRoot.findViewById(R.id.recyclerViewPlaces);

        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Access to the data base
        placeLogic = new PlaceLogic(getActivity());
        placeLogic.open();
        //getting all the places from the data base
        places = placeLogic.getAllPlaces();

        adapter = new PlaceAdapter(getActivity(), places);

        recyclerViewPlaces.setAdapter(adapter);

        return linearLayoutRoot;
    }
//refresh adapter if place logic null open new one
    public void refreshAdapter() {
       if (placeLogic!=null)
            placeLogic.open();
        else {
           placeLogic = new PlaceLogic(getActivity());
           placeLogic.open();
       }
        //getting all the places from the data base
        places.clear();
        places.addAll(placeLogic.getAllPlaces());
        adapter.notifyDataSetChanged();
    }
    public void takeFromDataBaseAndRefeshList() {
        //Get all places from DB to places arrayList
        try {
            ArrayList<Place> placesFromDB = placeLogic.getAllPlaces();
            places.clear();
            places.addAll(placesFromDB);

        } catch (Exception e) {
           PlaceLogic placeLogic = new PlaceLogic(getActivity());
            placeLogic.open();
            ArrayList<Place> placesFromDB = placeLogic.getAllPlaces();
            placesFromDB.clear();
            places.addAll(placesFromDB);
        }
        //Refresh adapter
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
      //  placeLogic.open();
    }

    @Override
    public void onPause() {
        super.onPause();
      //  placeLogic.close();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Refresh adapter
        adapter.notifyDataSetChanged();

        if (SEARCH_ON_START == 0 && Helper.isNetworkAvailable(getActivity()) == false) {
            //If it's first search and the is connection get all places from DB
            takeFromDataBaseAndRefeshList();
        }
        SEARCH_ON_START = 1;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save SEARCH_ON_START on rotation
        outState.putInt("searchOnStart", SEARCH_ON_START);
    }
}
