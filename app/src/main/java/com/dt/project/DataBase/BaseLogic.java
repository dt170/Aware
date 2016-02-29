package com.dt.project.DataBase;


import android.app.Activity;

// The base class for all business logic classes:
public abstract class BaseLogic {

    // The dal object:
    protected DAL dal;

    // constructor:
    public BaseLogic(Activity activity) {
        dal = new DAL(activity);
    }

    // Open the database:
    public void open() {
        dal.open();
    }

    // close the database:
    public void close() {
        dal.close();
    }
}
