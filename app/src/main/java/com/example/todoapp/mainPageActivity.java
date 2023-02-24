package com.example.todoapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class mainPageActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        toolbar = findViewById(R.id.mainPageToolbar);
        toolbar.setTitle(R.string.todo);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.mainPageActivityDrawer);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        NavController navController = navHostFragment.getNavController();
        navigationView = findViewById(R.id.mainPageNavigation);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(item -> {
            int navId = navController.getCurrentDestination().getId();
            if(navId != R.id.mainPageFragment && item.getItemId() == R.id.toDoMenu){
                if(navId == R.id.statisticFragment){
                    navController.navigate(R.id.action_statisticFragment_to_mainPageFragment);
                }else if (navId == R.id.challengesFragment){
                    navController.navigate(R.id.action_challengesFragment_to_mainPageFragment);
                }
            }else if(navId != R.id.challengesFragment && item.getItemId() == R.id.challengeMenu){
                if(navId == R.id.statisticFragment){
                    navController.navigate(R.id.action_statisticFragment_to_challengesFragment);
                }else if (navId == R.id.mainPageFragment){
                    navController.navigate(R.id.action_mainPageFragment_to_challengesFragment);
                }
            }else if(navId != R.id.statisticFragment && item.getItemId() == R.id.statisticMenu){
                if(navId == R.id.challengesFragment){
                    navController.navigate(R.id.action_challengesFragment_to_statisticFragment);
                }else if (navId == R.id.mainPageFragment){
                    navController.navigate(R.id.action_mainPageFragment_to_statisticFragment);
                }
            }else if(item.getItemId() == R.id.logout){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(mainPageActivity.this, MainActivity.class));
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
        }
    }
}