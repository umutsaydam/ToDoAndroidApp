package com.example.todoapp.Fragments;


import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signInFragment extends Fragment {
    private EditText editTxtEmail, editTxtPassword;
    private TextView txtForgotMyPassword, txtSignUp;
    private Button btnSignInToMainPage;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        editTxtEmail = view.findViewById(R.id.editTxtEmail);
        editTxtPassword = view.findViewById(R.id.editTxtPassword);
        btnSignInToMainPage = view.findViewById(R.id.btnSignInToMainPage);
        btnSignInToMainPage.setOnClickListener(view1 -> performSignIn(view));

       txtForgotMyPassword = view.findViewById(R.id.txtForgotMyPassword);
       txtForgotMyPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
           }
       });

        txtSignUp = view.findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_signInFragment_to_introFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
       return view;
    }

    public void performSignIn(View view) {
        String userEmail = editTxtEmail.getText().toString();
        String userPassword = editTxtPassword.getText().toString();

        if(!userEmail.isEmpty() && !userPassword.isEmpty()){
            auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_mainPageActivity);
                    }else{
                        Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}