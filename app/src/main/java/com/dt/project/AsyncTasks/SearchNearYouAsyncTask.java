package com.dt.project.AsyncTasks;


import android.os.AsyncTask;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//search near user by types or location
public class SearchNearYouAsyncTask extends AsyncTask<URL, Void,String> {

    private Callbacks callbacks;
    private String errorMessage = null;

    public SearchNearYouAsyncTask(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected void onPreExecute() {
        callbacks.onAboutToStart();
    }

    protected String doInBackground(URL... params) {

        try {

            URL url = params[0];

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int httpStatusCode = connection.getResponseCode();

            if (httpStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                errorMessage = "No Such Symbol";
                return null;
            }
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                errorMessage = connection.getResponseMessage();
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String result = "";

            String oneLine = bufferedReader.readLine();

            while (oneLine != null) {
                result += oneLine + "\n";
                oneLine = bufferedReader.readLine();
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            return result;

        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            return null;
        }
    }

    protected void onPostExecute(String result) {

        if (errorMessage != null) {
            callbacks.onError(errorMessage);
        } else {
            try {
                callbacks.onSuccess(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callbacks {
        void onAboutToStart();

        void onSuccess(String result) throws JSONException;

        void onError(String errorMessage);
    }
}
