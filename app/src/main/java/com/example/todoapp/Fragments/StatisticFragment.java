package com.example.todoapp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.todoapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class StatisticFragment extends Fragment {
    private TextView txtNoStatistic;
    private PieChart statisticPieChart;
    private ArrayList<PieEntry> categoryStatistic;
    private boolean diffLang = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        ((Toolbar) getActivity().findViewById(R.id.mainPageToolbar)).setTitle(getString(R.string.my_statistics));

        txtNoStatistic = view.findViewById(R.id.txtNoStatistic);
        statisticPieChart = view.findViewById(R.id.statisticPieChart);
        categoryStatistic = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext().getSharedPreferences("Settings.Lang", Context.MODE_PRIVATE).getString("Lang", "EN").equals("TR")) {
            diffLang = true;
        }
        fetchStatisticData();
    }

    private void fetchStatisticData() {
        String instance = "https://todoapp-32d07-default-rtdb.europe-west1.firebasedatabase.app/";
        FirebaseDatabase.getInstance(instance)
                .getReference("UsersActivitiesCurrent/" + FirebaseAuth.getInstance().getUid())
                .child("Statistic").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                categoryStatistic.add(new PieEntry(((Long) dataSnapshot.getValue()).floatValue(), dataSnapshot.getKey()));
                            }
                            if (getContext() != null)
                                setStatisticChart(!diffLang ? categoryStatistic : changeLang(categoryStatistic));
                        } else {
                            txtNoStatistic.setVisibility(View.VISIBLE);
                            System.out.println("no statistic data");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.getMessage());
                    }
                });
    }

    private ArrayList<PieEntry> changeLang(ArrayList<PieEntry> categoryStatistic) {
        if (getContext() != null) {
            String[] statisticCategory = getActivity().getResources().getStringArray(R.array.challengeCategory);
            String[] statisticCategoryByDefLang = getContext().getResources().getStringArray(R.array.challengeCategoryForDB);

            for (int i = 0; i < categoryStatistic.size(); i++) {
                int ind = Arrays.asList(statisticCategoryByDefLang).indexOf(categoryStatistic.get(i).getLabel());
                if (ind != -1)
                    categoryStatistic.get(i).setLabel(Arrays.asList(statisticCategory).get(ind));
            }
        }
        return categoryStatistic;
    }

    private void setStatisticChart(ArrayList<PieEntry> categoryStatistic) {
        PieDataSet categoryDataSet = new PieDataSet(categoryStatistic, "");
        categoryDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        categoryDataSet.setValueTextColor(Color.WHITE);
        categoryDataSet.setValueTextSize(16f);

        PieData statisticData = new PieData(categoryDataSet);

        statisticPieChart.setData(statisticData);
        statisticPieChart.getDescription().setEnabled(false);
        statisticPieChart.setCenterText(getContext().getString(R.string.my_statistics));
        statisticPieChart.animate();
        statisticPieChart.setVisibility(View.VISIBLE);
    }
}