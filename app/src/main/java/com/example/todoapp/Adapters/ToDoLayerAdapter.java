package com.example.todoapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ToDoLayerAdapter extends RecyclerView.Adapter<ToDoLayerAdapter.ToDoLayerHolder> {
    private final ArrayList<String> timesPeriods;
    private final Context context;
    private final Activity activity;
    private final FirebaseAuth mAuth;
    private final String[] datesForDB;
    private String currDateDB;

    public ToDoLayerAdapter(ArrayList<String> timesPeriods, Context context, Activity activity, FirebaseAuth mAuth) {
        this.timesPeriods = timesPeriods;
        this.context = context;
        this.activity = activity;
        this.mAuth = mAuth;
        this.datesForDB = context.getResources().getStringArray(R.array.datesForDB);
    }

    @NonNull
    @Override
    public ToDoLayerAdapter.ToDoLayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.to_do_layer, parent, false);
        return new ToDoLayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoLayerAdapter.ToDoLayerHolder holder, int position) {
        currDateDB = Arrays.asList(datesForDB)
                .get(Arrays.asList(context.getResources().getStringArray(R.array.dates)).indexOf(timesPeriods.get(position)));
        holder.layerTitle.setText(timesPeriods.get(position));
        holder.getData(currDateDB, timesPeriods.get(position));
    }

    @Override
    public int getItemCount() {
        return timesPeriods.size();
    }

    public class ToDoLayerHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutShell;
        TextView txtCompleted, layerTitle, txtNoToDo;
        ImageView imgArrow;
        RelativeLayout relativeLayout;
        ProgressBar progressBar;
        RecyclerView recyclerToDoList;

        public ToDoLayerHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutShell = itemView.findViewById(R.id.linearLayoutShell);
            txtCompleted = itemView.findViewById(R.id.txtCompleted);
            layerTitle = itemView.findViewById(R.id.layerTitle);
            txtNoToDo = itemView.findViewById(R.id.txtNoToDo);
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

        public void getData(String timePeriod, String langTimePeriod) {
            System.out.println(timePeriod);
            ArrayList<ToDoModel> models = new ArrayList<>();
            recyclerToDoList.setHasFixedSize(false);
            ToDoAdapter adapter = new ToDoAdapter(models, context, timePeriod, activity);
            recyclerToDoList.setAdapter(adapter);

            if (mAuth.getUid() != null) {
                String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";
                FirebaseDatabase.getInstance(instance).getReference("UsersActivitiesCurrent/" + mAuth.getUid() + "/ToDo/").child(currDateDB).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(snapshot.getChildrenCount() == 0 ? View.GONE : View.VISIBLE);
                        if (snapshot.exists()) {
                            models.clear();
                            txtNoToDo.setVisibility(View.GONE);
                            int counter = 0;
                            for (DataSnapshot data : snapshot.getChildren()) {
                                ToDoModel model = data.getValue(ToDoModel.class);

                                assert model != null;
                                if (timePeriod.equals(datesForDB[1]) && isOutOfDate(model.getDate()) && model.isSelected()) {
                                    deleteToDo(model, timePeriod);
                                } else if (timePeriod.equals(datesForDB[2]) &&
                                        ((getDate().split("/"))[0].equals(model.getDate().split("/")[0]) || isOutOfDate(model.getDate()))) {
                                    deleteToDo(model, timePeriod);
                                    if (!model.isSelected()) {
                                        moveToDo(model);
                                    }
                                } else {
                                    models.add(model);
                                    counter += model.isSelected() ? 1 : 0;
                                }
                            }
                            setCompleted((int) (snapshot.getChildrenCount() - counter));

                            ScaleAnimation scaleAnimation = new ScaleAnimation((int) (((double) counter / (double) snapshot.getChildrenCount()) * 100), 100, 0, 0);
                            scaleAnimation.setDuration(10);
                            progressBar.setAnimation(scaleAnimation);
                            progressBar.setProgress((int) (((double) counter / (double) snapshot.getChildrenCount()) * 100));
                            adapter.notifyDataSetChanged();
                        } else {
                            models.clear();
                            txtNoToDo.setText(langTimePeriod + "" + context.getResources().getString(R.string.no_to_do));
                            txtNoToDo.setVisibility(View.VISIBLE);
                            setCompleted(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void moveToDo(ToDoModel model) {
            Task<Void> moveToDo = FirebaseDatabase.getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("UsersActivitiesCurrent/" + FirebaseAuth.getInstance().getUid() + "/ToDo/")
                    .child("Today").child(model.getId()).setValue(model).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, ""+context.getResources().getString(R.string.updated), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void deleteToDo(ToDoModel model, String timePeriod) {
            Task<Void> deleteToDo = FirebaseDatabase
                    .getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("UsersActivitiesCurrent/" + FirebaseAuth.getInstance().getUid() + "/ToDo/")
                    .child(timePeriod).child(model.getId()).removeValue();
            deleteToDo.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, ""+context.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setCompleted(int num) {
            if (num == 0) {
                txtCompleted.setVisibility(View.GONE);
            } else {
                txtCompleted.setVisibility(View.VISIBLE);
                txtCompleted.setText(String.valueOf(num));
            }
        }

        public boolean isOutOfDate(String toDoDate) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            try {
                String[] toDosDate = toDoDate.split("/");
                String[] currentDate = getDate().split("/");
                if (Objects.requireNonNull(format.parse(currentDate[0])).after(format.parse(toDosDate[0]))) {
                    return true;
                }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
            return false;
        }

        public String getDate() {
            Date date = Calendar.getInstance().getTime();
            return (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", date);
        }
    }
}
