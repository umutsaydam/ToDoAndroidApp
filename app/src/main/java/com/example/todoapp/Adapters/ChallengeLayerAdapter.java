package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Models.ChallengeModel;
import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChallengeLayerAdapter extends RecyclerView.Adapter<ChallengeLayerAdapter.ChallengeLayerHolder> {
    private ArrayList<ChallengeModel> challengeModels;
    private final Context context;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/");

    public ChallengeLayerAdapter(ArrayList<ChallengeModel> challengeModels, Context context) {
        this.challengeModels = challengeModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ChallengeLayerAdapter.ChallengeLayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.challenge_layer, parent, false);
        return new ChallengeLayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeLayerAdapter.ChallengeLayerHolder holder, int position) {
        holder.layerTitle.setText(challengeModels.get(position).getChallengeTitle());
        holder.setStatus(challengeModels.get(position).getChallangeStatus());
        holder.txtInfoCategory.setText(challengeModels.get(position).getChallengeCategory());
        holder.txtInfoDescription.setText(challengeModels.get(position).getChallengeDescription());
    }

    @Override
    public int getItemCount() {
        return challengeModels.size();
    }

    public class ChallengeLayerHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutShell;
        TextView txtCompleted, layerTitle, txtInfoDescription, txtInfoCategory;
        ImageView imgArrow;
        RelativeLayout relativeLayout;
        ProgressBar progressBar;
        RecyclerView recyclerChallengeStatus;
        Button btnCheckChallengeStatus;
        int a = 0;
        public ChallengeLayerHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutShell = itemView.findViewById(R.id.linearLayoutShell);
            txtCompleted = itemView.findViewById(R.id.txtCompleted);
            layerTitle = itemView.findViewById(R.id.layerTitle);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            progressBar = itemView.findViewById(R.id.progressBar);
            recyclerChallengeStatus = itemView.findViewById(R.id.recyclerChallengeStatus);
            txtInfoDescription = itemView.findViewById(R.id.txtInfoDescription);
            txtInfoCategory = itemView.findViewById(R.id.txtInfoCategory);
            btnCheckChallengeStatus = itemView.findViewById(R.id.btnCheckChallangeStatus);

            linearLayoutShell.setOnClickListener(view -> setVisibilityAndArrow());
            linearLayoutShell.setOnLongClickListener(view -> {
                showPopUpChallenge();
                return false;
            });
            btnCheckChallengeStatus.setOnClickListener(view -> checkChallengeStatus());
        }

        private void showPopUpChallenge() {
            PopupMenu  popupMenu = new PopupMenu(context, linearLayoutShell);
            popupMenu.getMenuInflater().inflate(R.menu.challenge_popum_menu, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId() == R.id.challengeDelete){
                    deleteChallenge(challengeModels.get(getAdapterPosition()).getId());
                }
                return false;
            });
        }

        private void deleteChallenge(String challengeId) {
            challengeModels.remove(getAdapterPosition());
            Task<Void> deleteChallenge = firebaseDatabase.
                    getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()+"/Challenges/"+challengeId).
                    removeValue();
            deleteChallenge.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            notifyDataSetChanged();
        }

        private void checkChallengeStatus() {
            ChallengeModel challengeModel = challengeModels.get(getAdapterPosition());
            String currDate = (String) android.text.format.DateFormat.format("dd/MM/yyyy", Calendar.getInstance().getTime());

            if(challengeModel.getChallengeStartDay().compareTo(currDate) <= 0 && challengeModel.getChallengeEndDay().compareTo(currDate) >= 0){
                int currPosition = challengeModel.getTimeDifference(challengeModel.getChallengeStartDay(), currDate);
                if(!challengeModel.getChallangeStatus().get(currPosition)){
                    System.out.println(challengeModel.getChallangeStatus());
                    challengeModel.getChallangeStatus().set(currPosition, true);
                    System.out.println(challengeModel.getChallangeStatus());
                    Task<Void> markStatus = firebaseDatabase.
                            getReference("UsersActivitiesCurrent/"+ FirebaseAuth.getInstance().getUid()+"/Challenges/"+challengeModel.getId()).setValue(challengeModel);
                    markStatus.addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Checked", Toast.LENGTH_SHORT).show();
                            if(challengeHaveDone(challengeModel.getChallengeEndDay())){
                                Toast.makeText(context, "You have done the challenge!", Toast.LENGTH_SHORT).show();
                                increaseStatistic(challengeModel);
                            }
                        }else{
                            Toast.makeText(context, "Error"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "You have already checked", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }

        private void increaseStatistic(ChallengeModel challengeModel) {
            Task<Void> reference = firebaseDatabase
                    .getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()+"/Statistic/")
                    .child(challengeModel.getChallengeCategory()).setValue(ServerValue.increment(challengeModel.getChallengeDay()));
            reference.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(context, "Statistic updated", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private boolean challengeHaveDone(String challengeEndDay) {
            return challengeEndDay.equals((String) android.text.format.DateFormat.format("dd/MM/yyyy", Calendar.getInstance().getTime()));
        }

        public void setVisibilityAndArrow() {
            relativeLayout.setVisibility(relativeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, relativeLayout.getVisibility() == View.VISIBLE ? R.anim.look_arrow_down : R.anim.look_arrow_right);
            imgArrow.setAnimation(animation);
            imgArrow.startAnimation(animation);
            imgArrow.setRotation(relativeLayout.getVisibility() == View.VISIBLE ? 90 : 0);
        }

        public void setStatus(List<Boolean> status){
            ChallengeAdapter adapter = new ChallengeAdapter(status, context);
            recyclerChallengeStatus.setHasFixedSize(false);
            recyclerChallengeStatus.setAdapter(adapter);
        }
    }
}
