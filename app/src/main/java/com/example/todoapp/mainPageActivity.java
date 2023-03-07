package com.example.todoapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class mainPageActivity extends AppCompatActivity {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(mainPageActivity.this, MainActivity.class));
        }

        Toolbar toolbar = findViewById(R.id.mainPageToolbar);
        toolbar.setTitle(R.string.todo);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.mainPageActivityDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        NavController navController = navHostFragment.getNavController();
        NavigationView navigationView = findViewById(R.id.mainPageNavigation);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(item -> {
            int navId = navController.getCurrentDestination().getId();
            if(navId != R.id.mainPageFragment && item.getItemId() == R.id.toDoMenu){
                if(navId == R.id.statisticFragment){
                    navController.navigate(R.id.action_statisticFragment_to_mainPageFragment);
                }else if (navId == R.id.challengesFragment){
                    navController.navigate(R.id.action_challengesFragment_to_mainPageFragment);
                }else if(navId == R.id.settingsFragment){
                    navController.navigate(R.id.action_settingsFragment_to_mainPageFragment);
                }
            }else if(navId != R.id.challengesFragment && item.getItemId() == R.id.challengeMenu){
                if(navId == R.id.statisticFragment){
                    navController.navigate(R.id.action_statisticFragment_to_challengesFragment);
                }else if (navId == R.id.mainPageFragment){
                    navController.navigate(R.id.action_mainPageFragment_to_challengesFragment);
                }else if(navId == R.id.settingsFragment){
                    navController.navigate(R.id.action_settingsFragment_to_challengesFragment);
                }
            }else if(navId != R.id.statisticFragment && item.getItemId() == R.id.statisticMenu){
                if(navId == R.id.challengesFragment){
                    navController.navigate(R.id.action_challengesFragment_to_statisticFragment);
                }else if (navId == R.id.mainPageFragment){
                    navController.navigate(R.id.action_mainPageFragment_to_statisticFragment);
                }else if(navId == R.id.settingsFragment){
                    navController.navigate(R.id.action_settingsFragment_to_statisticFragment);
                }
            }else if (navId != R.id.settingsFragment && item.getItemId() == R.id.settingsMenu){
                if (navId == R.id.mainPageFragment) {
                    navController.navigate(R.id.action_mainPageFragment_to_settingsFragment);
                }else if(navId == R.id.challengesFragment){
                    navController.navigate(R.id.action_challengesFragment_to_settingsFragment);
                }else if(navId == R.id.statisticFragment){
                    navController.navigate(R.id.action_statisticFragment_to_settingsFragment);
                }
            }else if(item.getItemId() == R.id.logout){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(mainPageActivity.this, MainActivity.class));
                finish();
            }
            drawer.close();
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
            Toast.makeText(this, "Basildi", Toast.LENGTH_SHORT).show();
        }
    }
}