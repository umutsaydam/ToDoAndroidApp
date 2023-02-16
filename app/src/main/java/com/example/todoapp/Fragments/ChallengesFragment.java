package com.example.todoapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.todoapp.Adapters.ChallengeLayerAdapter;
import com.example.todoapp.Models.ChallengeModel;
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
    private RecyclerView challangeLayerRecycler;
    private ArrayList<ChallengeModel> challengeModels;
    private FirebaseAuth mAuth;
    private ChallengeLayerAdapter adapter;
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

        challengeModels = new ArrayList<>();
        challangeLayerRecycler = view.findViewById(R.id.challangeLayerRecycler);
        adapter = new ChallengeLayerAdapter(challengeModels, getContext());
        challangeLayerRecycler.setHasFixedSize(false);
        challangeLayerRecycler.setAdapter(adapter);
        fetchChallenges();
        return view;
    }

    private void fetchChallenges() {
        FirebaseDatabase.getInstance(instance).getReference("UsersActivitiesCurrent/"+mAuth.getUid()+"/Challenges/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    challengeModels.clear();
                    System.out.println("data exist");
                    for(DataSnapshot data : snapshot.getChildren()){
                        challengeModels.add(data.getValue(ChallengeModel.class));
                    }
                }else{
                    System.out.println("no data");
                }
                adapter.notifyDataSetChanged();
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

        reference.setValue(new ChallengeModel(reference.getKey(), title, category, "16/02/2023", "19/02/2023", description))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        System.out.println("addded");
                        Toast.makeText(getContext(), "Challange added", Toast.LENGTH_SHORT).show();
                    }else{
                        System.out.println("hata");
                        System.out.println(task.getException().getMessage());
                    }
                });
        fetchChallenges();
        System.out.println(challengeModels.size()+" size");
    }
}