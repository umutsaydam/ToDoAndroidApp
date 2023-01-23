package com.example.todoapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.todoapp.Adapters.ToDoAdapter;
import com.example.todoapp.Models.ToDoModel;
import com.example.todoapp.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class mainPageFragment extends Fragment {
    private TextView txtCompletedToday, txtCompletedTomorrow, txtCompletedLater;
    private ImageView imgTodayArrow, imgTomorrowArrow, imgLaterArrow;
    private ProgressBar progressToday, progressTomorrow, progressLater;
    private FirebaseAuth mAuth;
    final String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton floatingActionButton = view.findViewById(R.id.mainPageFloatingAction);
        floatingActionButton.setOnClickListener(this::floatingAction);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerToday = view.findViewById(R.id.recyclerToday);
        RecyclerView recyclerTomorrow = view.findViewById(R.id.recyclerTomorrow);
        RecyclerView recyclerLater = view.findViewById(R.id.recyclerLater);

        progressToday = view.findViewById(R.id.progressToday);
        progressTomorrow = view.findViewById(R.id.progressTomorrow);
        progressLater = view.findViewById(R.id.progressLater);

        getData(recyclerToday,"Today", progressToday);
        getData(recyclerTomorrow, "Tomorrow", progressTomorrow);
        getData(recyclerLater,"Later", progressLater);


        RelativeLayout relativeLayoutExpandableToday = view.findViewById(R.id.relativeLayoutExpandableToday);
        RelativeLayout relativeLayoutExpandableTomorrow = view.findViewById(R.id.relativeLayoutExpandableTomorrow);
        RelativeLayout relativeLayoutExpandableLater = view.findViewById(R.id.relativeLayoutExpandableLater);

        imgTodayArrow = view.findViewById(R.id.imgTodayArrow);
        imgTomorrowArrow = view.findViewById(R.id.imgTomorrowArrow);
        imgLaterArrow = view.findViewById(R.id.imgLaterArrow);

        txtCompletedToday = view.findViewById(R.id.txtCompletedToday);
        txtCompletedTomorrow = view.findViewById(R.id.txtCompletedTomorrow);
        txtCompletedLater = view.findViewById(R.id.txtCompletedLater);

       LinearLayout linearLayoutToday = view.findViewById(R.id.linearLayoutToday);
        linearLayoutToday.setOnClickListener(view1 -> setVisibilityAndArrow(relativeLayoutExpandableToday, imgTodayArrow));
       LinearLayout linearLayoutTomorrow = view.findViewById(R.id.linearLayoutTomorrow);
        linearLayoutTomorrow.setOnClickListener(view1 -> setVisibilityAndArrow(relativeLayoutExpandableTomorrow, imgTomorrowArrow));
       LinearLayout linearLayoutLater = view.findViewById(R.id.linearLayoutLater);
        linearLayoutLater.setOnClickListener(view1 -> setVisibilityAndArrow(relativeLayoutExpandableLater, imgLaterArrow));
    }

    private void setVisibilityAndArrow(RelativeLayout relativeLayout, ImageView view) {
        relativeLayout.setVisibility(relativeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        Animation  animation = AnimationUtils.loadAnimation(getContext(), relativeLayout.getVisibility() == View.VISIBLE ? R.anim.look_arrow_down : R.anim.look_arrow_right);

        view.setAnimation(animation);
        view.startAnimation(animation);
        view.setRotation(relativeLayout.getVisibility() == View.VISIBLE ? 90 : 0);
    }

    private void getData(RecyclerView recycler, String timePeriod, ProgressBar progressBar){
        ArrayList<ToDoModel> models = new ArrayList<>();
        recycler.setHasFixedSize(false);
        ToDoAdapter adapter = new ToDoAdapter(models, getContext(), timePeriod, getActivity());
        recycler.setAdapter(adapter);

        if (mAuth.getUid() != null){
            FirebaseDatabase.getInstance(instance).getReference("UsersActivitiesCurrent/"+mAuth.getUid()).child(timePeriod).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getKey().equals("Today")){
                        txtCompletedToday.setText(snapshot.getChildrenCount() == 0 ? "" : String.valueOf(snapshot.getChildrenCount()));
                    } else if (snapshot.getKey().equals("Tomorrow")) {
                        txtCompletedTomorrow.setText(snapshot.getChildrenCount() == 0 ? "" : String.valueOf(snapshot.getChildrenCount()));
                    }else if(snapshot.getKey().equals("Later")){
                        txtCompletedLater.setText(snapshot.getChildrenCount() == 0 ? "" : String.valueOf(snapshot.getChildrenCount()));
                    }
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
                                        .getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()).child(timePeriod)
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
                        Toast.makeText(getContext(), timePeriod+": no data ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isOutOfDate(String toDoDate) {
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

    private String getDate(){
        Date date = Calendar.getInstance().getTime();
        String formattedDate = (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", date);
        return formattedDate;
    }
    //  private void setProgress(View view, int completed, int totalToDO, String  timePeriod){

  //  }

    public void floatingAction(View view) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyleTheme);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.bottom_sheet_main_page);

        EditText editTextTaskTitle = dialog.findViewById(R.id.editTextTaskTitle);
        final String[] spinnerDate = new String[1];
        Spinner spinner = dialog.findViewById(R.id.spinnerDate);
        String [] dates = getResources().getStringArray(R.array.dates);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerDate[0] = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerDate[0] = "Select date";
            }
        });

        Button btnAddNewTask = dialog.findViewById(R.id.btnAddNewTask);
        btnAddNewTask.setOnClickListener(view1 -> {
            String toDoTitle = editTextTaskTitle.getText().toString();
            if (!toDoTitle.isEmpty() && !spinnerDate[0].equals("Select date")){
                addNewTask(toDoTitle, spinnerDate[0]);
            }else{
                Toast.makeText(getContext(), "Please fill the fields", Toast.LENGTH_SHORT).show();
            }
        });

        TextView txtBackToList = dialog.findViewById(R.id.txtBackToList);
        txtBackToList.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void addNewTask(String toDoTitle,String  spinnerDate){
       DatabaseReference reference = FirebaseDatabase.getInstance(instance)
               .getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance()
                       .getUid()+"/"+spinnerDate+"/").push();
       reference.setValue(new ToDoModel(reference.getKey(), toDoTitle, false, getDate())).addOnCompleteListener(task -> {
           if (task.isSuccessful())
               Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
           else
               Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
       });
    }
}