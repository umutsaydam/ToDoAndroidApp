package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeHolder> {
    private List<Boolean> status;
    private Context context;
    public ChallengeAdapter(List<Boolean> status, Context context) {
        this.status = status;
        this.context = context;
    }

    @NonNull
    @Override
    public ChallengeAdapter.ChallengeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.status_box, parent, false);
        return new ChallengeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeAdapter.ChallengeHolder holder, int position) {
        holder.imgStatus.setImageResource(status.get(position) ? R.drawable.challange_status_box_active : R.drawable.challange_status_box_passive);
    }

    @Override
    public int getItemCount() {
        return status.size();
    }

    public class ChallengeHolder extends RecyclerView.ViewHolder {
        private ImageView imgStatus;

        public ChallengeHolder(@NonNull View itemView) {
            super(itemView);

            imgStatus = itemView.findViewById(R.id.imgStatus);
        }
    }
}
