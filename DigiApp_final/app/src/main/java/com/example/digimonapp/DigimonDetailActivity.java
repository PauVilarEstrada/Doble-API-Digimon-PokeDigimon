package com.example.digimonapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class DigimonDetailActivity extends AppCompatActivity {
    private final String DETAILS_PREFS_NAME = "DigimonDetailsPrefs";
    private static final String KEY_DETAILS_JSON = "key_details_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digimon_detail);

        if (savedInstanceState != null) {
            String detailsJson = savedInstanceState.getString(KEY_DETAILS_JSON);
            if (detailsJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(detailsJson);
                    onDetailsLoaded(jsonObject);
                } catch (JSONException e) {
                    Log.e("DigimonDetailActivity", "Error parsing cached Digimon details JSON", e);
                    e.printStackTrace();
                }
                return;
            }
        }

        String name = getIntent().getStringExtra("name");
        String level = getIntent().getStringExtra("level");
        String img = getIntent().getStringExtra("img");

        ImageView imageView = findViewById(R.id.imageView);
        TextView nameTextView = findViewById(R.id.name_txt);
        TextView levelTextView = findViewById(R.id.level_txt);

        Glide.with(this)
                .load(img)
                .into(imageView);

        nameTextView.setText(name);
        levelTextView.setText("Level\n" + level);

        String cachedDetails = getCachedDetailsFromPrefs(name);
        if (cachedDetails != null) {
            try {
                JSONObject jsonObject = new JSONObject(cachedDetails);
                onDetailsLoaded(jsonObject);
            } catch (JSONException e) {
                Log.e("DigimonDetailActivity", "Error parsing cached Digimon details JSON", e);
                e.printStackTrace();
            }
        } else {
            new GetDigimonDetails(DigimonDetailActivity.this).execute(name);
        }
    }

    public void onDetailsLoaded(JSONObject details) {
        try {
            String type = details.getJSONArray("types").getJSONObject(0).getString("type");
            String attribute = details.getJSONArray("attributes").getJSONObject(0).getString("attribute");
            String description = details.getJSONArray("descriptions").getJSONObject(0).getString("description");

            TextView typeTextView = findViewById(R.id.type_txt);
            TextView attributeTextView = findViewById(R.id.attribute_txt);
            TextView descriptionTextView = findViewById(R.id.description_txt);

            typeTextView.setText("Type\n" + type);
            attributeTextView.setText("Attribute\n" + attribute);
            descriptionTextView.setText("Description: " + description);
            RecyclerView recyclerView = findViewById(R.id.evolutionsRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            JSONArray evolutionsArray = details.getJSONArray("nextEvolutions");

            EvolutionAdapter evolutionAdapter = new EvolutionAdapter(evolutionsArray);
            recyclerView.setAdapter(evolutionAdapter);
            saveDetailsToPrefs(details.getString("name"), details.toString());

        } catch (JSONException e) {
            Log.e("DigimonDetailActivity", "Error parsing Digimon details JSON", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        TextView descriptionTextView = findViewById(R.id.description_txt);
        String detailsJson = descriptionTextView.getText().toString();
        outState.putString(KEY_DETAILS_JSON, detailsJson);
    }

    private String getCachedDetailsFromPrefs(String digimonName) {
        SharedPreferences prefs = getSharedPreferences(DETAILS_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(digimonName, null);
    }

    private void saveDetailsToPrefs(String digimonName, String details) {
        SharedPreferences prefs = getSharedPreferences(DETAILS_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(digimonName, details);
        editor.apply();
    }
}