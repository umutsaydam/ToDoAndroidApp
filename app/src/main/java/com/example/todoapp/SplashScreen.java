package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings.Theme", MODE_PRIVATE);

        if(sharedPreferences.getBoolean("darkTheme", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        sharedPreferences = getSharedPreferences("Settings.Lang", MODE_PRIVATE);
        setLang(sharedPreferences.getString("Lang", ""));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Intent intent;
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            intent = new Intent(SplashScreen.this, mainPageActivity.class);
        }else{
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void setLang(String lang) {
        if(!lang.equals("")){
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            SharedPreferences.Editor editor = getSharedPreferences("Settings.Lang", MODE_PRIVATE).edit();
            editor.putString("Lang", lang);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}