package com.example.digimonapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EvolutionAdapter extends RecyclerView.Adapter<EvolutionAdapter.ViewHolder> {

    private final JSONArray evolutions;

    public EvolutionAdapter(JSONArray evolutions) {
        this.evolutions = evolutions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evolution_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject evolution = evolutions.getJSONObject(position);
            String digimonName = evolution.getString("digimon");
            String imageUrl = evolution.getString("image");
            String condition = evolution.getString("condition");

            holder.digimonNameTextView.setText(digimonName);

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.digimonImageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return evolutions.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView digimonNameTextView;
        public final ImageView digimonImageView;
        public final TextView digimonConditionTextView;

        public ViewHolder(View view) {
            super(view);
            digimonNameTextView = view.findViewById(R.id.evolution_name_txt);
            digimonImageView = view.findViewById(R.id.evolution_image);
            digimonConditionTextView = view.findViewById(R.id.evolution_condition_txt);
        }
    }
}
