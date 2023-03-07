package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}