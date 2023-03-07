package com.example.todoapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Adapters.ToDoLayerAdapter;
import com.example.todoapp.Models.ToDoModel;
import com.example.todoapp.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class mainPageFragment extends Fragment {
    private FirebaseAuth mAuth;
    private final String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        ((Toolbar) getActivity().findViewById(R.id.mainPageToolbar)).setTitle(R.string.todo);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.mainPageFloatingAction);
        floatingActionButton.setOnClickListener(this::floatingAction);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView listLayersRecycler = view.findViewById(R.id.listLayersRecycler);
        ArrayList<String> timesPeriods = new ArrayList<>();
        timesPeriods.add(getString(R.string.today));
        timesPeriods.add(getString(R.string.tomorrow));
        timesPeriods.add(getString(R.string.later));
        ToDoLayerAdapter adapter = new ToDoLayerAdapter(timesPeriods, getContext(), getActivity(), FirebaseAuth.getInstance());
        listLayersRecycler.setHasFixedSize(true);
        listLayersRecycler.setAdapter(adapter);
    }

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
                spinnerDate[0] = getString(R.string.select_a_date);
            }
        });

        Button btnAddNewTask = dialog.findViewById(R.id.btnAddNewTask);
        btnAddNewTask.setOnClickListener(view1 -> {
            String toDoTitle = editTextTaskTitle.getText().toString();
            if (!toDoTitle.isEmpty() && !spinnerDate[0].equals(getString(R.string.select_a_date))){
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

    public void addNewTask(String toDoTitle, String  spinnerDate){
        DatabaseReference reference = FirebaseDatabase.getInstance(instance)
                .getReference("UsersActivitiesCurrent/"+ FirebaseAuth.getInstance()
                        .getUid()+"/ToDo/"+spinnerDate+"/").push();
        reference.setValue(new ToDoModel(reference.getKey(), toDoTitle, false, getDate())).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
        });
    }

    public String getDate(){
        Date date = Calendar.getInstance().getTime();
        String formattedDate = (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", date);
        return formattedDate;
    }



}