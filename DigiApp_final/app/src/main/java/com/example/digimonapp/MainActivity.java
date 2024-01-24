package com.example.digimonapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DigimonAdapter.OnDigimonClickListener {

    private String JSON_URL = "https://digimon-api.vercel.app/api/digimon/";
    private static final String champion_digimon = "https://digimon-api.vercel.app/api/digimon/level/champion";
    private static final String roockie_digimon = "https://digimon-api.vercel.app/api/digimon/level/rookie";
    private static final String all_digimon = "https://digimon-api.vercel.app/api/digimon";
    private static final String mega_digimon = "https://digimon-api.vercel.app/api/digimon/level/mega";
    private static final String ultimate_digimon = "https://digimon-api.vercel.app/api/digimon/level/ultimate";
    private static final String fresh_digimon = "https://digimon-api.vercel.app/api/digimon/level/fresh";
    private static final String armor_digimon = "https://digimon-api.vercel.app/api/digimon/level/armor";
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String KEY_DATA = "storedData";
    private static final String KEY_IS_DATA_LOADED = "isDataLoaded";
    private static final String KEY_CURRENT_URL = "currentUrl";
    private List<DigimonModel> fullDigimonList;
    private List<DigimonModel> displayedDigimonList;
    private List<DigimonModel> receivedData = null;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private Switch switchLayout;
    private DigimonAdapter adapter;

    private void loadData() {
        if (receivedData == null) {
            String storedData = getStoredData();
            if (!storedData.isEmpty()) {
                fullDigimonList = parseStoredData(storedData);
                receivedData = new ArrayList<>(fullDigimonList);
                displayedDigimonList.addAll(fullDigimonList);
                putDataIntoRecyclerView(displayedDigimonList);
            } else {
                GetDigimonData getDigimonData = new GetDigimonData();
                getDigimonData.execute(JSON_URL);
            }
        } else {
            displayedDigimonList.clear();
            displayedDigimonList.addAll(receivedData);
            putDataIntoRecyclerView(displayedDigimonList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            JSON_URL = savedInstanceState.getString(KEY_CURRENT_URL, JSON_URL);
        }
        fullDigimonList = new ArrayList<>();
        displayedDigimonList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerview);
        searchView = findViewById(R.id.searchView);
        switchLayout = findViewById(R.id.switchLayout);


        if (!isDataLoaded()){
            loadData();
        }else {
            loadStoredData();
        }

        updateRecyclerViewLayout(1);
        adapter = new DigimonAdapter(this, displayedDigimonList, switchLayout.isChecked());
        adapter.setOnDigimonClickListener(this);

        Spinner spinner = findViewById(R.id.toolbar_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.digimon_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                changeDigimonOptionsSpinner(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDigimonList(newText);
                return true;
            }
        });

        switchLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    updateRecyclerViewLayout(3);
                } else {
                    updateRecyclerViewLayout(1);
                }
            }
        });
    }
    @Override
    public void onDigimonClick(int position) {
        DigimonModel clickedDigimon = displayedDigimonList.get(position);

        new GetDigimonDetails(MainActivity.this).execute(clickedDigimon.getName());
    }

    private void filterDigimonList(String query) {
        displayedDigimonList.clear();

        if (query.isEmpty()) {
            displayedDigimonList.addAll(fullDigimonList);
        } else {
            for (DigimonModel digimon : fullDigimonList) {
                if (digimon.getName().toLowerCase().contains(query.toLowerCase())) {
                    displayedDigimonList.add(digimon);
                }
            }
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void updateRecyclerViewLayout(int columnasPorFila) {
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, columnasPorFila);
        recyclerView.setLayoutManager(layoutManager);
        putDataIntoRecyclerView(displayedDigimonList);
    }

    private void loadStoredData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String storedData = sharedPreferences.getString(KEY_DATA, "");
        if (!storedData.isEmpty()) {
            fullDigimonList = parseStoredData(storedData);
            displayedDigimonList.addAll(fullDigimonList);
            putDataIntoRecyclerView(displayedDigimonList);
        }
    }

    private List<DigimonModel> parseStoredData(String storedData) {
        List<DigimonModel> parsedList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(storedData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                DigimonModel digimon = new DigimonModel(
                        jsonObject1.getString("name"),
                        jsonObject1.getString("img"),
                        jsonObject1.getString("level")
                );
                parsedList.add(digimon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parsedList;
    }

    private void saveDataLocally(String dataToSave) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DATA, dataToSave);
        editor.putBoolean(KEY_IS_DATA_LOADED, true);
        editor.apply();
    }
    private String getStoredData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DATA, "");
    }
    private void putDataIntoRecyclerView(List<DigimonModel> digimonList) {
        Log.d("PutData", "Putting data into RecyclerView. Count: " + digimonList.size());
        adapter = new DigimonAdapter(this, digimonList, switchLayout.isChecked());
        recyclerView.setAdapter(adapter);
    }

    public class GetDigimonData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String current = "";

            try {
                URL url = new URL(strings[0]);
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
                Log.e("GetDigimonData", "Error in doInBackground", e);
                e.printStackTrace();
            }

            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("GetDigimonData", "Received data: " + s);

            if (!s.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(s);

                    fullDigimonList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        DigimonModel digimon = new DigimonModel(
                                jsonObject1.getString("name"),
                                jsonObject1.getString("img"),
                                jsonObject1.getString("level")
                        );
                        fullDigimonList.add(digimon);
                    }

                    saveDataLocally(s);
                    receivedData = new ArrayList<>(fullDigimonList);
                    displayedDigimonList.clear();
                    displayedDigimonList.addAll(fullDigimonList);
                    putDataIntoRecyclerView(displayedDigimonList);

                } catch (JSONException e) {
                    Log.e("GetDigimonData", "Error parsing JSON", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void changeDigimonOptionsSpinner(String category) {
        if (("All".equals(category))){
            JSON_URL = all_digimon;
        } else if ("Roockie".equals(category)) {
            JSON_URL = roockie_digimon;
        } else if ("Fresh".equals(category)) {
            JSON_URL = fresh_digimon;
        } else if ("Champion".equals(category)) {
            JSON_URL = champion_digimon;
        } else if ("Mega".equals(category)) {
            JSON_URL = mega_digimon;
        } else if ("Ultimate".equals(category)) {
            JSON_URL = ultimate_digimon;
        } else if ("Armor".equals(category)) {
            JSON_URL = armor_digimon;
        }
        GetDigimonData getDigimonData = new GetDigimonData();
        getDigimonData.execute(JSON_URL);
    }
    private boolean isDataLoaded() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isDataLoaded", false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_URL, JSON_URL);    }

}