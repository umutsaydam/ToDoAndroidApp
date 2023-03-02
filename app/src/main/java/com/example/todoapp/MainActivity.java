package com.example.todoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("Settings", MainActivity.MODE_PRIVATE);
        String lan = preferences.getString("Lang", "");

        if (lan.equals(""))
            showLanguageDialog();
        else
        setLocale(lan);

        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            startActivity(new Intent(MainActivity.this, mainPageActivity.class));
        }
    }

    private void showLanguageDialog() {
        final String [] langs = {"EN", "TR"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(R.string.select_a_language);
        mBuilder.setSingleChoiceItems(langs, -1, (dialogInterface, i) -> {
            if(i == 0){
                setLocale(langs[i]);
                recreate();
            }else if(i == 1){
                setLocale(langs[i]);
                recreate();
            }
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Lang", lang);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}