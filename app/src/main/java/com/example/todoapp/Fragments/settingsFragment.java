package com.example.todoapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.todoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class settingsFragment extends Fragment {
    private AlertDialog.Builder builder;
    private SharedPreferences.Editor editor;
    private boolean darkTheme;
    private TextView userNameSurname;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Settings.Theme", MODE_PRIVATE);
        darkTheme = sharedPreferences.getBoolean("darkTheme", false);
        editor = getActivity().getSharedPreferences("Settings.Theme", MODE_PRIVATE).edit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        userNameSurname = view.findViewById(R.id.userNameSurname);
        getUserNameSurname();

        view.findViewById(R.id.btnChangeLang).setOnClickListener(this::changeLangDialog);

        SwitchCompat switchCompat = view.findViewById(R.id.switchChangeTheme);
        switchCompat.setChecked(darkTheme);
        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            System.out.println("calisti ***/*/*/*/*/*/*/*/*/*");
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("darkTheme", b);
            editor.apply();
        });

        view.findViewById(R.id.btnAboutAppAndDev).setOnClickListener(this::showAboutAppAndDev);

        return view;
    }

    private void getUserNameSurname() {
        Task<DataSnapshot> getUsernameSurname = FirebaseDatabase.getInstance("https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("UserActivitiesCurrent/"+FirebaseAuth.getInstance().getUid()).child("nameAndSurname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                       userNameSurname.setText(task.getResult().getValue().toString());
                    }
                });
    }

    private void showAboutAppAndDev(View view) {
        builder = new AlertDialog.Builder(getActivity(), R.style.dialogStyle);
        builder.setTitle(R.string.about_app_and_developer);
        builder.setMessage(R.string.about_app_and_developer_message);
        builder.setPositiveButton(R.string.okay, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeLangDialog(View view) {
        builder = new AlertDialog.Builder(getActivity(), R.style.dialogStyle);
        builder.setTitle(R.string.change_language);
        final String [] langs = {"EN", "TR"};
        builder.setSingleChoiceItems(langs, -1, (dialogInterface, i) -> {
            if(!Locale.getDefault().getCountry().equals(langs[i])){
                Locale locale = new Locale(langs[i]);
                Locale.setDefault(locale);
                Configuration configuration = new Configuration();
                configuration.setLocale(locale);
                getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().getBaseContext().getResources().getDisplayMetrics());
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
                editor.putString("Lang", langs[i]);
                editor.apply();
                getActivity().recreate();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}