package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dt.project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayoutRoot = (LinearLayout) inflater.inflate(R.layout.fragment_search, container, false);

       LinearLayout linearLayoutItems = (LinearLayout) linearLayoutRoot.findViewById(R.id.linearLayoutItems);

        // finding the layout that hold the tag of the search types
        for (int i = 0; i < linearLayoutItems.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) linearLayoutItems.getChildAt(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                linearLayout.getChildAt(j).setOnClickListener(this);
            }
        }


        return linearLayoutRoot;
    }
//passing to main activity the user pressed by callback
    @Override
    public void onClick(View v) {
        try {
            //getting the tag of the layout then we know what item the user pressed
            String searchType = ((LinearLayout) v).getTag().toString();
            Callbacks callbacks = (Callbacks) getActivity();
            callbacks.popularSearchType(searchType);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public interface Callbacks {
        void popularSearchType(String searchType);

    }
}
