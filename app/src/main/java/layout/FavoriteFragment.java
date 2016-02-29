package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dt.project.DataBase.FavoritePlaceLogic;
import com.dt.project.FavoritePlace.FavoritePlace;
import com.dt.project.FavoritePlace.FavoritePlaceAdapter;
import com.dt.project.R;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment{

    private FavoritePlaceLogic favoritePlaceLogic;
    private FavoritePlaceAdapter adapter;
    private ArrayList<FavoritePlace> favoritePlaces;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View linearLayoutRoot = inflater.inflate(R.layout.fragment_favorite, container, false);

        RecyclerView recyclerViewFavoritePlaces = (RecyclerView) linearLayoutRoot.findViewById(R.id.recyclerViewFavoritePlaces);
        recyclerViewFavoritePlaces.setLayoutManager(new LinearLayoutManager(getActivity()));

        favoritePlaceLogic = new FavoritePlaceLogic(getActivity());
        favoritePlaceLogic.open();
//bring all favorite places from DB
        favoritePlaces = favoritePlaceLogic.getAllPlaces();
        adapter = new FavoritePlaceAdapter(getActivity(), favoritePlaces);
        recyclerViewFavoritePlaces.setAdapter(adapter);

        return linearLayoutRoot;
    }
//refresh the adapter
    public void refreshAdapter() {
        favoritePlaceLogic.open();
        //getting all the places from the data base
        favoritePlaces.clear();
        favoritePlaces.addAll(favoritePlaceLogic.getAllPlaces());
        adapter.notifyDataSetChanged();
    }
}
