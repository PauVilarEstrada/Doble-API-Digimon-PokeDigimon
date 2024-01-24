package com.example.digimonapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DigimonAdapter extends RecyclerView.Adapter<DigimonAdapter.DigimonViewHolder> {

    private static final int VIEW_TYPE_LIST = 1;
    private static final int VIEW_TYPE_GRID = 2;

    private Context mContext;
    private List<DigimonModel> mData;
    private boolean isGrid;
    public interface OnDigimonClickListener {
        void onDigimonClick(int position);
    }
    private OnDigimonClickListener listener;
    public void setOnDigimonClickListener(OnDigimonClickListener listener) {
        this.listener = listener;
    }

    public DigimonAdapter(Context mContext, List<DigimonModel> mData, boolean isGrid) {
        this.mContext = mContext;
        this.mData = mData;
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public DigimonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_LIST) {
            v = LayoutInflater.from(mContext).inflate(R.layout.digimon_item, parent, false);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.grid_layout, parent, false);
        }
        return new DigimonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DigimonViewHolder holder, @SuppressLint("RecyclerView")  int position) {
        holder.name.setText("Name: " + mData.get(position).getName());
        holder.level.setText("Level: " + mData.get(position).getLevel());

        Glide.with(mContext)
                .load(mData.get(position).getImg())
                .into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DigimonModel selectedDigimon = mData.get(position);

                Intent intent = new Intent(mContext, DigimonDetailActivity.class);

                intent.putExtra("name", selectedDigimon.getName());
                intent.putExtra("level", selectedDigimon.getLevel());
                intent.putExtra("img", selectedDigimon.getImg());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isGrid ? VIEW_TYPE_GRID : VIEW_TYPE_LIST;
    }

    public void setGrid(boolean grid) {
        this.isGrid = grid;
        notifyDataSetChanged();
    }

    public static class DigimonViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView img;
        private TextView level;

        public DigimonViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_txt);
            img = itemView.findViewById(R.id.imageView2);
            level = itemView.findViewById(R.id.level_txt);
        }
    }

}
