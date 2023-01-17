package com.example.todoapp.Fragments;


import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.todoapp.R;

public class introFragment extends Fragment {
    private Button btnSignIn, btnSignUp;
    public introFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        btnSignIn = view.findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this::goToSignIn);

        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnSignIn.setOnClickListener(this::goToSignIn);

        btnSignUp.setOnClickListener(this::goToSignUp);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);


        return view;
    }

    private void goToSignIn(View view) {
        Navigation.findNavController(view).navigate(R.id.action_introFragment_to_signInFragment);
    }

    private void goToSignUp(View view){
        Navigation.findNavController(view).navigate(R.id.action_introFragment_to_signUpFragment);
    }

}