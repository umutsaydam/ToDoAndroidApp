package com.example.todoapp.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChallengeLayerAdapter extends RecyclerView.Adapter<ChallengeLayerAdapter.ChallengeLayerHolder> {
    @NonNull
    @Override
    public ChallengeLayerAdapter.ChallengeLayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeLayerAdapter.ChallengeLayerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChallengeLayerHolder extends RecyclerView.ViewHolder {
        public ChallengeLayerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
