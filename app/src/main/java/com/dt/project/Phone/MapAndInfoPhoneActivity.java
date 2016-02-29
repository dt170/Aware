package com.dt.project.Phone;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dt.project.Tablet.MapFragment;
import com.dt.project.Place.Place;
import com.dt.project.R;
import com.dt.project.Receiver.PhoneReceiver;

public class MapAndInfoPhoneActivity extends AppCompatActivity {
    private static final int UPDATE_PLACE = 4;
    private Place place;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_and_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getting the place information
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        place = (Place) bundle.getSerializable("place");
//setting the map by the user chose
        MapFragment mapFragment = new MapFragment();
        Bundle bundleMap = new Bundle();
        bundleMap.putSerializable("place", place);
        mapFragment.setArguments(bundleMap);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentMap, mapFragment)
                .commit();
        //Sending the place information the the info activity to show more info on the place
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapAndInfoPhoneActivity.this, InfoPlaceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("place", place);
                intent.putExtras(bundle);
                startActivityForResult(intent, UPDATE_PLACE);
            }
        });

        //Adding a back button
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //closing the activity
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //checking if the place being update if it does if set result ok (in order to let the main activity handle this)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == UPDATE_PLACE) {
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
    public void setPlace(Place place) {
        this.place = place;
    }
}



