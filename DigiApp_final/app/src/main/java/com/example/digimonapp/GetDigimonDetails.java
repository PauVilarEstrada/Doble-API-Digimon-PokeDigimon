package com.example.digimonapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetDigimonDetails extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    public GetDigimonDetails(MainActivity activity) {
        this.mainActivity = activity;
    }
    private DigimonDetailActivity activity;
    public GetDigimonDetails(DigimonDetailActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String current = "";

        try {
            String digimonName = strings[0];
            String apiUrl = "https://digi-api.com/api/v1/digimon/" + digimonName;
            URL url = new URL(apiUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int data = isr.read();
            while (data != -1) {
                current += (char) data;
                data = isr.read();
            }

            return current;

        } catch (IOException e) {
            Log.e("GetDigimonDetails", "Error in doInBackground", e);
            e.printStackTrace();
        }

        return current;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("GetDigimonDetails", "Received details data: " + s);

        if (!s.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                activity.onDetailsLoaded(jsonObject);
            } catch (JSONException e) {
                Log.e("GetDigimonDetails", "Error parsing JSON", e);
                e.printStackTrace();
            }
        }
    }
}
