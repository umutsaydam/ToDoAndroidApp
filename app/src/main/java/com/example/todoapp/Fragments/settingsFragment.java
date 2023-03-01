package com.example.todoapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.todoapp.R;

import java.util.Locale;

public class settingsFragment extends Fragment {
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean darkTheme;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = getActivity().getSharedPreferences("Settings.Theme", MODE_PRIVATE);
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

        builder = new AlertDialog.Builder(getActivity(), R.style.dialogStyle);

        view.findViewById(R.id.btnChangeLang).setOnClickListener(this::changeLangDialog);

        SwitchCompat switchCompat = view.findViewById(R.id.switchChangeTheme);
        switchCompat.setChecked(darkTheme);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putBoolean("darkTheme", true);
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    editor.putBoolean("darkTheme", false);
                   // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                editor.apply();
            }
        });

        view.findViewById(R.id.btnAboutAppAndDev).setOnClickListener(this::showAboutAppAndDev);

        return view;
    }

    private void showAboutAppAndDev(View view) {
        builder.setTitle(R.string.about_app_and_developer);
        builder.setMessage(R.string.about_app_and_developer_message);
        builder.setPositiveButton(R.string.okay, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeThemeDialog(View view) {

    }

    private void changeLangDialog(View view) {
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