package com.example.flowers.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import adapter.admin.CustomBottomNavigationAdmin;

public class AdminActivity extends AppCompatActivity {
    private ImageView imgProduct, btnSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Cài đặt navigation bottom admin
        BottomNavigationView bnv = findViewById(R.id.BottomNavViewAdmin);
        bnv.setSelectedItemId(R.id.nav_home);
        CustomBottomNavigationAdmin.setupBottomNavigationAdmin(this, bnv);

        imgProduct = findViewById(R.id.imgProduct);
        btnSetting = findViewById(R.id.btnSetting);

        // Bắt sự kiện khi nhấn vào imgProduct sẽ chuyển sang ProductFlowerAdminActivity
        imgProduct.setOnClickListener(v -> {
             startActivity(new Intent(AdminActivity.this, ProductFlowerAdminActivity.class));
             overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Bắt sự kiện khi nhấn vào btnSetting sẽ chuyển sang ProfileAdminActivity
        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileAdminActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}