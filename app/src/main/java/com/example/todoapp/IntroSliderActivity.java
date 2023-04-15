package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.example.todoapp.Adapters.SliderAdapter;
import com.example.todoapp.Fragments.slider1Fragment;
import com.example.todoapp.Fragments.slider2Fragment;
import com.example.todoapp.Fragments.slider3Fragment;

import java.util.ArrayList;

public class IntroSliderActivity extends AppCompatActivity {
    private Button btnSlider;
    private ViewPager2 introPager;
    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        introPager = findViewById(R.id.introPager);
        btnSlider = findViewById(R.id.btnSlider);

        fragments = new ArrayList<>();
        fragments.add(new slider1Fragment());
        fragments.add(new slider2Fragment());
        fragments.add(new slider3Fragment());

        SliderAdapter adapter = new SliderAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.setFragments(fragments);
        introPager.setAdapter(adapter);
        introPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderListener(introPager.getCurrentItem());
            }
        });

        btnSlider.setOnClickListener(view -> {
            if (introPager.getCurrentItem()+1 == fragments.size()){
                SharedPreferences.Editor editor = getSharedPreferences("Settings.introSlider", MODE_PRIVATE).edit();
                editor.putBoolean("introSlider", true);
                editor.apply();
                startActivity(new Intent(IntroSliderActivity.this, mainPageActivity.class));
                finish();
            }
            introPager.setCurrentItem(introPager.getCurrentItem()+1);
            sliderListener(introPager.getCurrentItem());
        });

    }

    public void sliderListener(int pageNum){
        if (pageNum+1 == fragments.size()){
            btnSlider.setText("Done !");
        }else{
            btnSlider.setText(R.string.next);
        }
    }
}