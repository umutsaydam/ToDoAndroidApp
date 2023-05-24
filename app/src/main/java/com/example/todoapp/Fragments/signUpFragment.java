package com.example.todoapp.Fragments;

import android.os.Bundle;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class signUpFragment extends Fragment {
    private EditText editTxtNameSurname, editTxtEmail, editTxtPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        editTxtNameSurname = view.findViewById(R.id.editTxtNameSurname);
        editTxtEmail = view.findViewById(R.id.editTxtEmail);
        editTxtPassword = view.findViewById(R.id.editTxtPassword);

        Button btnSignUpSignUpFragment = view.findViewById(R.id.btnSignUpSignUpFragment);
        btnSignUpSignUpFragment.setOnClickListener(view12 -> performSignUp(view12));

        TextView txtSignIn = view.findViewById(R.id.txtSignIn);
        txtSignIn.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_signUpFragment_to_signInFragment));

        return view;
    }

    public void performSignUp(View view) {
        String name = editTxtNameSurname.getText().toString();
        String password = editTxtPassword.getText().toString();
        String email = editTxtEmail.getText().toString();

        if (!name.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment);

                    Task<Void> setUsernameSurname = FirebaseDatabase.getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("UsersActivitiesCurrent/" + auth.getUid()).child("nameAndSurname").setValue(name);
                    setUsernameSurname.addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getContext(), "" + getContext().getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "" + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Something went wrong " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            Toast.makeText(getContext(), "" + getContext().getResources().getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
        }


    }
}