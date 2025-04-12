package com.example.flowers.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import adapter.admin.CustomBottomNavigationAdmin;

public class NoficationAdminActivity extends AppCompatActivity {
    private ImageView btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nofication_admin);

        // Cài đặt navigation bottom admin
        BottomNavigationView bnv = findViewById(R.id.BottomNavViewAdmin);
        bnv.setSelectedItemId(R.id.nav_nofi);
        CustomBottomNavigationAdmin.setupBottomNavigationAdmin(this, bnv);

        btnSetting = findViewById(R.id.btnSetting);

        // Bắt sự kiện khi nhấn vào btnSetting sẽ chuyển sang ProfileAdminActivity
        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileAdminActivity.class));
        });

    }
}