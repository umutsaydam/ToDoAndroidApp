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
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class signUpFragment extends Fragment {
    private TextView txtSignIn;
    private EditText editTxtNameSurname, editTxtEmail, editTxtPassword;
    private Button btnSignUpSignUpFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        txtSignIn = view.findViewById(R.id.txtSignIn);
        editTxtNameSurname = view.findViewById(R.id.editTxtNameSurname);
        editTxtEmail = view.findViewById(R.id.editTxtEmail);
        editTxtPassword = view.findViewById(R.id.editTxtPassword);

        btnSignUpSignUpFragment = view.findViewById(R.id.btnSignUpSignUpFragment);
        btnSignUpSignUpFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSignUp(view);
            }
        });

        txtSignIn = view.findViewById(R.id.txtSignIn);
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_introFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        return view;
    }

    public void performSignUp(View view) {
        String name = editTxtNameSurname.getText().toString();
        String password = editTxtPassword.getText().toString();
        String email = editTxtEmail.getText().toString();
        
        if(!name.isEmpty() && !password.isEmpty() && !email.isEmpty()){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment);

                        Task<Void> setUsernameSurname = FirebaseDatabase.getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("UserActivitiesCurrent/"+auth.getUid()).child("nameAndSurname").setValue(name);
                        setUsernameSurname.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Something went wrong "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }else{
            Toast.makeText(getContext(), "Fill the fields", Toast.LENGTH_SHORT).show();
        }
        

    }
}