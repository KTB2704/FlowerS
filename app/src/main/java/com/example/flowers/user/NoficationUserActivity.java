package com.example.flowers.user;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import adapter.user.CustomBottomNavigation;

public class NoficationUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nofication_user);
        BottomNavigationView bnv = findViewById(R.id.BottomNavView);
        bnv.setSelectedItemId(R.id.nav_nofi);
        CustomBottomNavigation.setupBottomNavigation(this, bnv);
    }
}