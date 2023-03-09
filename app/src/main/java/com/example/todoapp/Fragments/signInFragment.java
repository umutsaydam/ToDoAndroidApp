package com.example.todoapp.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class signInFragment extends Fragment {
    private EditText editTxtEmail, editTxtPassword;
    private Button btnSignInGoogle;
    private FirebaseAuth auth;
    private GoogleSignInClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        editTxtEmail = view.findViewById(R.id.editTxtEmail);
        editTxtPassword = view.findViewById(R.id.editTxtPassword);
        Button btnSignInToMainPage = view.findViewById(R.id.btnSignInToMainPage);
        btnSignInToMainPage.setOnClickListener(view1 -> performSignIn(view));

        TextView txtForgotMyPassword = view.findViewById(R.id.txtForgotMyPassword);
       txtForgotMyPassword.setOnClickListener(view12 -> Navigation.findNavController(view12).navigate(R.id.action_signInFragment_to_forgotPasswordFragment));

        TextView txtSignUp = view.findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(view13 -> Navigation.findNavController(view13).navigate(R.id.action_signInFragment_to_signUpFragment));

        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(getContext(), options);
        btnSignInGoogle = view.findViewById(R.id.btnSignInGoogle);
        btnSignInGoogle.setOnClickListener(view14 -> {
            Intent intent = new Intent(client.getSignInIntent());
            someActivity.launch(intent);
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

    ActivityResultLauncher<Intent> someActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            auth.signInWithCredential(credential).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()){
                                    FirebaseUser user = auth.getCurrentUser();
                                    Navigation.findNavController(btnSignInGoogle)
                                            .navigate(R.id.action_signInFragment_to_mainPageActivity);
                                    Task<Void> database = FirebaseDatabase
                                            .getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference("UsersActivitiesCurrent/"+FirebaseAuth.getInstance().getUid())
                                            .child("nameAndSurname").setValue(user.getDisplayName());
                                    getActivity().finish();
                                }else{
                                    System.out.println("Error 106 signIn with Google");
                                }
                            });
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void performSignIn(View view) {
        String userEmail = editTxtEmail.getText().toString();
        String userPassword = editTxtPassword.getText().toString();

        if(!userEmail.isEmpty() && !userPassword.isEmpty()){
            auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_mainPageActivity);
                }else{
                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}