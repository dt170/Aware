package com.dt.project.AsyncTasks;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.InputStream;

//Taking url String and preform AsyncTask in order to show the image
public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private Callbacks callbacks;

    public DownloadImageAsyncTask(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                ex.printStackTrace();
            }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        callbacks.onSuccess(result);
    }
    public interface Callbacks{
        void onSuccess(Bitmap result);
    }
}
