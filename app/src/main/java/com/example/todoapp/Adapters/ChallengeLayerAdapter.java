package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        holder.layerTitle.setText(challengeModels.get(position).getChallengeTitle());
    }

    @Override
    public int getItemCount() {
        return challengeModels.size();
    }

    public class ChallengeLayerHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutShell;
        TextView txtCompleted, layerTitle;
        ImageView imgArrow;
        RelativeLayout relativeLayout;
        ProgressBar progressBar;
        RecyclerView recyclerToDoList;
        public ChallengeLayerHolder(@NonNull View itemView) {
            super(itemView);

            linearLayoutShell = itemView.findViewById(R.id.linearLayoutShell);
            txtCompleted = itemView.findViewById(R.id.txtCompleted);
            layerTitle = itemView.findViewById(R.id.layerTitle);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            progressBar = itemView.findViewById(R.id.progressBar);
            recyclerToDoList = itemView.findViewById(R.id.recyclerToDoList);

            linearLayoutShell.setOnClickListener(view -> setVisibilityAndArrow());
        }

        public void setVisibilityAndArrow() {
            relativeLayout.setVisibility(relativeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, relativeLayout.getVisibility() == View.VISIBLE ? R.anim.look_arrow_down : R.anim.look_arrow_right);

            imgArrow.setAnimation(animation);
            imgArrow.startAnimation(animation);
            imgArrow.setRotation(relativeLayout.getVisibility() == View.VISIBLE ? 90 : 0);
        }
    }
}
