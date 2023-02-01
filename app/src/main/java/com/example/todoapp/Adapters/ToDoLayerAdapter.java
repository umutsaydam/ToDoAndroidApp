package com.example.todoapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Models.ToDoModel;
import com.example.todoapp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ToDoLayerAdapter extends RecyclerView.Adapter<ToDoLayerAdapter.ToDoLayerHolder> {
    private final ArrayList<String> timesPeriods;
    private final Context context;
    private final Activity activity;
    private final FirebaseAuth mAuth;
    private final String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";

    public ToDoLayerAdapter(ArrayList<String> timesPeriods, Context context, Activity activity, FirebaseAuth mAuth) {
        this.timesPeriods = timesPeriods;
        this.context = context;
        this.activity = activity;
        this.mAuth = mAuth;
    }

    @NonNull
    @Override
    public ToDoLayerAdapter.ToDoLayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.to_do_layer, parent, false);
        return  new ToDoLayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoLayerAdapter.ToDoLayerHolder holder, int position) {
        holder.layerTitle.setText(timesPeriods.get(position));
        holder.getData(timesPeriods.get(position));
    }

    @Override
    public int getItemCount() {
        return timesPeriods.size();
    }

    public class ToDoLayerHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutShell;
        TextView txtCompleted, layerTitle;
        ImageView imgArrow;
        RelativeLayout relativeLayout;
        ProgressBar progressBar;
        RecyclerView recyclerToDoList;

        public ToDoLayerHolder(@NonNull View itemView) {
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

        public void getData(String timePeriod){
            ArrayList<ToDoModel> models = new ArrayList<>();
            recyclerToDoList.setHasFixedSize(false);
            System.out.println(timePeriod+" <<");
            ToDoAdapter adapter = new ToDoAdapter(models, context, timePeriod, activity);
            recyclerToDoList.setAdapter(adapter);

            if (mAuth.getUid() != null){
                FirebaseDatabase.getInstance(instance).getReference("UsersActivitiesCurrent/"+mAuth.getUid()+"/ToDo/").child(timePeriod).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txtCompleted.setText(snapshot.getChildrenCount() == 0 ? "" : String.valueOf(snapshot.getChildrenCount()));
                        progressBar.setVisibility(snapshot.getChildrenCount()==0 ? View.GONE : View.VISIBLE);
                        if (snapshot.exists()){
                            models.clear();
                            int counter = 0;
                            for(DataSnapshot data: snapshot.getChildren()){
                                System.out.println(timePeriod+" "+data.getValue(ToDoModel.class).getDate());
                                if (isOutOfDate(data.getValue(ToDoModel.class).getDate())){
                                    models.add(data.getValue(ToDoModel.class));
                                    counter += data.getValue(ToDoModel.class).isSelected() ? 1 : 0;
                                }else{
                                    Task<Void> deleteToDo = FirebaseDatabase
                                            .getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference("UsersActivitiesCurrent/"+ FirebaseAuth.getInstance().getUid()+"/ToDo/").child(timePeriod)
                                            .child(data.getValue(ToDoModel.class).getId()).removeValue();
                                }
                            }
                            ScaleAnimation scaleAnimation = new ScaleAnimation((int)(((double) counter/(double) snapshot.getChildrenCount())*100), 100, 0,0);
                            scaleAnimation.setDuration(10);
                            progressBar.setAnimation(scaleAnimation);
                            progressBar.setProgress( (int)(((double) counter/(double) snapshot.getChildrenCount())*100));
                            adapter.notifyDataSetChanged();
                        }else{
                            models.clear();
                            Toast.makeText(context, timePeriod+": no data ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public boolean isOutOfDate(String toDoDate) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            try {
                String [] toDosDate = toDoDate.split("/");
                String [] currentDate = getDate().split("/");
                if (format.parse(currentDate[0]).after(format.parse(toDosDate[0]))){
                    System.out.println("false");
                    return false;
                }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("true");
            return true;
        }

        public String getDate(){
            Date date = Calendar.getInstance().getTime();
            String formattedDate = (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", date);

            return formattedDate;
        }
    }
}
