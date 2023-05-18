package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class mainPageActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private AdView mAdView;
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


        FirebaseMessaging.getInstance().subscribeToTopic("Reminding")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "DONE !";
                        if (!task.isSuccessful()){
                            msg = "FAILED !";
                        }
                    }
                });

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

        mAdView = findViewById(R.id.adView);
        loadAds();
    }

    private void loadAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(mainPageActivity.this, ""+loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
            }
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