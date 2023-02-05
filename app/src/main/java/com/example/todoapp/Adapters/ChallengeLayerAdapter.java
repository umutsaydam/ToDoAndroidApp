package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Models.ChallengeModel;
import com.example.todoapp.R;

import java.util.ArrayList;

public class ChallengeLayerAdapter extends RecyclerView.Adapter<ChallengeLayerAdapter.ChallengeLayerHolder> {
    private ArrayList<ChallengeModel> challengeModels;
    private Context context;

    public ChallengeLayerAdapter(ArrayList<ChallengeModel> challengeModels, Context context) {
        this.challengeModels = challengeModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ChallengeLayerAdapter.ChallengeLayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.to_do_layer, parent, false);
        return new ChallengeLayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeLayerAdapter.ChallengeLayerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return challengeModels.size();
    }

    public class ChallengeLayerHolder extends RecyclerView.ViewHolder {
        public ChallengeLayerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
