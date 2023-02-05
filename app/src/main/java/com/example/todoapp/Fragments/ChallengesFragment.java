package com.example.todoapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

import com.example.todoapp.Models.ChallengeModel;
import com.example.todoapp.Models.ExtendableLayerModel;
import com.example.todoapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChallengesFragment extends Fragment {
    private FirebaseAuth mAuth;
    private final String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);

        ((Toolbar) getActivity().findViewById(R.id.mainPageToolbar)).setTitle(R.string.challenge);
        FloatingActionButton addChallengeFloatingBtn = view.findViewById(R.id.addChallengeFloatingBtn);
        addChallengeFloatingBtn.setOnClickListener(this::newChallenge);
        mAuth = FirebaseAuth.getInstance();

        fetchChallenges();

        return view;
    }

    private void fetchChallenges() {
        ArrayList<ChallengeModel> list = new ArrayList<>();
        FirebaseDatabase.getInstance(instance).getReference("UsersActivitiesCurrent/"+mAuth.getUid()+"/Challenges/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot data : snapshot.getChildren()){
                        ChallengeModel challengeModel = data.getValue(ChallengeModel.class);
                        System.out.println(challengeModel.getChallengeTitle());
                        System.out.println(challengeModel.getChallangeStatus());
                        System.out.println(challengeModel.getChallengeCategory());
                        System.out.println(challengeModel.getChallengeDescription());
                    }
                }else{
                    System.out.println("no data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }

    public void newChallenge(View view) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetStyleTheme);
        bottomDialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        bottomDialog.setContentView(R.layout.bottom_sheet_challenges);

        EditText editTxtChallengeTitle = bottomDialog.findViewById(R.id.editTxtChallengeTitle);
        Spinner spinnerChallengeCategory = bottomDialog.findViewById(R.id.spinnerChallengeCategory);
        String [] challengeCategory = {""};
        spinnerChallengeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                challengeCategory[0] = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                challengeCategory[0] = "Choose a category";
            }
        });

        EditText editTxtChallengeDay = bottomDialog.findViewById(R.id.editTxtChallengeDay);
        EditText editTxtChallengeDescription = bottomDialog.findViewById(R.id.editTxtChallengeDescription);
        Button btnAddNewChallenge = bottomDialog.findViewById(R.id.btnAddNewChallenge);
        TextView txtBackToChallenges = bottomDialog.findViewById(R.id.txtBackToChallenges);

        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(getContext(), R.array.challengeCategory,
                android.R.layout.simple_spinner_item );

        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChallengeCategory.setAdapter(adapterCategory);

        btnAddNewChallenge.setOnClickListener(view1 -> {
            String challengeTitle = editTxtChallengeTitle.getText().toString();
            String challengeDay = editTxtChallengeDay.getText().toString();
            String challengeDescription = editTxtChallengeDescription.getText().toString();

            if(!challengeTitle.isEmpty() && !challengeDay.isEmpty() && !challengeDescription.isEmpty() && !challengeCategory[0].equals("Choose a category")){
                addNewChallenge(challengeTitle, challengeCategory[0], challengeDay, challengeDescription);
            }else{
                Toast.makeText(getContext(), "Please fill the areas.", Toast.LENGTH_SHORT).show();
            }
        });

        txtBackToChallenges.setOnClickListener(view1 -> bottomDialog.dismiss());

        bottomDialog.show();
        bottomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void addNewChallenge(String title, String category, String day, String description) {
        DatabaseReference reference = FirebaseDatabase.getInstance(instance)
                .getReference("UsersActivitiesCurrent/"+mAuth.getUid()+"/Challenges/").push();

        reference.setValue(new ChallengeModel(reference.getKey(), title, category, Integer.parseInt(day), description))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Challange added", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}