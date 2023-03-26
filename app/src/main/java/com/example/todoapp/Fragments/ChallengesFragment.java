package com.example.todoapp.Fragments;

import android.app.DatePickerDialog;
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
import java.util.Calendar;

public class ChallengesFragment extends Fragment {
    private ArrayList<ChallengeModel> challengeModels;
    private FirebaseAuth mAuth;
    private ChallengeLayerAdapter adapter;
    private TextView txtNoChallenges;
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

        txtNoChallenges = view.findViewById(R.id.txtNoChallenges);

        challengeModels = new ArrayList<>();
        RecyclerView challengeLayerRecycler = view.findViewById(R.id.challangeLayerRecycler);
        adapter = new ChallengeLayerAdapter(challengeModels, getContext());
        challengeLayerRecycler.setHasFixedSize(false);
        challengeLayerRecycler.setAdapter(adapter);
        fetchChallenges();
        return view;
    }

    private void fetchChallenges() {
        FirebaseDatabase.getInstance(instance).getReference("UsersActivitiesCurrent/"+mAuth.getUid()+"/Challenges/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    challengeModels.clear();
                    txtNoChallenges.setVisibility(View.INVISIBLE);
                    System.out.println("data exist");
                    for(DataSnapshot data : snapshot.getChildren()){
                        challengeModels.add(data.getValue(ChallengeModel.class));
                    }
                }else{
                    System.out.println("no data");
                    txtNoChallenges.setVisibility(View.VISIBLE);
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
        String [] challengeCategory = {String.valueOf(R.string.category)};
        spinnerChallengeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                challengeCategory[0] = getContext().getResources().getStringArray(R.array.challengeCategoryForDB)[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                challengeCategory[0] = getString(R.string.category);
            }
        });

        Button btnChallengeStartDay = bottomDialog.findViewById(R.id.btnChallengeStartDay);
        assert btnChallengeStartDay != null;
        btnChallengeStartDay.setOnClickListener(this::getChallengeDateInfo);
        Button btnChallengeEndDay = bottomDialog.findViewById(R.id.btnChallengeEndDay);
        assert btnChallengeEndDay != null;
        btnChallengeEndDay.setOnClickListener(this::getChallengeDateInfo);
        EditText editTxtChallengeDescription = bottomDialog.findViewById(R.id.editTxtChallengeDescription);
        Button btnAddNewChallenge = bottomDialog.findViewById(R.id.btnAddNewChallenge);
        TextView txtBackToChallenges = bottomDialog.findViewById(R.id.txtBackToChallenges);

        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(getContext(), R.array.challengeCategory,
                android.R.layout.simple_spinner_item );

        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChallengeCategory.setAdapter(adapterCategory);

        assert btnAddNewChallenge != null;
        btnAddNewChallenge.setOnClickListener(view1 -> {
            assert editTxtChallengeTitle != null;
            String challengeTitle = editTxtChallengeTitle.getText().toString();
            assert editTxtChallengeDescription != null;
            String challengeDescription = editTxtChallengeDescription.getText().toString();

            String startChallengeDate = btnChallengeStartDay.getText().toString();
            String endChallengeDate = btnChallengeEndDay.getText().toString();


                if(!challengeTitle.isEmpty() && !challengeDescription.isEmpty() &&
                        !btnChallengeStartDay.getText().equals(getString(R.string.select_a_date)) &&
                        !btnChallengeEndDay.getText().equals(R.string.select_a_date) && !challengeCategory[0].equals(getString(R.string.category))){
                    if(startChallengeDate.compareTo(endChallengeDate) <= 0) {
                        addNewChallenge(challengeTitle, btnChallengeStartDay.getText().toString(), btnChallengeEndDay.getText().toString(),
                                challengeCategory[0], challengeDescription);
                    }else{
                        Toast.makeText(getContext(), "Dates are incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Please fill the areas.", Toast.LENGTH_SHORT).show();
                }
        });

        assert txtBackToChallenges != null;
        txtBackToChallenges.setOnClickListener(view1 -> bottomDialog.dismiss());

        bottomDialog.show();
        bottomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void getChallengeDateInfo(View view) {
        Button button = (Button) view;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String[] date = {""};
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year1, month1, day1) -> {
            date[0] = (day1 < 10 ? "0"+day1 : day1) +"/0"+(month1 +1)+"/"+ year1;
            button.setText(date[0]);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void addNewChallenge(String title, String challengeStartDate, String challengeEndDate, String category, String description) {
        DatabaseReference reference = FirebaseDatabase.getInstance(instance)
                .getReference("UsersActivitiesCurrent/"+mAuth.getUid()+"/Challenges/").push();

        reference.setValue(new ChallengeModel(reference.getKey(), title, category, challengeStartDate, challengeEndDate, description))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        System.out.println("added");
                        Toast.makeText(getContext(), "Challenge added", Toast.LENGTH_SHORT).show();
                    }else{
                        System.out.println("Error. "+task.getException().getMessage());
                    }
                });
        fetchChallenges();
        System.out.println(challengeModels.size()+" size");
    }
}