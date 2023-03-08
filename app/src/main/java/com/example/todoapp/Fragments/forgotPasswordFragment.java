package com.example.todoapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPasswordFragment extends Fragment {
    private EditText editTxtEmail;
    private Button btnSendEmail;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        
        editTxtEmail = view.findViewById(R.id.editTxtEmail);
        btnSendEmail = view.findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(view1 -> {
            String mail = editTxtEmail.getText().toString();
            if(!mail.isEmpty()){
                FirebaseAuth.getInstance().useAppLanguage();
                FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), R.string.sent_email_repassword, Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_signInFragment);
                    }else{
                        Toast.makeText(getContext(), R.string.couldnt_send, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        return view;
    }
}