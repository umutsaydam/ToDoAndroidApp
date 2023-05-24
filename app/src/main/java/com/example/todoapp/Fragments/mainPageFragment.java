package com.example.todoapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class mainPageFragment extends Fragment {
    private FirebaseAuth mAuth;

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
        assert getContext() != null;
        ToDoLayerAdapter adapter = new ToDoLayerAdapter(timesPeriods, getContext(), getActivity(), mAuth);
        listLayersRecycler.setHasFixedSize(true);
        listLayersRecycler.setAdapter(adapter);
    }

    public void floatingAction(View view) {
        assert getContext() != null;
        BottomSheetDialog bottomDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyleTheme);
        BottomSheetBehavior<View> bottomSheetBehavior;

        View bottomView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_main_page, null);
        bottomDialog.setContentView(bottomView);

        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        EditText editTextTaskTitle = bottomDialog.findViewById(R.id.editTextTaskTitle);
        final String[] spinnerDate = new String[1];
        Spinner spinner = bottomDialog.findViewById(R.id.spinnerDate);
        String[] dates = getResources().getStringArray(R.array.dates);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerDate[0] = getContext().getResources().getStringArray(R.array.datesForDB)[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerDate[0] = getContext().getResources().getStringArray(R.array.datesForDB)[0];
            }
        });

        Button btnAddNewTask = bottomDialog.findViewById(R.id.btnAddNewTask);
        assert btnAddNewTask != null;
        btnAddNewTask.setOnClickListener(view1 -> {
            assert editTextTaskTitle != null;
            String toDoTitle = editTextTaskTitle.getText().toString();
            if (!toDoTitle.isEmpty() && !spinnerDate[0].equals(getContext().getResources().getStringArray(R.array.datesForDB)[0])) {
                addNewTask(toDoTitle, spinnerDate[0]);
            } else {
                Toast.makeText(getContext(), ""+getContext().getResources().getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
            }
        });
        TextView txtBackToList = bottomDialog.findViewById(R.id.txtBackToList);
        assert txtBackToList != null;
        txtBackToList.setOnClickListener(view1 -> bottomDialog.dismiss());


        bottomDialog.show();
    }

    public void addNewTask(String toDoTitle, String spinnerDate) {
        String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";
        DatabaseReference reference = FirebaseDatabase.getInstance(instance)
                .getReference("UsersActivitiesCurrent/" + mAuth
                        .getUid() + "/ToDo/" + spinnerDate + "/").push();
        reference.setValue(new ToDoModel(reference.getKey(), toDoTitle, false,
                spinnerDate.equals(getResources().getStringArray(R.array.datesForDB)[1]) ? getDate() : increaseDay(spinnerDate))).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(getContext(), ""+getContext().getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
        });
    }

    public String getDate() {
        Date date = Calendar.getInstance().getTime();
        return (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", date);
    }

    public String increaseDay(String spinnerDate) {
        Toast.makeText(getContext(), "calisti", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        Date dt;
        if (spinnerDate.equals(getResources().getStringArray(R.array.datesForDB)[2])) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dt = calendar.getTime();
            return (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", dt);
        }
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        dt = calendar.getTime();
        return (String) android.text.format.DateFormat.format("yyyy.MM.dd'/'HH:mm:ss", dt);
    }
}