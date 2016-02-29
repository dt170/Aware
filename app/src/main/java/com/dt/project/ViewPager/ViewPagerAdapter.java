package com.dt.project.ViewPager;


import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import layout.FavoriteFragment;
import layout.ResultFragment;
import layout.SearchFragment;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final static int FAVORITE_FRAGMENT = 0;
    private final static int SEARCH_FRAGMENT = 1;
    private final static int RESULT_FRAGMENT = 2;
    private Activity activity;
    private FavoriteFragment favoriteFragment = new FavoriteFragment();
    private ResultFragment resultFragment = new ResultFragment();
    private SearchFragment searchFragment = new SearchFragment();

    public ViewPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        //setting the fragments order
        switch (position) {
            case FAVORITE_FRAGMENT:
                return favoriteFragment;
            case SEARCH_FRAGMENT:
                return searchFragment;
            case RESULT_FRAGMENT:
                return resultFragment;
        }
        return null;
    }


    @Override
    public int getCount() {

        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //Matching the name of the tabs to the fragment
        switch (position) {
            case FAVORITE_FRAGMENT:
                return "Favorite";
            case SEARCH_FRAGMENT:
                return "Search";
            case RESULT_FRAGMENT:
                return "Result";
        }
        return "";
    }

    @Override
    public Parcelable saveState() {
        //Don't save new state after rotation
        return null;
    }
}
