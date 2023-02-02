package com.example.todoapp.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChallengesFragment extends Fragment {
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
        return view;
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

    private void addNewChallenge(String challengeTitle, String challengeCategory, String challengeDay, String challengeDescription) {
    }
}