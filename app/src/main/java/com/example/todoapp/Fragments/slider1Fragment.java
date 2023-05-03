package com.example.todoapp.Fragments;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.todoapp.R;

public class slider1Fragment extends Fragment {
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider1, container, false);

        context = getContext();

        ImageView  imageView = view.findViewById(R.id.imgTest);
        startAnim(R.drawable.ic_to_do_add_btn, imageView);

        ImageView imageView1 = view.findViewById(R.id.imgToDoLayer);
        startAnim(R.drawable.ic_to_do_layer,  imageView1);
        return view;
    }

    public void startAnim(int drawableImg, ImageView img){
        AnimatedVectorDrawableCompat animatedVectorDrawable = AnimatedVectorDrawableCompat.create(context, drawableImg);
        Animatable animatable = (Animatable) img.getDrawable();
        animatable.start();
    }

}